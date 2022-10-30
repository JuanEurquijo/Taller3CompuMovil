package com.edu.compumovil.taller3.models.database;

public class DatabaseRoutes {
    public final static String USERS_PATH = "users";
    public final static String IMAGES_PATH = "profileImages";

    public final static String getUser (String uuid){
        return String.format("%s/%s", USERS_PATH, uuid);
    }
    public static String getImage (String uuid){
        return String.format("%s/%s", IMAGES_PATH, uuid);
    }

}