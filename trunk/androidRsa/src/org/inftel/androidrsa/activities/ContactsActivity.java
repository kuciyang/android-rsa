
package org.inftel.androidrsa.activities;

import java.util.ArrayList;
import java.util.Collection;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.adapters.ContactsAdapter;
import org.inftel.androidrsa.xmpp.ChatMan;
import org.inftel.androidrsa.xmpp.Conexion;
import org.inftel.androidrsa.xmpp.RosterManager;
import org.inftel.androidrsa.xmpp.Status;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsActivity extends ListActivity {
    private static final String TAG = "ContactsActivity";
    private Connection connection;
    public static Roster roster;
    private ArrayList<String> listaJid = new ArrayList<String>();
    private boolean showAll = true;
    private ContactsAdapter adapter;
    private ListView myListView;
    public static ChatMan chatMan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connection = Conexion.getInstance();
        roster = RosterManager.getRosterInstance();
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

        pintarUI();
        chatMan = new ChatMan(this);
        chatMan.initListener();

    }

    private void loadContacts() {
        Collection<RosterEntry> entries = roster.getEntries();
        listaJid.clear();
        for (RosterEntry entry : entries) {
            if ((showAll) && (entry.getName() != null)) {
                listaJid.add(roster.getPresence(entry.getUser()).getFrom());
            }
            else if (((!showAll) && (entry.getName() != null))) {
                int status = Status.getStatusFromPresence(roster.getPresence(entry.getUser()));
                if ((status == Status.CONTACT_STATUS_AVAILABLE)
                        || (status == Status.CONTACT_STATUS_AVAILABLE_FOR_CHAT)
                        || (status == Status.CONTACT_STATUS_AWAY)
                        || (status == Status.CONTACT_STATUS_BUSY)) {
                    listaJid.add(roster.getPresence(entry.getUser()).getFrom());
                }
            }
        }
    }

    private void pintarUI() {
        loadContacts();

        adapter = new ContactsAdapter(this, listaJid);
        setListAdapter(adapter);

        myListView = getListView();
        myListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                TextView nombre = (TextView) view.findViewById(R.id.nombre);
                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                i.putExtra("destJid", RosterManager.findByName(nombre.getText().toString())
                        .getUser());
                startActivity(i);
            }

        });

        roster.addRosterListener(new RosterListener() {
            public void entriesDeleted(Collection<String> addresses) {
            }

            public void entriesUpdated(Collection<String> addresses) {
            }

            public void presenceChanged(Presence presence) {
                refreshAdapter();
            }

            public void entriesAdded(Collection<String> arg0) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MenuToggle:
                showAll = !showAll;
                loadContacts();
                refreshAdapter();
                return true;
            case R.id.MenuChangeState:
                return true;
            case R.id.available:
                // Presence presence = new Presence(Presence.Type.available);
                roster.getPresence(connection.getUser()).setMode(Mode.available);
                // presence.setMode(Presence.Mode.available);
                // presence.setStatus("aqui estamos ya!");
                // connection.sendPacket(presence);
                return true;
            case R.id.away:
                roster.getPresence(connection.getUser()).setMode(Mode.away);
                // Presence presence2 = new Presence(Presence.Type.available);
                // presence2.setStatus("De parranda!");
                // presence2.setMode(Presence.Mode.away);
                // connection.sendPacket(presence2);
                return true;
            case R.id.busy:
                roster.getPresence(connection.getUser()).setMode(Mode.dnd);
                // Presence presence3 = new Presence(Presence.Type.available);
                // presence3.setStatus("Trabajando!");
                // presence3.setMode(Presence.Mode.dnd);
                // connection.sendPacket(presence3);
                return true;
            case R.id.unavailable:
                roster.getPresence(connection.getUser()).setType(Type.unavailable);
                // Presence presence5 = new Presence(Presence.Type.unavailable);
                // presence5.setStatus("Desconectado!");
                // presence5.setMode(Presence.Mode.away);
                // connection.sendPacket(presence5);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshAdapter() {
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Conexion.disconnect();
        super.onBackPressed();
        super.finish();
    }

}
