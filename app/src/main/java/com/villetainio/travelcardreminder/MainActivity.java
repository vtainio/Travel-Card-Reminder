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

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends Activity {

    private TextView periodStatusView, cardValueView, nfcStatusView;
    private NfcAdapter nfcAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        periodStatusView = (TextView) findViewById(R.id.periodStatus);
        cardValueView = (TextView) findViewById(R.id.cardValue);
        nfcStatusView = (TextView) findViewById(R.id.nfcStatus);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, R.string.error_message_no_nfc_on_device, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, nfcAdapter);

        SharedPreferences cardStorage = getSharedPreferences(getString(R.string.card_storage_name),
                Context.MODE_PRIVATE);
        String periodStart = cardStorage.getString(getString(R.string.card_storage_period_start), "");
        String periodEnd = cardStorage.getString(getString(R.string.card_storage_period_end), "");
        String cardValue = cardStorage.getString(getString(R.string.card_storage_value), "");

        if (!periodStart.equals("") && !periodEnd.equals("")) {
            int days = calculateRemainingPeriodDays(periodStart, periodEnd);
            if (days > 0) {
                periodStatusView.setText(String.valueOf(days));
            } else {
                periodStatusView.setText(R.string.status_message_period_not_valid);
            }
        }

        if (!cardValue.equals("")) {
            cardValueView.setText(cardValue);
        }

        if (!nfcAdapter.isEnabled()) {
            nfcStatusView.setText(R.string.error_message_nfc_disabled);
        }

    }

    @Override
    protected  void onPause() {
        stopForegroundDispatch(this, nfcAdapter);
        super.onPause();
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), ReadCardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType("*/*"); //TODO Use only needed MIME based dispatches.
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("MIME type not supported.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    public int calculateRemainingPeriodDays(String start, String end) {
        Date startDate = new Date(start);
        Date endDate = new Date(end);

        return (int) (startDate.getTime() - endDate.getTime()) / (1000 * 60 * 60 * 24);
    }
}
