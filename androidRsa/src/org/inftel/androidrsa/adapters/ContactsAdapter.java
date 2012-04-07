
package org.inftel.androidrsa.adapters;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.activities.ContactsActivity;
import org.inftel.androidrsa.xmpp.Status;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final String TAG = "ContactsAdapter";

    public ContactsAdapter(Context context, String[] values) {
        super(context, R.layout.contactrow, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.contactrow, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.nombre);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values[position]);
        String s = values[position];
        setStatusImage(imageView, s);
        return rowView;
    }

    private void setStatusImage(ImageView iv, String nombre) {
        Roster roster = ContactsActivity.roster;
        for (RosterEntry entry : roster.getEntries()) {
            if ((entry.getName() != null) && (entry.getName().equals(nombre))) {
                int status = Status.getStatusFromPresence(roster.getPresence(entry.getUser()));
                // Log.i(TAG,entry.getName()+" : " + status);
                if ((status == Status.CONTACT_STATUS_AVAILABLE)
                        || (status == Status.CONTACT_STATUS_AVAILABLE_FOR_CHAT)) {
                    iv.setImageResource(R.drawable.status_available);
                }
                else if ((status == Status.CONTACT_STATUS_AWAY)
                        || (status == Status.CONTACT_STATUS_BUSY)) {
                    iv.setImageResource(R.drawable.status_idle);
                }
                else {
                    iv.setImageResource(R.drawable.status_away);
                }
                break;
            }
        }

    }
}
