package com.android.movieReviews;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader;

import android.util.LruCache;
import android.graphics.Bitmap;
import android.content.Context;

public class VolleyApplication extends Application {

  public static final String TAG = VolleyApplication.class.getSimpleName();
  private RequestQueue mRequestQueue;
  private ImageLoader mImageLoader;
  private static VolleyApplication mInstance;
  private static Context mCtx;

  @Override
  public void onCreate() {
    super.onCreate();
    mInstance = this;

    mRequestQueue = getRequestQueue();
    mImageLoader = new ImageLoader(mRequestQueue,
                          new ImageLoader.ImageCache() {
                            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

                            @Override
                            public Bitmap getBitmap(String url) {
                              return cache.get(url);
                            }

                            @Override
                            public void putBitmap(String url, Bitmap bitmap) {
                              cache.put(url, bitmap);
                            }
                          });
  }

  public static synchronized VolleyApplication getInstance() {
    return mInstance;
  }

  public RequestQueue getRequestQueue() {
    if(mRequestQueue == null) {
      mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }
    return mRequestQueue;
  }

  public <T> void addToRequestQueue(Request<T> req, String tag) {
    req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);;
    getRequestQueue().add(req);
  }

  public <T> void addToRequestQueue(Request<T> req) {
    req.setTag(TAG);
    getRequestQueue().add(req);
  }

  public void cancelPendingRequests(Object tag) {
    if(mRequestQueue != null) {
      mRequestQueue.cancelAll(tag);
    }
  }

  public ImageLoader getImageLoader() {
    return mImageLoader;
  }
}
