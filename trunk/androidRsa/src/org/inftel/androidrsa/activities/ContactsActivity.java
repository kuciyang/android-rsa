
package org.inftel.androidrsa.activities;

import java.util.ArrayList;
import java.util.Collection;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.xmpp.Conexion;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ContactsActivity extends ListActivity {
    private static final String TAG = "ContactsActivity";
    private Connection connection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connection = Conexion.getInstance();
        Roster roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        ArrayList<String> list = new ArrayList<String>();
        for (RosterEntry entry : entries) {
            if (entry.getName() != null) {
                list.add(entry.getName());
            }
        }
        String[] values = new String[list.size()];
        list.toArray(values);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.contactrow, R.id.textView1, values);

        setListAdapter(adapter);
    }
}
