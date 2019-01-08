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
package com.microfocus.sv.svconfigurator.util;

import java.util.Collection;

import com.microfocus.sv.svconfigurator.core.IProjectElement;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;

public class ProjectUtils {
    public static final String ENTITY_VIRTUAL_SERVICE = "Virtual service";
    public static final String ENTITY_DATA_MODEL = "Data model";
    public static final String ENTITY_PERFORMANCE_MODEL = "Performance model";


    // ============================== STATIC ATTRIBUTES
    // ========================================

    // ============================== INSTANCE ATTRIBUTES
    // ======================================

    // ============================== STATIC METHODS
    // ===========================================

    public static <E extends IProjectElement> E findProjElem(Collection<E> elems, String ident) {
        if (ident == null) {
            return null;
        }

        for (E elem : elems) {
            if (elem.getId().equals(ident) || elem.getName().equals(ident)) {
                return elem;
            }
        }
        throw new IllegalArgumentException("Element [" + ident + "] was not found.");
    }

    public static <E extends IProjectElement> E findProjElem(Collection<E> elems, String ident, String entityType)
            throws CommandExecutorException {
        try {
            return findProjElem(elems, ident);
        } catch (IllegalArgumentException e) {
            throw new CommandExecutorException(entityType + " '" + ident + "' not found in the project");
        }
    }

    public static Credentials createCredentials(String username, String password) {
        return (username != null ? new Credentials(username, password) : null);
    }

    public static String decodeInclude(String file) {
        // https://msdn.microsoft.com/en-us/library/bb383819%28v=vs.90%29.aspx
        if (file == null) {
            return null;
        }
        return file.replace("%24", "$").replace("%40", "@").replace("%27", "'")
                .replace("%3B", ";").replace("%3F", "?").replace("%2A", "*")
                .replace("%25", "%");
    }

    public static String encodeInclude(String file) {
        // https://msdn.microsoft.com/en-us/library/bb383819%28v=vs.90%29.aspx
        return file.replace("%", "%25").replace("$", "%24").replace("@", "%40")
                .replace("'", "%27").replace(";", "%3B").replace("?", "%3F")
                .replace("*", "%2A");
    }

    // ============================== CONSTRUCTORS
    // =============================================

    // ============================== ABSTRACT METHODS
    // =========================================

    // ============================== OVERRIDEN METHODS
    // ========================================

    // ============================== INSTANCE METHODS
    // =========================================

    // ============================== PRIVATE METHODS
    // ==========================================

    // ============================== GETTERS / SETTERS
    // ========================================

    // ============================== INNER CLASSES
    // ============================================

}
