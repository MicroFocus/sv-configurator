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
package com.microfocus.sv.svconfigurator;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.Normalizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;

public class Global {
    //============================== STATIC ATTRIBUTES ========================================

    private static final Logger LOG = LoggerFactory.getLogger(Global.class);

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    public synchronized static String getClientId(ICommandExecutor executor) {
        String username;
        String creds = executor.getClient().getClient().getUsername();
        String clientHost = getClientHost();
        if (creds != null && "https".equals(executor.getClient().getMgmtUri().getProtocol())) {
            String serverHost = getServerHost(executor);
            username = creds + "/" + serverHost;
        } else {
            username = System.getProperty("user.name");
        }
        String res = username.toLowerCase() + "@" + clientHost.toLowerCase() +"/SVConfigurator";
        return Normalizer.normalize(res, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    private static String getClientHost() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOG.debug(e.toString(), e);
            return "unknown";
        }
    }

    private static String getServerHost(ICommandExecutor executor) {
        String host;
        try {
            String id = executor.getClient().getServerInfo().getId();
            if (id.contains(":")) {
                // id of older SV versions was URL
                host = new URL(id).getHost();
            } else {
                host = id;
            }
            return host.split("\\.", 2)[0];
        } catch (Exception e) {
            return executor.getClient().getMgmtUri().getHost();
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
