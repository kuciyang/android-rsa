
package org.inftel.androidrsa.activities;

import java.util.ArrayList;
import java.util.Collection;

import org.inftel.androidrsa.adapters.ContactsAdapter;
import org.inftel.androidrsa.xmpp.Conexion;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

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
                Log.i(TAG,
                        "Presence changed: " + presence.getFrom()
                                + " " + presence.getMode());
            }

            public void entriesAdded(Collection<String> arg0) {
            }
        });

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
    }
}
