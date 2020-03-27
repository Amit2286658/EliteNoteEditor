package com.me.android.noteeditor.contract.FoldersDataBase;

public class Folder_content_class {
    private int id;
    private String folderName;

    public Folder_content_class(String folderName, int id){
        this.id = id;
        this.folderName = folderName;
    }

    public int getId() {
        return id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
