package com.me.android.noteeditor.contract;

public class content_class {

    private String Title;
    private String content;
    private String Time;
    private String folderName;
    private int id;

    public content_class(int id, String title, String content, String time, String folderName){
        this.Title = title;
        this.content = content;
        this.id = id;
        this.Time = time;
        this.folderName = folderName;
    }
    public content_class(int id, String title, String time, String folderName){
        this.id = id;
        this.Title = title;
        this.Time = time;
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public String getContent() {
        return content;
    }

    public void setTime(String time) {
        Time = time;
    }
    public String getTime() {
        return Time;
    }
}
