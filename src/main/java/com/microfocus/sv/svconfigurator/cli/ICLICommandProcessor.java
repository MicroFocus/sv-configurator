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
package com.microfocus.sv.svconfigurator.cli;

/**
 * <p/>
 * Interface for Command Line Processor designed for some concrete command. There several commands that defines the main
 * goal to be done. These commands should be disjoint (with their functionality) and thus there is separate serverclient
 * for any of them.
 */
public interface ICLICommandProcessor {

    public static final int EXIT_CODE_OK = 0;
    public static final int EXIT_CODE_PARSE = 1000;
    public static final int EXIT_CODE_PROJECT_BUILD = 1100;
    public static final int EXIT_CODE_COMMUNICATION = 1200;
    public static final int EXIT_CODE_CONDITIONS = 1300;
    public static final int EXIT_CODE_ABSTRACT_SV = 1500;


    //============================= STATIC METHODS ===========================================


    //============================= PRIVATE METHODS ===========================================


    //============================= ABSTRACT METHODS ===========================================

    /**
     * Process the command
     *
     * @param args any other argument (first argument should not be the command itself)
     * @return an exit code (0 = PRESENT; 1000 = Command line argument parse error; 1100 = Error during project file parsing;
     *         1200 = Error during communication with the server; 1300 = Some condition does not let the operation to be
     *         accomplished)
     */
    public abstract int process(String[] args);

    //========================================= INNER CLASSES ===========================================

}
