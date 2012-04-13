
package org.inftel.androidrsa.rsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import javax.security.cert.Certificate;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.inftel.androidrsa.R;
import org.jivesoftware.smack.util.Base64;

import android.content.Context;
import android.util.Log;

public class RSA {

    // public static PrivateKey getPrivateKey(File privKeyFile) throws
    // IOException,
    // NoSuchAlgorithmException,
    // InvalidKeySpecException {
    // BufferedReader in = new BufferedReader(new FileReader(privKeyFile));
    // String line = in.readLine();
    // if (line.contains("-----BEGIN PRIVATE KEY-----") == false)
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
    // Log.d("PRIVATE KEY", base64);
    // in.close();
    //
    // byte[] privKeyBytes = Base64.decode(base64);
    //
    // KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    // KeySpec ks = new PKCS8EncodedKeySpec(privKeyBytes);
    // PrivateKey privKey = keyFactory.generatePrivate(ks);
    //
    // return privKey;
    // }

    public static PrivateKey getPrivateKeyDecryted(byte[] pk, String passphrase)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, NoSuchProviderException,
            UnsupportedEncodingException, InvalidKeySpecException {
        byte[] privKeyDecrypted = decrytpKey(pk, passphrase);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec ks = new PKCS8EncodedKeySpec(privKeyDecrypted);
        PrivateKey privKey = keyFactory.generatePrivate(ks);
        return privKey;
    }

    public static byte[] getPrivateKeyEncrytedBytes(File privKeyFile, String passphrase)
            throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
            NoSuchProviderException, NoSuchPaddingException, ShortBufferException,
            IllegalBlockSizeException, BadPaddingException {

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
        Log.d("PRIVATE KEY BYTES", privKeyBytes.toString());
        return encrytpKey(privKeyBytes, passphrase);
    }

    public static byte[] encrytpKey(byte[] input, String passphrase)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, NoSuchProviderException, UnsupportedEncodingException {

        SecretKeySpec key = generateKey(passphrase);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(input);

        return cipherText;
    }

    public static byte[] decrytpKey(byte[] input, String passphrase)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, NoSuchProviderException, UnsupportedEncodingException {

        SecretKeySpec key = generateKey(passphrase);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(input);

        return plainText;
    }

    public static SecretKeySpec generateKey(String passphrase) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {
        byte[] key = (passphrase).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
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
    public static String cipher(String text, PublicKey key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {

        byte[] bytes = text.getBytes("UTF-8");

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(bytes);

        return new String(encryptedBytes, "UTF-8");
    }

    // TO VIEW THE DECIPHER STRING USE: String str = new String (bytes)
    public static String decipher(String text, PrivateKey key) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {

        byte[] bytes = text.getBytes("UTF-8");
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(bytes);

        return new String(decryptedBytes, "UTF-8");
    }

    // USE TO CIPHER AND DECIPHER
    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

}
