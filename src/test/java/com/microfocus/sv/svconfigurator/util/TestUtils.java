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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestUtils {

    public static String[] createParams(String line) {
        if (line == null) throw new IllegalArgumentException("Line can not be null");
        
        StringLooper sl = new StringLooper(line);
        List<String> params = new ArrayList<String>();

        while (sl.hasNext()) {
            char ch = sl.next();
            if (ch == ' ') { // space is skipped
                continue;
            } else if (ch == '\"') { // start of Quoted Param
                params.add(parseQuotedParam(sl));
            } else {
                params.add(parseSimpleParam(sl));
            }
        }

        return params.toArray(new String[params.size()]);
    }

    private static String parseSimpleParam(StringLooper sl) {
        StringBuilder sb = new StringBuilder();
        do {
            char ch = sl.peek();
            if (ch == ' ') { // end of simple param
                break;
            } else {
                sb.append(ch);
            }
        } while (sl.hasNext() && sl.next() == sl.peek());
        
        return sb.toString();
    }

    private static String parseQuotedParam(StringLooper sl) {
        StringBuilder sb = new StringBuilder();
        while (sl.hasNext()) {
            char ch = sl.next();
            if (ch == '\"') { // end of quoted param
                break;
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    @Test
    public void testCreateParams() {
        assertArrayEquals(
                new String[] { "param1", "param2", "param3" },
                createParams("param1 param2 param3"));

        assertArrayEquals(
                new String[] { "param", "longer param with spaces", "endParam" },
                createParams("param \"longer param with spaces\" endParam"));

        assertArrayEquals(
                new String[] { "name of the service", "longer param with spaces", "96df-6sdf-646sdf-654df" },
                createParams("\"name of the service\" \"longer param with spaces\" 96df-6sdf-646sdf-654df"));

        assertArrayEquals(
                new String[] {},
                createParams(""));

        assertArrayEquals(
                new String[] { "asdf adsfas dfsdjkhgsk fsglkskjdfg dfgs 0123 sdfgsdf" },
                createParams("\"asdf adsfas dfsdjkhgsk fsglkskjdfg dfgs 0123 sdfgsdf\""));

        assertArrayEquals(
                new String[] { "asdf adsfas dfsdjkhgsk fsglkskjdfg dfgs 0123 sdfgsdf" },
                createParams("\"asdf adsfas dfsdjkhgsk fsglkskjdfg dfgs 0123 sdfgsdf\""));

        try {
            createParams(null);
            Assert.fail("Should throw an IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            // this is fine
        }

        assertArrayEquals(
                new String[] { "0", "1", "2", "0123", "+\u011B\u0161\u010D\u0159\u017E\u00FD\u00E1\u00ED\u00E9", "=\u00E9\u00ED\u00E1\u00FD\u017E\u0159\u010D\u0161\u011B+" },
                createParams("0 1 2 0123 +\u011B\u0161\u010D\u0159\u017E\u00FD\u00E1\u00ED\u00E9 \"=\u00E9\u00ED\u00E1\u00FD\u017E\u0159\u010D\u0161\u011B+\""));

        assertArrayEquals(
                new String[] { "0", "1", "2" },
                createParams("0 1 2 "));
    }

    @Test
    public void testStringLooper() {
        StringLooper sl = new StringLooper("12;34 ");

        try {
            sl.peek();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        }

        assertEquals(true, sl.hasNext());
        assertEquals('1', sl.next());
        assertEquals('1', sl.peek());

        assertTrue(sl.hasNext());

        assertEquals('2', sl.next());
        assertEquals('2', sl.peek());

        assertEquals(';', sl.next());
        assertEquals('3', sl.next());
        assertEquals('4', sl.next());
        assertEquals(' ', sl.next());

        try {
            sl.next();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
            // ok
        }

        assertFalse(sl.hasNext());
    }

    private static class StringLooper {

        private int index;
        private char[] chars;

        public StringLooper(String str) {
            this.chars = str.toCharArray();
            this.index = -1;
        }

        public boolean hasNext() {
            return (index + 1) < this.chars.length;
        }

        public char peek() {
            return this.chars[index];
        }

        public char next() {
            if (!hasNext())
                throw new ArrayIndexOutOfBoundsException(index + 1);

            return this.chars[++this.index];
        }

    }

}
