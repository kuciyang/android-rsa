
package org.inftel.androidrsa.xmpp;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.content.SharedPreferences;
import android.util.Log;

public class Conexion {
    private static final String TAG = "Conexion";
    private static Connection con;
    private SharedPreferences prefs;

    public Conexion() {
        super();
    }

    // patron singleton, unica conexion para toda la aplicacion
    public static Connection getInstance(String host, int port, String userid, String password)
            throws XMPPException {
        if (con != null) {
            Log.d(TAG, "La conexión ya existe,devolviendo!");
            return con;
        }
        else {
            Log.d(TAG, "Creando una conexión: ");
            // Create the configuration for this new connection
            ConnectionConfiguration config = new ConnectionConfiguration("host", port);
            config.setCompressionEnabled(true);
            config.setSASLAuthenticationEnabled(true);

            con = new XMPPConnection(config);
            // Connect to the server
            con.connect();
            // Log into the server
            con.login(userid, password, "androidRSA");
            return con;
        }

    }

}
