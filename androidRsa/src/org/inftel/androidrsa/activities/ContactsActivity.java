
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

        pintarUI();

    }

    private void pintarUI() {
        Collection<RosterEntry> entries = roster.getEntries();
        ArrayList<String> list = new ArrayList<String>();
        for (RosterEntry entry : entries) {
            if (entry.getName() != null) {
                list.add(entry.getName());
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
    protected void onDestroy() {
        Conexion.disconnect();
        super.onDestroy();
    }

}
