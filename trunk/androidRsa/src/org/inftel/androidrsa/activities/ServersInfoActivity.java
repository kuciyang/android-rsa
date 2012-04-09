
package org.inftel.androidrsa.activities;

import org.inftel.androidrsa.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ServersInfoActivity extends Activity {

    private static final String TAG = "ServersInfoActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_server);

        Bundle bundle = getIntent().getExtras();
        String host = bundle.getString("host");
        String port = bundle.getString("port");
        String service = bundle.getString("service");

        TextView textHost = (TextView) findViewById(R.id.label_host);
        textHost.setText(host);

        TextView textPort = (TextView) findViewById(R.id.label_port);
        textPort.setText(port);

        TextView textService = (TextView) findViewById(R.id.label_service);
        textService.setText(service);

    }
}
