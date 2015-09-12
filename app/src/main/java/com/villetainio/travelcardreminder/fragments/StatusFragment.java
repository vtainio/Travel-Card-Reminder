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
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.villetainio.travelcardreminder.R;
import com.villetainio.travelcardreminder.TravelCardReminder;

import org.w3c.dom.Text;

public class StatusFragment extends Fragment {
    SharedPreferences cardStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardStorage = this.getActivity().getSharedPreferences(getString(R.string.card_storage_name),
                Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View statusView =  inflater.inflate(R.layout.status_fragment, container, false);

        TextView periodStatus = (TextView) statusView.findViewById(R.id.periodStatus);
        TextView cardValue = (TextView) statusView.findViewById(R.id.cardValue);

        periodStatus.setText(TravelCardReminder.cardStorage.getString(
                getString(R.string.card_storage_period_days_remaining), "No period value"));
        cardValue.setText(TravelCardReminder.cardStorage.getString(
                getString(R.string.card_storage_value), "No value"));

        return statusView;
    }
}
