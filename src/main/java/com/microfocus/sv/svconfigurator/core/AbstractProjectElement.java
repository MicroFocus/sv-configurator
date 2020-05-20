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
package com.microfocus.sv.svconfigurator.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.microfocus.sv.svconfigurator.core.encryption.EncryptionMetadata;
import com.microfocus.sv.svconfigurator.core.impl.encryption.ProjectElementDecryptorImpl;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.processor.printer.NonPrintable;

public abstract class AbstractProjectElement implements IProjectElement {

    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private final ProjectElementDecryptorImpl decryptor;
    protected EncryptionMetadata encryptionMetadata;
    private String id;
    private String name;
    @NonPrintable
    protected String projectPassword;
    @NonPrintable
    private IProjectElementDataSource ds;
    private byte[] decryptedBytes = null;

    //============================== CONSTRUCTORS =============================================
    public AbstractProjectElement(String id, String name, IProjectElementDataSource ds) {
        this(id, name, ds, null, null);
    }

    public AbstractProjectElement(String id, String name, IProjectElementDataSource ds, EncryptionMetadata encryptionMetadata, String projectPassword) {
        this.id = id;
        this.name = name;
        this.ds = ds;
        this.encryptionMetadata = encryptionMetadata;
        this.projectPassword = projectPassword;
        if (encryptionMetadata != null && encryptionMetadata.hasEncryptedNodes()) {
            this.decryptor = new ProjectElementDecryptorImpl(encryptionMetadata);
        } else {
            this.decryptor = null;
        }
    }

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    public void close() throws IOException {
        this.ds.close();
    }


    //============================== INSTANCE METHODS =========================================

    //============================== GETTERS / SETTERS ========================================

    @Override
    public InputStream getData() throws IOException, SVCParseException {
        InputStream retVal;
        if (this.encryptionMetadata == null || !this.encryptionMetadata.hasEncryptedNodes()) {
            retVal = this.ds.getData();
        } else {
            retVal = decodeData(this.projectPassword, this.ds.getData());
        }
        return retVal;
    }

    private InputStream decodeData(String projectPassword, InputStream data) throws SVCParseException {
        if (decryptor != null) {
            return new ByteArrayInputStream(getDecryptedBytes(projectPassword, data));
        }
        return data;
    }

    private byte[] getDecryptedBytes(String projectPassword, InputStream data) throws SVCParseException {
        if (decryptedBytes == null) {
            decryptedBytes = this.decryptor.decodeStream(data, projectPassword);
        }
        return decryptedBytes;
    }

    @Override
    public long getDataLength() throws IOException, SVCParseException {
        if (decryptor != null) {
            return getDecryptedBytes(this.projectPassword, ds.getData()).length;
        }
        return this.ds.getDataSize();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
