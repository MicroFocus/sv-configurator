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
package com.microfocus.sv.svconfigurator.core.impl.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entry", namespace = ServerInfo.W3_ATOM_NAMESPACE)
public class ServerInfo {
    //============================== STATIC ATTRIBUTES ========================================

    private static final String INFO_TEMPLATE = "SV Server[type = %s; serverVersion = %s; productVersion = %s; edition = %s; id = %s]";
    public static final String W3_ATOM_NAMESPACE = "http://www.w3.org/2005/Atom";

    //============================== INSTANCE ATTRIBUTES ======================================

    private String serverVersion;
    private String productVersion;
    private String serverType;
    private String serverEditionName;
    private String id;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    public String toString() {
        return String.format(INFO_TEMPLATE, this.serverType, this.serverVersion, this.productVersion, serverEditionName, id);
    }


    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    @XmlElement(name = "ServerVersion")
    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    @XmlElement(name = "ProductVersion")
    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    @XmlElement(name = "ServerType")
    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    @XmlElement(name = "ServerEditionName")
    public String getServerEditionName() {
        return serverEditionName;
    }

    public void setServerEditionName(String serverEditionName) {
        this.serverEditionName = serverEditionName;
    }

    @XmlElement(name = "id", namespace = W3_ATOM_NAMESPACE)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //============================== INNER CLASSES ============================================

}
