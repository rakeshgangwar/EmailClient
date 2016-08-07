package com.rakeshgangwar.emailclient;

import android.app.Application;

/**
 * Created by Rakesh on 8/8/2016.
 */
public class EmailApplication extends Application {
    private String BASE_URL = "http://192.168.0.160:8088/";

    public String getBaseUrl() {
        return BASE_URL;
    }
}
