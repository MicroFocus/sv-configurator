/*
 *  Certain versions of software and/or documents ("Material") accessible here may contain branding from
 *  Hewlett-Packard Company (now HP Inc.) and Hewlett Packard Enterprise Company.  As of September 1, 2017,
 *  the Material is now offered by Micro Focus, a separately owned and operated company.  Any reference to the HP
 *  and Hewlett Packard Enterprise/HPE marks is historical in nature, and the HP and Hewlett Packard Enterprise/HPE
 *  marks are the property of their respective owners.
 * __________________________________________________________________
 * MIT License
 *
 * Copyright (c) 2012-2018 Micro Focus or one of its affiliates.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are set forth in the express warranty statements
 * accompanying such products and services. Nothing herein should be construed as
 * constituting an additional warranty. Micro Focus shall not be liable for technical
 * or editorial errors or omissions contained herein.
 * The information contained herein is subject to change without notice.
 * __________________________________________________________________
 *
 */
package com.microfocus.sv.svconfigurator.processor;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.microfocus.sv.svconfigurator.build.parser.DataModelElementParser;
import com.microfocus.sv.svconfigurator.build.parser.DataSetElementParser;
import com.microfocus.sv.svconfigurator.build.parser.ManifestElementParser;
import com.microfocus.sv.svconfigurator.build.parser.PerfModelElementParser;
import com.microfocus.sv.svconfigurator.build.parser.ServiceDescriptionElementParser;
import com.microfocus.sv.svconfigurator.build.parser.ServiceElementParser;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServerInfo;
import com.microfocus.sv.svconfigurator.processor.export.AbstractWriter;
import com.microfocus.sv.svconfigurator.processor.export.FolderWriter;
import com.microfocus.sv.svconfigurator.processor.export.ZipWriter;
import com.microfocus.sv.svconfigurator.processor.utils.DataModelContentFilesExtractor;
import com.microfocus.sv.svconfigurator.serverclient.IServerManagementEndpointClient;
import com.microfocus.sv.svconfigurator.util.XmlUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ServiceListAtom;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ServiceListAtom.ServiceEntry;
import com.microfocus.sv.svconfigurator.processor.utils.DataModelScaExtractor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;
import org.w3c.dom.Document;

public class ExportProcessor {

    public static final String SERVER_TYPE_EMBEDDED = "Embedded";
    public static final String EMBEDDED_SERVER_URN = "urn:Embedded Server";
    private ICommandExecutorFactory commandExecutorFactory;

    private static final Logger LOG = LoggerFactory.getLogger(ExportProcessor.class);

    public ExportProcessor(ICommandExecutorFactory commandExecutorFactory) {
        this.commandExecutorFactory = commandExecutorFactory;
    }

    public ICommandExecutorFactory getCommandExecutorFactory() {
        return commandExecutorFactory;
    }

    public void process(ICommandExecutor exec, String directory, String svc, IProject project, boolean ignoreErrors, boolean exportAsArchive)
            throws Exception {
        File root = new File(directory);
        if (!root.exists()) {
            if (!root.mkdirs()) {
                throw new CommandExecutorException("Cannot create output directory: " + directory);
            }
        } else if (!root.isDirectory()) {
            throw new IllegalArgumentException("Specified path is not a directory: " + directory);
        }

        ServiceListAtom services = exec.getServiceList(null);
        Map<String, List<ServiceEntry>> projects = new HashMap<String, List<ServiceEntry>>();
        for (ServiceEntry entry : services.getEntries()) {
            if (svc != null && !svc.equals(entry.getId()) && !svc.equals(entry.getTitle())) {
                continue;
            }

            if (project != null && !isServiceInProject(entry.getId(), project)) {
                continue;
            }

            List<ServiceEntry> e = projects.get(entry.getProjectId());
            if (e == null) {
                e = new ArrayList<ServiceListAtom.ServiceEntry>();
                projects.put(entry.getProjectId(), e);
            }

            e.add(entry);
        }

        // we have project -> services mapping now
        for (Map.Entry<String, List<ServiceEntry>> entry : projects.entrySet()) {
            saveProject(entry.getValue(), root, exec, ignoreErrors, exportAsArchive);
        }

        if (projects.isEmpty()) {
            LOG.info("Successfully finished, but no project was exported.");
        } else {
            LOG.info("Successfully finished");
        }
    }

    private boolean isServiceInProject(String serviceId, IProject project) {
        for (IService svc: project.getServices()) {
            if (serviceId.equals(svc.getId())) {
                return true;
            }
        }
        return false;
    }

