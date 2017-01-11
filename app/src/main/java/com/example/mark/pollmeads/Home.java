package com.example.mark.pollmeads;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Home extends Activity implements View.OnClickListener {
    private Button bCreate, bSearch, bMyPolls, bHistory, bLocation;
    private TextView label;
    private InterstitialAd mInterstitialAd;

    private static final String AD_UNIT_ID = "XXXXX";
    private static final String AD_UNIT_ID_TEST = "XXXXX";

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(AD_UNIT_ID);
        requestNewInterstitial();

        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto-Thin.ttf");
        label = (TextView) findViewById(R.id.label);
        label.setTypeface(typeface);
        bCreate = (Button) findViewById(R.id.bCreate);
        bCreate.setOnClickListener(this);
        bCreate.setTypeface(typeface);

        bSearch = (Button) findViewById(R.id.bSearch);
        bSearch.setOnClickListener(this);
        bSearch.setTypeface(typeface);

        bMyPolls = (Button) findViewById(R.id.bMyPolls);
        bMyPolls.setOnClickListener(this);
        bMyPolls.setTypeface(typeface);

        bHistory = (Button) findViewById(R.id.bHistory);
        bHistory.setOnClickListener(this);
        bHistory.setTypeface(typeface);

        bLocation = (Button) findViewById(R.id.bLocation);
        bLocation.setOnClickListener(this);
        bLocation.setTypeface(typeface);
    }

    private void openCreatePage(){
        Intent intent = new Intent(".CreatePollActivity");
        startActivity(intent);
    }

    private void openSearchPage(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Intent intent = new Intent(".SearchActivity");
            startActivity(intent);
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Intent intent = new Intent(".SearchActivity");
                startActivity(intent);
            }
        });
    }

    private void openMyPollsPage(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Intent intent = new Intent(".MyPollsActivity");
            startActivity(intent);
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Intent intent = new Intent(".MyPollsActivity");
                startActivity(intent);
            }
        });
    }

    private void openHistoryPage(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Intent intent = new Intent(".HistoryActivity");
            startActivity(intent);
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Intent intent = new Intent(".HistoryActivity");
                startActivity(intent);
            }
        });
    }

    private void openLocationPage(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Intent intent = new Intent(".LocationActivity");
            startActivity(intent);
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Intent intent = new Intent(".LocationActivity");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bCreate:
                openCreatePage();
                break;
            case R.id.bSearch:
                openSearchPage();
                break;
            case R.id.bMyPolls:
                openMyPollsPage();
                break;
            case R.id.bHistory:
                openHistoryPage();
                break;
            case R.id.bLocation:
                openLocationPage();
                break;
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        //.addTestDevice("test")
        mInterstitialAd.loadAd(adRequest);
    }
}