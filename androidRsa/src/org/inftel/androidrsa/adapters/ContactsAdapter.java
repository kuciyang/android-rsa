
package org.inftel.androidrsa.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.activities.ContactsActivity;
import org.inftel.androidrsa.xmpp.AvatarsCache;
import org.inftel.androidrsa.xmpp.RosterManager;
import org.inftel.androidrsa.xmpp.Status;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Context;
import android.graphics.Bitmap;
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
    private HashMap<String, Bitmap> avatarMap;

    static class ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public ImageView imageViewSec;
        public ImageView imageViewAvatar;
    }

    public ContactsAdapter(Context context, ArrayList<String> lista) {
        super(context, R.layout.contactrow, lista);
        this.context = context;
        this.list = lista;
        AvatarsCache.clear();
        AvatarsCache.populateFromRoster(ContactsActivity.roster);
        this.avatarMap = AvatarsCache.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.contactrow, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) rowView.findViewById(R.id.nombre);
            viewHolder.imageView = (ImageView) rowView.findViewById(R.id.icon);
            viewHolder.imageViewSec = (ImageView) rowView.findViewById(R.id.iconsec);
            viewHolder.imageViewAvatar = (ImageView) rowView.findViewById(R.id.avatar);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.imageViewSec.setVisibility(View.GONE);
        RosterEntry entry = RosterManager.findByJid(list.get(position));
        String name = null;
        if (entry != null) {
            name = entry.getName();
        }
        if (name != null) {
            holder.textView.setText(name);
        }
        String jid = list.get(position);
        setIcons(holder.imageView, holder.imageViewSec, jid, rowView);
        if (avatarMap.containsKey(jid) && (avatarMap.get(jid)) != null) {
            holder.imageViewAvatar.setImageBitmap(avatarMap.get(jid));
        }
        else {
            holder.imageViewAvatar.setImageResource(R.drawable.ic_launcher);
        }
        return rowView;
    }

    private void setIcons(ImageView iv, ImageView ivSec, String jid,
            View rowview) {
        Roster roster = ContactsActivity.roster;

        RosterEntry entry = RosterManager.findByJid(jid);
        if ((entry != null) && (entry.getName() != null)) {
            // icono estado
            int status = Status.getStatusFromPresence(roster.getPresence(entry.getUser()));
            if (roster.getPresence(entry.getUser()).equals(Presence.Type.unsubscribed)) {
                iv.setImageResource(R.drawable.status_unsubscribed);
            }
            else if ((status == Status.CONTACT_STATUS_AVAILABLE)
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

            // icono RSA
            if (StringUtils.parseResource(roster.getPresence(jid).getFrom()).startsWith(
                    "androidRSA")) {
                ivSec.setImageResource(R.drawable.secure);
                ivSec.setVisibility(View.VISIBLE);
            }
            else {
                ivSec.setVisibility(View.GONE);
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
