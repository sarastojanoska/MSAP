package com.example.myapplication;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class MagazinesLoader extends AsyncTaskLoader<String> {
    private String mQueryString;

    public MagazinesLoader(Context context, String queryString){
        super(context);
        mQueryString = queryString;
    }


    @Override
    public void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        return NetworkUtils.getMagazineInfo(mQueryString);
    }
}
