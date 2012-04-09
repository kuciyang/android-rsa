
package org.inftel.androidrsa.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.utils.AndroidRsaConstants;
import org.inftel.androidrsa.utils.Server;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

/**
 * @author http://www.android-codes-examples.blogspot.com/
 */

public class ServersActivity extends ListActivity {
    private static final String TAG = "ServersActivity";

    private static ArrayList<String> items;
    private ArrayList<Server> listOn;
    private ArrayList<Server> toRemove;
    private ArrayList<CheckedTextView> toRemoveChecked;
    private boolean isManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servers);

        // Lists
        toRemove = new ArrayList<Server>();
        toRemoveChecked = new ArrayList<CheckedTextView>();

        // Fill list
        try {
            listOn = readServer(AndroidRsaConstants.SERVERS_FILE_PATH);
        } catch (IOException e) {
            listOn = new ArrayList<Server>();
            e.printStackTrace();
        }
        items = new ArrayList<String>();

        for (Server s : listOn) {
            items.add(s.getService());
        }

        // Obtiene el objeto ListView
        ListView objListView = getListView();

        // Modo de selecci—n
        objListView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, items));

        // Muestra info de usuario al hacer pulsacion larga
        objListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {
                Server selectedServer = listOn.get(position);
                Intent i = new Intent(ServersActivity.this, ServersInfoActivity.class);
                i.putExtra("host", selectedServer.getHost());
                i.putExtra("port", selectedServer.getPort());
                i.putExtra("service", selectedServer.getService());
                startActivity(i);
                return false;
            }
        });

    }

    public static ArrayList<Server> readServer(String path) throws IOException {
        ArrayList<Server> list = new ArrayList<Server>();
        BufferedReader in = new BufferedReader(new FileReader(path));
        String line = in.readLine();
        StringTokenizer tokens;
        while (line != null) {
            Server s = new Server();
            tokens = new StringTokenizer(line, ":");
            s.setHost(tokens.nextToken());
            s.setPort(tokens.nextToken());
            s.setService(tokens.nextToken());
            list.add(s);
            line = in.readLine();
        }
        in.close();
        return list;
    }

    public static void deleteServer(ArrayList<Server> toRemove, String path) throws IOException {
        ArrayList<Server> servers = readServer(path);
        servers.removeAll(toRemove);

        File file = new File(path);
        FileWriter writer = new FileWriter(file, false);
        for (Server s : servers)
            writer.write(s.getHost() + ":" + s.getPort() + ":" + s.getService() + '\n');

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {

        // A–ade o borra, elementos seleccionados de la lista
        CheckedTextView check = (CheckedTextView) v;
        if (!check.isChecked()) {
            toRemove.add(listOn.get(position));
            toRemoveChecked.add(check);
        }
        else {
            toRemove.remove(listOn.get(position));
            toRemoveChecked.remove(check);
        }
        // Si el check est‡ activo, lo desactivo y viceversa
        check.setChecked(!check.isChecked());

    }

    public void onClickButtonMinus(View view) {
        if (!toRemove.isEmpty()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.info_r_u_sure_permanently)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    // Actualiza el ListView
                                    for (CheckedTextView check : toRemoveChecked)
                                        check.setChecked(false);

                                    for (Server s : toRemove) {
                                        ((ArrayAdapter) getListAdapter()).remove(s.getService());
                                    }

                                    try {
                                        deleteServer(toRemove,
                                                AndroidRsaConstants.SERVERS_FILE_PATH);
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            })
                    .setNegativeButton(getResources().getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void onClickButtonPlus(View view) {
        Intent i = new Intent(this, ServersAddActivity.class);
        startActivity(i);
    }
}
