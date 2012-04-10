
package org.inftel.androidrsa.activities;

import java.util.ArrayList;

import org.inftel.androidrsa.adapters.ContactsAdapter;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;

public class ChatActivity extends ListActivity {
    private static final String TAG = "ChatActivity";
    private ArrayList<String> listaNombres = new ArrayList<String>();
    private boolean showAll = true;
    private ContactsAdapter adapter;
    private ListView myListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pintarUI();

    }

    private void pintarUI() {
        // listaNombres.clear();
        // for (RosterEntry entry : entries) {
        // if ((showAll) && (entry.getName() != null)) {
        // listaNombres.add(entry.getName());
        // }
        // else if (((!showAll) && (entry.getName() != null))) {
        // int status =
        // Status.getStatusFromPresence(roster.getPresence(entry.getUser()));
        // if ((status == Status.CONTACT_STATUS_AVAILABLE)
        // || (status == Status.CONTACT_STATUS_AVAILABLE_FOR_CHAT)
        // || (status == Status.CONTACT_STATUS_AWAY)
        // || (status == Status.CONTACT_STATUS_BUSY)) {
        // listaNombres.add(entry.getName());
        // }
        // }
        // }
        //
        // adapter = new ContactsAdapter(this, listaNombres);
        // runOnUiThread(new Runnable() {
        // public void run() {
        // setListAdapter(adapter);
        // }
        // });
        //
        // myListView = getListView();
        //
        // myListView.setOnItemClickListener(new OnItemClickListener() {
        // public void onItemClick(AdapterView<?> parent, View view,
        // int position, long id) {
        // TextView nombre = (TextView) view.findViewById(R.id.nombre);
        // Toast.makeText(getApplicationContext(), "pulsado " +
        // nombre.getText(),
        // Toast.LENGTH_SHORT).show();
        //
        // }
        //
        // });

    }
}
