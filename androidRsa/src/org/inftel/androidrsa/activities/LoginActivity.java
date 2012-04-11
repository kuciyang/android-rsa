
package org.inftel.androidrsa.activities;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.asynctask.LoginTask;
import org.inftel.androidrsa.utils.AndroidRsaConstants;
import org.jivesoftware.smack.Connection;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private SharedPreferences prefs;
    private Connection connection;
    private ProgressDialog pDialog;
    private String selectedItem;
    private static final int DIALOG_RUN_ONCE = 1001;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        prefs = getSharedPreferences(AndroidRsaConstants.SHARED_PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        loadPreferences();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.services_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedItem = (String) parent.getItemAtPosition(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    // Carga los campos de la última ejecución
    private void loadPreferences() {
        if (!prefs.getString("userid", "default").equals("default")) {
            EditText e = (EditText) findViewById(R.id.userid);
            e.setText(prefs.getString("userid", "default"));
        }
        String service = prefs.getString("service", "default");
        if (!service.equals("default")) {
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            if (service.equals("Gmail"))
                spinner.setSelection(0);
            else if (service.equals("Facebook"))
                spinner.setSelection(1);
            else if (service.equals("Jabber"))
                spinner.setSelection(2);
            else
                spinner.setSelection(3);
        }
        // Cuenta gmail de accounts manager
        else if (prefs.getString("userid", "default").equals("default")) {
            String userid = "";
            EditText e = (EditText) findViewById(R.id.userid);
            Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
            for (Account account : accounts) {
                userid = account.name;
            }
            Log.i(TAG, "Encontrado user por defecto: " + userid);
            e.setText(userid);
        }
    }

    public void login(View v) {
        String userid = ((EditText) findViewById(R.id.userid)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        if (userid != null && !userid.isEmpty() && password != null && !password.isEmpty()) {
            // Check we've been run once.
            boolean runOnce = prefs.getBoolean(AndroidRsaConstants.SP_KEY_RUN_ONCE, false);
            if (!runOnce) {
                Editor editor = prefs.edit();
                editor.putString(AndroidRsaConstants.PASSWORD_TO_ENCRYPT, password);
                editor.apply();
                showDialog(DIALOG_RUN_ONCE);
                Intent i = new Intent(this, RegisterActivity.class);
                startActivity(i);
            } else {
                // saving preferences
                Editor editor = prefs.edit();
                editor.putString("service", selectedItem);
                editor.putString("userid", userid);
                editor.apply();

                // launching task
                LoginTask task = new LoginTask(this, connection, selectedItem,
                        userid, password, this);
                task.execute();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.info_empty_fields),
                    Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void savePreferences(String userid) {

    }

    /** llamado cuando se pulsa el boton menu. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    /** llamado cuando un elemento del menu es seleccionado. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.button_register:
                i = new Intent(this, RegisterActivity.class);
                startActivity(i);
                break;
        // TODO cambiar contrase�a
        // case R.id.button_change_password:
        // i = new Intent(this, ChangePassword.class);
        // startActivity(i);
        // break;

        }
        return true;
    }

    // Dialogs
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
                                        dialog.dismiss();
                                    }
                                });
                break;
        }
        dialog = builder.show();
        return dialog;
    }

}
