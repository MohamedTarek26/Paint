package com.paint.paint.upload;


public class FileUploadResponse {

    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private Long size;
    public void setName(String fileName2){
        this.fileName = fileName2;
    }
    public void setDownloadUri(String fileDownloadUri){
        this.fileDownloadUri = fileDownloadUri;
    }
    public void setType(String fileType){
        this.fileType = fileType;
    }
    public void setSize(long size){
        this.size = size;
    }
    public String getName(){
        return fileName;
    }
    public String getDownloadUri(){
        return fileDownloadUri;
    }
    public String getType(){
        return fileType;
    }
    public Long getSize(){
        return size;
    }
}