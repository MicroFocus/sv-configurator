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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.impl.Server;
import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.core.server.ServerParser;
import com.microfocus.sv.svconfigurator.processor.printer.PrinterFactory;

public class CliUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CliUtils.class);

    private static final String PARAM_URL = "url";
    private static final String LONG_PARAM_URL = "mgmt-url";
    private static final String PARAM_USER = "usr";
    private static final String LONG_PARAM_USER = "username";
    private static final String PARAM_PASS = "pwd";
    private static final String LONG_PARAM_PASS = "password";
    private static final String LONG_SERVERS_PARAM = "servers";
    private static final String LONG_USE_SERVER_PARAM = "use-server";
    private static final String LONG_TRUST_EVERYONE_PARAM = "trust-everyone";
    public static final String DEFAULT_SERVER_ID = "Default";
    private static final String PARAM_OUTPUT_FORMAT = "of";
    private static final String LONG_PARAM_OUTPUT_FORMAT = "output-format";

    // ============================== STATIC ATTRIBUTES
    // ========================================

    // ============================== INSTANCE ATTRIBUTES
    // ======================================

    // ============================== STATIC METHODS
    // ===========================================

    /**
     * @param usage
     * @param properties
     *            -uri, -f, -r properties
     * @param mandatParams
     *            <project_file>, ... mandatParams
     */
    public static void printHelp(String usage, Options properties,
            Options mandatParams) {
        printPropertyHelp(usage, properties);
        printMandatParamsHelp(mandatParams);
    }

    public static void printPropertyHelp(String usage, Options props) {
        HelpFormatter formatter = new HelpFormatter();

        formatter.setSyntaxPrefix("Usage: ");
        formatter.setLongOptPrefix("--");
        formatter.setLeftPadding(2);
        formatter.setWidth(100);
        formatter.setOptionComparator(null);

        formatter.printHelp(usage, "\nParameters: ", props, "");
    }

    public static void printMandatParamsHelp(Options param) {
        if (param == null) {
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);

        pw.append("\nMandatory Parameters: \n");

        HelpFormatter formatter = new HelpFormatter();

        formatter.setLongOptPrefix("");
        formatter.setOptPrefix("");
        formatter.setWidth(100);

        formatter.printOptions(pw, formatter.getWidth(), param, 2, 3);
        pw.close();
        System.out.println(baos.toString());
    }

    /**
     * Appends the Management endpoint connection info options
     */
    public static Options addConnectionOptions(Options opts) {
        opts.addOption(PARAM_URL, LONG_PARAM_URL, true,
                "URL of the server management endpoint.");
        opts.addOption(null, LONG_TRUST_EVERYONE_PARAM, false,
                "Don't validate server certificates. (may be a security risk)");
        opts.addOption(PARAM_USER, LONG_PARAM_USER, true,
                "Username for server management endpoint connection.");
        opts.addOption(PARAM_PASS, LONG_PARAM_PASS, true,
                "Password for server management endpoint connection.");
        opts.addOption(null, LONG_SERVERS_PARAM, true,
                "A file containing connection properties to one or more SV servers.\n"
                        + "The file format is described at: https://github.com/MicroFocus/sv-configurator");
        opts.addOption(null, LONG_USE_SERVER_PARAM, true,
                "Selects the server if the servers file specified by --" + LONG_SERVERS_PARAM
                        + " parameter contains multiple items.");
        return opts;
    }

    public static Options addOutputFormatOptions(Options opts) {
        opts.addOption(PARAM_OUTPUT_FORMAT, LONG_PARAM_OUTPUT_FORMAT, true,
                "Output format. Supported values are: '"
                        + StringUtils.joinWithDelim("', '", (Object[]) PrinterFactory.getSupportedFormats())
                        + "'. Default value is '" + PrinterFactory.getDefaultFormat() + "'.");
        return opts;
    }

    public static String obtainOutputFormat(CommandLine line) {
        return (line.hasOption(LONG_PARAM_OUTPUT_FORMAT))
                ? line.getOptionValue(LONG_PARAM_OUTPUT_FORMAT) : PrinterFactory.getDefaultFormat();
    }

    public static List<Server> obtainServers(CommandLine line, IProject project)
            throws AbstractSVCException {
        return obtainServers(line, project, false);
    }

    public static List<Server> obtainServers(CommandLine line,
            IProject project, boolean justOneServer)
            throws AbstractSVCException {
        if (line.hasOption(LONG_SERVERS_PARAM)) {
            if (project != null) {
                LOG.info("Skipping project URL '" + project.getServerUrl()
                        + "'");
            }

            String filePath = line.getOptionValue(LONG_SERVERS_PARAM);
            File file = new File(filePath);
            if (!file.exists() && !file.isFile() && !file.canRead()) {
                throw new SVCParseException(
                        "Defined file '"
                                + file
                                + "' does not exist, or is not a file, or is not readable.");
            }

            List<Server> servers = ServerParser.parseServers(
                    file,
                    line.hasOption(LONG_USE_SERVER_PARAM) ? line
                            .getOptionValue(LONG_USE_SERVER_PARAM) : null);
            if (servers == null || servers.isEmpty()) {
                throw new SVCParseException(
                        "No server found in the defined server file '"
                                + filePath + "'");
            }

            if (justOneServer && servers.size() != 1) {
                throw new SVCParseException(
                        "Only one SV server is supported by this command. Use --"
                                + LONG_USE_SERVER_PARAM
                                + " <Server ID> to select just one server. Defined server IDs: "
                                + servers);
            }
            return servers;
        } else {
            Server srv = obtainMgmtEndpointInfo(line);
            if (srv == null
                    || (srv.getURL() == null && (project == null || project
                            .getServerUrl() == null))) {
                throw new SVCParseException("No server management URL defined");
            } else if (srv.getURL() == null) {
                srv = new Server(srv.getId(), project.getServerUrl(),
                        line.hasOption(LONG_TRUST_EVERYONE_PARAM), srv.getCredentials());
            }
            return Arrays.asList(srv);
        }
    }

    /**
     * Obtains the information about management endpoint connection from the
     * command line
     *
     * @throws SVCParseException
     *             if there is no management endpoint connection info in the
     *             command line.
     */
    public static Server obtainMgmtEndpointInfo(CommandLine line)
            throws SVCParseException {
        try {
            URL mgmtUri = line.hasOption(PARAM_URL) ? new URL(
                    line.getOptionValue(PARAM_URL)) : null;
            String username = line.hasOption(PARAM_USER) ? line
                    .getOptionValue(PARAM_USER) : null;
            String password = line.hasOption(PARAM_PASS) ? line
                    .getOptionValue(PARAM_PASS) : null;
            Credentials credentials = username != null ? new Credentials(
                    username, password) : null;
            boolean trustEveryone = line.hasOption(LONG_TRUST_EVERYONE_PARAM);

            return new Server(DEFAULT_SERVER_ID, mgmtUri, trustEveryone, credentials);
        } catch (MalformedURLException e) {
            throw new SVCParseException("Invalid URL defined: '"
                    + line.getOptionValue(PARAM_URL) + "'", e);
        }
    }

    // ============================== CONSTRUCTORS
    // =============================================

    // ============================== ABSTRACT METHODS
    // =========================================

    // ============================== OVERRIDEN METHODS
    // ========================================

    // ============================== INSTANCE METHODS
    // =========================================

    // ============================== PRIVATE METHODS
    // ==========================================

    // ============================== GETTERS / SETTERS
    // ========================================

    // ============================== INNER CLASSES
    // ============================================

}
