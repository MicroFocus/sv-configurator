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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.util.StringUtils;

public class ListProjectProcessor implements IListProjectProcessor {
    
    private static final Logger LOG = LoggerFactory.getLogger(ListProjectProcessor.class);

    @Override
    public void process(ListProjectProcessorInput input) {
        IProject proj = input.getProject();
        
        LOG.info("project: "+ proj.getName());
        LOG.info("services: ");
        
        Collection<IService> svcs = proj.getServices();
        List<List<String>> tableData = new ArrayList<List<String>>(svcs.size());
        
        for (IService svc : svcs) {
            tableData.add(Arrays.asList(svc.getName(), svc.getId()));
        }
        
        LOG.info(StringUtils.createTable(tableData));
    }

}
