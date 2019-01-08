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
package com.microfocus.sv.svconfigurator.util;

import java.util.Collection;

public class StringUtils {
    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    public static String removeSuffix(String str, String suffix) {
        int pos = str.lastIndexOf(suffix);
        return str.substring(0, pos);
    }

    public static String joinWithDelim(String delim, Object... objs) {
        StringBuilder sb = new StringBuilder();
        for (Object o : objs) {
            if (sb.length() > 0) {
                sb.append(delim);
            }
            sb.append(o.toString());
        }
        return sb.toString();
    }

    /**
     * Returns the table for command line interface.
     *
     * @param data data to be printed. It is treated as rows (outer collection) and columns (inner collection). The
     *             number of columns have to be the same in each row.
     * @return
     */
    public static String createTable(Collection<? extends Collection<String>> data) {
        return createTable(data, null);
    }

    /**
     * Returns the table for command line interface
     *
     * @param data
     * @param headers
     * @return
     */
    public static String createTable(Collection<? extends Collection<String>> data, Collection<String> headers) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("There are no data for table creation.");
        }

        String format = countTableStringFormat(data, headers == null ? data.iterator().next() : headers);

        StringBuilder sb = new StringBuilder();
        if (headers != null) {
            sb.append(String.format(format, headers.toArray()));
        }

        for (Collection<?> row : data) {
            sb.append(String.format(format, row.toArray()));
        }
        return sb.toString();
    }

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    private static String countTableStringFormat(Collection<? extends Collection<String>> data, Collection<String> headers) {
        int cols = headers.size();
        int[] colSizes = new int[cols];

        //count needed space
        int i = 0;
        for (String s : headers) {
            colSizes[i] = s.length() + 2;
            i++;
        }

        for (Collection<?> row : data) {
            if (row.size() != cols) {
                throw new IllegalArgumentException("data row has different column count!");
            }

            i = 0;
            for (Object cell : row) {
                String s = cell.toString();
                int len = s.length() + 2;
                if (colSizes[i] < len) {
                    colSizes[i] = len;
                }
                i++;
            }
        }

        String format = "";
        for (int size : colSizes) {
            format += "%-" + size + "s ";
        }
        return format + "\n";
    }

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
