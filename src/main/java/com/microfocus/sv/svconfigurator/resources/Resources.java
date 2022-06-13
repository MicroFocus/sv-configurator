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
package com.microfocus.sv.svconfigurator.resources;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class Resources {
    //============================== STATIC ATTRIBUTES ========================================

    public static final String SYSTEM_PROPERTY_EXTERNAL_RES = "SVConfigurator.externalResources";

    private static File externRes = null;

    static {
        String extern = System.getProperty(SYSTEM_PROPERTY_EXTERNAL_RES);
        if (extern != null) {
            String rel = Resources.class.getPackage().getName().replaceAll("\\.", "/");
            externRes = new File(new File(extern), rel);
        }
    }

    public static File getResource(String name) {
//        System.out.println(Resources.class.getPackage().getName());
        if (externRes != null) {
            return new File(externRes, name);
        } else {
            URL url = Resources.class.getResource(name);
            return new File(url.getPath());
        }
    }

    public static InputStream getResourceStream(String name) {
        return Resources.class.getResourceAsStream(name);
    }

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
