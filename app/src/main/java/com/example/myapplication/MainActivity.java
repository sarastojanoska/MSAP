package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private EditText mMagazineInput;
    private TextView mTitleText;
    private TextView mDescriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMagazineInput = (EditText) findViewById(R.id.magazineInput);
        mTitleText = (TextView) findViewById(R.id.titleText);
        mDescriptionText = (TextView) findViewById(R.id.descriptionText);

        if(getSupportLoaderManager().getLoader(0)!= null){
            getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    public void searchMagazines(View view) {
        String queryString = mMagazineInput.getText().toString();

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected() && queryString.length()!=0){
            mDescriptionText.setText("");
            mTitleText.setText("Loading..");
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            getSupportLoaderManager().restartLoader(0,queryBundle,this);
        }
        else {
            if(queryString.length() == 0) {
                mDescriptionText.setText("");
                mTitleText.setText("Please enter a search term");
            }
            else {
                mDescriptionText.setText("");
                mTitleText.setText("Please check your network connection and try again");
            }
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new MagazinesLoader(this,args.getString("queryString"));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            int i = 0;
            String title = null;
            String description = null;

            while (i < itemsArray.length() || (description == null && title == null)){
                JSONObject magazine = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = magazine.getJSONObject("volumeInfo");

                try{
                    title = volumeInfo.getString("title");
                    description = volumeInfo.getString("description");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }
            if(title != null && description != null){
                mTitleText.setText(title);
                mDescriptionText.setText(description);
                mMagazineInput.setText("");
            }
            else{
                mTitleText.setText("No Results found");
                mDescriptionText.setText("");
            }
        } catch (JSONException e) {
            mTitleText.setText("No Results Found");
            e.printStackTrace();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
