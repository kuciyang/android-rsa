
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
import android.os.Bundle;
import android.util.Log;
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
    private String[] nombres;
    private boolean showAll = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connection = Conexion.getInstance();
        roster = connection.getRoster();
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

        roster.addRosterListener(new RosterListener() {
            // Ignored events public void entriesAdded(Collection<String>
            // addresses) {}
            public void entriesDeleted(Collection<String> addresses) {
            }

            public void entriesUpdated(Collection<String> addresses) {
            }

            public void presenceChanged(Presence presence) {
                Log.i(TAG, "Presence changed: "
                        + presence.getFrom() + " "
                        + Status.getStatusFromPresence(presence));
                // pintarUI();
            }

            public void entriesAdded(Collection<String> arg0) {
            }
        });

        pintarUI(true);

    }

    private void pintarUI(boolean todos) {
        Collection<RosterEntry> entries = roster.getEntries();
        ArrayList<String> list = new ArrayList<String>();
        for (RosterEntry entry : entries) {
            if ((todos) && (entry.getName() != null)) {
                list.add(entry.getName());
            }
            else if (((!todos) && (entry.getName() != null))) {
                int status = Status.getStatusFromPresence(roster.getPresence(entry.getUser()));
                if ((status == Status.CONTACT_STATUS_AVAILABLE)
                        || (status == Status.CONTACT_STATUS_AVAILABLE_FOR_CHAT)) {
                    list.add(entry.getName());
                }
            }
        }

        nombres = new String[list.size()];
        list.toArray(nombres);

        ContactsAdapter adapter = new ContactsAdapter(this, nombres);
        setListAdapter(adapter);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                TextView nombre = (TextView) view.findViewById(R.id.nombre);
                Toast.makeText(getApplicationContext(), "pulsado " + nombre.getText(),
                        Toast.LENGTH_SHORT).show();

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
                return true;
            case R.id.MenuToggle:
                Toast.makeText(this, "pulsado toogle", Toast.LENGTH_SHORT).show();
                pintarUI(!showAll);
                showAll = !showAll;
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
                Presence presence2 = new Presence(Presence.Type.unavailable);
                presence2.setStatus("De parranda!");
                presence2.setMode(Presence.Mode.away);
                connection.sendPacket(presence2);
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
