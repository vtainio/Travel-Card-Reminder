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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.villetainio.travelcardreminder.R;
import com.villetainio.travelcardreminder.TravelCardReminder;

public class SettingsFragment extends Fragment {
    SharedPreferences cardStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardStorage = TravelCardReminder.cardStorage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        final View view = inflater.inflate(R.layout.settings_fragment, container, false);

        // Enable notifications is true by default
        CheckBox enableNotificationsView = (CheckBox) view.findViewById(R.id.enable_notifications);
        boolean enableNotifications = cardStorage.getBoolean(
                getString(R.string.settings_enable_notifications),
                true
        );
        enableNotificationsView.setChecked(enableNotifications);

        // Day to start notifications is 3 days before by default.
        int days = cardStorage.getInt(getString(R.string.settings_days), 3);
        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.days_pick);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(5); //TODO get current days left

        // Time to start notifications is 12.00 by default.
        int hours = cardStorage.getInt(getString(R.string.settings_hours), 12);
        int minutes = cardStorage.getInt(getString(R.string.settings_minutes), 0);
        TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_pick);
        timePicker.setCurrentHour(hours);
        timePicker.setCurrentMinute(minutes);

        // Save settings.
        Button saveButton = (Button) view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveValues(view);
                Toast.makeText(getActivity().getApplicationContext(),
                        R.string.success_message_save_settings,
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

        return view;
    }

    private void saveValues(View view) {
        SharedPreferences.Editor editor = cardStorage.edit();
        // Enable notifications
        editor.putBoolean(
                getString(R.string.settings_enable_notifications),
                ((CheckBox) view.findViewById(R.id.enable_notifications)).isChecked()
        );

        // Days before
        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.days_pick);
        editor.putInt(
                getString(R.string.settings_days),
                numberPicker.getValue()
        );

        // Time to start
        TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_pick);
        editor.putInt(
                getString(R.string.settings_hours),
                timePicker.getCurrentHour()
        );
        editor.putInt(
                getString(R.string.settings_minutes),
                timePicker.getCurrentMinute()
        );

        editor.apply();
    }
}
