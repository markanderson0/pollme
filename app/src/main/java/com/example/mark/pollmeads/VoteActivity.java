package com.example.mark.pollmeads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

public class VoteActivity extends Activity implements View.OnClickListener {
    private Firebase mFirebaseRef;
    private TextView tvQuestion;
    private String pollId, deviceId, question, from, answer1, answer2, answer3, answer4, answer5;
    private RadioGroup rgAnswerGroup;
    private RadioButton rbAnswer1, rbAnswer2, rbAnswer3, rbAnswer4, rbAnswer5;
    private Button bSubmit;
    private Typeface typeface;

    private static final String REPO_URL = "XXXXX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote);
        Firebase.setAndroidContext(this);
        Bundle extras = getIntent().getExtras();
        pollId = extras.getString("pollId");
        question = extras.getString("tvQuestion");
        from = extras.getString("from");
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tm.getDeviceId();
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto-Thin.ttf");

        mFirebaseRef = new Firebase(REPO_URL + "polls/"+pollId);
        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                CreatePoll post = snapshot.getValue(CreatePoll.class);
                answer1 = post.getAnswer1();
                answer2 = post.getAnswer2();
                answer3 = post.getAnswer3();
                answer4 = post.getAnswer4();
                answer5 = post.getAnswer5();
                initialiseView();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
    @Override
    public void onClick(View v) {
        updateResults();
        updateHistory();
        Intent intent = new Intent(".ResultsActivity");
        intent.putExtra("pollId", pollId);
        intent.putExtra("tvQuestion", question);
        intent.putExtra("tvAnswer1", answer1);
        intent.putExtra("tvAnswer2", answer2);
        intent.putExtra("tvAnswer3", answer3);
        intent.putExtra("tvAnswer4", answer4);
        intent.putExtra("tvAnswer5", answer5);
        intent.putExtra("from", "vote");
        startActivity(intent);
    }

    private void updateHistory(){
        Firebase historyRef = new Firebase("https://brilliant-inferno-4839.firebaseio.com/history/"+pollId);
        Firebase postRef = historyRef.push();
        postRef.setValue(deviceId);
    }

    private void updateResults(){
        String answer;
        int selectedId = rgAnswerGroup.getCheckedRadioButtonId();
        if(rbAnswer1.getId() == selectedId){
            answer = "result1";
        }
        else if(rbAnswer2.getId() == selectedId){
            answer = "result2";
        }
        else if(rbAnswer3.getId() == selectedId){
            answer = "result3";
        }
        else if(rbAnswer4.getId() == selectedId){
            answer = "result4";
        }
        else{
            answer = "result5";
        }
        Firebase voteRef = new Firebase("https://brilliant-inferno-4839.firebaseio.com/results/"+pollId+"/"+answer);
        voteRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                //This method will be called once with the results of the transaction.
            }
        });
    }

    private void initialiseView(){
        tvQuestion = (TextView) findViewById(R.id.tvQuestion);
        tvQuestion.setTypeface(typeface);

        rbAnswer1 = (RadioButton) findViewById(R.id.rbAnswer1);
        rbAnswer1.setTypeface(typeface);
        rbAnswer1.setChecked(true);

        rbAnswer2 = (RadioButton) findViewById(R.id.rbAnswer2);
        rbAnswer2.setTypeface(typeface);

        rbAnswer3 = (RadioButton) findViewById(R.id.rbAnswer3);
        rbAnswer3.setTypeface(typeface);
        rbAnswer3.setVisibility(View.INVISIBLE);

        rbAnswer4 = (RadioButton) findViewById(R.id.rbAnswer4);
        rbAnswer4.setTypeface(typeface);
        rbAnswer4.setVisibility(View.INVISIBLE);

        rbAnswer5 = (RadioButton) findViewById(R.id.rbAnswer5);
        rbAnswer5.setTypeface(typeface);
        rbAnswer5.setVisibility(View.INVISIBLE);

        rgAnswerGroup = (RadioGroup) findViewById(R.id.rgAnswerGroup);
        tvQuestion.setText(question);
        rbAnswer1.setText(answer1);
        rbAnswer2.setText(answer2);
        rbAnswer3.setText(answer3);
        rbAnswer4.setText(answer4);
        rbAnswer5.setText(answer5);

        if(!rbAnswer3.getText().equals("")){
            rbAnswer3.setVisibility(View.VISIBLE);
        }
        if(!rbAnswer4.getText().equals("")){
            rbAnswer4.setVisibility(View.VISIBLE);
        }
        if(!rbAnswer5.getText().equals("")){
            rbAnswer5.setVisibility(View.VISIBLE);
        }

        bSubmit = (Button) findViewById(R.id.bSumbitVote);
        bSubmit.setOnClickListener(this);
        bSubmit.setTypeface(typeface);
        bSubmit.setVisibility(View.VISIBLE);
        rgAnswerGroup.setVisibility(View.VISIBLE);
        tvQuestion.setVisibility(View.VISIBLE);
    }
}
