package com.microfocus.sv.svconfigurator.core.impl;

import com.microfocus.sv.svconfigurator.core.AbstractProjectElement;
import com.microfocus.sv.svconfigurator.core.ILoggedServiceCallList;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.IProjectElementVisitor;
import com.microfocus.sv.svconfigurator.core.encryption.EncryptionMetadata;

public class LoggedServiceCallList extends AbstractProjectElement implements ILoggedServiceCallList {
    private final String vsId;

    public LoggedServiceCallList(String id, String name, String vsId, IProjectElementDataSource ds, EncryptionMetadata encryptionMetadata, String projectPassword) {
        super(id, name, ds, encryptionMetadata, projectPassword);
        this.vsId = vsId;
    }

    @Override
    public void accept(IProjectElementVisitor visitor) {
        visitor.visit(this);
   }

    @Override
    public String VsId() {
        return vsId;
    }
}
