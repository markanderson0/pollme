package com.example.mark.pollmeads;

import android.app.AlertDialog;
import android.app.ListActivity;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocationActivity extends ListActivity implements Searcher {
    private Firebase mFirebaseRef;
    private Map<String, String> mapQuestions = new HashMap<>();
    private Map<String, String> mapPrivacy = new HashMap<>();
    private Map<String, String> mapPassword = new HashMap<>();
    private Map<String, String> mapCategory = new HashMap<>();
    private Map<String, String> mapPollCreator = new HashMap<>();
    private Map<String, ArrayList<String>> mapPollHistory = new HashMap<>();
    private ArrayList<String> listQuestions = new ArrayList<>();
    private ArrayList<String> listPrivacy = new ArrayList<>();
    private ArrayList<String> listPassword = new ArrayList<>();
    private ArrayList<String> listPolls = new ArrayList<>();
    private ArrayList<String> listLongitude = new ArrayList<>();
    private ArrayList<String> listLatitude = new ArrayList<>();
    private ArrayList<String> listMyPolls = new ArrayList<>();

    private EditText etSearch;
    private Spinner spinFilter;
    private ListView list;
    private Typeface typeface;

    private LocationManager locationManager = null;
    private String provider, longitude, latitude;
    private Location location;

    private static final String REPO_URL = "XXXXX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        etSearch = (EditText) findViewById(R.id.etSearch);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto-Thin.ttf");
        etSearch.setTypeface(typeface);
        spinFilter = (Spinner) findViewById(R.id.spinFilter);
        setSpinFilter();

        Firebase.setAndroidContext(this);
        if (isLocationEnabled(getApplicationContext())) {    //Check if location services are on
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Sorry. Your location could not be determined. Please try again later."); //if not inform the user
                alertDialogBuilder.setPositiveButton(R.string.positive_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent positveActivity = new Intent(".Home");
                                startActivity(positveActivity);
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                onLocationChanged(location);
                loadPoll();
            }
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("To view local polls you must enable location services on your device."); //if not inform the user
            alertDialogBuilder.setPositiveButton(R.string.positive_button,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent positveActivity = new Intent(".Home");
                            startActivity(positveActivity);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void loadPoll() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String deviceid = tm.getDeviceId();
        mFirebaseRef = new Firebase(REPO_URL);
        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " history");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Log.i("postshapshot", postSnapshot.getKey());
                    if (postSnapshot.getKey().equals("history")) {
                        for (DataSnapshot postPostsnapshot : postSnapshot.getChildren()) {
                            ArrayList<String> listPollHistory = new ArrayList<>();
                            for (DataSnapshot childSnapshot : postPostsnapshot.getChildren()) {
                                listPollHistory.add(childSnapshot.getValue().toString());
                            }
                            mapPollHistory.put(postPostsnapshot.getKey(), listPollHistory);
                        }
                    }
                    if (postSnapshot.getKey().equals("mypolls")) {
                        for (DataSnapshot postPostsnapshot : postSnapshot.getChildren()) {
                            if (postPostsnapshot.getKey().toString().equals(deviceid)) {
                                for (DataSnapshot childSnapshot : postPostsnapshot.getChildren()) {
                                    listMyPolls.add(childSnapshot.getValue().toString());
                                }
                                break;
                            }
                        }
                    }
                    if (postSnapshot.getKey().equals("polls")) {
                        for (DataSnapshot postPostsnapshot : postSnapshot.getChildren()) {
                            CreatePoll post = postPostsnapshot.getValue(CreatePoll.class);
                            if (!post.getLatitude().equals("Location not available")
                                    && !mapPollHistory.get(postPostsnapshot.getKey()).contains(deviceid)
                                    && !listMyPolls.contains(postPostsnapshot.getKey())) {
                                if (getDistance(post) <= 50) {
                                    mapQuestions.put(postPostsnapshot.getKey(), post.getQuestion());
                                    mapPrivacy.put(postPostsnapshot.getKey(), post.getPrivacy());
                                    mapPassword.put(postPostsnapshot.getKey(), post.getPassword());
                                    mapPollCreator.put(postPostsnapshot.getKey(), post.getDeviceId());
                                    mapCategory.put(postPostsnapshot.getKey(), post.getCategory());
                                    listQuestions.add(post.getQuestion());
                                    listPrivacy.add(post.getPrivacy());
                                    listPassword.add(post.getPassword());
                                    listPolls.add(postPostsnapshot.getKey());
                                    listLatitude.add(post.getLatitude());
                                    listLongitude.add(post.getLongitude());
                                }
                            }
                        }
                    }
                }
                fillList();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProvider;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProvider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProvider);
        }
    }

    private void onLocationChanged(Location location) {
        double lat = (location.getLatitude());
        double lng = (location.getLongitude());
        longitude = String.valueOf(lng);
        latitude = String.valueOf(lat);
    }

    private float distanceBetween(double lat1, double long1, double lat2, double long2) {
        float distance = 0;
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(long1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(long2);
        distance = loc1.distanceTo(loc2);
        return distance;
    }

    private float getDistance(CreatePoll post) {
        double templong = Double.valueOf(post.getLongitude());
        double templat = Double.valueOf(post.getLatitude());
        double lng = Double.valueOf(longitude);
        double lat = Double.valueOf(latitude);
        float distance = distanceBetween(templat, templong, lat, lng);
        return distance;
    }

    public void fillList() {
        /**
         * Check if an item in the list has been selected and open up the respective poll.
         */
        ArrayList<String> tempQuestions = new ArrayList<>();
        ArrayList<String> tempPrivacy = new ArrayList<>();
        ArrayList<String> tempPassword = new ArrayList<>();
        ArrayList<String> tempPolls = new ArrayList<>();
        tempQuestions.clear();
        tempPrivacy.clear();
        for (String key : mapQuestions.keySet()) {
            tempQuestions.add(mapQuestions.get(key));
            tempPrivacy.add(mapPrivacy.get(key));
            tempPassword.add(mapPassword.get(key));
            tempPolls.add(key);
        }
        final CustomAdapter ca = new CustomAdapter(getApplicationContext(), tempQuestions, tempPrivacy);
        list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(ca);
        listPrivacy = tempPrivacy;
        listPassword = tempPassword;
        listQuestions = tempQuestions;
        listPolls = tempPolls;
        textSearch();
        filterCategory();
        itemSelected();
    }

    public void textSearch() {
        /**
         * Check if a seach term has been entered and update the poll list accordingly.
         */
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                searchAndFilter();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void filterCategory() {
        spinFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                searchAndFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void searchAndFilter() {
        CustomAdapter temp;
        listQuestions.clear();
        listPassword.clear();
        listPrivacy.clear();
        listPolls.clear();
        for (String key : mapQuestions.keySet()) {
            if (etSearch.getText().length() <= mapQuestions.get(key).length() && mapQuestions.get(key).toLowerCase().contains(etSearch.getText().toString().toLowerCase())) {
                if (mapCategory.get(key).equals(spinFilter.getSelectedItem())) {
                    listQuestions.add(mapQuestions.get(key));
                    listPrivacy.add(mapPrivacy.get(key));
                    listPassword.add(mapPassword.get(key));
                    listPolls.add(key);
                } else if (spinFilter.getSelectedItem().equals("Select Category")) {
                    listQuestions.add(mapQuestions.get(key));
                    listPrivacy.add(mapPrivacy.get(key));
                    listPassword.add(mapPassword.get(key));
                    listPolls.add(key);
                }
            }
        }
        temp = new CustomAdapter(getApplicationContext(), listQuestions, listPrivacy);
        list.setAdapter(temp);
    }

    public void itemSelected() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View viewClicked, final int position, long id) {
                final int listPosition = (int) id;
                if (listPrivacy.get(listPosition).equals("true")) {
                    final EditText input = new EditText(LocationActivity.this);
                    new AlertDialog.Builder(LocationActivity.this).setTitle("Poll is private.")
                            .setMessage("Enter Password")
                            .setView(input)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Editable value = input.getText();
                            if (listPassword.get(listPosition).equals(value.toString())) {
                                {
                                    Intent intent = new Intent(".VoteActivity");
                                    intent.putExtra("pollId", listPolls.get(listPosition));
                                    intent.putExtra("tvQuestion", listQuestions.get(listPosition));
                                    intent.putExtra("from", "location");
                                    startActivity(intent);
                                }
                            }
                        }
                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            }).show();
                } else {
                    Intent intent = new Intent(".VoteActivity");
                    intent.putExtra("pollId", listPolls.get(listPosition));
                    intent.putExtra("tvQuestion", listQuestions.get(listPosition));
                    intent.putExtra("from", "location");
                    startActivity(intent);
                }
            }
        });
    }

    public void setSpinFilter() {
        String[] categories = {"Select Category", "Misc", "Business", "Electronics", "Entertainment", "Food", "Health & Fitness", "Sport"};
        ArrayAdapter<String> adapter_categories = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categories) {
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
        adapter_categories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFilter.setAdapter(adapter_categories);
    }

    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(".Home");
        startActivity(intentHome);
    }
}