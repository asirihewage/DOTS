/*
 * Copyright (C) 2018 Adin Kwok (adinkwok)
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

package com.viralapps.asiri.dots.game;

public class User {
    private String userID;
    private String deviceID;
    private String name;
    private String email;
    private String avatar;
    private String dotsPerSecond;
    private String scoreSet;
    private String timestamp;
    private String totalTime;


    public User() {

    }

    public User(String avatar, String deviceID, String dotsPerSecond, String email, String name, String scoreSet, String timestamp, String totalTime, String userID) {
        this.avatar = avatar;
        this.deviceID  = deviceID;
        this.dotsPerSecond =  dotsPerSecond;
        this.email = email;
        this.name = name;
        this.scoreSet = scoreSet;
        this.timestamp = timestamp;
        this.totalTime = totalTime;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDotsPerSecond() {
        return dotsPerSecond;
    }

    public void setDotsPerSecond(String dotsPerSecond) {
        this.dotsPerSecond = dotsPerSecond;
    }

    public String getScoreSet() {
        return scoreSet;
    }

    public void setScoreSet(String scoreSet) {
        this.scoreSet = scoreSet;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return this.userID;
    }

    public void setUserId(String userId) {
        this.userID = userId;
    }

    public String gethighScore() {
        return this.dotsPerSecond;
    }

    public void sethighScore(String highScore) {
        this.dotsPerSecond = highScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}