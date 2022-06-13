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
package com.microfocus.sv.svconfigurator.unit.processor.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.microfocus.sv.svconfigurator.LogConf;
import com.microfocus.sv.svconfigurator.processor.utils.DataModelScaExtractor;
import com.microfocus.sv.svconfigurator.resources.Resources;

public class DataModelScaExtractorTest {

    public static final String EASY_FILE = "test/dmSca/Echo Data Model.vsmodel";
    public static final String NONE_FILE = "test/dmSca/Echo Data Model0.vsmodel";
    public static final String ADVANCED_FILE = "test/dmSca/Echo Data Model2.vsmodel";
    
    @BeforeClass
    public static void init() {
        LogConf.configure();
    }
    
    @Test
    public void testEasyExtraction() throws Exception {
        File f = Resources.getResource(EASY_FILE);
        FileInputStream fis = new FileInputStream(f);
        byte[] data = IOUtils.toByteArray(fis);
        IOUtils.closeQuietly(fis);
        
        assertNotNull(data);
        assertTrue(data.length > 10);
        
        Set<String> sdIds = DataModelScaExtractor.extractScaServiceDescriptionIds(data);
        assertNotNull(sdIds);
        assertEquals(1, sdIds.size());
        assertTrue(sdIds.contains("d7101774-7ef0-425e-a67e-ede7181e5438"));
    }
    
    @Test
    public void testNoneExtraction() throws Exception {
        File f = Resources.getResource(NONE_FILE);
        FileInputStream fis = new FileInputStream(f);
        byte[] data = IOUtils.toByteArray(fis);
        IOUtils.closeQuietly(fis);
        
        assertNotNull(data);
        assertTrue(data.length > 10);
        
        Set<String> sdIds = DataModelScaExtractor.extractScaServiceDescriptionIds(data);
        assertNotNull(sdIds);
        assertEquals(0, sdIds.size());
    }
        
    @Test
    public void testAdvancedExtraction() throws Exception {
        File f = Resources.getResource(ADVANCED_FILE);
        FileInputStream fis = new FileInputStream(f);
        byte[] data = IOUtils.toByteArray(fis);
        IOUtils.closeQuietly(fis);
        
        assertNotNull(data);
        assertTrue(data.length > 10);
        
        Set<String> sdIds = DataModelScaExtractor.extractScaServiceDescriptionIds(data);
        assertNotNull(sdIds);
        assertEquals(6, sdIds.size());
        assertTrue(sdIds.contains("d7101774-7ef0-425e-a67e-ede7181e5438"));
        assertTrue(sdIds.contains("d8101774-7ef0-425e-a67e-ede7181e5438"));
        assertTrue(sdIds.contains("da101774-7ef0-425e-a67e-ede7181e5438"));
        assertTrue(sdIds.contains("dc101774-7ef0-425e-a67e-ede7181e5438"));
        assertTrue(sdIds.contains("dd101774-7ef0-425e-a67e-ede7181e5438"));
        assertTrue(sdIds.contains("df101774-7ef0-425e-a67e-ede7181e5438"));
    }
    
}
