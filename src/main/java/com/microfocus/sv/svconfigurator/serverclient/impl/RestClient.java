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
package com.microfocus.sv.svconfigurator.serverclient.impl;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ExceptionTransferHolder;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.core.impl.processor.ElementStatus;
import com.microfocus.sv.svconfigurator.serverclient.IJaxbProcessor;
import com.microfocus.sv.svconfigurator.serverclient.IRestClient;
import com.microfocus.sv.svconfigurator.util.HttpMessageUtil;

public class RestClient implements IRestClient {
    // ============================== STATIC ATTRIBUTES ========================================

    private static Logger LOG = LoggerFactory.getLogger(RestClient.class);
    // ============================== INSTANCE ATTRIBUTES ======================================
    private Credentials cred;
    private HttpClient client;
    private IJaxbProcessor jaxbProc;

    // ============================== STATIC METHODS ===========================================

    // ============================== CONSTRUCTORS =============================================

    public RestClient(Credentials cred, HttpClient httpClient) {
        this.cred = cred;
        this.client = httpClient;

        this.jaxbProc = new JaxbProcessor();
    }
    
    public String getUsername() {
        if (cred != null) {
            return cred.getUsername();
        }
        return null;
    }

    // ============================== ABSTRACT METHODS =========================================

    // ============================== OVERRIDEN METHODS ========================================

    // ============================== INSTANCE METHODS =========================================

    @Override
    public void post(URI uri, Object entity) throws CommunicatorException {
        LOG.debug("POST: " + uri);
        HttpPost post = new HttpPost(uri);
        this.executeJaxbSend(post, entity);
    }

    @Override
    public void post(URI uri, InputStream data, long dataLength, ContentType ct) throws CommunicatorException {
        LOG.debug("POST: " + uri);
        HttpPost post = new HttpPost(uri);
        this.executeStreamSend(post, data, dataLength, ct);
    }

    @Override
    public void put(URI uri, Object entity) throws CommunicatorException {
        LOG.debug("PUT: " + uri);
        HttpPut put = new HttpPut(uri);
        this.executeJaxbSend(put, entity);
    }

    @Override
    public void put(URI uri, Object entity, ContentType accept) throws CommunicatorException {
        LOG.debug("PUT: " + uri);
        HttpPut put = new HttpPut(uri);

        if (accept != null) {
            HttpMessageUtil.accept(put, ContentType.APPLICATION_XML);
        }

        this.executeJaxbSend(put, entity);
    }

    @Override
    public void put(URI uri, InputStream data, long dataLength, ContentType ct) throws CommunicatorException {
        LOG.debug("PUT: " + uri);
        HttpPut put = new HttpPut(uri);
        this.executeStreamSend(put, data, dataLength, ct);
    }

    @Override
    public <E> E get(URI uri, Class<E> resType) throws CommunicatorException {
        return this.get(uri, ContentType.APPLICATION_XML, resType);
    }

