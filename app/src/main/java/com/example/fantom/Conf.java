package com.example.fantom;

public class Conf {
    public static final String FIREBASE_CHAT_URI = "chat/"; // chat URI
    private static final String FIREBASE_DOMAIN_URI = "https://meetup-99fea.firebaseio.com/";//meetup URI
    public  static  boolean isShown = false;

    public static String firebaseDomainUri() {
        return FIREBASE_DOMAIN_URI;
    }

   /* public static String firebaseConverstionUri(String chatId) {
        if (chatId != null && !chatId.isEmpty()) {
            return firebaseDomainUri() + FIREBASE_CHAT_URI + FIREBASE_CONVERSATION_URI + chatId + "/" + "message-list/";
        }
        retur*/
}
