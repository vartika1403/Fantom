package com.entertainment.fantom;

public class Conf {
    public static final String FIREBASE_CHAT_URI = "chat/"; // chat URI
    public static final String FIREBASE_DOMAIN_URI = "https://meetup-99fea.firebaseio.com/categories";//meetup URI
    public static final String FIREBASE_USER_URI = "https://meetup-99fea.firebaseio.com/users";
    public static final String FIREBASE_STORAGE_URL = "https://console.firebase.google.com/project/meetup-99fea/storage/meetup-99fea.appspot.com/files";
    public static boolean isShown = false;

    public static String firebaseDomainUri() {
        return FIREBASE_DOMAIN_URI;
    }

}
