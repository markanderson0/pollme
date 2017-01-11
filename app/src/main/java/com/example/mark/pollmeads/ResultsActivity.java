package com.example.mark.pollmeads;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class ResultsActivity extends Activity implements View.OnClickListener {
    private Firebase mFirebaseRef;
    private InterstitialAd mInterstitialAd;
    private TextView tvQuestion, tvAnswer1, tvAnswer2, tvAnswer3, tvAnswer4, tvAnswer5, tvPercent1, tvPercent2, tvPercent3, tvPercent4, tvPercent5;
    private ProgressBar pbAnswer1, pbAnswer2, pbAnswer3, pbAnswer4, pbAnswer5;
    private Button bHome, bSearch;
    private LinearLayout linResults, linLayout;
    private String question, answer1, answer2, answer3, answer4, answer5, pollId, from, searchType;
    private int result1, result2, result3, result4, result5, total;
    private float percent1, percent2, percent3, percent4, percent5;

    private static final String REPO_URL = "XXXXX";
    private static final String AD_UNIT_ID = "ca-app-pub-5131083385461018/3060389883";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(AD_UNIT_ID);
        //ca-app-pub-5131083385461018/3060389883
        //TEST:ca-app-pub-3940256099942544/1033173712
        requestNewInterstitial();
        Firebase.setAndroidContext(this);
        Bundle extras = getIntent().getExtras();
        pollId = extras.getString("pollId");
        from = extras.getString("from");
        question = extras.getString("tvQuestion");
        getAnswers();
    }

    private void getAnswers(){
        mFirebaseRef = new Firebase(REPO_URL + "polls/"+pollId);
        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " answers");
                CreatePoll post = snapshot.getValue(CreatePoll.class);
                answer1 = post.getAnswer1();
                answer2 = post.getAnswer2();
                answer3 = post.getAnswer3();
                answer4 = post.getAnswer4();
                answer5 = post.getAnswer5();
                getResults();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void getResults(){
        mFirebaseRef = new Firebase(REPO_URL + "results/"+pollId);
        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " results");
                Results post = snapshot.getValue(Results.class);
                result1 = post.getResult1();
                result2 = post.getResult2();
                result3 = post.getResult3();
                result4 = post.getResult4();
                result5 = post.getResult5();
                initialiseLayout();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }


    private void initialiseLayout(){
        linLayout = (LinearLayout) findViewById(R.id.linLayout);
        linResults = (LinearLayout) findViewById(R.id.linRes);
        linResults.setVisibility(View.VISIBLE);
        linLayout.setVisibility(View.VISIBLE);

        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto-Thin.ttf");
        tvQuestion = (TextView) findViewById(R.id.tVRQuestion);
        tvQuestion.setText(question);
        tvQuestion.setVisibility(View.VISIBLE);
        tvQuestion.setTypeface(typeface);
        tvAnswer1 = (TextView) findViewById(R.id.tvRAnswer1);
        tvAnswer1.setText(" " + answer1);
        pbAnswer1 = (ProgressBar) findViewById(R.id.pbAnswer1);
        tvAnswer1.setTypeface(typeface);
        tvPercent1 = (TextView) findViewById(R.id.tvRPercent1);
        tvPercent1.setTypeface(typeface);
        tvAnswer2 = (TextView) findViewById(R.id.tvRAnswer2);
        tvAnswer2.setText(" " + answer2);
        pbAnswer2 = (ProgressBar) findViewById(R.id.pbAnswer2);
        tvAnswer2.setTypeface(typeface);
        tvPercent2 = (TextView) findViewById(R.id.tvRPercent2);
        tvPercent2.setTypeface(typeface);

        total = result1 + result2 + result3 + result4 + result5;
        pbAnswer1.setMax(total);
        pbAnswer1.setProgress(result1);
        pbAnswer2.setMax(total);
        pbAnswer2.setProgress(result2);
        percent1 = result1 * 100f / total;
        percent2 = result2 * 100f / total;
        tvPercent1.setText(Math.round(percent1) + "%");
        tvPercent2.setText(Math.round(percent2) + "%");

        if(!answer3.equals("")) {
            tvAnswer3 = (TextView) findViewById(R.id.tvRAnswer3);
            tvAnswer3.setTypeface(typeface);
            tvAnswer3.setVisibility(View.VISIBLE);
            pbAnswer3 = (ProgressBar) findViewById(R.id.pbAnswer3);
            pbAnswer3.setVisibility(View.VISIBLE);
            tvPercent3 = (TextView) findViewById(R.id.tvRPercent3);
            tvPercent3.setTypeface(typeface);
            tvPercent3.setVisibility(View.VISIBLE);

            tvAnswer3.setText(" " + answer3);
            pbAnswer3.setMax(total);
            pbAnswer3.setProgress(result3);
            percent3 = result3 * 100f / total;
            tvPercent3.setText(Math.round(percent3) + "%");

            DisplayMetrics displayMetrics = getApplicationContext()
                    .getResources().getDisplayMetrics();
            int px = Math
                    .round(205 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, px);
            int px2 = Math
                    .round(10 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            lp.setMargins(0, 0, 0, px2);
            linResults.setLayoutParams(lp);
        }

        if(!answer4.equals("")) {
            tvAnswer4 = (TextView) findViewById(R.id.tvRAnswer4);
            tvAnswer4.setTypeface(typeface);
            tvAnswer4.setVisibility(View.VISIBLE);
            pbAnswer4 = (ProgressBar) findViewById(R.id.pbAnswer4);
            pbAnswer4.setVisibility(View.VISIBLE);
            tvPercent4 = (TextView) findViewById(R.id.tvRPercent4);
            tvPercent4.setTypeface(typeface);
            tvPercent4.setVisibility(View.VISIBLE);

            tvAnswer4.setText(" " + answer4);
            pbAnswer4.setMax(total);
            pbAnswer4.setProgress(result4);
            percent4 = result4 * 100f / total;
            tvPercent4.setText(Math.round(percent4) + "%");

            DisplayMetrics displayMetrics = getApplicationContext()
                    .getResources().getDisplayMetrics();
            int px = Math
                    .round(265 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, px);
            int px2 = Math
                    .round(10 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            lp.setMargins(0, 0, 0, px2);
            linResults.setLayoutParams(lp);
        }

        if(!answer5.equals("")) {
            tvAnswer5 = (TextView) findViewById(R.id.tvRAnswer5);
            tvAnswer5.setTypeface(typeface);
            tvAnswer5.setVisibility(View.VISIBLE);
            pbAnswer5 = (ProgressBar) findViewById(R.id.pbAnswer5);
            pbAnswer5.setVisibility(View.VISIBLE);
            tvPercent5 = (TextView) findViewById(R.id.tvRPercent5);
            tvPercent5.setTypeface(typeface);
            tvPercent5.setVisibility(View.VISIBLE);

            tvAnswer5.setText(" " + answer5);
            pbAnswer5.setMax(total);
            pbAnswer5.setProgress(result5);
            percent5 = result5 * 100f / total;
            tvPercent5.setText(Math.round(percent5) + "%");

            DisplayMetrics displayMetrics = getApplicationContext()
                    .getResources().getDisplayMetrics();
            int px = Math
                    .round(325 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, px);
            int px2 = Math
                    .round(10 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            lp.setMargins(0, 0, 0, px2);
            linResults.setLayoutParams(lp);
        }

        bHome = (Button) findViewById(R.id.bRHome);
        bHome.setVisibility(View.VISIBLE);
        bHome.setOnClickListener(this);
        bHome.setTypeface(typeface);
        bSearch = (Button) findViewById(R.id.bRSearch);
        bSearch.setVisibility(View.VISIBLE);
        bSearch.setOnClickListener(this);
        bSearch.setTypeface(typeface);
        if (from.equals("vote")) {
            bSearch.setText("Search");
            searchType = ".SearchActivity";
        } else if (from.equals("mypolls")) {
            bSearch.setText("My Polls");
            searchType = ".MyPollsActivity";
        } else if (from.equals("history")) {
            bSearch.setText("History");
            searchType = ".HistoryActivity";
        } else if (from.equals("location")) {
            bSearch.setText("Search Local Polls");
            searchType = ".LocationActivity";
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bRHome:
                Intent intentHome = new Intent(".Home");
                startActivity(intentHome);
                break;
            case R.id.bRSearch:
                Intent intentSearch = new Intent(searchType);
                startActivity(intentSearch);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(from.equals("vote")){
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
        else if(from.equals("mypolls")){
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
        else if(from.equals("history")){
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
        else if(from.equals("location")){
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
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
                //.addTestDevice("test")
        mInterstitialAd.loadAd(adRequest);
    }
}
