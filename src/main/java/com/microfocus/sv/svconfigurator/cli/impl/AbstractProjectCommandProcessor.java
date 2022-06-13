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
package com.microfocus.sv.svconfigurator.cli.impl;

import java.io.File;

import org.apache.commons.cli.CommandLine;

import com.microfocus.sv.svconfigurator.build.IProjectBuilder;
import com.microfocus.sv.svconfigurator.cli.ICLICommandProcessor;
import com.microfocus.sv.svconfigurator.cli.impl.factory.CommandLineOptions;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.impl.exception.ProjectBuilderException;

public abstract class AbstractProjectCommandProcessor implements ICLICommandProcessor {

    private final IProjectBuilder projectBuilder;

    public AbstractProjectCommandProcessor(IProjectBuilder projectBuilder) {
        this.projectBuilder = projectBuilder;
    }

    protected IProject getProject(CommandLine line) throws ProjectBuilderException {
        String projectPath = getProjectPath(line);
        if (projectPath == null) {
            return null;
        }
        return this.getProject(projectPath, getProjectPassword(line));
    }
        
    protected String getProjectPassword(CommandLine line) {
        String projectPassword = null;
        if (line.hasOption(CommandLineOptions.PROPERTY_PROJ_PASSWORD)) {
            projectPassword = line.getOptionValue(CommandLineOptions.PROPERTY_PROJ_PASSWORD);
        }
        return projectPassword;
    }
    
    protected String getProjectPath(CommandLine line) {
        String projectPath = null;
        if (line.hasOption(CommandLineOptions.PROP_PROJ)) {
            projectPath = line.getOptionValue(CommandLineOptions.PROP_PROJ);
        } else if (line.getArgs().length == 1) {
            projectPath = line.getArgs()[0];
        }
        return projectPath;
    }

    protected IProject getProject(String filePath, String password) throws ProjectBuilderException {
        if (filePath == null) {
            return null;
        } else {
            return this.projectBuilder.buildProject(new File(filePath), password);
        }
    }
}
