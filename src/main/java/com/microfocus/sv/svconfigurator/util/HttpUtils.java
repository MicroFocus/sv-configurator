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
package com.microfocus.sv.svconfigurator.util;

import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;

import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.serverclient.IRestClient;
import com.microfocus.sv.svconfigurator.serverclient.IServerManagementEndpointClient;
import com.microfocus.sv.svconfigurator.serverclient.impl.RestClient;
import com.microfocus.sv.svconfigurator.serverclient.impl.ServerManagementEndpointClient;


public class HttpUtils {
    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    public static IServerManagementEndpointClient createServerManagementEndpointClient(URL mgmtUri, boolean trustEveryone, Credentials credentials) throws CommunicatorException {
        HttpClient httpClient = (trustEveryone) ? trustEveryoneSslHttpClient() : new DefaultHttpClient();
        IRestClient restClient = new RestClient(credentials, httpClient);

        return new ServerManagementEndpointClient(mgmtUri, restClient);
    }

    public static HttpClient trustEveryoneSslHttpClient() {
        try {
            SSLSocketFactory socketFactory = new SSLSocketFactory(new TrustStrategy() {

                public boolean isTrusted(final X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }

            }, org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            DefaultHttpClient client = new DefaultHttpClient();
            client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
            return client;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
