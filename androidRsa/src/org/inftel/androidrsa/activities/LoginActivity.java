
package org.inftel.androidrsa.activities;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.asynctask.LoginTask;
import org.jivesoftware.smack.Connection;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private SharedPreferences prefs;
    private Connection connection;
    private ProgressDialog pDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        prefs = this.getApplicationContext().getSharedPreferences("login", 0);
        loadPreferences();
    }

    // Carga los campos de la última ejecución
    private void loadPreferences() {
        if (!prefs.getString("host", "default").equals("default")) {
            EditText e = (EditText) findViewById(R.id.host);
            e.setText(prefs.getString("host", "default"));
        }
        else if (!prefs.getString("port", "default").equals("default")) {
            EditText e = (EditText) findViewById(R.id.port);
            e.setText(prefs.getString("port", "default"));
        }
        else if (!prefs.getString("service", "default").equals("default")) {
            EditText e = (EditText) findViewById(R.id.service);
            e.setText(prefs.getString("service", "default"));
        }
        else if (!prefs.getString("userid", "default").equals("default")) {
            EditText e = (EditText) findViewById(R.id.userid);
            e.setText(prefs.getString("userid", "default"));
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
        savePreferences();
        Editor editor = prefs.edit();
        final EditText host = (EditText) findViewById(R.id.host);
        final EditText port = (EditText) findViewById(R.id.port);
        final EditText service = (EditText) findViewById(R.id.service);
        final EditText userid = (EditText) findViewById(R.id.userid);
        final EditText password = (EditText) findViewById(R.id.password);

        LoginTask task = new LoginTask(connection, host.getText().toString(),
                Integer.parseInt(port.getText().toString()),
                service.getText().toString(),
                userid.getText().toString(),
                password.getText().toString(), this);
        task.execute();

    }

    public void savePreferences() {
        Editor editor = prefs.edit();
        EditText host = (EditText) findViewById(R.id.host);
        EditText port = (EditText) findViewById(R.id.port);
        EditText service = (EditText) findViewById(R.id.service);
        EditText userid = (EditText) findViewById(R.id.userid);

        editor.putString("host", host.getText().toString());
        editor.putString("port", host.getText().toString());
        editor.putString("service", host.getText().toString());
        editor.putString("userid", host.getText().toString());

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

    // DEBUG
    public void test(View v) {
        // TODO eleminar TEST
        Intent i = new Intent(this, DecodeActivity.class);
        startActivity(i);
    }
}
