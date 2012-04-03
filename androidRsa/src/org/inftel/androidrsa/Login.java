
package org.inftel.androidrsa;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class Login extends Activity {

    private static final String TAG = "TAG_LOGIN";

    private static ProgressDialog pd;

    private static EditText textoEmail;
    private static EditText textoPassword;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

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
        // TODO preferencias
        // case R.id.button_preferences:
        // i = new Intent(this, Preferences.class);
        // startActivity(i);
        // break;
        // TODO cambiar contraseña
        // case R.id.button_change_password:
        // i = new Intent(this, ChangePassword.class);
        // startActivity(i);
        // break;

        }
        return true;
    }

    public void onClickButtonLogin(View view) throws IOException {
        textoEmail = (EditText) findViewById(R.id.text_email_login);
        textoPassword = (EditText) findViewById(R.id.text_password_login);

    }

    public void onClickButtonRegister(View view) throws IOException {
        // TODO registro
        // Intent i = new Intent(this, Register.class);
        // startActivity(i);
    }

}
