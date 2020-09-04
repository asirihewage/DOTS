package com.viralapps.asiri.dots.game;


import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viralapps.asiri.dots.game.menu.MainMenuActivity;
import com.viralapps.asiri.dots.game.menu.SingleGraphActivity;

import java.text.DecimalFormat;

public class UserListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] maintitle;
    private final String[] subtitle;
    private final String[] imgid;
    private final String[] scoreSet;
    private String TAG;

    public UserListAdapter(Activity context, String[] maintitle,String[] subtitle, String[] imgid, String[] scoreSetArray) {
        super(context, R.layout.leaderboard_activity, maintitle);
        this.context=context;
        this.maintitle=maintitle;
        this.subtitle=subtitle;
        this.imgid=imgid;
        this.scoreSet=scoreSetArray;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {

        TAG = "LeaderBoardActivity";

        SharedPreferences mUserSettings;
        mUserSettings = this.context.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mUserSettings.edit();

        boolean mIsNightMode = mUserSettings.getBoolean("night_mode", false);
        String username = mUserSettings.getString("name", "");

        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.user, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.username);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.useravatar);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.highestMark);
        Button rankbtn = (Button) rowView.findViewById(R.id.rankbtn);
        TextView rank = (TextView) this.context.findViewById(R.id.highestMarkUser);
        View usercard = rowView.findViewById(R.id.usercard);

        DecimalFormat df = new DecimalFormat("###.##");

        titleText.setText(maintitle[position]);
        subtitleText.setText(df.format(Double.parseDouble(subtitle[position])) + " Dots per second.");
        rankbtn.setText(String.valueOf(position + 1));
        Picasso.get().load(imgid[position]).into(imageView);

        if(maintitle[position].equals(username)){
            rank.setText("You ranked #" + (position + 1));
            editor.putString("rank", String.valueOf(position));
            editor.apply();
        }

        if (mIsNightMode) {
            titleText.setTextColor(Color.WHITE);
            rankbtn.setTextColor(Color.WHITE);
            subtitleText.setTextColor(Color.WHITE);
            usercard.setBackgroundColor(Color.BLACK);
            rankbtn.setBackgroundColor(Color.DKGRAY);
            if(maintitle[position].equals(username)){
                usercard.setBackgroundColor(Color.DKGRAY);
            }
        } else {
            titleText.setTextColor(Color.BLACK);
            subtitleText.setTextColor(Color.BLACK);
            usercard.setBackgroundColor(Color.WHITE);
            if(maintitle[position].equals(username)){
                usercard.setBackgroundColor(Color.LTGRAY);
            }
        }

//        rankbtn.setOnClickListener(view2 -> {
//            Log.e(TAG,"clicked: "+ rankbtn.getText());
//            UserSettings.startClickSound(this.context);
//            Intent dataGraph = new Intent(context, SingleGraphActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            dataGraph.putExtra("data", scoreSet[position]);
//            context.startActivity(dataGraph);
//        });

        return rowView;

    };
}