    @Override
    public <E> E get(URI uri, ContentType accept, Class<E> resType) throws CommunicatorException {
        LOG.debug("GET: " + uri);
        HttpGet get = new HttpGet(uri);

        HttpMessageUtil.accept(get, accept);

        try {
            HttpResponse resp = this.exec(get);

            this.validateResponse(resp);

            return this.jaxbProc.unmasrhall(resp.getEntity().getContent(), resType);
        } catch (IOException e) {
            throw new CommunicatorException("Network error: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void delete(URI uri) throws CommunicatorException {
        LOG.debug("DELETE " + uri);
        HttpDelete del = new HttpDelete(uri);
        HttpResponse response;
        try {
            response = this.exec(del);
            this.validateResponse(response);
            EntityUtils.consumeQuietly(response.getEntity());
        } catch (IOException e) {
            throw new CommunicatorException("Network error: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * executes an GET but treats only http status (if a resource is present or not)
     *
     * @throws CommunicatorException
     */
    @Override
    public ElementStatus getStatus(URI uri) throws CommunicatorException {
        HttpResponse resp = this.get(uri, ContentType.APPLICATION_XML);
        int status = resp.getStatusLine().getStatusCode();
        EntityUtils.consumeQuietly(resp.getEntity());
        if (HttpURLConnection.HTTP_OK == status) {
            return ElementStatus.PRESENT;
        } else if (HttpURLConnection.HTTP_BAD_REQUEST == status || HttpURLConnection.HTTP_NOT_FOUND == status) {
            return ElementStatus.NOT_PRESENT;
        } else {
            try {
                this.validateResponse(resp);
            } catch (CommunicatorException e){
                if (e.getStatusCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                    return ElementStatus.NOT_PRESENT;
                }
                throw e;
            }
            return ElementStatus.NOT_PRESENT;
        }
    }

    /**
     * executes an GET but treats only http status (if a resource is present or not)
     *
     * @throws CommunicatorException
     */
    @Override
    public void pingServer(URI pingUrl) throws CommunicatorException {
        HttpResponse resp = this.get(pingUrl, ContentType.APPLICATION_XML);
        int status = resp.getStatusLine().getStatusCode();
        EntityUtils.consumeQuietly(resp.getEntity());
        if (HttpURLConnection.HTTP_FORBIDDEN == status) {
            // SV server returns 403 instead of 401 in case of bad password. Ping endpoint should be without
            // authorization, so 403 always means bad password. (AFAIK :-)
            throw new CommunicatorException("Bad username or password.", status);
        } else {
            this.validateResponse(resp);
        }
    }

    // ============================== PRIVATE METHODS ==========================================

    private void executeStreamSend(HttpEntityEnclosingRequestBase req, InputStream data, long dataLength, ContentType ct) throws CommunicatorException {
        HttpMessageUtil.contentType(req, ct);

        req.setEntity(new InputStreamEntity(data, dataLength, ct));
        this.executeSend(req);
    }

    private void executeJaxbSend(HttpEntityEnclosingRequestBase req, Object entity) throws CommunicatorException {
        HttpMessageUtil.contentType(req, ContentType.APPLICATION_XML);

        try {
            String entStr = this.jaxbProc.marshall(entity);
            req.setEntity(new StringEntity(entStr));
        } catch (IOException e) {
            throw new CommunicatorException("Error during entity serialization: " + e.getLocalizedMessage(), e);
        }

        this.executeSend(req);
    }

    private void executeSend(HttpRequestBase req) throws CommunicatorException {
        try {
            HttpResponse resp = this.exec(req);
            this.validateResponse(resp);
            EntityUtils.consumeQuietly(resp.getEntity());
        } catch (IOException e) {
            throw new CommunicatorException("Network error: " + e.getLocalizedMessage(), e);
        }
    }

    private HttpResponse get(URI uri, ContentType accept) throws CommunicatorException {
        LOG.debug("GET " + uri);
        HttpGet httpget = new HttpGet(uri);
        if (accept != null) {
            HttpMessageUtil.accept(httpget, accept);
        }

        HttpResponse response;
        try {
            response = this.exec(httpget);
            return response;
        } catch (SSLPeerUnverifiedException e) {
            throw new CommunicatorException("Unable to create SSL connection to SV server. " +
                    "This may be caused by SV server running on newer Windows and SVConfigurator with old Java which may not support newer cipher suites required by the server. Detail: " + e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new CommunicatorException("Network error: " + e.getLocalizedMessage(), e);
        }
    }

    private void validateResponse(HttpResponse resp) throws CommunicatorException {
        int status = resp.getStatusLine().getStatusCode();
        if (HttpURLConnection.HTTP_OK != status) {
            throw this.createException(resp);
        }
    }

    private CommunicatorException createException(HttpResponse resp) {
        int status = resp.getStatusLine().getStatusCode();
        if (status == 401) {
            return new CommunicatorException("Bad username or password.", status);
        } else if (status == 403) {
            return new CommunicatorException("You are unauthorized to perform the current operation. Please, check your credentials and assigned permissions.", status);
        } else if (status == 405) {
            return new CommunicatorException("Server returned Method not found (HTTP 405) result. Please, check your server management URL.", status);
        } else if (status == 400) {
            HttpEntity entity = resp.getEntity();
            ExceptionTransferHolder eth;
            try {
                eth = this.jaxbProc.unmasrhall(entity.getContent(), ExceptionTransferHolder.class);
            } catch (IOException ex) {
                return new CommunicatorException("Network error: " + ex.getLocalizedMessage(), status, ex);
            } catch (CommunicatorException ex) {
                return new CommunicatorException("Error during server error response read (status = " + status + "): " + ex.getLocalizedMessage(), status, ex);
            }
            return new CommunicatorException("Server error response: " + eth.getMessage(), status);
        }
        return new CommunicatorException("Response with error code " + status + " received", status);
    }

    private HttpResponse exec(HttpRequestBase req) throws IOException {
        if (this.cred != null) {
            HttpMessageUtil.basicAuthentication(req, this.cred);
        }
        return this.client.execute(req);
    }

    @Override
    public byte[] getPayload(URI uri, ContentType accept) throws CommunicatorException {
        LOG.debug("GET " + uri);
        HttpGet httpget = new HttpGet(uri);
        if (accept != null) {
            HttpMessageUtil.accept(httpget, ContentType.APPLICATION_XML);
        }
        HttpResponse response;
        try {
            response = this.exec(httpget);
            if (response.getStatusLine().getStatusCode() != 200) {
                // close response properly
                EntityUtils.consumeQuietly(response.getEntity());
                throw new CommunicatorException(String.format("GET of %s failed: %s", uri, response.getStatusLine()));
            }
            return EntityUtils.toByteArray(response.getEntity());
        } catch (IOException e) {
            throw new CommunicatorException("Network error: " + e.getLocalizedMessage(), e);
        }
    }

    // ============================== GETTERS / SETTERS ========================================

    // ============================== INNER CLASSES ============================================

}
