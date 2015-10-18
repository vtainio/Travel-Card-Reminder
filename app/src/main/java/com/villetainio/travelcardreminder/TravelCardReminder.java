/*
* Copyright 2015 Ville Tainio
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.villetainio.travelcardreminder;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Date;

public class TravelCardReminder extends Application {
    public static SharedPreferences cardStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        cardStorage = getSharedPreferences(getString(R.string.card_storage_name),
                Context.MODE_PRIVATE);
    }

    public int calculateStatus() {
        String start = cardStorage.getString(
                getString(R.string.card_storage_period_start),
                null);
        short periodLength = Short.valueOf(cardStorage.getString(
                getString(R.string.card_storage_period_length),
                "0"));

        return calculateRemainingPeriodDays(start, periodLength);
    }

    private int calculateRemainingPeriodDays(String start, short periodLength) {
        if (start != null && periodLength != 0) {
            Date startDate = new Date(start);
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            Calendar today = Calendar.getInstance();

            long daysBetween = (today.getTimeInMillis() - startCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000);
            return (int) Math.max(periodLength - daysBetween, 0);
        }
        return 0;
    }
}
