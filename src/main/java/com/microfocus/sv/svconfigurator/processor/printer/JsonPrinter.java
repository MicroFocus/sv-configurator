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
package com.microfocus.sv.svconfigurator.processor.printer;

import java.lang.reflect.Type;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeReport;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ServiceListAtom;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.helper.ReferenceElement;

public class JsonPrinter implements IPrinter {
    private static class ReferenceElementSerializer implements JsonSerializer<ReferenceElement> {
        public JsonElement serialize(ReferenceElement src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getRef());
        }
    }
    private static class ServiceDetails {
        IService service;
        ServiceRuntimeConfiguration runtimeConfiguration;
        ServiceRuntimeReport runtimeReport;

        public ServiceDetails(IService service, ServiceRuntimeConfiguration runtimeConfiguration, ServiceRuntimeReport runtimeReport) {
            this.service = service;
            this.runtimeConfiguration = runtimeConfiguration;
            this.runtimeReport = runtimeReport;
        }
    }

    @Override
    public String createServiceInfoOutput(IService svc, ServiceRuntimeConfiguration conf, ServiceRuntimeReport report) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(ReferenceElement.class, new ReferenceElementSerializer())
                .addSerializationExclusionStrategy(new ServiceInfoExclusionStrategy())
                .create();
        return gson.toJson(new ServiceDetails(svc, conf, report));
    }
    @Override
    public String createServiceListOutput(ServiceListAtom atom) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(atom.getEntries());
    }

    private static class ServiceInfoExclusionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(NonPrintable.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}
