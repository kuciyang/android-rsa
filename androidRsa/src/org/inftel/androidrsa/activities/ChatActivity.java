
package org.inftel.androidrsa.activities;

import java.util.ArrayList;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.adapters.ChatAdapter;
import org.inftel.androidrsa.xmpp.ChatMan;
import org.inftel.androidrsa.xmpp.Conexion;
import org.inftel.androidrsa.xmpp.RosterManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends ListActivity {
    private static final String TAG = "ChatActivity";
    private Connection connection;
    private ChatMan chatMan;
    private Roster roster;
    private static ArrayList<Message> listMessages = new ArrayList<Message>();
    private ChatAdapter adapter;
    private static ListView myListView;
    private String destJid;
    private String myJid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.connection = Conexion.getInstance();
        this.roster = RosterManager.getRosterInstance();
        chatMan = ContactsActivity.chatMan;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        destJid = getIntent().getStringExtra("destJid");
        myJid = this.connection.getUser();

        if (ChatMan.chat == null) {
            chatMan.createChat(destJid, messageListener);
        }

        adapter = new ChatAdapter(this, listMessages);
        setListAdapter(adapter);
        myListView = getListView();

    }

    public void send(View view) {
        if (!chatMan.isCipher()) {
            try {
                EditText editText = (EditText) findViewById(R.id.textInput);
                Message m = new Message(destJid);
                m.setBody(editText.getText().toString());
                m.setFrom(myJid);
                m.setTo(destJid);
                chatMan.getChat().sendMessage(m);
                Log.d(TAG, "Enviando: " + m.getBody());
                editText.setText("");
                listMessages.add(m);
                myListView.setSelection(myListView.getAdapter().getCount() - 1);

            } catch (XMPPException e) {
                Log.d(TAG, "ERROR al enviar mensaje");
            }
        }
        else {
            // TODO obtener clave publica del destino y mandar mensaje cifrado
        }
    }

    private MessageListener messageListener = new MessageListener() {
        public void processMessage(Chat chat, Message message) {
            if (!chatMan.isCipher()) {
                if (message.getBody() != null) {
                    // Log.i(TAG, "Recibido mensaje: " + message.getBody());
                    listMessages.add(message);
                    refreshAdapter();
                    myListView.smoothScrollToPosition(adapter.getCount() - 1);

                }
            }
            else {
                // TODO descifrar el mensaje si es un chat cifrado
            }

        }
    };

    private void refreshAdapter() {
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        chatMan.chat = null;
        super.onBackPressed();
    }

}
