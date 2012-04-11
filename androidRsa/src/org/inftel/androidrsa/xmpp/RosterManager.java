
package org.inftel.androidrsa.xmpp;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.util.StringUtils;

public class RosterManager {
    private static Roster roster = Conexion.getInstance().getRoster();

    public static Roster getRosterInstance() {
        if (roster != null) {
            return roster;
        }
        else {
            return Conexion.getInstance().getRoster();
        }
    }

    public static String findByName(String name) {
        for (RosterEntry entry : roster.getEntries()) {
            if ((entry.getName() != null) && (entry.getName().equals(name))) {
                return roster.getPresence(entry.getUser()).getFrom();
            }
        }
        return null;
    }

    public static boolean isSecure(String jid) {
        return StringUtils.parseResource(jid).startsWith("androidRSA");
    }
}
