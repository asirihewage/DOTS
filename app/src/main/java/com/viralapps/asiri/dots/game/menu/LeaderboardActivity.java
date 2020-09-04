/*
 * Copyright (C) 2020 Asiri H.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.viralapps.asiri.dots.game.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.viralapps.asiri.dots.game.R;
import com.viralapps.asiri.dots.game.User;
import com.viralapps.asiri.dots.game.UserListAdapter;
import com.viralapps.asiri.dots.game.UserSettings;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends Activity {
    private Context mContext;
    private View leaderboardviewRootView;
    private View leaderboardview;
    private TextView mFastestScore;
    private boolean mIsGraphLaunch;
    private boolean mIsSoundOn;
    private boolean mIsNightMode;
    private SharedPreferences mUserSettings;
    private DatabaseReference usersRef;
    private TextView usernameTxt;
    private ListView userlistView;
    private String username;
    private String dotsPerSecond;
    private ListView list;
    TextView highesrmark;
    ProgressBar progressBar;
    private String TAG;
    UserListAdapter adapter;

    @SuppressLint({"CutPasteId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_activity);
        mContext = this;
        TAG = "LeaderBoardActivity";

//        AdView mAdView = new AdView(this);
//        mAdView.setAdUnitId("ca-app-pub-1300983540101573/7550736743");
//        mAdView.setAdSize(AdSize.BANNER);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        enableImmersiveMode();

        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        usernameTxt = findViewById(R.id.username);
        userlistView = findViewById(R.id.userlistView);

        UserSettings.startMenuMusic(this);

        mUserSettings = this.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        mIsSoundOn = mUserSettings.getBoolean("sound_on", true);
        UserSettings.setSoundEnable(mIsSoundOn);
        mIsNightMode = mUserSettings.getBoolean("night_mode", false);
        username = mUserSettings.getString("name", "");
        dotsPerSecond = mUserSettings.getString("dotsPerSecond", "0.0");
        usernameTxt.setText(username);

        updateColors();

        Snackbar.make(userlistView,"Hi, " + username, Snackbar.LENGTH_SHORT)
                .show();

        //----- set user score
        // load image
        String imageUri = mUserSettings.getString("avatar", "");
        ImageView useravatar = findViewById(R.id.useravatar);
        Picasso.get().load(imageUri).into(useravatar);
        highesrmark = findViewById(R.id.highestMark);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        /*
         * create user list
         */
        ArrayList <User> userArr = new ArrayList<>();

        usersRef.orderByChild("dotsPerSecond").addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, String prevChildKey) {
//                    User newUser = snapshot.getValue(User.class);
                    String avatar = snapshot.child("avatar").getValue().toString();
                    String deviceID = snapshot.child("deviceID").getValue().toString();
                    String dps = snapshot.child("dotsPerSecond").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    String scoreSet = snapshot.child("scoreSet").getValue().toString();
                    String totalTime = snapshot.child("totalTime").getValue().toString();
                    String userID = snapshot.child("userID").getValue().toString();
                    String name = snapshot.child("name").getValue().toString();
                    String timestamp = snapshot.child("timestamp").getValue().toString();

//                    Log.e(TAG,avatar);
                    User usr = new User(avatar, deviceID, dps, email, name, scoreSet, timestamp, totalTime, userID);
                    userArr.add(usr);
                    setList(userArr);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                    String deviceID = dataSnapshot.child("deviceID").getValue().toString();
                    String dps = dataSnapshot.child("dotsPerSecond").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String scoreSet = dataSnapshot.child("scoreSet").getValue().toString();
                    String totalTime = dataSnapshot.child("totalTime").getValue().toString();
                    String userID = dataSnapshot.child("userID").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String timestamp = dataSnapshot.child("timestamp").getValue().toString();

                    User usr = new User(avatar, deviceID, dps, email, name, scoreSet, timestamp, totalTime, userID);
                    int u = userArr.indexOf(usr);
                    Log.e(TAG, usr.toString());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                    String deviceID = dataSnapshot.child("deviceID").getValue().toString();
                    String dps = dataSnapshot.child("dotsPerSecond").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String scoreSet = dataSnapshot.child("scoreSet").getValue().toString();
                    String totalTime = dataSnapshot.child("totalTime").getValue().toString();
                    String userID = dataSnapshot.child("userID").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String timestamp = dataSnapshot.child("timestamp").getValue().toString();

                    User usr = new User(avatar, deviceID, dps, email, name, scoreSet, timestamp, totalTime, userID);
                    userArr.remove(usr);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });

    }

    private void setList(ArrayList<User> userList){
        list=(ListView)findViewById(R.id.userlistView);
        List<String> name = new ArrayList<>();
        List<String> highScore = new ArrayList<>();
        List<String> imgid = new ArrayList<>();
        List<String> scoreSet = new ArrayList<>();

        for(User user: userList) {
            name.add(user.getName());
            highScore.add(user.gethighScore());
            imgid.add(user.getAvatar());
            scoreSet.add(user.getScoreSet());
        }

        String[] nameArray = new String[ name.size() ];
        name.toArray( nameArray );
        String[] highScoreArray = new String[ highScore.size() ];
        highScore.toArray( highScoreArray );
        String[] scoreSetArray = new String[ scoreSet.size() ];
        scoreSet.toArray( scoreSetArray );
        String[] imgidArray = new String[ imgid.size() ];
        imgid.toArray( imgidArray );

        adapter = new UserListAdapter(this, nameArray, highScoreArray, imgidArray, scoreSetArray);
        list.setAdapter(adapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mIsGraphLaunch) {
            UserSettings.pauseMenuMusic();
        }
        mIsGraphLaunch = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserSettings.startMenuMusic(mContext);
    }

    private void enableImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void updateColors() {
        leaderboardview = findViewById(R.id.userlistView);
        View usercard = findViewById(R.id.usercard);
        TextView userName = findViewById(R.id.username);
        TextView userscore = findViewById(R.id.highestMarkUser);

        leaderboardviewRootView = leaderboardview.getRootView();

        if (mIsNightMode) {
            userscore.setTextColor(Color.BLACK);
            userName.setTextColor(Color.BLACK);
            usercard.setBackgroundColor(Color.WHITE);
            leaderboardviewRootView.setBackgroundColor(Color.BLACK);
        } else {
            userscore.setTextColor(Color.WHITE);
            userName.setTextColor(Color.WHITE);
            usercard.setBackgroundColor(Color.BLACK);
            leaderboardviewRootView.setBackgroundColor(Color.WHITE);

        }
    }

}
