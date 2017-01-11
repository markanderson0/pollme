package com.example.mark.pollmeads;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

class CustomAdapter extends ArrayAdapter<String> {
    private ArrayList<String> privacy;
    private ArrayList<String> questions;
    private HashMap<String, String> questionPrivacy;

    CustomAdapter(Context context, ArrayList<String> questions, ArrayList<String> privacy){
        super(context, R.layout.listitem, questions);
        this.questions = questions;
        this.privacy = privacy;
        questionPrivacy = new HashMap<>();
        for(int i = 0; i < questions.size(); i++){
            questionPrivacy.put(questions.get(i), privacy.get(i));
        }
    }

    @SuppressWarnings("static-access")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater listInflater = LayoutInflater.from(getContext());
        View customView = listInflater.inflate(R.layout.listitem, parent, false);
        String listItem = getItem(position);

        TextView tvSelect = (TextView) customView.findViewById(R.id.tvSelect);
        ImageView imagePassword = (ImageView) customView.findViewById(R.id.imagePassword);
        imagePassword.setVisibility(convertView.INVISIBLE);
        tvSelect.setText(listItem);
        tvSelect.setTextColor(Color.BLACK);
        if (questionPrivacy.get(listItem).equals("true")) {
            imagePassword.setVisibility(convertView.VISIBLE);
            imagePassword.setImageResource(R.drawable.ic_lock_idle_lock);
        }
        else{
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int px = Math.round(25 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.setMargins(-px, 0, 0, 0);
            tvSelect.setLayoutParams(lp);
        }

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),"Roboto-Thin.ttf");
        tvSelect.setTypeface(typeface);
        return customView;
    }
}
