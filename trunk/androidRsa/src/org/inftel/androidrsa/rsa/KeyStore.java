
package org.inftel.androidrsa.rsa;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.security.cert.Certificate;

public class KeyStore {
    private static KeyStore Instance = new KeyStore();
    private Map<String, Certificate> data;
    private PrivateKey pk;
    private PublicKey pb;

    private KeyStore() {
        data = new HashMap<String, Certificate>();
    }

    public static KeyStore getInstance() {
        return Instance;
    }

    public void setCertificate(String alias, Certificate cert) {
        if (data.containsKey(alias)) {
            data.remove(alias);
            data.put(alias, cert);
        } else {
            data.put(alias, cert);
        }
    }

    public Certificate getCertificate(String alias) {
        return data.get(alias);
    }

    public PrivateKey getPk() {
        return pk;
    }

    public void setPk(PrivateKey pk) {
        this.pk = pk;
    }

    public PublicKey getPb() {
        return pb;
    }

    public void setPb(PublicKey pb) {
        this.pb = pb;
    }
}
