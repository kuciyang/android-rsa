
package org.inftel.androidrsa.xmpp;

import org.inftel.androidrsa.utils.AndroidRsaConstants;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
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
        this.con = null;
    }

    // patron singleton, unica conexion para toda la aplicacion
    public static Connection getInstance(String service, String userid,
            String password)
            throws XMPPException {
        String host;
        int port;
        String serv;
        if (con != null) {
            Log.d(TAG, "La conexión ya existe,devolviendo!");
            return con;
        }
        else {
            if (service.equals("Gmail")) {
                host = AndroidRsaConstants.GMAIL_HOST;
                port = AndroidRsaConstants.GMAIL_PORT;
                serv = AndroidRsaConstants.GMAIL_SERVICE;
                SmackConfiguration.setPacketReplyTimeout(60000);
                Log.d(TAG, "Creando una conexión con " + host + ":" + port);
                // Create the configuration for this new connection
                ConnectionConfiguration config = new ConnectionConfiguration(host, port, serv);
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
            } else if (service.equals("Facebook")) {
                return null;
            } else if (service.equals("Jabber")) {
                return null;
            } else {
                // windows live
                return null;
            }
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
