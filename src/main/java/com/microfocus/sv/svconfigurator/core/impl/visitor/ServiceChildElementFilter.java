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
package com.microfocus.sv.svconfigurator.core.impl.visitor;

import java.util.HashSet;
import java.util.Set;

import com.microfocus.sv.svconfigurator.core.IContentFile;
import com.microfocus.sv.svconfigurator.core.IDataModel;
import com.microfocus.sv.svconfigurator.core.IPerfModel;
import com.microfocus.sv.svconfigurator.core.IServiceDescription;

public class ServiceChildElementFilter extends ProjectVisitorAdapter {

    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private Set<IDataModel> dataModels = new HashSet<IDataModel>();
    private Set<IPerfModel> perfModels = new HashSet<IPerfModel>();
    private Set<IServiceDescription> svcDescriptions = new HashSet<IServiceDescription>();
    private Set<IContentFile> svcContentFiles = new HashSet<IContentFile>();

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    public void visit(IDataModel dm) {
        this.dataModels.add(dm);
    }

    @Override
    public void visit(IPerfModel pm) {
        this.perfModels.add(pm);
    }

    @Override
    public void visit(IServiceDescription sd) {
        this.svcDescriptions.add(sd);
    }

    @Override
    public void visit(IContentFile cf) {
        this.svcContentFiles.add(cf);
    }

    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    public Set<IDataModel> getDataModels() {
        return dataModels;
    }

    public Set<IPerfModel> getPerfModels() {
        return perfModels;
    }

    public Set<IServiceDescription> getSvcDescriptions() {
        return svcDescriptions;
    }

    public Set<IContentFile> getSvcContentFiles() {
        return svcContentFiles;
    }

    //============================== INNER CLASSES ============================================

}
