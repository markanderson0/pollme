package com.example.mark.pollmeads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreatePollActivity extends Activity implements View.OnClickListener {
    private Firebase mFirebaseRef;
    private EditText etQuestion, etAnswer1, etAnswer2, etAnswer3, etAnswer4, etAnswer5, etPassword;
    private TextView tvCategory;
    private Button bSubmitPoll, bAddAnswer, bRemoveAnswer;
    private Spinner spinCategory;
    private CheckBox cbPassword, cbLocation;
    private String longitude, latitude;
    private LocationManager locationManager = null;
    private String provider;
    private LinearLayout linScroll;
    private int answers = 2;
    private Typeface typeface;

    private static final String REPO_URL = "XXXXX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createpoll);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto-Thin.ttf");
        Firebase.setAndroidContext(this);
        initializeLayout();
    }

    private void initializeLayout() {
        linScroll = (LinearLayout) findViewById(R.id.linScroll);
        bAddAnswer = (Button) findViewById(R.id.bAddAnswer);
        bAddAnswer.setOnClickListener(this);
        bRemoveAnswer = (Button) findViewById(R.id.bRemoveAnswer);
        bRemoveAnswer.setOnClickListener(this);
        bRemoveAnswer.setVisibility(View.INVISIBLE);

        etQuestion = (EditText) findViewById(R.id.etQuestion);
        etQuestion.setTypeface(typeface);
        etAnswer1 = (EditText) findViewById(R.id.etAnswer1);
        etAnswer1.setTypeface(typeface);
        etAnswer2 = (EditText) findViewById(R.id.etAnswer2);
        etAnswer2.setTypeface(typeface);
        etAnswer3 = (EditText) findViewById(R.id.etAnswer3);
        etAnswer3.setTypeface(typeface);
        etAnswer3.setVisibility(View.INVISIBLE);
        etAnswer4 = (EditText) findViewById(R.id.etAnswer4);
        etAnswer4.setTypeface(typeface);
        etAnswer4.setVisibility(View.INVISIBLE);
        etAnswer5 = (EditText) findViewById(R.id.etAnswer5);
        etAnswer5.setTypeface(typeface);
        etAnswer5.setVisibility(View.INVISIBLE);

        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvCategory.setTypeface(typeface);
        latitude = "Location not available";
        longitude = "Location not available";

        cbPassword = (CheckBox) findViewById(R.id.cbPassword);
        cbPassword.setTypeface(typeface);
        cbPassword
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            etPassword.setVisibility(View.VISIBLE);
                        } else {
                            etPassword.setVisibility(View.INVISIBLE);
                        }
                    }
                });

        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword.setTypeface(typeface);
        etPassword.setVisibility(View.INVISIBLE);
        bSubmitPoll = (Button) findViewById(R.id.bSubmitPoll);
        bSubmitPoll.setOnClickListener(this);
        bSubmitPoll.setTypeface(typeface);
        spinCategory = (Spinner) findViewById(R.id.spinCategory);
        String[] cats = {"Select Category", "Business", "Electronics", "Entertainment",
                "Food", "Health & Fitness", "Misc", "Sports"};
        ArrayAdapter<String> adapter_cats = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, cats) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(typeface);
                return v;
            }

            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(typeface);
                v.setBackgroundColor(Color.WHITE);
                return v;
            }
        };
        adapter_cats.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinCategory.setAdapter(adapter_cats);

        cbLocation = (CheckBox) findViewById(R.id.cbLocation);
        cbLocation.setTypeface(typeface);
        cbLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && isLocationEnabled(getApplicationContext())) {    //if checkbox pressed and location services enabled

                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    provider = locationManager.getBestProvider(criteria, false);
                    Location location = locationManager.getLastKnownLocation(provider);    //get the last known location

                    if (location == null) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreatePollActivity.this);
                        alertDialogBuilder.setMessage("Sorry. Your location could not be determined. Please try again later."); //inform the user
                        alertDialogBuilder.setPositiveButton(R.string.positive_button,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface arg0, int arg1) {
                                        cbLocation.setChecked(false);
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        cbLocation.setChecked(false);
                        latitude = "Location not available";    //otherwise no location has been set
                        longitude = "Location not available";
                    } else {
                        onLocationChanged(location);

                    }
                } else if (isChecked && !isLocationEnabled(getApplicationContext())) {    //if checkbox pressed and no location services

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreatePollActivity.this);
                    alertDialogBuilder.setMessage("To view local polls you must enable location services on your device."); //inform the user
                    alertDialogBuilder.setPositiveButton(R.string.positive_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface arg0, int arg1) {
                                    cbLocation.setChecked(false);
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    latitude = "Location not available";    //otherwise no location has been set
                    longitude = "Location not available";
                }
            }
        });
    }

    public void onLocationChanged(Location location) {
        double lat = (location.getLatitude());
        double lng = (location.getLongitude());
        longitude = String.valueOf(lng);
        latitude = String.valueOf(lat);
    }

    @SuppressLint("InlinedApi")
    @SuppressWarnings("deprecation")
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(
                        context.getContentResolver(),
                        Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case (R.id.bSubmitPoll):
                if (errorCheck()) {
                    final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                    final String deviceId = tm.getDeviceId();
                    String sCategory = spinCategory.getSelectedItem().toString();
                    String sQuestion = etQuestion.getText().toString();
                    String sAns1 = etAnswer1.getText().toString();
                    String sAns2 = etAnswer2.getText().toString();
                    String sAns3 = etAnswer3.getText().toString();
                    String sAns4 = etAnswer4.getText().toString();
                    String sAns5 = etAnswer5.getText().toString();
                    String sPassword = etPassword.getText().toString();

                    mFirebaseRef = new Firebase("https://brilliant-inferno-4839.firebaseio.com/");
                    Firebase postRef = mFirebaseRef.child("polls");
                    Firebase newPostRef = postRef.push();
                    // Add some data to the new location
                    Map<String, String> post1 = new HashMap<>();
                    post1.put("deviceId", deviceId);
                    post1.put("category", sCategory);
                    post1.put("latitude", latitude);
                    post1.put("longitude", longitude);
                    post1.put("question", sQuestion);
                    post1.put("answer1", sAns1);
                    post1.put("answer2", sAns2);
                    post1.put("answer3", sAns3);
                    post1.put("answer4", sAns4);
                    post1.put("answer5", sAns5);
                    if (cbPassword.isChecked()) {
                        post1.put("privacy", "true");
                        post1.put("password", sPassword);
                    } else {
                        post1.put("privacy", "false");
                        post1.put("password", "NA");
                    }
                    newPostRef.setValue(post1);
                    // Get the unique ID generated by push()
                    String pollId = newPostRef.getKey();

                    Firebase resultRef = mFirebaseRef.child("results").child(pollId);
                    com.example.mark.pollmeads.Results result = new com.example.mark.pollmeads.Results(0, 0, 0, 0, 0);
                    resultRef.setValue(result);

                    Firebase historyRef = new Firebase(REPO_URL + "history/" + pollId);
                    Firebase posthistoryRef = historyRef.push();
                    posthistoryRef.setValue("00000000initial");

                    Firebase userRef = new Firebase(REPO_URL + "mypolls/" + deviceId);
                    Firebase userpostRef = userRef.push();
                    userpostRef.setValue(pollId);

                    final String email = deviceId + "@firebase.com";
                    final String password = "firebasepass";

                    mFirebaseRef.createUser(email, password, new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            mFirebaseRef.authWithPassword(email, password, null);
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            mFirebaseRef.authWithPassword(email, password, null);
                        }
                    });

                    Intent intentHome = new Intent(".Home");
                    startActivity(intentHome);
                }
                break;

            case (R.id.bAddAnswer):
                if (answers > 5) {
                    bAddAnswer.setVisibility(View.INVISIBLE);
                } else {
                    answers++;
                }
                if (answers == 3) {
                    bRemoveAnswer.setVisibility(View.VISIBLE);
                    etAnswer3.setVisibility(View.VISIBLE);
                    etAnswer3.setHintTextColor(Color.WHITE);
                    DisplayMetrics displayMetrics = getApplicationContext()
                            .getResources().getDisplayMetrics();
                    int px = Math
                            .round(140 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, px);
                    int px2 = Math
                            .round(10 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    lp.setMargins(0, 0, 0, px2);
                    linScroll.setLayoutParams(lp);
                }
                if (answers == 4) {
                    bRemoveAnswer.setVisibility(View.VISIBLE);
                    etAnswer4.setVisibility(View.VISIBLE);
                    etAnswer4.setHintTextColor(Color.WHITE);
                    DisplayMetrics displayMetrics = getApplicationContext()
                            .getResources().getDisplayMetrics();
                    int px = Math
                            .round(190 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, px);
                    int px2 = Math
                            .round(10 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    lp.setMargins(0, 0, 0, px2);
                    linScroll.setLayoutParams(lp);
                } else if (answers == 5) {
                    etAnswer5.setVisibility(View.VISIBLE);
                    etAnswer5.setHintTextColor(Color.WHITE);
                    DisplayMetrics displayMetrics = getApplicationContext()
                            .getResources().getDisplayMetrics();
                    int px = Math
                            .round(240 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, px);
                    int px2 = Math
                            .round(10 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    lp.setMargins(0, 0, 0, px2);
                    linScroll.setLayoutParams(lp);
                    bAddAnswer.setVisibility(View.INVISIBLE);
                }
                break;

            case (R.id.bRemoveAnswer):
                if (answers == 3) {
                    answers--;
                    etAnswer3.setVisibility(View.INVISIBLE);
                    etAnswer3.setText("");
                    DisplayMetrics displayMetrics = getApplicationContext()
                            .getResources().getDisplayMetrics();
                    int px = Math
                            .round(90 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, px);
                    int px2 = Math
                            .round(10 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    lp.setMargins(0, 0, 0, px2);
                    linScroll.setLayoutParams(lp);
                    bRemoveAnswer.setVisibility(View.INVISIBLE);
                } else if (answers == 4) {
                    answers--;
                    etAnswer4.setVisibility(View.INVISIBLE);
                    etAnswer4.setText("");
                    DisplayMetrics displayMetrics = getApplicationContext()
                            .getResources().getDisplayMetrics();
                    int px = Math
                            .round(140 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, px);
                    int px2 = Math
                            .round(10 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    lp.setMargins(0, 0, 0, px2);
                    linScroll.setLayoutParams(lp);
                } else if (answers == 5) {
                    answers--;
                    etAnswer5.setVisibility(View.INVISIBLE);
                    etAnswer5.setText("");
                    DisplayMetrics displayMetrics = getApplicationContext()
                            .getResources().getDisplayMetrics();
                    int px = Math
                            .round(190 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, px);
                    int px2 = Math
                            .round(10 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    lp.setMargins(0, 0, 0, px2);
                    linScroll.setLayoutParams(lp);
                    bAddAnswer.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private boolean errorCheck() {
        boolean allOk = false;
        ArrayList<EditText> allEditTexts = new ArrayList<>();
        allEditTexts.add(etQuestion);
        allEditTexts.add(etAnswer1);
        allEditTexts.add(etAnswer2);
        if (etAnswer3.isShown()) {
            allEditTexts.add(etAnswer3);
        }
        if (etAnswer4.isShown()) {
            allEditTexts.add(etAnswer4);
        }
        if (etAnswer5.isShown()) {
            allEditTexts.add(etAnswer5);
        }
        if (etPassword.isShown()) {
            allEditTexts.add(etPassword);
        }

        for (final EditText editTest : allEditTexts) {
            if (editTest.getText().toString().trim().equals("")) {
                editTest.setText("");
            }
        }

        if (etQuestion.getText().toString().trim().equals("")
                || etAnswer1.getText().toString().trim().equals("")
                || etAnswer2.getText().toString().trim().equals("")
                || etAnswer3.isShown()
                && etAnswer3.getText().toString().trim().equals("")
                || etAnswer4.isShown()
                && etAnswer4.getText().toString().trim().equals("")
                || etAnswer5.isShown()
                && etAnswer5.getText().toString().trim().equals("")
                || cbPassword.isChecked()
                && etPassword.getText().toString().equals("")) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Fields with red text have not been filled out. Please do so to submit the poll.");
            alertDialogBuilder.setPositiveButton(R.string.positive_button,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            etQuestion.setHintTextColor(Color.RED);
                            etAnswer1.setHintTextColor(Color.RED);
                            etAnswer2.setHintTextColor(Color.RED);
                            etAnswer3.setHintTextColor(Color.RED);
                            etAnswer4.setHintTextColor(Color.RED);
                            etAnswer5.setHintTextColor(Color.RED);
                            etPassword.setHintTextColor(Color.RED);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (etQuestion.getText().toString().equals(etAnswer1.getText().toString())
                || etQuestion.getText().toString().equals(etAnswer2.getText().toString())
                || etQuestion.getText().toString().equals(etAnswer3.getText().toString())
                || etQuestion.getText().toString().equals(etAnswer4.getText().toString())
                || etQuestion.getText().toString().equals(etAnswer5.getText().toString())) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("An answer must not be the same as the question. Please change to submit the poll");
            alertDialogBuilder.setPositiveButton(R.string.positive_button,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (etAnswer1.getText().toString()
                .equals(etAnswer2.getText().toString())
                || etAnswer3.isShown() && etAnswer1.getText().toString().equals(etAnswer3.getText().toString())
                || etAnswer4.isShown() && etAnswer1.getText().toString().equals(etAnswer4.getText().toString())
                || etAnswer5.isShown() && etAnswer1.getText().toString().equals(etAnswer5.getText().toString())
                || etAnswer3.isShown() && etAnswer2.getText().toString().equals(etAnswer3.getText().toString())
                || etAnswer4.isShown() && etAnswer2.getText().toString().equals(etAnswer4.getText().toString())
                || etAnswer5.isShown() && etAnswer2.getText().toString().equals(etAnswer5.getText().toString())
                || etAnswer4.isShown() && etAnswer3.getText().toString().equals(etAnswer4.getText().toString())
                || etAnswer5.isShown() && etAnswer3.getText().toString().equals(etAnswer5.getText().toString())
                || etAnswer5.isShown() && etAnswer4.getText().toString().equals(etAnswer5.getText().toString())) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("All answers must be different from one another. Please change to submit the poll.");
            alertDialogBuilder.setPositiveButton(R.string.positive_button,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (etQuestion.getText().toString().trim().length() > 65) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Questions can only have a maximum of 65 characters. Please change to submit the poll.");
            alertDialogBuilder.setPositiveButton(R.string.positive_button,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (etAnswer1.getText().toString().trim().length() > 25
                || etAnswer2.getText().toString().trim().length() > 25
                || etAnswer3.isShown() && etAnswer3.getText().toString().trim().length() > 25
                || etAnswer4.isShown() && etAnswer4.getText().toString().trim().length() > 25
                || etAnswer5.isShown() && etAnswer5.getText().toString().trim().length() > 25) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Answers can only have a maximum of 25 characters. Please change to submit the poll.");
            alertDialogBuilder.setPositiveButton(R.string.positive_button,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (spinCategory.getSelectedItem().equals("Select Category")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Please select a cateogry for the poll.");
            alertDialogBuilder.setPositiveButton(R.string.positive_button,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            allOk = true;
        }
        return allOk;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
