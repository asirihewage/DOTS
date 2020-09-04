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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.viralapps.asiri.dots.game.DotsActivity;
import com.viralapps.asiri.dots.game.Grids;
import com.viralapps.asiri.dots.game.R;
import com.viralapps.asiri.dots.game.UserSettings;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class MainMenuActivity extends Activity {
    private static final String TAG = "MAIN ACTIVITY";
    private static final int RC_SIGN_IN = 0;
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
    private String userID;
    private int mTextColor;
    private SharedPreferences mUserSettings;
    DatabaseReference userRef;
    private String deviceID;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private String username;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
        mContext = this;

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        enableImmersiveMode();

        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mFastestScore = findViewById(R.id.fastest_score);
        mFastestScore1 = findViewById(R.id.fastest_score_1);
        mFastestScore2 = findViewById(R.id.fastest_score_2);
        mFastestScore3 = findViewById(R.id.fastest_score_3);

        mTextColor = getColor(R.color.primaryTextColor);

        UserSettings.startMenuMusic(this);

        mLogo = findViewById(R.id.logo);

        mUserSettings = this.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        mIsSoundOn = mUserSettings.getBoolean("sound_on", true);
        UserSettings.setSoundEnable(mIsSoundOn);
        mIsVibrateOn = mUserSettings.getBoolean("vibrate_on", true);
        mIsNightMode = mUserSettings.getBoolean("night_mode", false);
        deviceID = mUserSettings.getString("deviceID", "");
        userID = mUserSettings.getString("userID", "");
        username = mUserSettings.getString("name", "");

        SharedPreferences.Editor editor = mUserSettings.edit();
        editor.putBoolean("ads_enabled", true);
        editor.apply();

        leaderboard = findViewById(R.id.leaderboard);

        if(checkUser()){
            Snackbar.make(leaderboard,"Hi, " + username + ". See the latest rank!", Snackbar.LENGTH_SHORT)
                    .show();
        }else{
            Snackbar.make(leaderboard,"Please Log in to access the Leader Board.", Snackbar.LENGTH_SHORT)
                    .show();
            leaderboard.setText("Log into LeaderBoard");
        }

        mPlayGameSingle = findViewById(R.id.play_game_single);
        mPlayGameSingle.setOnClickListener(view -> {
            Intent singlePlayerGame = new Intent(mContext, DotsActivity.class);
            singlePlayerGame.putExtra("game_mode", 1);
            UserSettings.startClickSound(mContext);
            startActivity(singlePlayerGame);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        mRootView = mPlayGameSingle.getRootView();

        mUnlockMoreStuff = findViewById(R.id.unlock_more_stuff);
        mUnlockMoreStuff.setOnClickListener(view -> {
            Intent twoPlayerGame = new Intent(mContext, DotsActivity.class);
            twoPlayerGame.putExtra("game_mode", 2);
            UserSettings.startClickSound(mContext);
            startActivity(twoPlayerGame);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        leaderboard.setOnClickListener(view -> {
            if(checkUser()){
                Intent twoPlayerGame = new Intent(mContext, LeaderboardActivity.class);
                twoPlayerGame.putExtra("game_mode", 2);
                UserSettings.startClickSound(mContext);
                startActivity(twoPlayerGame);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }else{
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        leaderboard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                signOut();
                Log.w(TAG, "sign out");
                return true;
            }
        });

        mCheckMeOut = findViewById(R.id.rate);
        mCheckMeOut.setOnClickListener(view -> {
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
            websiteIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.viralapps.asiri.dots.game"));
            UserSettings.startClickSound(mContext);
            startActivity(websiteIntent);
        });

        mFastestScore1.setOnClickListener(view -> {
            mIsGraphLaunch = true;
            UserSettings.startClickSound(mContext);
            Intent dataGraph = new Intent(mContext, SingleGraphActivity.class);
            dataGraph.putExtra("data", mScore1Set);
            startActivity(dataGraph);
        });

        mFastestScore2.setOnClickListener(view -> {
            mIsGraphLaunch = true;
            UserSettings.startClickSound(mContext);
            Intent dataGraph = new Intent(mContext, SingleGraphActivity.class);
            dataGraph.putExtra("data", mScore2Set);
            startActivity(dataGraph);
        });

        mFastestScore3.setOnClickListener(view -> {
            mIsGraphLaunch = true;
            UserSettings.startClickSound(mContext);
            Intent dataGraph = new Intent(mContext, SingleGraphActivity.class);
            dataGraph.putExtra("data", mScore3Set);
            startActivity(dataGraph);
        });

        mSound = findViewById(R.id.sound);
        mSound.setImageResource(mIsSoundOn ? R.drawable.ic_sound_on : R.drawable.ic_sound_off);
        mSound.setOnClickListener(v -> {
            mIsSoundOn = !mIsSoundOn;
            mSound.setImageResource(mIsSoundOn ? R.drawable.ic_sound_on : R.drawable.ic_sound_off);
            if (mIsSoundOn) {
                UserSettings.setSoundEnable(mIsSoundOn);
                UserSettings.startClickSound(mContext);
                UserSettings.startMenuMusic(mContext);
                Snackbar.make(leaderboard,"Background music turned On.", Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                UserSettings.pauseMenuMusic();
                UserSettings.setSoundEnable(mIsSoundOn);
                Snackbar.make(leaderboard,"Background music turned Off.", Snackbar.LENGTH_SHORT)
                        .show();
            }
            editor.putBoolean("sound_on", mIsSoundOn);
            editor.apply();
        });

        mVibrate = findViewById(R.id.vibrate);
        mVibrate.setImageResource(mIsVibrateOn ? R.drawable.ic_vibrate_on : R.drawable.ic_vibrate_off);
        mVibrate.setOnClickListener(v -> {
            mIsVibrateOn = !mIsVibrateOn;
            UserSettings.startClickSound(mContext);
            mVibrate.setImageResource(mIsVibrateOn ? R.drawable.ic_vibrate_on : R.drawable.ic_vibrate_off);
            if (mIsVibrateOn) {
                assert vb != null;
                vb.vibrate(15);
            }
            editor.putBoolean("vibrate_on", mIsVibrateOn);
            editor.apply();
        });

        mNightMode = findViewById(R.id.night_mode);
        mNightMode.setOnClickListener(v -> {
            mIsNightMode = !mIsNightMode;
            UserSettings.startClickSound(mContext);
            editor.putBoolean("night_mode", mIsNightMode);
            editor.apply();
            updateColors();
            if (mIsNightMode) {
                Snackbar.make(leaderboard, "Night Mode turned on", Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Snackbar.make(leaderboard, "Night Mode turned off.", Snackbar.LENGTH_SHORT)
                        .show();
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
        refreshScores();
        updateColors();
        UserSettings.startMenuMusic(mContext);
    }

    private void refreshScores() {
        SharedPreferences savedScores = this.getSharedPreferences("saved_scores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mUserSettings.edit();
        double score1 = savedScores.getInt("score1", 0) / 1000.0;
        deviceID = mUserSettings.getString("deviceID", "");

        //set unique id
        if(deviceID.equals("")){
            //Getting the current date
            Date date = new Date();
            //This method returns the time in millis
            long timeMilli = date.getTime();
            editor.putString("deviceID", Long.toString(timeMilli));
            editor.apply();
        }

        if (score1 == 0) {
            mFastestScore.setVisibility(View.GONE);
            mFastestScore1.setVisibility(View.GONE);
            mFastestScore2.setVisibility(View.GONE);
            mFastestScore3.setVisibility(View.GONE);
        } else {
            double rate1 = Grids.singleGridSize / score1;
            mFastestScore.setVisibility(View.VISIBLE);
            mFastestScore1.setVisibility(View.VISIBLE);
            String score1Text = "1. " + String.format(getString(R.string.fastest_score_show), score1, rate1);
            mFastestScore1.setText(score1Text);
            mScore1Set = convertStringToIntArray(savedScores.getString("score1set", ""));

            if(!userID.equals("")) {
                long tsLong = System.currentTimeMillis()/1000;
                String timestamp = Long.toString(tsLong);
                String userID = mUserSettings.getString("userID", "");

                //save score firebase
                Map<String, String> score = new HashMap<>();
                score.put("totalTime", Double.toString(score1));
                score.put("dotsPerSecond", Double.toString(rate1));
                score.put("scoreSet", savedScores.getString("score1set", "[]"));
                score.put("timestamp", timestamp);
                score.put("deviceID", deviceID);
                score.put("userID", mUserSettings.getString("userID", ""));
                score.put("name", mUserSettings.getString("name", ""));
                score.put("email", mUserSettings.getString("email", ""));
                score.put("avatar", mUserSettings.getString("avatar", ""));

                userRef.child(userID).setValue(score);
            }

            double score2 = savedScores.getInt("score2", 0) / 1000.0;
            if (score2 == 0) {
                mFastestScore2.setVisibility(View.GONE);
                mFastestScore3.setVisibility(View.GONE);
                mFastestScore.setText(R.string.fastest_score_text_single);
            } else {
                double rate2 = Grids.singleGridSize / score2;
                mFastestScore2.setVisibility(View.VISIBLE);
                mFastestScore.setText(R.string.fastest_score_text);
                String score2Text = "2. " + String.format(getString(R.string.fastest_score_show), score2, rate2);
                mFastestScore2.setText(score2Text);
                mScore2Set = convertStringToIntArray(savedScores.getString("score2set", ""));

                double score3 = savedScores.getInt("score3", 0) / 1000.0;
                if (score3 == 0) {
                    mFastestScore3.setVisibility(View.GONE);
                } else {
                    double rate3 = Grids.singleGridSize / score3;
                    mFastestScore3.setVisibility(View.VISIBLE);
                    String score3Text = "3. " + String.format(getString(R.string.fastest_score_show), score3, rate3);
                    mFastestScore3.setText(score3Text);
                    mScore3Set = convertStringToIntArray(savedScores.getString("score3set", ""));

                }
            }
        }
    }

    private int[] convertStringToIntArray(String str) {
        StringTokenizer st = new StringTokenizer(str, ",");
        int[] intArray = new int[Grids.singleGridSize];
        for (int i = 0; i < Grids.singleGridSize; i++) {
            intArray[i] = Integer.parseInt(st.nextToken());
        }
        return intArray;
    }

    private void updateColors() {
//        Drawable[] layers = new Drawable[2];
//        layers[0] = ContextCompat.getDrawable(this, R.drawable.logo_dot);
//        layers[1] = ContextCompat.getDrawable(this, R.drawable.logo_text);
        if (mIsNightMode) {
//            layers[1] = ContextCompat.getDrawable(this, R.drawable.logo_text_white);
            mPlayGameSingle.setTextColor(Color.WHITE);
            mUnlockMoreStuff.setTextColor(Color.WHITE);
            mCheckMeOut.setTextColor(Color.WHITE);
            leaderboard.setTextColor(Color.WHITE);
            mFastestScore.setTextColor(Color.WHITE);
            mFastestScore1.setTextColor(Color.WHITE);
            mFastestScore2.setTextColor(Color.WHITE);
            mFastestScore3.setTextColor(Color.WHITE);
            mSound.setColorFilter(Color.WHITE);
            mVibrate.setColorFilter(Color.WHITE);
            mNightMode.setColorFilter(Color.WHITE);
            mRootView.setBackgroundColor(Color.BLACK);
            mLogo.setImageResource(R.drawable.logo_white);
        } else {
//            layers[1] = ContextCompat.getDrawable(this, R.drawable.logo_text);
            mPlayGameSingle.setTextColor(Color.BLACK);
            mUnlockMoreStuff.setTextColor(Color.BLACK);
            mCheckMeOut.setTextColor(Color.BLACK);
            leaderboard.setTextColor(Color.BLACK);
            mFastestScore.setTextColor(mTextColor);
            mFastestScore1.setTextColor(mTextColor);
            mFastestScore2.setTextColor(mTextColor);
            mFastestScore3.setTextColor(mTextColor);
            mSound.setColorFilter(Color.BLACK);
            mVibrate.setColorFilter(Color.BLACK);
            mNightMode.setColorFilter(Color.BLACK);
            mRootView.setBackgroundColor(Color.WHITE);
            mLogo.setImageResource(R.drawable.splash_logo);

        }
//        mLogo.setImageDrawable(new LayerDrawable(layers));
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

    private void setUser(GoogleSignInAccount account){
        SharedPreferences.Editor editor = mUserSettings.edit();
        editor.putString("userID", account.getId());
        editor.putString("name", account.getDisplayName());
        editor.putString("email", account.getEmail());
        editor.putString("avatar", account.getPhotoUrl().toString());
        editor.apply();
    }

    private boolean checkUser(){
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            setUser(account);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if(account != null){
                // Signed in successfully, show authenticated UI.
                Snackbar.make(leaderboard,"Now you can check your rank on LeaderBoard...", Snackbar.LENGTH_SHORT)
                        .show();
                leaderboard.setText("WORLD RANKING");
                setUser(account);
            }else{
                Snackbar.make(leaderboard,"Sorry, LeaderBoard is not available!", Snackbar.LENGTH_SHORT)
                        .show();
            }
        } catch (ApiException e) {
            Snackbar.make(leaderboard,"Sorry, LeaderBoard is not available!", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(leaderboard,"Signed Out! Removed from the LeaderBoard.", Snackbar.LENGTH_SHORT)
                                .show();
                        leaderboard.setText("Log into LeaderBoard");
                    }
                });
    }
}
