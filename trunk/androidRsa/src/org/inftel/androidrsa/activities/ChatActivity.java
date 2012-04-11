
package org.inftel.androidrsa.activities;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.xmpp.ChatMan;
import org.inftel.androidrsa.xmpp.Conexion;
import org.inftel.androidrsa.xmpp.RosterManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ChatActivity extends ListActivity {
    private static final String TAG = "ChatActivity";
    private Connection connection;
    private ChatMan chatMan;
    private Roster roster;
    private boolean cipher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.connection = Conexion.getInstance();
        this.roster = RosterManager.getRosterInstance();
        chatMan = ContactsActivity.chatMan;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        String destJid = getIntent().getStringExtra("destJid");
        // Crear chat o no crear...
        if (ChatMan.chat == null) {
            chatMan.createChat(destJid);
        }
    }

    private void pintarUI() {

    }

    public void send(View view) {
        if (!cipher) {
            try {
                EditText m = (EditText) findViewById(R.id.textInput);
                chatMan.getChat().sendMessage(m.getText().toString());
                Log.d(TAG, "Enviado: " + m.getText().toString());
                m.setText("");
            } catch (XMPPException e) {
                Log.d(TAG, "ERROR al enviar mensaje");
            }
        }
        else {
            // TODO obtener clave publica del destino y mandar mensaje cifrado
        }
    }

    @Override
    public void onBackPressed() {
        ChatMan.chat = null;
        super.onBackPressed();
    }

}
