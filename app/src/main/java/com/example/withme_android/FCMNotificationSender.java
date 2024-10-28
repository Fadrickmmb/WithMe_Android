package com.example.withme_android;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMNotificationSender {
    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Activity mActivity;

    private RequestQueue requestQueue;
    private final String posturl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "";

    public FCMNotificationSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void SendNotifications() {
        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notifObject = new JSONObject();
            notifObject.put("title", title);
            notifObject.put("body", body);
            notifObject.put("icon", "baseline_notifications");
            mainObj.put("notification", notifObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, posturl, mainObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("FCM", "Notification sent successfully: " + response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("FCM", "Failed to send notification: " + error.getMessage());
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "key=" + fcmServerkey);
                    return headers;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            Log.e("FCM", "Failed to create notification JSON: " + e.getMessage());
        }
    }

}
