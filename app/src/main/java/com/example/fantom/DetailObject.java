package com.example.fantom;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailObject implements Parcelable {
    private String fbLink;
    private String name;
    private String webLink;
    private String imageUrl;
    private String email;

    public DetailObject() {

    }
    public DetailObject(String name,String fbLink,  String webLink, String imageUrl) {
        this.fbLink = fbLink;
        this.name = name;
        this.webLink = webLink;
        this.imageUrl = imageUrl;
    }
    public String getFbLink() {
        return fbLink;
    }

    public void setFbLink(String fbLink) {
        this.fbLink = fbLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
