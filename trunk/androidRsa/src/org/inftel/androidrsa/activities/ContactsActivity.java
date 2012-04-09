
package org.inftel.androidrsa.activities;

import java.util.ArrayList;
import java.util.Collection;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.adapters.ContactsAdapter;
import org.inftel.androidrsa.xmpp.Conexion;
import org.inftel.androidrsa.xmpp.Status;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsActivity extends ListActivity {
    private static final String TAG = "ContactsActivity";
    private Connection connection;
    public static Roster roster;
    private ArrayList<String> listaNombres = new ArrayList<String>();
    private boolean showAll = true;
    private ContactsAdapter adapter;
    private ListView myListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connection = Conexion.getInstance();
        roster = connection.getRoster();
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

        final ProgressDialog pd = ProgressDialog.show(this,
                "",
                "Loading contacts...",
                true, false);

        new Thread() {
            public void run() {
                pintarUI();
                pd.dismiss();
            }
        }.start();
        // pintarUI();

    }

    private void pintarUI() {
        Collection<RosterEntry> entries = roster.getEntries();
        listaNombres.clear();
        for (RosterEntry entry : entries) {
            if ((showAll) && (entry.getName() != null)) {
                listaNombres.add(entry.getName());
            }
            else if (((!showAll) && (entry.getName() != null))) {
                int status = Status.getStatusFromPresence(roster.getPresence(entry.getUser()));
                if ((status == Status.CONTACT_STATUS_AVAILABLE)
                        || (status == Status.CONTACT_STATUS_AVAILABLE_FOR_CHAT)
                        || (status == Status.CONTACT_STATUS_AWAY)
                        || (status == Status.CONTACT_STATUS_BUSY)) {
                    listaNombres.add(entry.getName());
                }
            }
        }

        adapter = new ContactsAdapter(this, listaNombres);
        runOnUiThread(new Runnable() {
            public void run() {
                setListAdapter(adapter);
            }
        });

        // setListAdapter(adapter);
        myListView = getListView();

        myListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                TextView nombre = (TextView) view.findViewById(R.id.nombre);
                Toast.makeText(getApplicationContext(), "pulsado " + nombre.getText(),
                        Toast.LENGTH_SHORT).show();

            }

        });

        roster.addRosterListener(new RosterListener() {
            public void entriesDeleted(Collection<String> addresses) {
            }

            public void entriesUpdated(Collection<String> addresses) {
            }

            public void presenceChanged(Presence presence) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
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
            case R.id.MenuAdd:
                Toast.makeText(this, "pulsado añadir", Toast.LENGTH_SHORT).show();
                // TODO dialogo agregar contacto nuevo al roster
                return true;
            case R.id.MenuToggle:
                Toast.makeText(this, "pulsado toogle", Toast.LENGTH_SHORT).show();
                showAll = !showAll;
                pintarUI();
                return true;
            case R.id.MenuChangeState:
                Toast.makeText(this, "pulsado cambiar estado", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.available:
                Toast.makeText(this, "cambiado estado a available", Toast.LENGTH_SHORT).show();
                Presence presence = new Presence(Presence.Type.available);
                presence.setMode(Presence.Mode.available);
                presence.setStatus("aqui estamos ya!");
                connection.sendPacket(presence);
                return true;
            case R.id.away:
                Toast.makeText(this, "cambiado estado a away", Toast.LENGTH_SHORT).show();
                Presence presence2 = new Presence(Presence.Type.available);
                presence2.setStatus("De parranda!");
                presence2.setMode(Presence.Mode.away);
                connection.sendPacket(presence2);
                return true;
            case R.id.busy:
                Toast.makeText(this, "cambiado estado a busy", Toast.LENGTH_SHORT).show();
                Presence presence3 = new Presence(Presence.Type.available);
                presence3.setStatus("Trabajando!");
                presence3.setMode(Presence.Mode.dnd);
                connection.sendPacket(presence3);
                return true;
            case R.id.unavailable:
                Toast.makeText(this, "cambiado estado a unavailable", Toast.LENGTH_SHORT).show();
                Presence presence5 = new Presence(Presence.Type.unavailable);
                presence5.setStatus("Desconectado!");
                presence5.setMode(Presence.Mode.away);
                connection.sendPacket(presence5);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        Conexion.disconnect();
        super.onDestroy();
    }

}
