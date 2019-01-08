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
package com.microfocus.sv.svconfigurator.unit.core.impl.jaxb;

import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeReport;
import com.microfocus.sv.svconfigurator.serverclient.IJaxbProcessor;
import com.microfocus.sv.svconfigurator.serverclient.impl.JaxbProcessor;
import com.microfocus.sv.svconfigurator.resources.Resources;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceRuntimeReportTest {

    public static final String XML_FILE = "test/jaxb/serviceReport.xml";

    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Test
    public void testJaxbParsing() throws Exception {
        IJaxbProcessor proc = new JaxbProcessor();
        ServiceRuntimeReport rep = proc.unmasrhall(Resources.getResourceStream(XML_FILE), ServiceRuntimeReport.class);

        assertEquals("message count error", 60, rep.getMessageCount());
        assertEquals("message size error", 3, rep.getMessageSize());
        assertEquals("errorNumber error", 1, rep.getErrorCount());
        assertEquals("warningNumber error", 2, rep.getWarningCount());
        assertEquals("perfAccuracy error", 76, rep.getPerfModelAccuracy());
        assertEquals("serviceId error", "18d93068-cdcb-424f-9eb0-62d359a22a69", rep.getServiceId());
        assertEquals("uniqueMessageCount error", 9, rep.getUniqueMsgCount());

        ServiceRuntimeReport.SimulationStatistics stats = rep.getSimulationStats();
        assertEquals("defaultRuleUsedCount error", 0, stats.getDefaultRuleUsedCount());
        assertEquals("defaultRuleUsedCount error", 30, stats.getRequestsCount());
        assertEquals("defaultRuleUsedCount error", 33, stats.getRequestsNotIncreasingCostCount());
        assertEquals("defaultRuleUsedCount error", 99, stats.getSimulationQualityPercentage());
        assertEquals("defaultRuleUsedCount error", 16, stats.getStatefulResponsesReturnedCount());
        assertEquals("defaultRuleUsedCount error", 37.6666667, stats.getTotalStatelessSimulationAccuracy(), Double.MIN_NORMAL);

        List<String> cls = rep.getClientIds();
        assertEquals("Clients size wrong", 2, cls.size());
        assertTrue("Clients error", cls.contains("16.55.172.69"));
        assertTrue("Clients error", cls.contains("16.55.172.70"));

        List<String> hs = rep.getHostnames();
        assertEquals("Hostnames size wrong", 2, hs.size());
        assertTrue("Hostnames error", hs.contains("Luke Skywalker"));
        assertTrue("Hostnames error", hs.contains("Darth Vader"));
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