    private void saveProject(List<ServiceEntry> serviceEntries, File root, ICommandExecutor exec, boolean ignoreErrors,
                             boolean exportAsArchive) throws Exception {
        ServiceEntry firstService = serviceEntries.get(0);
        String projectName = firstService.getProjectName();
        if (projectName == null) {
            projectName = firstService.getTitle() + " Project";
        }

        AbstractWriter writer = (exportAsArchive) ? new ZipWriter(root, projectName) : new FolderWriter(root, projectName);

        List<String> projectEntries = new ArrayList<String>();
        int vsIndex = 0;

        HashMap<String, String> scaSdIds2ServiceIds = new HashMap<String, String>();
        HashMap<String, HashSet<String>> service2ContentFileIds = new HashMap<String, HashSet<String>>();

        for (ServiceEntry entry : serviceEntries) {
            List<String> serviceFiles = new ArrayList<String>();
            HashMap<String, String> serviceScaIds = new HashMap<String, String>();
            HashSet<String> contentFileIds = new HashSet<String>();

            try {
                saveService(serviceFiles, writer, entry, exec, vsIndex++, serviceScaIds, contentFileIds);
                service2ContentFileIds.put(entry.getId(), contentFileIds);
                // add content to project maps if saveService passed
                projectEntries.addAll(serviceFiles);
                scaSdIds2ServiceIds.putAll(serviceScaIds);
                writer.commit();
            } catch (Exception e) {
                LOG.error("Failed to save virtual service '" + entry.getTitle() + "'. Project: '" + projectName + "'", e);
                if (!ignoreErrors) {
                    throw e;
                }
                writer.rollback();
            }
        }

        // service descriptions of service call activities
        int scaSdIndex = 0;
        for (String sdId : scaSdIds2ServiceIds.keySet()) {
            writeServiceDataToFile(exec.getClient().fetchServiceDescription(scaSdIds2ServiceIds.get(sdId), sdId),
                    writer, projectName, " SCA-SD " + (scaSdIndex++) + ".vsdsc", projectEntries);
        }

        // shared scripts
        List<Map<String, String>> contentFilesMetadata = new ArrayList<Map<String, String>>();
        HashSet<String> processedContentFileIds = new HashSet<String>(); //not to download shared files multiple times

        for (String vsId : service2ContentFileIds.keySet()) {
            HashSet<String> contentFileIds = service2ContentFileIds.get(vsId);
            for (String cfId : contentFileIds) {
                if(processedContentFileIds.contains(cfId)) {
                    continue;
                }
                byte[] contentFile = exec.getClient().fetchContentFile(vsId, cfId);
                Map<String,String> contentFileMetadata = new HashMap<String, String>();
                byte[] rawContentFile = extractContentFile(contentFile, contentFileMetadata);
                writer.addData(getContentFilePath(contentFileMetadata), rawContentFile);
                contentFilesMetadata.add(contentFileMetadata);
                processedContentFileIds.add(cfId);
            }
        }

        String serverUrl = String.valueOf(exec.getClient().getMgmtUri()).replaceAll("/+$", "");
        ServerInfo serverInfo = exec.getClient().getServerInfo();
        String serverEdition = serverInfo.getServerEditionName();
        String serverVersion = serverInfo.getServerVersion();
        String serverType = serverInfo.getServerType();

        String serverIdentifier = (SERVER_TYPE_EMBEDDED.equals(serverType)) ? EMBEDDED_SERVER_URN : serverUrl;
        byte[] projectFilePayload = createVproj(projectEntries, contentFilesMetadata, firstService.getProjectId(),
                serverUrl, serverVersion, serverEdition, serverIdentifier).getBytes("UTF-8");
        writer.addData(projectName + ".vproj", projectFilePayload);
        writer.commit();
        writer.close();
    }

    private byte[] extractContentFile(byte[] contentFile, Map<String,String> metadata) throws SVCParseException {
        Document doc;
        ByteArrayInputStream bis = null;
        try {
            bis = new ByteArrayInputStream(contentFile);
            doc = XmlUtils.createDoc(bis);
        } catch (Exception e) {
            throw new SVCParseException("Failed to parse content file", e);
        } finally {
            IOUtils.closeQuietly(bis);
        }

        if (doc != null) {
            try {
                metadata.putAll(XmlUtils.getNodeAsKeyValueMap(doc.getLastChild(), Document.ATTRIBUTE_NODE));
                if(metadata.containsKey("xmlns")) {
                    metadata.remove("xmlns");
                }
                return Base64.decodeBase64(doc.getLastChild().getTextContent().getBytes());
            } catch (Exception e) {
                throw new SVCParseException("Content File metadata extraction failed", e);
            }
        }

        return null;
    }

    private String getContentFilePath(Map<String,String> metadata) throws SVCParseException {
        final String relativePathMetadataName = "relativePath";
        if(metadata.containsKey(relativePathMetadataName)) {
            return metadata.get(relativePathMetadataName);
        }
        throw new SVCParseException("Content File doesn't contain relativePath attribute.");
    }

