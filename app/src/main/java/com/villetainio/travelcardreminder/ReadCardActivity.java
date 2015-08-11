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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.widget.Toast;

import com.hsl.cardproducts.TravelCard;
import com.hsl.example.CardOperations;

import java.io.IOException;
import java.util.Date;

public class ReadCardActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_read_card);
        storeTravelCardData(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        storeTravelCardData(intent);
    }

    private TravelCard readCardData(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        IsoDep isoDep = IsoDep.get(tagFromIntent);

        try {
            isoDep.connect();
            return CardOperations.readTravelCardData(isoDep);
        } catch (IOException ioErr) {
            Toast err = Toast.makeText(this, ioErr.getMessage(), Toast.LENGTH_LONG);
            err.show();
            return new TravelCard(TravelCard.STATUS_NO_HSL_CARD);
        }
    }

    private void storeTravelCardData(Intent intent) {
        TravelCard card = readCardData(intent);

        if (card.getErrorStatus() == TravelCard.STATUS_NO_HSL_CARD) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error_label_card_read_failure)
                    .setMessage(R.string.error_message_card_read_failure)
                    .setNegativeButton(R.string.button_value_close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            moveToMainActivity();
                        }
                    });

            AlertDialog alert = builder.show();
            alert.show();

        } else {
            // Get most recent period information from the card.
            Date start = card.getPeriodStartDate1();
            Date end = card.getPeriodEndDate1();
            String value = CardOperations.getTravelCardValue(card);

            SharedPreferences cardStorage = getSharedPreferences(getString(R.string.card_storage_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = cardStorage.edit();

            if (start != null && end != null) {
                editor.putString(getString(R.string.card_storage_period_start), start.toString());
                editor.putString(getString(R.string.card_storage_period_end), end.toString());
            }
            editor.putString(getString(R.string.card_storage_value), value);
            editor.apply();

            Toast.makeText(getApplicationContext(), R.string.success_message_read_card, Toast.LENGTH_LONG)
                    .show();

            moveToMainActivity();
        }
    }

    private void moveToMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }
}
