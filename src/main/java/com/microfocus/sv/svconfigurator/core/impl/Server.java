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
package com.microfocus.sv.svconfigurator.core.impl;

import java.net.URL;

import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;

public class Server {

    private final String id;
    private final URL url;
    private final Credentials credentials;
    
    public Server(String id, URL url, Credentials credentials) {
        this.id = id;
        this.url = url;
        this.credentials = credentials;
    }
    
    public String getId() {
        return id;
    }
    
    public URL getURL() {
        return url;
    }
    
    public Credentials getCredentials() {
        return credentials;
    }
    
    @Override
    public String toString() {
        return id;
    }
    
}
