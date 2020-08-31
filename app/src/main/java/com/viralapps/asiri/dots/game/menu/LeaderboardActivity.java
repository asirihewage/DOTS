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
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.viralapps.asiri.dots.game.R;
import com.viralapps.asiri.dots.game.UserListAdapter;
import com.viralapps.asiri.dots.game.UserSettings;

import java.util.Objects;

public class LeaderboardActivity extends Activity {
    private Context mContext;
    private View mRootView;
    private ImageView mLogo;
    private Button mPlayGameSingle;
    private Button leaderboard;
    private Button mUnlockMoreStuff;
    private Button mCheckMeOut;
    private TextView mFastestScore;
    private TextView mFastestScore1;
    private TextView mFastestScore2;
    private TextView mFastestScore3;
    private ImageButton mSound;
    private ImageButton mVibrate;
    private ImageButton mNightMode;
    private int[] mScore1Set;
    private int[] mScore2Set;
    private int[] mScore3Set;
    private boolean mIsGraphLaunch;
    private boolean mIsSoundOn;
    private boolean mIsVibrateOn;
    private boolean mIsNightMode;
    private int mTextColor;
    private SharedPreferences mUserSettings;
    private DatabaseReference devicesRef;
    private String deviceID;
    private TextView usernameTxt;
    private ListView userlistView;
    private String username;
    private String dotsPerSecond;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_activity);
        mContext = this;

        AdView mAdView = new AdView(this);
        mAdView.setAdUnitId("ca-app-pub-1300983540101573/7550736743");
        mAdView.setAdSize(AdSize.BANNER);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        devicesRef = database.getReference("devices");

        ListView list;

//        // Read from the database
//        devicesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
////                String value = dataSnapshot.getValue(String.class);
////                Log.d(TAG, "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Failed to read value
////                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });

        String[] maintitle ={
                "Title 1","Title 2",
                "Title 3","Title 4",
                "Title 5","Title 1","Title 2",
                "Title 3","Title 4",
                "Title 5",
        };

        String[] subtitle ={
                "Sub Title 1","Sub Title 2",
                "Sub Title 3","Sub Title 4",
                "Sub Title 5","Sub Title 1","Sub Title 2",
                "Sub Title 3","Sub Title 4",
                "Sub Title 5",
        };

        Integer[] imgid={
                R.drawable.logo,R.drawable.ic_dot_sprite_blue,
                R.drawable.ic_dot_sprite_blue,R.drawable.ic_dot_sprite_blue,
                R.drawable.ic_dot_sprite_blue,R.drawable.ic_dot_sprite_blue,
                R.drawable.ic_dot_sprite_blue,
                R.drawable.ic_dot_sprite_blue,R.drawable.ic_dot_sprite_blue,
                R.drawable.ic_dot_sprite_blue,
        };

        UserListAdapter adapter=new UserListAdapter(this, maintitle, subtitle,imgid);
        list=(ListView)findViewById(R.id.userlistView);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // TODO Auto-generated method stub
                if(position == 0) {
                    //code specific to first list item
                    Toast.makeText(getApplicationContext(),"Place Your First Option Code",Toast.LENGTH_SHORT).show();
                }
            }
        });

        enableImmersiveMode();

        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mFastestScore = findViewById(R.id.fastest_score);
        usernameTxt = findViewById(R.id.username);
        userlistView = findViewById(R.id.userlistView);

        mTextColor = getColor(R.color.primaryTextColor);

        UserSettings.startMenuMusic(this);

        mLogo = findViewById(R.id.logo);

        mUserSettings = this.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        mIsSoundOn = mUserSettings.getBoolean("sound_on", true);
        UserSettings.setSoundEnable(mIsSoundOn);
        mIsVibrateOn = mUserSettings.getBoolean("vibrate_on", true);
        mIsNightMode = mUserSettings.getBoolean("night_mode", false);
        deviceID = mUserSettings.getString("deviceID", "");
        username = mUserSettings.getString("name", "");
        dotsPerSecond = mUserSettings.getString("dotsPerSecond", "0");
        usernameTxt.setText(username);

        Snackbar.make(userlistView,"Hi, " + username, Snackbar.LENGTH_SHORT)
                .show();

        //----- set user score
        // load image
        String imageUri = mUserSettings.getString("avatar", "");
        ImageView useravatar = findViewById(R.id.useravatar);
        Picasso.get().load(imageUri).into(useravatar);
        TextView highesrmark = findViewById(R.id.highestMark);
        highesrmark.setText(dotsPerSecond + " Dots per second.");



        /*
         * Create a DatabaseReference to the data; works with standard DatabaseReference methods
         * like limitToLast() and etc.
         */
        devicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String dotsPerSecond = Objects.requireNonNull(ds.child("dotsPerSecond").getValue()).toString();
                    //etc
                    Log.w("dfdgggggggggggggggggg", ds.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
