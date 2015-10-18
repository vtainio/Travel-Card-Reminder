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
package com.villetainio.travelcardreminder.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.villetainio.travelcardreminder.R;
import com.villetainio.travelcardreminder.TravelCardReminder;
import com.villetainio.travelcardreminder.view.TravelCardPagerAdapter;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class MainActivity extends FragmentActivity {

    private NfcAdapter nfcAdapter;
    TravelCardPagerAdapter pagerAdapter;
    VerticalViewPager pager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, R.string.error_message_no_nfc_on_device, Toast.LENGTH_LONG).show();
            finish();
        }

        pagerAdapter = new TravelCardPagerAdapter(getSupportFragmentManager());
        pager = (VerticalViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            ActionBar.TabListener tabListener = new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
                    pager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
                    // Unselect
                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
                    // Reselect
                }
            };

            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Status")
                            .setTabListener(tabListener)
            );

            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Settings")
                            .setTabListener(tabListener)
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, nfcAdapter);
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
}
