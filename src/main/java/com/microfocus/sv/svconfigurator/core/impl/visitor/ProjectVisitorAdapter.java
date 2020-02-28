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

public abstract class ProjectVisitorAdapter implements IProjectElementVisitor {

    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    public void visit(IProject s) {
        //nothing
    }

    @Override
    public void visit(IService s) {
        //nothing
    }

    @Override
    public void visit(IDataModel dm) {
        //nothing
    }

    @Override
    public void visit(IPerfModel pm) {
        //nothing
    }

    @Override
    public void visit(IServiceDescription sd) {
        //nothing
    }

    @Override
    public void visit(IContentFile cf) {
        //nothing
    }

    @Override
    public void visit(IDataSet ds) {
        //nothing
    }

    @Override
    public void visit(IManifest m) {
        //nothing
    }

    @Override
    public void visit(ITopology t) {
        //nothing
    }

    @Override
    public void visit(ILoggedServiceCallList loggedServiceCallList) {
        //nothing
    }
    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
