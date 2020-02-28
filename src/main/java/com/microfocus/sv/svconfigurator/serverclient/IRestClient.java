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
package com.microfocus.sv.svconfigurator.serverclient;

import java.io.InputStream;
import java.net.URI;

import org.apache.http.entity.ContentType;

import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.processor.ElementStatus;

public interface IRestClient {

    void post(URI uri, Object entity) throws CommunicatorException;

    void post(URI uri, InputStream data, long dataLength, ContentType ct) throws CommunicatorException;

    void put(URI uri, Object entity) throws CommunicatorException;

    void put(URI uri, Object entity, ContentType accept) throws CommunicatorException;

    void put(URI uri, InputStream data, long dataLength, ContentType ct) throws CommunicatorException;

    <E> E get(URI uri, Class<E> resType) throws CommunicatorException;

    <E> E get(URI uri, ContentType accept, Class<E> resType) throws CommunicatorException;

    void delete(URI uri) throws CommunicatorException;

    ElementStatus getStatus(URI uri) throws CommunicatorException;

    void pingServer(URI pingUrl) throws CommunicatorException;

    byte[] getPayload(URI uri, ContentType accept) throws CommunicatorException;

    FileInfo getFileInfo(URI uri, ContentType accept) throws CommunicatorException;

    String getUsername();
}

