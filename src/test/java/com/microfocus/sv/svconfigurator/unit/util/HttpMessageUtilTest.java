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
package com.microfocus.sv.svconfigurator.unit.util;

import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.util.HttpMessageUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpMessageUtilTest {


    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Test
    public void testAccept() throws Exception {
        HttpGet get = new HttpGet();
        HttpMessageUtil.accept(get, ContentType.APPLICATION_XML);

        assertEquals("application/xml", get.getLastHeader("Accept").getValue());
    }

    @Test
    public void testContentType() throws Exception {
        HttpGet get = new HttpGet();
        HttpMessageUtil.contentType(get, ContentType.APPLICATION_XML);

        assertEquals("application/xml", get.getLastHeader("Content-Type").getValue());
    }

    @Test
    public void testBasicAuthentication() throws Exception {
        HttpGet get = new HttpGet();
        HttpMessageUtil.basicAuthentication(get, new Credentials("user", "pass"));

        String base64 = new String(Base64.encodeBase64("user:pass".getBytes()));
        assertEquals("Basic "+ base64, get.getLastHeader("Authorization").getValue());
    }


    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
