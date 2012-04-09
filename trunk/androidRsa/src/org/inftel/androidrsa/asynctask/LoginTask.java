
package org.inftel.androidrsa.asynctask;

import org.inftel.androidrsa.activities.ContactsActivity;
import org.inftel.androidrsa.activities.LoginActivity;
import org.inftel.androidrsa.xmpp.Conexion;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LoginTask extends AsyncTask<Object, Void, Boolean> {
    private static final String TAG = "LoginTask";
    private Connection c;
    private String host;
    private int port;
    private String service;
    private String user;
    private String password;
    private LoginActivity activity;
    private ProgressDialog pDialog;

    public LoginTask(Connection c, String host, int port, String service, String user,
            String password, LoginActivity activity) {
        super();
        this.c = c;
        this.host = host;
        this.port = port;
        this.service = service;
        this.user = user;
        this.password = password;
        this.activity = activity;
    }

    public void onPostExecute(Boolean success) {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
        if (success) {
            Log.i(TAG, "Conexión creada correctamente!");
            Intent i = new Intent(activity, ContactsActivity.class);
            activity.startActivity(i);
        }
        else {
            Log.e(TAG, "ERROR al crear conexión.");
            Toast.makeText(activity, "Error al conectar,intentelo de nuevo.", Toast.LENGTH_LONG)
                    .show();
            Conexion.disconnect();
        }
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        try {
            c = Conexion.getInstance(host, port, service, user, password);
            Presence presence = new Presence(Presence.Type.available);
            presence.setProperty("rsaEnabled", true);
            presence.setMode(Presence.Mode.available);
            // TODO enviar foto y clave publica
            c.sendPacket(presence);
            return true;
        } catch (XMPPException e) {
            e.printStackTrace();
            c = null;
            return false;
        }
    }

    @Override
    protected void onPreExecute() {
        this.pDialog = new ProgressDialog(activity);
        this.pDialog.setMessage(" Loading... ");
        this.pDialog.show();
    }

}
