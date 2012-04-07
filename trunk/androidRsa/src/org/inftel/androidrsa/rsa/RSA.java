
package org.inftel.androidrsa.rsa;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.utils.AndroidRsaConstants;
import org.jivesoftware.smack.util.Base64;

import android.content.Context;
import android.util.Log;

public class RSA {
    public static PrivateKey getPrivateKey(Context ctx) throws IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException {

        // SharedPreferences prefs =
        // ctx.getSharedPreferences(AndroidRsaConstants.SHARED_PREFERENCE_FILE,
        // Context.MODE_PRIVATE);
        // String keyPath = prefs.getString(AndroidRsaConstants.KEY_PATH, "");

        // DEBUG
        String keyPath = AndroidRsaConstants.EXTERNAL_SD_PATH
                + File.separator + AndroidRsaConstants.KEY_NAME + "C1.pem";

        File privKeyFile = new File(keyPath);
        BufferedInputStream bis;

        bis = new BufferedInputStream(new FileInputStream(privKeyFile));

        byte[] privKeyBytes = new byte[(int) privKeyFile.length()];
        bis.read(privKeyBytes);
        bis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec ks = new PKCS8EncodedKeySpec(privKeyBytes);
        RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(ks);

        return privKey;

    }

    public static PublicKey getPublicKeyFromCertificate(String path) throws IOException,
            CertificateException {
        BufferedReader in = new BufferedReader(new FileReader(path));
        String line = in.readLine();
        if (line.contains("-----BEGIN CERTIFICATE-----") == false)
            throw new IOException("Couldnt find");
        line = line.substring(27);

        String base64 = new String();
        boolean trucking = true;
        while (trucking) {

            if (line.contains("-----")) {
                trucking = false;
                base64 += line.substring(0, line.indexOf("-----"));
            }
            else {
                base64 += line;
                line = in.readLine();
            }
        }
        Log.d("CERTIFICATE", base64);
        in.close();
        byte[] certifacteData = Base64.decode(base64);
        X509Certificate c = X509Certificate.getInstance(certifacteData);
        return c.getPublicKey();
    }

    public static void checkCertificate(String path, Context ctx) throws IOException,
            CertificateException, InvalidKeyException, NoSuchAlgorithmException,
            NoSuchProviderException, SignatureException {
        PublicKey caKey = getCAPublicKey(ctx);

        BufferedReader in = new BufferedReader(new FileReader(path));
        String line = in.readLine();
        if (line.contains("-----BEGIN CERTIFICATE-----") == false)
            throw new IOException("Couldnt find");
        line = line.substring(27);

        String base64 = new String();
        boolean trucking = true;
        while (trucking) {

            if (line.contains("-----")) {
                trucking = false;
                base64 += line.substring(0, line.indexOf("-----"));
            }
            else {
                base64 += line;
                line = in.readLine();
            }
        }
        Log.d("CERTIFICATE", base64);
        in.close();
        byte[] certifacteData = Base64.decode(base64);
        X509Certificate c = X509Certificate.getInstance(certifacteData);

        c.verify(caKey);
    }

    public static PublicKey getCAPublicKey(Context ctx) throws IOException,
            CertificateException {
        InputStream inputStream = ctx.getResources().openRawResource(R.raw.ca);
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String line = in.readLine();
        if (line.contains("-----BEGIN CERTIFICATE-----") == false)
            throw new IOException("Couldnt find");
        line = line.substring(27);

        String base64 = new String();
        boolean trucking = true;
        while (trucking) {

            if (line.contains("-----")) {
                trucking = false;
                base64 += line.substring(0, line.indexOf("-----"));
            }
            else {
                base64 += line;
                line = in.readLine();
            }
        }
        Log.d("CERTIFICATE", base64);
        in.close();
        byte[] certifacteData = Base64.decode(base64);
        X509Certificate c = X509Certificate.getInstance(certifacteData);
        return c.getPublicKey();
    }

    public static String cipherString(String text, PublicKey key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {

        byte[] textByte = android.util.Base64.decode(text, android.util.Base64.DEFAULT);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherData = cipher.doFinal(textByte);

        return cipherData.toString();
    }

    public static String decipherString(String text, PrivateKey key) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {

        byte[] textByte = android.util.Base64.decode(text, android.util.Base64.DEFAULT);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decipherData = cipher.doFinal(textByte);

        return decipherData.toString();
    }

}
