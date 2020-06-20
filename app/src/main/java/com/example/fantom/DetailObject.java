package com.example.fantom;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailObject implements Parcelable {
    private String fbLink;
    private String name;
    private String webLink;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;
    private String email;

    public DetailObject() {

    }
    public DetailObject(String name,String fbLink,  String webLink, String image) {
        this.fbLink = fbLink;
        this.name = name;
        this.webLink = webLink;
        this.image = image;
    }

    protected DetailObject(Parcel in) {
        fbLink = in.readString();
        name = in.readString();
        webLink = in.readString();
        image = in.readString();
        email = in.readString();
    }

    public static final Creator<DetailObject> CREATOR = new Creator<DetailObject>() {
        @Override
        public DetailObject createFromParcel(Parcel in) {
            return new DetailObject(in);
        }

        @Override
        public DetailObject[] newArray(int size) {
            return new DetailObject[size];
        }
    };

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
        return image;
    }

    public void setImageUrl(String imageUrl) {
        this.image = imageUrl;
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
        parcel.writeString(fbLink);
        parcel.writeString(name);
        parcel.writeString(webLink);
        parcel.writeString(image);
        parcel.writeString(email);
    }
}
