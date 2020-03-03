package com.microfocus.sv.svconfigurator.serverclient;

public class FileInfo {
    private final String fileName;
    private final byte[] content;

    public FileInfo(String fileName, byte[] content){
        this.fileName = fileName;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }
    public byte[] getContent() {
        return content;
    }
}
