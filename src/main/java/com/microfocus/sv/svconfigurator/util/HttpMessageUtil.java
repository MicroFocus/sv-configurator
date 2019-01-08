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

import org.apache.commons.codec.binary.Base64;
import org.apache.http.entity.ContentType;
import org.apache.http.message.AbstractHttpMessage;

import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;

public class HttpMessageUtil {
    //============================== STATIC ATTRIBUTES ========================================

    private static final String KEY_ACCEPT = "Accept";
    private static final String KEY_CONTENT_TYPE = "Content-Type";
    private static final String KEY_AUTHORIZATION = "Authorization";

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    public static void accept(AbstractHttpMessage m, ContentType ct) {
        if (ct != null) {
            m.setHeader(KEY_ACCEPT, ct.getMimeType());
        }
    }

    public static void contentType(AbstractHttpMessage m, ContentType ct) {
        m.setHeader(KEY_CONTENT_TYPE, ct.getMimeType());
    }

    public static void basicAuthentication(AbstractHttpMessage m, String username, String password) {
        String auth = "Basic " + new String(Base64.encodeBase64((username + ":" + password).getBytes()));
        m.setHeader(KEY_AUTHORIZATION, auth);
    }

    public static void basicAuthentication(AbstractHttpMessage m, Credentials cred) {
        if (cred.getUsername() == null) {
            return;
        }

        basicAuthentication(m, cred.getUsername(), cred.getPassword());
    }

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
