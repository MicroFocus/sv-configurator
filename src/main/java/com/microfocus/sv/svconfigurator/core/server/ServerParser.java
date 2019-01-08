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
package com.microfocus.sv.svconfigurator.core.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.core.impl.Server;
import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;

public class ServerParser {

    private static Logger LOG = LoggerFactory.getLogger(ServerParser.class);

    public static final Charset FILE_ENCODING = Charset.forName("UTF-8");

    public static final String URL_SUFFIX = ".url";
    public static final String USERNAME_SUFFIX = ".username";
    public static final String PASSWORD_SUFFIX = ".password";

    public static List<Server> parseServers(File inputFile, String selectedServerId)
            throws AbstractSVCException {
        if (inputFile == null) {
            throw new SVCParseException("Input file cannot be empty");
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(inputFile);
            return parseServers(fis, selectedServerId);
        } catch (IOException e) {
            throw new SVCParseException("Failed to parse input file", e);
        } finally {
            IOUtils.closeQuietly(fis);
        }

    }

    public static List<Server> parseServers(InputStream input, String selectedServerId)
            throws AbstractSVCException {
        if (input == null) {
            throw new SVCParseException("Input cannot be empty");
        }

        Properties props = new Properties();
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(input, FILE_ENCODING);
            props.load(isr);
        } catch (IOException e) {
            throw new SVCParseException("Failed to parse input file", e);
        } finally {
            IOUtils.closeQuietly(isr);
        }

        List<Server> servers = new ArrayList<Server>();
        Enumeration<Object> keys = props.keys();
        Set<String> serverIds = new HashSet<String>();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            serverIds.add(getServerId(key));
        }

        for (String serverId : serverIds) {
            if (selectedServerId != null && !selectedServerId.equals(serverId)) {
                LOG.debug("Skipping unselected server ID '" + serverId + "'...");
                continue;
            }
            
            LOG.debug("Loading server ID '" + serverId + "'...");
            String urlValue = props.getProperty(serverId + URL_SUFFIX);
            if (urlValue == null) {
                throw new SVCParseException("No URL key '" + serverId + URL_SUFFIX
                        + "' found for server ID '" + serverId + "'.");
            }
            
            String usernameValue = props
                    .getProperty(serverId + USERNAME_SUFFIX);
            String passwordValue = props
                    .getProperty(serverId + PASSWORD_SUFFIX);

            try {
                Server srv;
                if (usernameValue == null && passwordValue == null) {
                    srv = new Server(serverId, new URL(urlValue), null);
                } else if (usernameValue != null && passwordValue != null) {
                    srv = new Server(serverId, new URL(urlValue),
                            new Credentials(usernameValue, passwordValue));
                } else {
                    throw new SVCParseException("Server ID '" + serverId
                            + "' is missing username or password.");
                }
                servers.add(srv);
            } catch (MalformedURLException e) {
                throw new SVCParseException("Failed to parse URL '" + urlValue
                        + "' for server ID '" + serverId + "'.");
            }
        }

        return servers;
    }

    public static String getServerId(String key) throws SVCParseException {
        int idx = key.indexOf('.');
        if (idx >= 0) {
            return key.substring(0, idx);
        }
        throw new SVCParseException("Failed to get server ID for the key '"
                + key + "'");
    }

}
