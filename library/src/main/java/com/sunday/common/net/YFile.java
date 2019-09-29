package com.sunday.common.net;

import java.io.File;

/**
 * YRequest上传文件类型
 * Created by Ace on 2016/8/18.
 */
public class YFile {
    private File file;  //文件
    private String fileKey; //与后台协定的接收名称
    private String fileName;    //文件名称

    public YFile(String fileKey, String fileName, File file) {
        this.fileKey = fileKey;
        this.fileName = fileName;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
