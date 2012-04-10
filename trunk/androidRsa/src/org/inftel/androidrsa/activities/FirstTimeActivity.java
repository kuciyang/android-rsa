
package org.inftel.androidrsa.activities;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.utils.AndroidRsaConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Window;

public class FirstTimeActivity extends Activity {

    private static final String TAG = "FirstTimeActivity";
    private static final int DIALOG_RUN_ONCE = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.first_time);

        // Get a reference to the Shared Preferences and a Shared Preference
        // Editor.
        SharedPreferences prefs = getSharedPreferences(AndroidRsaConstants.SHARED_PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        Editor prefsEditor = prefs.edit();

        // Save that we've been run once.
        if (prefs.getBoolean(AndroidRsaConstants.SP_KEY_RUN_ONCE, false)) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        } else {
            prefsEditor.putBoolean(AndroidRsaConstants.SP_KEY_RUN_ONCE, true);
            showDialog(DIALOG_RUN_ONCE);
        }
    }

    // Dialog
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new Builder(this);

        switch (id) {
            case DIALOG_RUN_ONCE:
                builder.setMessage(R.string.first_time_configuration)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(getApplicationContext(),
                                                RegisterActivity.class);
                                        startActivity(i);
                                        finish();
                                        dialog.dismiss();
                                    }
                                });
                break;
        }
        dialog = builder.show();
        return dialog;
    }

}