    private String createVproj(List<String> files, List<Map<String, String>> contentFilesMetadata, String projectId, String serverUrl, final String serverVersion, final String serverEdition, String serverIdentifier) throws SVCParseException {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
        sb.append("<Project ToolsVersion=\"4.0\" xmlns=\"http://schemas.microsoft.com/developer/msbuild/2003\" DefaultTargets=\"Build\"><PropertyGroup><ProjectGuid>");
        sb.append(projectId);
        sb.append("</ProjectGuid><Configuration Condition=\" '$(Configuration)' == '' \">Debug</Configuration><Platform Condition=\" '$(Platform)' == '' \">x86</Platform><ServerIdentifier>")
                .append(serverIdentifier).append("</ServerIdentifier><ServerUrl>").append(serverUrl)
                .append("</ServerUrl><ProductVersion>").append(serverVersion).append("</ProductVersion><ProductEdition>")
                .append(serverEdition).append("</ProductEdition></PropertyGroup><PropertyGroup Condition=\" '$(Platform)' == 'x86' \"><PlatformTarget>x86</PlatformTarget></PropertyGroup><ItemGroup>");
        for (String f : files) {
            sb.append("<None Include=\"").append(f).append("\" />");
        }
        for (Map<String, String> contentFileMetadata : contentFilesMetadata) {
            String contentFilePath = getContentFilePath(contentFileMetadata);
            sb.append("<Content Include=\"").append(contentFilePath).append("\">");
            for (String metadataKey : contentFileMetadata.keySet()) {
                sb.append("<").append(metadataKey).append(">")
                  .append(contentFileMetadata.get(metadataKey))
                  .append("</").append(metadataKey).append(">");
            }
            sb.append("</Content>");
        }
        sb.append("</ItemGroup></Project>");
        return sb.toString();
    }

    private void saveService(List<String> files, AbstractWriter writer, ServiceEntry entry, ICommandExecutor exec,
                             int vsIndex, HashMap<String, String> scaSdIds2ServiceIds, Set<String> projectContentFileIds) throws Exception {

        String serviceName = entry.getTitle() + " " + vsIndex;
        serviceName = AbstractWriter.entityNameToFileName(serviceName);
        LOG.info("  writing virtual service '" + serviceName + "'...");

        String vsId = entry.getId();

        IServerManagementEndpointClient client = exec.getClient();

        // write virtual service
        writeServiceDataToFile(client.fetchVirtualService(vsId), writer, serviceName,
                ServiceElementParser.FILE_EXTENSION, files);

        // write performance models
        Collection<String> pmIds = client.getPerformanceModelIds(vsId);
        int pmIndex = 0;
        for (String pmId : pmIds) {
            writeServiceDataToFile(client.fetchPerformanceModel(vsId, pmId), writer, serviceName,
                    " PM " + (pmIndex++) + PerfModelElementParser.FILE_EXTENSION, files);
        }

        // write data models & datasets

        Collection<String> scaSdIds = new HashSet<String>();
        Collection<String> vsSdIds = client.getServiceDescriptionIds(vsId);
        Collection<String> dmIds = client.getDataModelIds(vsId);
        Set<String> contentFileIds = new HashSet<String>();

        Map<String, Dataset> datasets = new HashMap<String, Dataset>();
        Map<String, Collection<String>> dm2refIds = new HashMap<String, Collection<String>>();
        int dmIndex = 0;
        int dsIndex = 0;
        for (String dmId : dmIds) {
            byte[] dataModel = client.fetchDataModel(vsId, dmId);
            writeServiceDataToFile(dataModel, writer, serviceName,
                    " DM " + (dmIndex++) + DataModelElementParser.FILE_EXTENSION, files);

            Set<String> datamodelRefIds = new HashSet<String>();
            dm2refIds.put(dmId, datamodelRefIds);
            // add all SD of VS to DM references
            datamodelRefIds.addAll(vsSdIds);
            try {
                // get list of referenced contentFiles and add it to set of this DM references and set of contentFiles for this VS
                Set<String> dmContentFileIds = new HashSet<String>();
                DataModelContentFilesExtractor.extractContentFileIds(dataModel, dmContentFileIds);
                contentFileIds.addAll(dmContentFileIds);
                datamodelRefIds.addAll(dmContentFileIds);

                // extract service description references
                Set<String> dmScaSdIds = DataModelScaExtractor.extractScaServiceDescriptionIds(dataModel);
                if (dmScaSdIds != null) {
                    for (String sdId : dmScaSdIds) {
                        scaSdIds2ServiceIds.put(sdId, vsId);
                    }
                    datamodelRefIds.addAll(dmScaSdIds);
                    scaSdIds.addAll(dmScaSdIds);
                }
            } catch (AbstractSVCException e) {
                LOG.error("Failed to parse DM '" + serviceName + "' for SCAs.", e);
                throw e;
            }

            Collection<String> dsIds = client.getDataSetIds(vsId, dmId);
            for (String dsId : dsIds) {
                byte[] dataSet = client.fetchDataSet(vsId, dmId, dsId);
                writeServiceDataToFile(dataSet, writer, serviceName,
                        " DS " + (dsIndex++) + DataSetElementParser.FILE_EXTENSION, files);
                Dataset dataset = parseDataset(dataSet, dsId);
                datasets.put(dataset.id, dataset);
                datamodelRefIds.add(dataset.id);
            }
        }

        int sdIndex = 0;
        for (String sdId : vsSdIds) {
            writeServiceDataToFile(client.fetchServiceDescription(vsId, sdId), writer, serviceName,
                    " SD " + (sdIndex++) + ServiceDescriptionElementParser.FILE_EXTENSION, files);
        }

        String vsmf = createMetafile(vsId, vsSdIds, scaSdIds, pmIds, dmIds, dm2refIds, datasets, contentFileIds);
        writeServiceDataToFile(vsmf.getBytes("UTF-8"), writer, serviceName, ManifestElementParser.FILE_SUFFIX, files);
        projectContentFileIds.addAll(contentFileIds);
    }

