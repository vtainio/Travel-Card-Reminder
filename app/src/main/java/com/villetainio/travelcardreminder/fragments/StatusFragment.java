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
package com.villetainio.travelcardreminder.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.villetainio.travelcardreminder.R;
import com.villetainio.travelcardreminder.TravelCardReminder;

import java.util.Calendar;
import java.util.Date;

public class StatusFragment extends Fragment {
    SharedPreferences cardStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardStorage = TravelCardReminder.cardStorage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View statusView =  inflater.inflate(R.layout.status_fragment, container, false);

        TextView periodStatus = (TextView) statusView.findViewById(R.id.periodStatus);
        TextView cardValue = (TextView) statusView.findViewById(R.id.cardValue);

        String start = cardStorage.getString(
                getString(R.string.card_storage_period_start),
                null);
        short periodLength = Short.valueOf(cardStorage.getString(
                getString(R.string.card_storage_period_length),
                "0"));
        int daysRemaining = calculateRemainingPeriodDays(start, periodLength);
        if (daysRemaining > 0) {
            periodStatus.setText(String.valueOf(daysRemaining));
        } else {
            periodStatus.setText(getString(R.string.status_message_period_not_valid));
        }
        cardValue.setText(cardStorage.getString(
                getString(R.string.card_storage_value),
                getString(R.string.status_message_no_value)));

        return statusView;
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
