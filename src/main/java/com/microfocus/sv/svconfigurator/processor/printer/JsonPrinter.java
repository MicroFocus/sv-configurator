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
import java.util.Map;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.Service;
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

    private class ServiceEntrySerializer implements JsonSerializer<ServiceListAtom.ServiceEntry> {
        public JsonElement serialize(ServiceListAtom.ServiceEntry src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject js = beginServiceSerialization(src, Boolean.parseBoolean(src.getNonExistentRealService()));

            Map runtimeIssuesMap = JsonPrinter.this.gson.fromJson(src.getRuntimeIssues(), Map.class);
            Object issues = runtimeIssuesMap.get("issues");
            if (issues != null) {
                js.add("runtimeIssues", JsonPrinter.this.gson.toJsonTree(issues));
            }

            return js;
        }
    }

    private class ServiceSerializer implements JsonSerializer<Service> {
        public JsonElement serialize(Service src, Type typeOfSrc, JsonSerializationContext context) {
            return beginServiceSerialization(src, src.NonExistentRealService());
        }
    }

    private JsonObject beginServiceSerialization(Object serviceObj, boolean nonExistentRealService) {
        JsonObject js = JsonPrinter.this.serializersGson.toJsonTree(serviceObj).getAsJsonObject();
        js.addProperty("useRealService", !nonExistentRealService);
        return js;
    }

    private static class ViewServiceDetails {
        IService service;
        ServiceRuntimeConfiguration runtimeConfiguration;
        ServiceRuntimeReport runtimeReport;

        public ViewServiceDetails(IService service, ServiceRuntimeConfiguration runtimeConfiguration, ServiceRuntimeReport runtimeReport) {
            this.service = service;
            this.runtimeConfiguration = runtimeConfiguration;
            this.runtimeReport = runtimeReport;
        }
    }

    private Gson serializersGson;
    private Gson gson;

    public JsonPrinter() {
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .registerTypeAdapter(ReferenceElement.class, new ReferenceElementSerializer())
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getAnnotation(NonPrintable.class) != null;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                });
        serializersGson = builder.create();
        builder.registerTypeAdapter(ServiceListAtom.ServiceEntry.class, new ServiceEntrySerializer());
        builder.registerTypeAdapter(Service.class, new ServiceSerializer());
        gson = builder.create();
    }

    @Override
    public String createServiceInfoOutput(IService svc, ServiceRuntimeConfiguration conf, ServiceRuntimeReport report) {
        return gson.toJson(new ViewServiceDetails(svc, conf, report));
    }
    @Override
    public String createServiceListOutput(ServiceListAtom atom) {
        return gson.toJson(atom.getEntries());
    }

    @Override
    public String createProjectListOutput(IProject project) {
        return gson.toJson(project);
    }
}
