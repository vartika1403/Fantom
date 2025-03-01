package com.entertainment.fantom;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailObject implements Parcelable {
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
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String webLink;
    private String fbLink;
    private String image;
    private String category;

    public DetailObject() {

    }

    public DetailObject(String userId, String name, String fbLink, String webLink,
                        String image, String phoneNumber, String category) {
        this.userId = userId;
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
        phoneNumber = in.readString();
        category = in.readString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getPhoneNum() {
        return phoneNumber;
    }

    public void setPhoneNum(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
