
package org.inftel.androidrsa.activities;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.xmpp.Conexion;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ChatActivity extends ListActivity {
    private static final String TAG = "ChatActivity";
    private Connection connection;
    private Chat chat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        connection = Conexion.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        createChat();
    }

    private void pintarUI() {

    }

    private void createChat() {
        ChatManager chatmanager = connection.getChatManager();
        MessageListener messageListener = new MessageListener() {
            public void processMessage(Chat chat, Message message) {
                Log.i(TAG, "Recibido mensaje: " + message.getBody());
            }
        };
        chat = chatmanager.createChat("miwe08@gmail.com", messageListener);

    }

    public void send(View view) {
        try {
            Log.d(TAG, "Leyendo editText:");
            EditText m = (EditText) findViewById(R.id.textInput);

            Message message = new Message("miwe08@gmail.com");
            message.setFrom(connection.getUser());
            message.setBody(m.getText().toString());

            Log.d(TAG, "From: " + connection.getUser());
            Log.d(TAG, "To: " + message.getTo());
            Log.d(TAG, "Body: " + m.getText());

            chat.sendMessage(m.getText().toString());
            m.setText("");

        } catch (XMPPException e) {
            Log.d(TAG, "ERROR al enviar mensaje");
        }
    }
}
