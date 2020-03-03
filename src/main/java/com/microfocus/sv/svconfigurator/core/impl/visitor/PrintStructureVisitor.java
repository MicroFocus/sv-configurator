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

import com.microfocus.sv.svconfigurator.core.*;

public class PrintStructureVisitor implements IProjectElementVisitor {

    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private StringBuilder sb = new StringBuilder();

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================
    @Override
    public void visit(IProject p) {
        this.sb.append("project: " + p.getId());

        for (IService svc : p.getServices()) {
            svc.accept(this);
        }
    }

    @Override
    public void visit(IService s) {
        this.indent(1);
        this.sb.append("service: " + s.getId());

        for (IDataModel dm : s.getDataModels()) {
            dm.accept(this);
        }

        for (IPerfModel pm : s.getPerfModels()) {
            pm.accept(this);
        }

        for (IServiceDescription sd : s.getDescriptions()) {
            sd.accept(this);
        }

        for (IContentFile cf : s.getContentFiles()) {
            cf.accept(this);
        }
    }

    @Override
    public void visit(IDataModel dm) {
        this.indent(2);
        this.sb.append("data model: " + dm.getId());

        for (IDataSet ds : dm.getDataSets()) {
            ds.accept(this);
        }
    }

    @Override
    public void visit(IPerfModel pm) {
        this.indent(2);
        this.sb.append("performance model: " + pm.getId());
    }

    @Override
    public void visit(IServiceDescription sd) {
        this.indent(2);
        this.sb.append("service description: " + sd.getId());
    }

    @Override
    public void visit(IContentFile cf) {
        this.indent(2);
        this.sb.append("content file: " + cf.getId());
    }

    @Override
    public void visit(IDataSet ds) {
        this.indent(3);
        this.sb.append("data set: " + ds.getId());
    }

    @Override
    public void visit(IManifest m) {
        //do nothing
    }

    @Override
    public void visit(ITopology t) {
        //do nothing
    }

    @Override
    public void visit(ILoggedServiceCallList loggedServiceCallList) {
        //do nothing
    }
    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    private void indent(int level) {
        this.sb.append("\n");
        for (int i = 0; i < level; i++) {
            this.sb.append("\t");
        }
    }

    //============================== GETTERS / SETTERS ========================================

    public String getStructure() {
        return this.sb.toString();
    }

    //============================== INNER CLASSES ============================================

}
