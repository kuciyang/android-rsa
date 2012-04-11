
package org.inftel.androidrsa.asynctask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.inftel.androidrsa.activities.ContactsActivity;
import org.inftel.androidrsa.activities.LoginActivity;
import org.inftel.androidrsa.utils.AndroidRsaConstants;
import org.inftel.androidrsa.utils.ReadFileAsByteArray;
import org.inftel.androidrsa.xmpp.Conexion;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LoginTask extends AsyncTask<Object, Void, Boolean> {
    private static final String TAG = "LoginTask";
    private Connection con;
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
        this.con = c;
        this.host = host;
        this.port = port;
        this.service = service;
        this.user = user;
        this.password = password;
        this.activity = activity;
    }

    public void onPostExecute(Boolean success) {
        if (success) {
            Log.i(TAG, "Conexión creada correctamente!");
            Log.i(TAG, "Conectado como " + con.getUser());
            Intent i = new Intent(activity, ContactsActivity.class);
            activity.startActivity(i);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
        else {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            Log.e(TAG, "ERROR al crear conexión.");
            Toast.makeText(activity, "Error al conectar,intentelo de nuevo.", Toast.LENGTH_LONG)
                    .show();
            Conexion.disconnect();
        }
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        try {
            con = Conexion.getInstance(host, port, service, user, password);
            VCard vCard = new VCard();
            SharedPreferences prefs = activity.getSharedPreferences(
                    AndroidRsaConstants.SHARED_PREFERENCE_FILE,
                    Context.MODE_PRIVATE);
            String avatarPath = prefs.getString(AndroidRsaConstants.ENCODED_IMAGE_PATH,
                    "default");
            ProviderManager.getInstance().addIQProvider("vCard",
                    "vcard-temp",
                    new VCardProvider());
            vCard.load(con);
            byte[] bytes = ReadFileAsByteArray.getBytesFromFile(new File(avatarPath));
            vCard.setAvatar(bytes);
            Thread.sleep(10000);
            vCard.save(con);
            return true;
        } catch (XMPPException e) {
            e.printStackTrace();
            Log.d(TAG, "Excepcion XMPP");
            con = null;
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPreExecute() {
        this.pDialog = new ProgressDialog(activity);
        this.pDialog.setMessage(" Login in... ");
        this.pDialog.show();
    }

    private Bitmap obtainBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public void writeFile(byte[] data, String fileName) throws IOException {
        OutputStream out = new FileOutputStream(fileName);
        out.write(data);
        out.close();
    }
}
