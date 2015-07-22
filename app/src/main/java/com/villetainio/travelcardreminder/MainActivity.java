package com.villetainio.travelcardreminder;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private TextView infoView;
    private NfcAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoView = (TextView) findViewById(R.id.viewTest);

        adapter = NfcAdapter.getDefaultAdapter(this);

        if (adapter == null) {
            Toast.makeText(this, R.string.error_message_no_nfc_on_device, Toast.LENGTH_LONG).show();
            finish();
        }

        if (!adapter.isEnabled()) {
            infoView.setText(R.string.error_message_nfc_disabled);
        } else {
            infoView.setText(R.string.app_name);
        }
    }
}