    private Dataset parseDataset(byte[] dataSet, String dsId) throws UnsupportedEncodingException {
        String x = new String(dataSet, "ASCII");
        int idx = x.indexOf("hc=\"");
        int endIdx = x.indexOf("\"", idx + 4);
        String hc = x.substring(idx + 4, endIdx);
        return new Dataset(dsId, hc);
    }

    private void writeServiceDataToFile(byte[] data, AbstractWriter writer, String serviceName, String fileSuffix, List<String> projectEntries) throws IOException {
        String filePath = serviceName + fileSuffix;
        projectEntries.add(filePath);
        writer.addData(filePath, data);
    }

    private String createMetafile(String vsId, Collection<String> sdIds, Collection<String> scaSdIds, Collection<String> pmIds,
                                  Collection<String> dmIds, Map<String, Collection<String>> dm2refIds,
                                  Map<String, Dataset> datasets, Set<String> contentFileIds) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>\r\n");
        sb.append("<deploymentManifest id=\"manifest_");
        sb.append(vsId);
        sb.append("\" rootItemId=\"");
        sb.append(vsId);
        sb.append("\" xmlns=\"http://hp.com/SOAQ/ServiceVirtualization/2010/\">");

        List<String> vsRefs = new ArrayList<String>();
        vsRefs.addAll(pmIds);
        vsRefs.addAll(dmIds);
        vsRefs.addAll(sdIds);
        sb.append(writeItem(vsId, vsRefs, "VirtualService", null));


        for (String dmId : dmIds) {
            Collection<String> dmRefs = dm2refIds.get(dmId);
            sb.append(writeItem(dmId, dmRefs, "DataModel", null));
        }

        for (String pmId : pmIds) {
            sb.append(writeItem(pmId, null, "PerformanceModel", null));
        }

        for (Dataset ds : datasets.values()) {
            sb.append(writeItem(ds.id, null, "Dataset", ds.hc));
        }

        Set<String> sdItems = new HashSet<String>();
        sdItems.addAll(sdIds);
        sdItems.addAll(scaSdIds);
        for (String sdId : sdItems) {
            sb.append(writeItem(sdId, null, "ServiceDescription", null));
        }

        for (String contentFileId : contentFileIds) {
            sb.append(writeItem(contentFileId, null, "ContentFile", null));
        }

        sb.append("</deploymentManifest>");

        return sb.toString();
    }

    private String writeItem(String id, Collection<String> references,
            String type, String hc) {
        StringBuilder sb = new StringBuilder("<Item id=\"");
        sb.append(id);
        sb.append("\" type=\"");
        sb.append(type);
        if (hc != null) {
            sb.append("\" contentHashcode=\"");
            sb.append(hc);
        }
        sb.append("\">");
        if (references == null || references.isEmpty()) {
            sb.append("<References />");
        } else {
            sb.append("<References>");
            for (String x : references) {
                sb.append("<Ref id=\"");
                sb.append(x);
                sb.append("\" />");
            }
            sb.append("</References>");
        }
        sb.append("</Item>");
        return sb.toString();
    }

    private static class Dataset {
        private String id;
        private String hc;

        private Dataset(String id, String hc) {
            this.id = id;
            this.hc = hc;
        }
    }
}
