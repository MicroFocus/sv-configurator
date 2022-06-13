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
package com.microfocus.sv.svconfigurator.build;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.microfocus.sv.svconfigurator.build.parser.AbstractProjectElementParser;
import com.microfocus.sv.svconfigurator.build.parser.LoggedServiceCallListParser;
import com.microfocus.sv.svconfigurator.core.*;
import com.microfocus.sv.svconfigurator.core.impl.datasource.ArchiveProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.datasource.ContentFileElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.datasource.FileProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.exception.ProjectBuilderException;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.core.impl.visitor.DataSetFilter;
import com.microfocus.sv.svconfigurator.core.impl.visitor.ProjectVisitorAdapter;
import com.microfocus.sv.svconfigurator.core.impl.visitor.ServiceChildElementFilter;
import com.microfocus.sv.svconfigurator.util.XmlUtils;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ServiceVirtualization project archive.
 */
public class ProjectBuilder implements IProjectBuilder {

    private final String ERROR_CONTENT_FILE_PARSING = "Error when parsing content file project items";

    public static final String VPROJA_SUFFIX = ".vproja";
    public static final String VPROJ_SUFFIX = ".vproj";

    private static final Logger LOG = LoggerFactory.getLogger(ProjectBuilder.class);

    public ProjectBuilder() {

    }

    /**
     * @param projFile File with project archive
     * @return Created project object representation
     */
    public IProject buildProject(File projFile, String projectPassword) throws ProjectBuilderException {
        Collection<IProjectElementDataSource> dataSources = getDataSources(projFile, projectPassword);
        Map<String, IProjectElement> entityMap = createEntityIdFileNameMap(dataSources, projectPassword);

        //Get all the manifests and Project instances
        ManifestProjectGrabber grabber = new ManifestProjectGrabber();
        for (IProjectElement el : entityMap.values()) {
            el.accept(grabber);
        }

        //Iterate through all the manifests and build Project tree structure (join objects together)
        IProject p = grabber.getProject();
        for (IManifest m : grabber.getManifests()) {
            IService svc = processManifest(m, entityMap);
            p.addService(svc);
        }

        return p;
    }

    public Collection<IProjectElementDataSource> getDataSources(File projFile, String projectPassword) throws ProjectBuilderException {
        String fileName = projFile.getAbsolutePath();
        if (!projFile.exists()) {
            throw new ProjectBuilderException("File \"" + fileName + "\" was not found.");
        }

        Collection<IProjectElementDataSource> dataSources;
        if (fileName.endsWith(VPROJ_SUFFIX)) { //it is normal project (in a folder)
            return obtainProjectDataSources(projFile);
        } else if (fileName.endsWith(VPROJA_SUFFIX)) { //it is a project archive (.vproja)
            return obtainProjectArchiveDataSources(projFile, projectPassword);
        }

        throw new ProjectBuilderException("Unknown project file: " + fileName);
    }

    //========================================= PRIVATE METHODS ============================================

