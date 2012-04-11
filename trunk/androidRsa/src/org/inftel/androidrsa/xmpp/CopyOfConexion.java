
package org.inftel.androidrsa.xmpp;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.content.SharedPreferences;
import android.util.Log;

public class CopyOfConexion {
    private static final String TAG = "Conexion";
    private static Connection con;
    private SharedPreferences prefs;

    public CopyOfConexion() {
        super();
        this.con = null;
    }

    // patron singleton, unica conexion para toda la aplicacion
    public static Connection getInstance(String host, int port, String service, String userid,
            String password)
            throws XMPPException {
        if (con != null) {
            Log.d(TAG, "La conexión ya existe,devolviendo!");
            return con;
        }
        else {
            SmackConfiguration.setPacketReplyTimeout(60000);
            Log.d(TAG, "Creando una conexión con " + host + ":" + port);
            // Create the configuration for this new connection
            ConnectionConfiguration config = new ConnectionConfiguration(host, port, service);
            config.setDebuggerEnabled(true);
            XMPPConnection.DEBUG_ENABLED = true;
            SASLAuthentication.supportSASLMechanism("PLAIN", 0);
            config.setSASLAuthenticationEnabled(true);

            con = new XMPPConnection(config);
            // Connect to the server
            con.connect();
            // Log into the server
            con.login(userid, password, "androidRSA");
            return con;
        }

    }

    public static Connection getInstance() {
        if (con != null) {
            Log.d(TAG, "La conexión ya existe,devolviendo!");
            return con;
        }
        else {
            throw new RuntimeException("Error, no esta logueado.");
        }
    }

    public static void disconnect() {
        if ((con != null) && (con.isConnected())) {
            con.disconnect();
        }
        con = null;
    }

}
