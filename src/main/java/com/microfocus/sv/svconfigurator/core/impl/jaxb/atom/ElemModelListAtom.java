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
package com.microfocus.sv.svconfigurator.core.impl.jaxb.atom;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Feed for dataModel / perfModel lists
 */
@XmlRootElement(name = AbstractFeed.EL_NAME, namespace = AbstractFeed.NAMESPACE)
public class ElemModelListAtom extends AbstractFeed<ElemModelListAtom.ElemModelEntry> {
    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private List<ElemModelEntry> entries = new ArrayList<ElemModelEntry>();

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    @Override
    @XmlElement(name = AbstractEntry.EL_NAME, namespace = NAMESPACE)
    public List<ElemModelEntry> getEntries() {
        return this.entries;
    }

    @Override
    public void setEntries(List<ElemModelEntry> entry) {
        this.entries = entry;
    }

    //============================== INNER CLASSES ============================================

    public static class ElemModelEntry extends AbstractEntry {

        private Boolean isOffline;

        @XmlElement(name="IsOfflinePerformanceModel", namespace="")
        public Boolean getIsOffline() {
            return isOffline;
        }

        public void setIsOffline(Boolean isOffline) {
            this.isOffline = isOffline;
        }
        
        
        
    }

}
