
package org.inftel.androidrsa.activities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.utils.AndroidRsaConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ServersAddActivity extends Activity {

    private static final String TAG = "ServersAddActivity";
    private ProgressDialog pd;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_server);
    }

    public void onClickButtonClean(View view) throws IOException {
        ((EditText) findViewById(R.id.host)).setText("");
        ((EditText) findViewById(R.id.port)).setText("");
        ((EditText) findViewById(R.id.service)).setText("");
    }

    public void onClickButtonAccept(View view) throws IOException {
        if (((EditText) findViewById(R.id.host)).getText().toString().equals("")
                ||
                ((EditText) findViewById(R.id.port)).getText().toString().equals("")
                ||
                ((EditText) findViewById(R.id.service)).getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.info_empty_fields)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {

            pd = ProgressDialog.show(this, "",
                    getString(R.string.saving), true, false);

            // Thread para informar al server
            new Thread(new Runnable() {
                public void run() {
                    File file = new File(AndroidRsaConstants.SERVERS_FILE_PATH);
                    FileWriter writer;
                    try {
                        writer = new FileWriter(file, true);
                        writer.write(((EditText) findViewById(R.id.host)).getText().toString()
                                + ":" + ((EditText) findViewById(R.id.port)).getText().toString()
                                + ":"
                                + ((EditText) findViewById(R.id.service)).getText().toString()
                                + '\n');
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    pd.dismiss();

                    // Se lanza la siguiente actividad
                    Intent i = new Intent(getApplicationContext(), ServersActivity.class);
                    startActivity(i);
                }
            }).start();
        }
    }

}
