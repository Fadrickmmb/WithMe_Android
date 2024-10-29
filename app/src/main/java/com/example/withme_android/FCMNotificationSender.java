package com.example.withme_android;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class FCMNotificationSender {
    private String userToken;
    private String title;
    private String message;
    private Context mContext;
    private String apiKey = "";  //stored

    private final String BACKEND_API = ""; // need to define this

    public FCMNotificationSender(String userToken, String title, String message, Context mContext, User_ViewProfile userViewProfile) {
        this.userToken = userToken;
        this.title = title;
        this.message = message;
        this.mContext = mContext;
    }

    public void SendNotifications() {
        JSONObject payload = new JSONObject();
        try {
            payload.put("token", userToken);
            payload.put("title", title);
            payload.put("body", message);
            payload.put("apiKey", apiKey);
        } catch (JSONException e) {
            Log.e("FCMNotificationSender", "Error creating JSON payload", e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BACKEND_API, payload,
                response -> Log.d("FCMNotificationSender", "Notification request sent successfully"),
                error -> Log.e("FCMNotificationSender", "Error sending notification request: " + error.getMessage())) {
        };

        Volley.newRequestQueue(mContext).add(request);
    }
}