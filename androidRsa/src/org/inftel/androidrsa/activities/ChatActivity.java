
package org.inftel.androidrsa.activities;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;

import javax.security.cert.Certificate;
import javax.security.cert.CertificateException;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.adapters.ChatAdapter;
import org.inftel.androidrsa.rsa.KeyStore;
import org.inftel.androidrsa.rsa.RSA;
import org.inftel.androidrsa.steganography.Decode;
import org.inftel.androidrsa.utils.AndroidRsaConstants;
import org.inftel.androidrsa.xmpp.AvatarsCache;
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
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends ListActivity {
    private static final String TAG = "ChatActivity";
    private Connection connection;
    public ChatMan chatMan;
    private Chat chat = null;
    private Roster roster;
    private static ArrayList<Message> listMessages = new ArrayList<Message>();
    private ChatAdapter adapter;
    private static ListView myListView;
    private String destJid;
    private String myJid;
    private boolean cipher;
    private Certificate cert;
    private String passphrase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.connection = Conexion.getInstance();
        this.roster = RosterManager.getRosterInstance();
        chatMan = ContactsActivity.chatMan;
        chat = chatMan.chat;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        destJid = getIntent().getStringExtra("destJid");
        myJid = this.connection.getUser();
        cipher = RosterManager.isSecure(destJid);

        if (chat == null) {
            chatMan.createChat(destJid, messageListener);
            chat = chatMan.getChat();
            if (cipher) {
                Message m = new Message();
            }
        } else {
            chat.addMessageListener(messageListener);
        }

        adapter = new ChatAdapter(this, listMessages);
        setListAdapter(adapter);
        myListView = getListView();

        if (cipher) {
            Bitmap bm = AvatarsCache.getAvatar(destJid);
            try {
                cert = Decode.decode(bm);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            }

            // Getting the passphrase to encrypt the private Key
            SharedPreferences prefs = getSharedPreferences(
                    AndroidRsaConstants.SHARED_PREFERENCE_FILE,
                    Context.MODE_PRIVATE);
            passphrase = prefs.getString(AndroidRsaConstants.USERID,
                    "thisisapassphrasedefault");
            Log.d(TAG, "PASSPHRASE (CHAT)" + passphrase);
            try {
                Log.d("SEGUIMIENTO",
                        "private key (chat) "
                                + RSA.getPrivateKeyDecryted(KeyStore.getInstance().getPk(),
                                        passphrase));
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

    }

    public void send(View view) {
        Message message = new Message(destJid);
        EditText editText = (EditText) findViewById(R.id.textInput);
        String plainText = editText.getText().toString();
        editText.setText("");

        message.setFrom(myJid);
        message.setTo(destJid);
        listMessages.add(message);
        refreshAdapter();
        myListView.smoothScrollToPosition(adapter.getCount() - 1);

        if (!cipher) {
            try {
                message.setBody(plainText);
                chatMan.getChat().sendMessage(message);
                Log.d(TAG, "Enviando: " + plainText);

            } catch (XMPPException e) {
                Log.d(TAG, "ERROR al enviar mensaje");
            }
        }
        else {
            String encodedMessage = "";
            try {
                encodedMessage = RSA.cipher(plainText,
                        cert.getPublicKey());
                message.setBody(encodedMessage);
                chatMan.getChat().sendMessage(message);
                Log.d(TAG, "Enviando cifrado: " + plainText);

            } catch (Exception e) {
                Log.d(TAG, "PETO ENVIANDO CIFRADOOOO");
                e.printStackTrace();
            }
        }
    }

    private MessageListener messageListener = new MessageListener() {
        public void processMessage(Chat chat, Message message) {
            if (message.getBody() != null) {
                if (!cipher) {

                    Log.i(TAG, "Recibido mensaje plano: " + message.getBody());
                    listMessages.add(message);
                    refreshAdapter();
                    myListView.smoothScrollToPosition(adapter.getCount() - 1);

                }
                else {

                    try {
                        PrivateKey pk = RSA.getPrivateKeyDecryted(KeyStore.getInstance().getPk(),
                                passphrase);
                        Log.d(TAG, "PRIVATE KEY " + pk.toString());
                        String decodedMessage = RSA.decipher(message.getBody(), pk);
                        Log.i(TAG, "Recibido mensaje cifrado: " + decodedMessage);

                        message.setBody(decodedMessage);
                        listMessages.add(message);
                        refreshAdapter();
                        myListView.smoothScrollToPosition(adapter.getCount() - 1);

                    } catch (Exception e) {
                        Log.d(TAG, "PETO AL DESCIFRAR");
                        e.printStackTrace();

                    }

                }
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
        chat = null;
        super.onBackPressed();
    }

}