    /**
     * Process the .proj file and returns the collection of IProjectElementDataSource instances representing single
     * project files
     *
     * @throws ProjectBuilderException
     */
    private Collection<IProjectElementDataSource> obtainProjectDataSources(File proj) throws ProjectBuilderException {
        try {
            Collection<IProjectElementDataSource> res = new HashSet<IProjectElementDataSource>();
            FileInputStream fis = new FileInputStream(proj);

            res.add(new FileProjectElementDataSource(proj));
            File parent = proj.getParentFile();

            NodeList childNodes = getProjectItems(fis);
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node projectFileNode = childNodes.item(i);
                String projectFilePath = projectFileNode.getAttributes().getNamedItem("Include").getTextContent();
                File f = new File(parent, projectFilePath);
                if (!f.exists()) {
                    throw new ProjectBuilderException("File " + f.getName() + " was not found.");
                }

                if (projectFileNode.getLocalName() == "None") {
                    res.add(new FileProjectElementDataSource(f));
                }
                if (projectFileNode.getLocalName() == "Content") {
                    res.add(new ContentFileElementDataSource(new FileProjectElementDataSource(f), XmlUtils.getNodeAsKeyValueMap(childNodes.item(i), Document.ELEMENT_NODE)));
                }
            }

            File[] msgLogs = proj.getParentFile().listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(LoggedServiceCallListParser.FILE_EXTENSION);
                }
            });
            for(File msgLogFile: msgLogs){
                res.add(new FileProjectElementDataSource(msgLogFile));
            }

            return res;
        } catch (FileNotFoundException e) {
            throw new ProjectBuilderException("Project file was not found", e);
        } catch (ParserConfigurationException e) {
            throw new ProjectBuilderException(ERROR_CONTENT_FILE_PARSING, e);
        } catch (TransformerException e) {
            throw new ProjectBuilderException(ERROR_CONTENT_FILE_PARSING, e);
        } catch (IOException e) {
            throw new ProjectBuilderException(ERROR_CONTENT_FILE_PARSING, e);
        }
    }

    /**
     * Process the .vproja file and returns the collection of its IProjectElementDataSource instances, representing
     * single project files
     *
     * @param archive         project archive file (.vproja)
     * @throws ProjectBuilderException
     */
    private Collection<IProjectElementDataSource> obtainProjectArchiveDataSources(File archive, String projectPassword) throws ProjectBuilderException {
        Collection<IProjectElementDataSource> res = new HashSet<IProjectElementDataSource>();

        try {
            ZipFile zipFile = new ZipFile(archive);

            if (zipFile.isEncrypted()) {
                if (projectPassword == null || projectPassword.length() == 0) {
                    throw new ProjectBuilderException("Project archive '" + archive.getAbsolutePath() + "' is encrypted but project password is not specified");
                }
                zipFile.setPassword(projectPassword.toCharArray());
            }
            //find .vproj file
            FileHeader proj = null;
            for (Object o : zipFile.getFileHeaders()) {
                FileHeader fileHeader = (FileHeader) o;
                if (fileHeader.getFileName().endsWith(VPROJ_SUFFIX)) {
                    proj = fileHeader;
                    break;
                }
            }

            if (proj == null) {
                throw new ProjectBuilderException(VPROJ_SUFFIX + " file was not found in the project archive");
            }

            res.add(new ArchiveProjectElementDataSource(zipFile, proj));

            NodeList childNodes = getProjectItems(zipFile.getInputStream(proj));
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node projectFileNode = childNodes.item(i);
                String projectFilePath = projectFileNode.getAttributes().getNamedItem("Include").getTextContent();
                FileHeader entry = zipFile.getFileHeader(projectFilePath);
                if (entry == null) {
                    throw new ProjectBuilderException(projectFilePath + " file was not found in project archive.");
                }

                if (projectFileNode.getLocalName() == "None") {
                    res.add(new ArchiveProjectElementDataSource(zipFile, entry));
                }
                if (projectFileNode.getLocalName() == "Content") {
                    res.add(new ContentFileElementDataSource(new ArchiveProjectElementDataSource(zipFile, entry), XmlUtils.getNodeAsKeyValueMap(childNodes.item(i), Document.ELEMENT_NODE)));
                }
            }

            for (Object o : zipFile.getFileHeaders()) {
                FileHeader fileHeader = (FileHeader) o;
                if (fileHeader.getFileName().endsWith(LoggedServiceCallListParser.FILE_EXTENSION)) {
                    res.add(new ArchiveProjectElementDataSource(zipFile, fileHeader));
                }
            }

            return res;
        } catch (ZipException e) {
            throw new ProjectBuilderException("Project Archive File processing exception.", e);
        } catch (TransformerException e) {
            throw new ProjectBuilderException(ERROR_CONTENT_FILE_PARSING, e);
        } catch (IOException e) {
            throw new ProjectBuilderException(ERROR_CONTENT_FILE_PARSING, e);
        } catch (ParserConfigurationException e) {
            throw new ProjectBuilderException(ERROR_CONTENT_FILE_PARSING, e);
        }
    }

    /**
     * Obtains all filenames that belongs to the project
     *
     * @param is input stream of .proj file
     * @throws ProjectBuilderException
     */
    private NodeList getProjectItems(InputStream is) throws ProjectBuilderException {
        try {
            Document doc = XmlUtils.createDoc(is);
            return XmlUtils.evalNodeListXpath("//*[local-name()='None' or local-name()='Content']", doc);
        } catch (IOException e) {
            throw new ProjectBuilderException(e.getMessage(), e);
        } catch (SVCParseException e) {
            throw new ProjectBuilderException(e.getMessage(), e);
        } catch (XPathExpressionException e) {
            throw new ProjectBuilderException(e.getMessage(), e);
        }
    }

    private IService processManifest(IManifest m, Map<String, IProjectElement> entities) throws ProjectBuilderException {
        ManifestProcessor mp = new ManifestProcessor(entities, m);

        IService svc = mp.getRoot();

        ServiceChildElementFilter svcChildFilter = new ServiceChildElementFilter();
        for (IProjectElement el : mp.getChildrenForElement(svc)) {
            el.accept(svcChildFilter);
        }

        //process data models
        for (IDataModel dm : svcChildFilter.getDataModels()) {
            //process data sets
            DataSetFilter dsFilter = new DataSetFilter();
            for (IProjectElement ch : mp.getChildrenForElement(dm)) {
                ch.accept(dsFilter);
                // may be SD of service call activity
                ch.accept(svcChildFilter);
            }
            for (IDataSet ds : dsFilter.getDataSets()) {
                dm.addDataSet(ds);
            }

            svc.addDataModel(dm);
        }

        //process performance models
        for (IPerfModel pm : svcChildFilter.getPerfModels()) {
            svc.addPerfModel(pm);
        }

        //process service description
        for (IServiceDescription sd : svcChildFilter.getSvcDescriptions()) {
            svc.addDescription(sd);
        }

        //process content file
        for (IContentFile cf : svcChildFilter.getSvcContentFiles()) {
            svc.addContentFile(cf);
        }

        for (ILoggedServiceCallList lscl : svcChildFilter.getLoggedServiceCallLists()) {
            svc.addLoggedServiceCallList(lscl);
        }

        return svc;
    }

    /**
     * Creates a Map where the keys are ElementIDS(ServiceID, DataModelId, ...) and values are IProjectElement
     * instances
     *
     * @param dataSources Collection of project's data sources
     */
    private Map<String, IProjectElement> createEntityIdFileNameMap(Collection<IProjectElementDataSource> dataSources, String projectPassword) throws ProjectBuilderException {
        Map<String, IProjectElement> res = new HashMap<String, IProjectElement>();

        for (IProjectElementDataSource ds : dataSources) {
            try {
                AbstractProjectElementParser parser = AbstractProjectElementParser.getParserForDataSource(ds);
                IProjectElement element = parser.create(ds, projectPassword);

                res.put(element.getId(), element);
            } catch (SVCParseException e) {
                throw new ProjectBuilderException(String.format("Error during entry parse phase (%s).", e.getMessage()), e);
            }
        }

        return res;
    }

    //============================== INNER CLASSES ======================================

    private static class ManifestProjectGrabber extends ProjectVisitorAdapter {

        private IProject p = null;
        private Set<IManifest> manifests = new HashSet<IManifest>();

        @Override
        public void visit(IProject p) {
            this.p = p;
        }

        @Override
        public void visit(IManifest m) {
            this.manifests.add(m);
        }

        public Collection<IManifest> getManifests() {
            return this.manifests;
        }

        public IProject getProject() {
            return this.p;
        }
    }

}
