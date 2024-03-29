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
package com.microfocus.sv.svconfigurator.core.impl.jaxb.helper;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * vs:service / vs:dataModel / vs:performanceModel element in the virtualServiceRuntimeConfiguration element
 */
public class ReferenceElement {

    private String ref;

    public ReferenceElement() {
    }

    public ReferenceElement(String serviceId) {
        this.ref = serviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReferenceElement)) return false;

        ReferenceElement that = (ReferenceElement) o;

        return !(ref != null ? !ref.equals(that.ref) : that.ref != null);

    }

    @Override
    public int hashCode() {
        return ref != null ? ref.hashCode() : 0;
    }

    @XmlAttribute(name = "ref")
    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
