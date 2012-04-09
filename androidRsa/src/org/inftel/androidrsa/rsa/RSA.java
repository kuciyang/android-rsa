
package org.inftel.androidrsa.rsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.cert.Certificate;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.inftel.androidrsa.R;
import org.jivesoftware.smack.util.Base64;

import android.content.Context;
import android.util.Log;

public class RSA {
    public static PrivateKey getPrivateKey(File privKeyFile) throws IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException {
        BufferedReader in = new BufferedReader(new FileReader(privKeyFile));
        String line = in.readLine();
        if (line.contains("-----BEGIN PRIVATE KEY-----") == false)
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
        Log.d("PRIVATE KEY", base64);
        in.close();

        byte[] privKeyBytes = Base64.decode(base64);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec ks = new PKCS8EncodedKeySpec(privKeyBytes);
        PrivateKey privKey = keyFactory.generatePrivate(ks);

        return privKey;
    }

    // public static PublicKey getPublicKeyFromCertificate(String path) throws
    // IOException,
    // CertificateException {
    // BufferedReader in = new BufferedReader(new FileReader(path));
    // String line = in.readLine();
    // if (line.contains("-----BEGIN CERTIFICATE-----") == false)
    // throw new IOException("Couldnt find");
    // line = line.substring(27);
    //
    // String base64 = new String();
    // boolean trucking = true;
    // while (trucking) {
    //
    // if (line.contains("-----")) {
    // trucking = false;
    // base64 += line.substring(0, line.indexOf("-----"));
    // }
    // else {
    // base64 += line;
    // line = in.readLine();
    // }
    // }
    // Log.d("CERTIFICATE", base64);
    // in.close();
    // byte[] certifacteData = Base64.decode(base64);
    // X509Certificate c = X509Certificate.getInstance(certifacteData);
    // return c.getPublicKey();
    // }

    public static Certificate getCertificate(String path) throws IOException,
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
        return c;
    }

    // public static void checkCertificate(String path, Context ctx) throws
    // IOException,
    // CertificateException, InvalidKeyException, NoSuchAlgorithmException,
    // NoSuchProviderException, SignatureException {
    // PublicKey caKey = getCAPublicKey(ctx);
    //
    // BufferedReader in = new BufferedReader(new FileReader(path));
    // String line = in.readLine();
    // if (line.contains("-----BEGIN CERTIFICATE-----") == false)
    // throw new IOException("Couldnt find");
    // line = line.substring(27);
    //
    // String base64 = new String();
    // boolean trucking = true;
    // while (trucking) {
    //
    // if (line.contains("-----")) {
    // trucking = false;
    // base64 += line.substring(0, line.indexOf("-----"));
    // }
    // else {
    // base64 += line;
    // line = in.readLine();
    // }
    // }
    // Log.d("CERTIFICATE", base64);
    // in.close();
    // byte[] certifacteData = Base64.decode(base64);
    // X509Certificate c = X509Certificate.getInstance(certifacteData);
    //
    // c.verify(caKey);
    // }

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

    // TO CIPHER A STRING USE: string.getBytes()
    public static byte[] cipher(byte[] text, PublicKey key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {

        byte[] encryptedBytes;
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        encryptedBytes = cipher.doFinal(text);

        return encryptedBytes;

    }

    // TO VIEW THE DECIPHER STRING USE: String str = new String (bytes)
    public static byte[] decipher(byte[] text, PrivateKey key) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {

        byte[] decryptedBytes;
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        decryptedBytes = cipher.doFinal(text);

        return decryptedBytes;
    }

}
