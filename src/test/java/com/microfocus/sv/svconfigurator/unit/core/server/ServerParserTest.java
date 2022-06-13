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
package com.microfocus.sv.svconfigurator.unit.core.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.microfocus.sv.svconfigurator.core.impl.Server;
import com.microfocus.sv.svconfigurator.core.server.ServerParser;
import com.microfocus.sv.svconfigurator.integration.TestConst;

public class ServerParserTest {

    @Test
    public void testParseServers() throws Exception {
        List<Server> servers = ServerParser.parseServers(TestConst.getServersProperties(), null);
        assertEquals(2, servers.size());
        
        boolean matchedCred = false;
        boolean matchedNoCred = false;
        for (int i = 0; i < servers.size(); i++) {
            Server s = servers.get(i);
            if (s.getCredentials() == null) {
                assertEquals("http://[::1]:6080/management", s.getURL().toString());
                assertEquals("server2", s.getId());
                matchedNoCred = true;
            } else {
                assertEquals("https://mpavm0252.hpswlabs.adapps.hp.com:6085/management", s.getURL().toString());
                assertEquals("test001", s.getCredentials().getUsername());
                assertEquals("$$$aaa111", s.getCredentials().getPassword());
                assertEquals("srv1", s.getId());
                matchedCred = true;
            }            
        }
        assertTrue(matchedCred);
        assertTrue(matchedNoCred);
    }
    
    @Test
    public void testParseSingleServers() throws Exception {
        List<Server> servers = ServerParser.parseServers(TestConst.getServersProperties(), "server2");
        assertEquals(1, servers.size());
        
        boolean matchedCred = false;
        boolean matchedNoCred = false;
        for (int i = 0; i < servers.size(); i++) {
            Server s = servers.get(i);
            if (s.getCredentials() == null) {
                assertEquals("http://[::1]:6080/management", s.getURL().toString());
                assertEquals("server2", s.getId());
                matchedNoCred = true;
            } else {
                assertEquals("https://mpavm0252.hpswlabs.adapps.hp.com:6085/management", s.getURL().toString());
                assertEquals("test001", s.getCredentials().getUsername());
                assertEquals("$$$aaa111", s.getCredentials().getPassword());
                assertEquals("srv1", s.getId());
                matchedCred = true;
            }            
        }
        Assert.assertFalse(matchedCred);
        Assert.assertTrue(matchedNoCred);
    }

    @Test
    public void testGetServerId() throws Exception {
        assertEquals("testId", ServerParser.getServerId("testId.url"));
    }

}
