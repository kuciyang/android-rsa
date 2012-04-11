
package org.inftel.androidrsa.xmpp;

import org.inftel.androidrsa.activities.ChatActivity;
import org.inftel.androidrsa.activities.ContactsActivity;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import android.content.Intent;
import android.util.Log;

public class ChatMan {
    private final static String TAG = "ChatActivity";
    private Connection connection;
    public static Chat chat = null;
    private boolean cipher;
    private ContactsActivity activity;

    private final static MessageListener messageListener = new MessageListener() {
        public void processMessage(Chat chat, Message message) {
            // FIXME comprobar messages con body null
            Log.i(TAG, "Recibido mensaje: " + message.getBody());
            // TODO descifrar el mensaje si es un chat cifrado
        }
    };

    public ChatMan(ContactsActivity cActivity) {
        this.activity = cActivity;
        this.connection = Conexion.getInstance();
    }

    public void initListener() {
        // Listener para detectar si el chat lo crea el otro
        org.jivesoftware.smack.ChatManager chatmanager = connection.getChatManager();
        ChatManagerListener chatManagerListener = new ChatManagerListener() {
            public void chatCreated(Chat chat, boolean createdLocally)
            {
                if (!createdLocally)
                    // TODO startActivity y asignar a field
                    ChatMan.chat = chat;
                chat.addMessageListener(messageListener);
                Intent i = new Intent(activity, ChatActivity.class);
                activity.startActivity(i);
            }
        };

        chatmanager.addChatListener(chatManagerListener);
    }

    public void createChat(String jidDest) {
        Log.d(TAG, "Creando chat con: " + jidDest);
        ChatManager chatmanager = connection.getChatManager();

        // Listener para recibir mensajes

        // Creo el chat
        chat = chatmanager.createChat(jidDest, messageListener);
        if (RosterManager.isSecure(jidDest)) {
            cipher = true;
        }
        else {
            cipher = false;
        }
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

}
