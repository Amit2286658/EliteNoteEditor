package com.me.android.noteeditor.Recyclerview_adapter.commonDataModel;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Amit on 1/3/2020.
 */

public class EditingContentModelData {

    public enum EditingContentType{
        TEXT,
        IMAGE,
        VIDEO
    }

    public enum EditingContentLinkType{
        URI,
        URL
    }

    private String image_link = null, html_text = null, video_link = null;
    private Uri image_uri = null;
    private EditingContentType type = null;
    private EditingContentLinkType linkType = null;

    /*
    * Use this constructor when using the setter methods, for convenience
    */
    public EditingContentModelData(){
        //empty
    }

    public EditingContentModelData(String image_link, String html_text, Uri image_uri, String video_link, EditingContentType type,
                                   EditingContentLinkType linkType
    ){
        this.html_text = html_text;
        this.image_link = image_link;
        this.image_uri = image_uri;
        this.video_link = video_link;
        this.type = type;
        this.linkType = linkType;
    }

    public EditingContentModelData(ArrayList<EditingContentModelData> editingContentModelData){
        for (EditingContentModelData item : editingContentModelData){
            this.html_text = item.getHtml_text();
            this.video_link = item.getVideo_link();
            this.image_uri = item.getImage_uri();
            this.image_link = item.getImage_link();
            this.type = item.getType();
            this.linkType = item.getLinkType();
        }
    }

    public void setLinkType(EditingContentLinkType linkType) {
        this.linkType = linkType;
    }

    public void setType(EditingContentType type) {
        this.type = type;
    }

    public void setHtml_text(String html_text) {
        this.html_text = html_text;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public void setImage_uri(Uri image_uri) {
        this.image_uri = image_uri;
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }

    public String getHtml_text() {
        return html_text;
    }

    public String getImage_link() {
        return image_link;
    }

    public Uri getImage_uri() {
        return image_uri;
    }

    public String getVideo_link() {
        return video_link;
    }

    public EditingContentType getType() {
        return type;
    }

    public EditingContentLinkType getLinkType() {
        return linkType;
    }
}
