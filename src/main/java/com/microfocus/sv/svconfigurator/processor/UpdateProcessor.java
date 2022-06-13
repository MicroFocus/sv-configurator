/*
 *  Certain versions of software and/or documents ("Material") accessible here may contain branding from
 *  Hewlett-Packard Company (now HP Inc.) and Hewlett Packard Enterprise Company.  As of September 1, 2017,
 *  the Material is now offered by Micro Focus, a separately owned and operated company.  Any reference to the HP
 *  and Hewlett Packard Enterprise/HPE marks is historical in nature, and the HP and Hewlett Packard Enterprise/HPE
 *  marks are the property of their respective owners.
 * __________________________________________________________________
 * MIT License
 *
 * Copyright (c) 2012-2022 Micro Focus or one of its affiliates.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.microfocus.sv.svconfigurator.build.IProjectBuilder;
import com.microfocus.sv.svconfigurator.build.parser.AbstractXMLElementParser;
import com.microfocus.sv.svconfigurator.build.parser.DataModelElementParser;
import com.microfocus.sv.svconfigurator.build.parser.DataSetElementParser;
import com.microfocus.sv.svconfigurator.build.parser.ManifestElementParser;
import com.microfocus.sv.svconfigurator.build.parser.PerfModelElementParser;
import com.microfocus.sv.svconfigurator.build.parser.ServiceDescriptionElementParser;
import com.microfocus.sv.svconfigurator.build.parser.ServiceElementParser;
import com.microfocus.sv.svconfigurator.core.IDataModel;
import com.microfocus.sv.svconfigurator.core.IDataSet;
import com.microfocus.sv.svconfigurator.core.IPerfModel;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IProjectElement;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.IServiceDescription;
import com.microfocus.sv.svconfigurator.core.impl.datasource.InMemoryProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration.RuntimeMode;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;
import com.microfocus.sv.svconfigurator.util.ProjectUtils;
import com.microfocus.sv.svconfigurator.util.XmlUtils;

public class UpdateProcessor {

    private static final Logger LOG = LoggerFactory
            .getLogger(UpdateProcessor.class);

    private final ICommandExecutorFactory commandExecutorFactory;
    private final IProjectBuilder projectBuilder;

    private final PerfModelElementParser pmParser;
    private final DataModelElementParser dmParser;
    private final DataSetElementParser dsParser;
    private final ManifestElementParser manifestParser;
    private final ServiceDescriptionElementParser sdParser;
    private final ServiceElementParser vsParser;

    private final Pattern numberResolver = Pattern
            .compile("^.* ([0-9]{3})\\.vsdataset$");
    private final Pattern unsafeCharacters = Pattern.compile("[/?<>\\:*|\"^]");

    public UpdateProcessor(IProjectBuilder projectBuilder,
            ICommandExecutorFactory commandExecutorFactory) {
        this.commandExecutorFactory = commandExecutorFactory;
        this.projectBuilder = projectBuilder;
        this.pmParser = new PerfModelElementParser();
        this.dmParser = new DataModelElementParser();
        this.dsParser = new DataSetElementParser();
        this.manifestParser = new ManifestElementParser();
        this.sdParser = new ServiceDescriptionElementParser();
        this.vsParser = new ServiceElementParser();
    }

    public void process(ICommandExecutor exec, String projectPath, String projectPassword, IProject project, String service)
            throws AbstractSVCException {
        if (project == null) {
            throw new CommandExecutorException(
                    "You have to specify the project.");
        }

        IService svc = ProjectUtils.findProjElem(project.getServices(), service, ProjectUtils.ENTITY_VIRTUAL_SERVICE);

        ServiceRuntimeConfiguration config = exec.getServiceRuntimeInfo(svc);
        if (config == null) {
            throw new CommandExecutorException("Cannot find the service [" + service + "] on the server.");
        }
        if (config.getRuntimeMode() != RuntimeMode.STAND_BY
                && config.getRuntimeMode() != RuntimeMode.SIMULATING) {
            throw new CommandExecutorException("Service [" + service + "] must be in the STAND_BY or SIMULATING mode.");
        }

        File projectRoot = new File(projectPath).getParentFile();
        Collection<IProjectElementDataSource> dataSources = projectBuilder
                .getDataSources(new File(projectPath), projectPassword);

        if (svc.getPerfModels() != null) {
            for (IPerfModel pm : svc.getPerfModels()) {
                // updating *.vspfmodel
                byte[] data = exec.getClient().fetchPerformanceModel(
                        svc.getId(), pm.getId());
                String pmFile = getName(dataSources, pm.getId(),
                        projectPassword, pmParser);
                if (pmFile == null) {
                    throw new CommandExecutorException(
                            "Failed to find the selected performance model file [ID=" + pm.getId() + "]");
                }
                persistFile(projectRoot, pmFile, data);
            }
        }

        if (svc.getDataModels() != null) {
            for (IDataModel dm : svc.getDataModels()) {
                // updating *.vsmodel
                byte[] data = exec.getClient().fetchDataModel(svc.getId(),
                        dm.getId());
                String dmFile = getName(dataSources, dm.getId(),
                        projectPassword, dmParser);
                if (dmFile == null) {
                    throw new CommandExecutorException(
                            "Failed to find the selected data model file [ID=" + dm.getId() + "]");
                }
                persistFile(projectRoot, dmFile, data);

                Collection<String> datasetIds = exec.getClient().getDataSetIds(
                        svc.getId(), dm.getId());
                List<String> foundDsNames = new ArrayList<String>();
                List<String> addedIds = new ArrayList<String>();
                for (String dsId : datasetIds) {
                    String dsFile = getName(dataSources, dsId, projectPassword,
                            dsParser);
                    if (dsFile == null) {
                        addedIds.add(dsId);
                        LOG.trace("DS not found: " + dsId);
                    } else {
                        // updating *.vsdataset
                        data = exec.getClient().fetchDataSet(svc.getId(),
                                dm.getId(), dsId);
                        foundDsNames.add(dsFile);
                        persistFile(projectRoot, dsFile, data);
                    }
                }

                if (!addedIds.isEmpty()) {
                    Map<String, IDataSet> notFoundDsIds = new HashMap<String, IDataSet>();
                    for (String dsId : addedIds) {
                        // adding new *.vsdataset
                        data = exec.getClient().fetchDataSet(svc.getId(), dm.getId(), dsId);
                        String nextDsFile = findNextName(foundDsNames, projectRoot, dm);
                        IDataSet ds = (IDataSet) dsParser.create(
                                new InMemoryProjectElementDataSource(data, "DS" + DataSetElementParser.FILE_EXTENSION),
                                null);
                        notFoundDsIds.put(nextDsFile, ds);
                        persistFile(projectRoot, nextDsFile, data);
                    }

                    // updating *.vsproj
                    addFiles(projectPath, notFoundDsIds.keySet());

                    // updating *.vsmf
                    String manifestFile = getName(dataSources, "manifest_" + svc.getId(), projectPassword, manifestParser);
                    if (manifestFile == null) {
                        throw new CommandExecutorException(
                                "Failed to find the manifest file of the service '"
                                        + svc.getId() + "'");
                    }
                    addManifestItems(projectRoot, manifestFile,
                            notFoundDsIds.values(), dm.getId());
                }
            }
        }

        // updating *.vsdsc
        if (svc.getDescriptions() != null) {
            for (IServiceDescription sd : svc.getDescriptions()) {
                byte[] data = exec.getClient().fetchServiceDescription(
                        svc.getId(), sd.getId());
                String sdFile = getName(dataSources, sd.getId(),
                        projectPassword, sdParser);
                if (sdFile == null) {
                    throw new CommandExecutorException("Failed to find the service description file [ID=" + sd.getId() + "]");
                }
                persistFile(projectRoot, sdFile, data);
            }
        }
        
        // updating *.vs
        byte[] data = exec.getClient().fetchVirtualService(svc.getId());
        String vsFile = getName(dataSources, svc.getId(), projectPassword, vsParser);
        if (vsFile == null) {
            throw new CommandExecutorException("Failed to find the virtual service file [ID=" + svc.getId() + "]");
        }
        persistFile(projectRoot, vsFile, data);
    }

    private void addManifestItems(File projectRoot, String manifestFile,
            Collection<IDataSet> values, String dmId)
            throws CommandExecutorException {
        File manifestPath = new File(projectRoot, manifestFile);
        Document doc = loadXml(manifestPath);

        Element root = doc.getDocumentElement();
        if (root == null) {
            throw new CommandExecutorException(
                    "Failed to find root element of the manifest file");
        }

        NodeList items = root.getElementsByTagName("Item");
        if (items == null || items.getLength() < 1) {
            throw new CommandExecutorException(
                    "Failed to find Item element of the manifest file");
        }

        Element dmElement = null;
        for (int i = 0; i < items.getLength(); i++) {
            Node item = items.item(i);
            if (item instanceof Element) {
                Element elementItem = (Element) item;
                String type = elementItem.getAttribute("type");
                String id = elementItem.getAttribute("id");
                if ("DataModel".equals(type) && dmId.equals(id)) {
                    dmElement = elementItem;
                }
            }
        }

        if (dmElement == null) {
            throw new CommandExecutorException(
                    "Failed to find data model Item element of the manifest file");
        }

        try {
            Element references = (Element) dmElement.getElementsByTagName(
                    "References").item(0);
            for (IDataSet ds : values) {
                Element item = doc.createElement("Ref");
                item.setAttribute("id", ds.getId());
                references.appendChild(item);
            }
        } catch (Exception e) {
            throw new CommandExecutorException(
                    "Failed to find data model References element of the manifest file");
        }

        for (IDataSet ds : values) {
            Element item = doc.createElement("Item");
            item.setAttribute("id", ds.getId());
            item.setAttribute("type", "Dataset");
            item.setAttribute("contentHashcode",
                    Long.toString(ds.getDataHashCode()));
            Element references = doc.createElement("References");
            item.appendChild(references);
            root.appendChild(item);
        }

        saveXml(manifestPath, doc);
    }

    private void addFiles(String projectPath, Set<String> keySet)
            throws CommandExecutorException {
        File projectFile = new File(projectPath);
        Document doc = loadXml(projectFile);

        Element root = doc.getDocumentElement();
        if (root == null) {
            throw new CommandExecutorException(
                    "Failed to find root element of the project file");
        }

        NodeList items = root.getElementsByTagName("ItemGroup");
        if (items == null || items.getLength() != 1) {
            throw new CommandExecutorException(
                    "Failed to find ItemGroup element of the project file");
        }

        Node itemsGroup = items.item(0);
        for (String path : keySet) {
            Element item = doc.createElement("None");
            item.setAttribute("Include", ProjectUtils.encodeInclude(path));
            itemsGroup.appendChild(item);
        }

        saveXml(projectFile, doc);
    }

    private String findNextName(List<String> foundDsNames, File projectRoot,
            IDataModel dm) {
        int max = -2;
        String baseName = null;
        for (String name : foundDsNames) {
            try {
                Matcher m = numberResolver.matcher(name);
                if (m.matches()) {
                    max = Math.max(Integer.parseInt(m.group(1), 10), max);
                    baseName = name.substring(0, name.length()
                            - DataSetElementParser.FILE_EXTENSION.length() - 4);
                } else {
                    max = Math.max(-1, max);
                    baseName = name.substring(0, name.length()
                            - DataSetElementParser.FILE_EXTENSION.length());
                }
            } catch (Exception e) {
                LOG.warn("Failed to detect a dataset name", e);
            }
        }
        String name;
        do {
            max++;
            if (max == -1) {
                name = unsafeCharacters.matcher(dm.getName()).replaceAll(
                        "_")
                        + " Dataset" + DataSetElementParser.FILE_EXTENSION;
            } else {
                if (baseName == null) {
                    name = unsafeCharacters.matcher(dm.getName())
                            .replaceAll("_")
                            + String.format(" Dataset %03d", max)
                            + DataSetElementParser.FILE_EXTENSION;
                } else {
                    name = baseName + String.format(" %03d", max)
                            + DataSetElementParser.FILE_EXTENSION;
                }
            }
            foundDsNames.add(name);
        } while (new File(projectRoot, name).exists());
        LOG.trace("Detected dataset name: " + name);
        return name;
    }

    private static String getName(
            Collection<IProjectElementDataSource> dataSources, String id,
            String projectPassword, AbstractXMLElementParser parser)
            throws AbstractSVCException {
        String file = null;
        for (IProjectElementDataSource ds : dataSources) {
            if (parser.isParserForDataSource(ds)) {
                IProjectElement pm = parser.create(ds, projectPassword);
                if (pm.getId().equals(id)) {
                    file = ds.getName();
                    break;
                }
            }
        }
        return file;
    }

    private static void persistFile(File projectRoot, String file, byte[] data)
            throws CommandExecutorException {
        File f = new File(projectRoot, file);
        FileOutputStream fos = null;
        try {
            boolean exists = f.exists();
            fos = new FileOutputStream(f, false);
            fos.write(data);
            if (exists) {
                LOG.info("Updated the file: " + f);
            } else {
                LOG.info("Created the file: " + f);
            }
        } catch (Exception e) {
            throw new CommandExecutorException("Failed to update the file '"
                    + f + "'", e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    private static Document loadXml(File f) throws CommandExecutorException {
        FileInputStream fis = null;
        Document doc;
        try {
            fis = new FileInputStream(f);
            doc = XmlUtils.createDoc(fis);
            return doc;
        } catch (Exception e) {
            throw new CommandExecutorException("Failed to open the file '" + f
                    + "'", e);
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    private static void saveXml(File f, Document doc)
            throws CommandExecutorException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f, false);
            XmlUtils.writeDoc(fos, doc);
        } catch (Exception e) {
            throw new CommandExecutorException("Failed to save the file '" + f
                    + "'", e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
        LOG.info("Updated the file: " + f);
    }

    public ICommandExecutorFactory getCommandExecutorFactory() {
        return commandExecutorFactory;
    }

}
