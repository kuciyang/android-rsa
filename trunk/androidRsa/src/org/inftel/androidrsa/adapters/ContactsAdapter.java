
package org.inftel.androidrsa.adapters;

import java.util.ArrayList;

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
    private Context context;
    private ArrayList<String> list;
    private String TAG = "ContactsAdapter";

    public ContactsAdapter(Context context, ArrayList<String> lista) {
        super(context, R.layout.contactrow, lista);
        this.context = context;
        this.list = lista;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.contactrow, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.nombre);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        ImageView imageViewSec = (ImageView) rowView.findViewById(R.id.iconsec);
        imageViewSec.setVisibility(View.GONE);
        textView.setText(list.get(position));
        String nombre = list.get(position);
        setImages(imageView, imageViewSec, nombre, rowView);
        return rowView;
    }

    private void setImages(ImageView iv, ImageView ivSec, String nombre, View rowview) {
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
                // TODO añadir subscription pending y separar seguro de inseguro
                else {
                    iv.setImageResource(R.drawable.status_away);
                }
                break;
            }
            if ((roster.getPresence(entry.getUser()).getProperty("rsaEnabled")
                        != null)
                    && ((Boolean)
                    roster.getPresence(entry.getUser()).getProperty("rsaEnabled"))) {
                ivSec.setImageResource(R.drawable.secure);
                ivSec.setVisibility(View.VISIBLE);
            }
        }

    }

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

}
