package com.sonali.mymoviemobiapp.web;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by sonali on 8/8/17.
 */

public class Ws {
    private static RequestQueue queue;

    public static RequestQueue getQueue(Context context) {
        if (queue == null)
            queue = Volley.newRequestQueue(context);
        return queue;
    }
}
