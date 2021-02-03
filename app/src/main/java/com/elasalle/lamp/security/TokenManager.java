package com.elasalle.lamp.security;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.elasalle.lamp.LampApp;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

public class TokenManager {

    private static final String TAG = TokenManager.class.getSimpleName();
    private static final String NAME = "encrypted-token";

    private final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private final String ALGORITHM = "RSA";
    private final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private final String ALIAS = "lamp-auth-token";
    private final BigInteger CERTIFICATE_SERIAL_NUMBER = new BigInteger("201603311844");
    private final String CERTIFICATE_SUBJECT = "CN=LaSalle Solutions - LAMP";
    private final String PROVIDER = "AndroidKeyStoreBCWorkaround";

    public void saveToken(String token) {
        try {
            encryptAndSaveToken(token);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            LampApp.getSessionManager().token(token);
        }

    }

    public String getToken() {
        return retrieveAndDecryptToken();
    }

    public static boolean deleteToken() {
        return FileUtils.deleteQuietly(getTokenFile());
    }

    private void encryptAndSaveToken(String token) throws Exception {
        PublicKey publicKey = getPublicKey();
        Cipher cipher = getCipherInstance();
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedToken = cipher.doFinal(token.getBytes());
        FileOutputStream outputStream = LampApp.getInstance().openFileOutput(NAME, Context.MODE_PRIVATE);
        outputStream.write(Base64.encodeToString(encryptedToken, Base64.DEFAULT).getBytes());
        outputStream.close();
    }

    private String retrieveAndDecryptToken() {
        byte[] decryptedToken = null;
        try {
            PrivateKey privateKey =  getPrivateKey();
            Cipher cipher = getCipherInstance();
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encryptedTokenDecoded = Base64.decode(getEncryptedTokenBytes(), Base64.DEFAULT);
            decryptedToken = cipher.doFinal(encryptedTokenDecoded);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (decryptedToken == null) {
            Log.e(TAG, "Decrypted token is null");
            String token = LampApp.getSessionManager().token();
            if (token == null) {
                Log.e(TAG, "Session token is null");
                token = "";
            }
            return token;
        } else {
            return new String(decryptedToken);
        }
    }

    private Cipher getCipherInstance() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Cipher.getInstance(TRANSFORMATION, PROVIDER);
        } else {
            return Cipher.getInstance(TRANSFORMATION);
        }
    }

    private byte[] getEncryptedTokenBytes() throws IOException {
        String encryptedToken = FileUtils.readFileToString(getTokenFile());
        return encryptedToken.getBytes();
    }

    private PublicKey getPublicKey() throws Exception {
        Certificate certificate = getCertificate();
        PublicKey publicKey = certificate == null ? null : certificate.getPublicKey();
        if (publicKey == null) {
            publicKey = generateKeyPair().getPublic();
        }
        return publicKey;
    }

    private Certificate getCertificate() throws Exception {
        AlgorithmParameterSpec spec = getAlgorithmParameterSpec();
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        Certificate certificate;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            certificate = ks.getCertificate(((KeyGenParameterSpec)spec).getKeystoreAlias());
        } else {
            //noinspection deprecation
            certificate = ks.getCertificate(((KeyPairGeneratorSpec)spec).getKeystoreAlias());
        }
        return certificate;
    }

    private KeyPair generateKeyPair() throws Exception {
        AlgorithmParameterSpec spec = getAlgorithmParameterSpec();
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM, ANDROID_KEY_STORE);
        kpg.initialize(spec);
        return kpg.generateKeyPair();
    }

    private PrivateKey getPrivateKey() throws Exception {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(ALIAS, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            Log.w(this.getClass().getSimpleName(), "Not an instance of a PrivateKeyEntry");
            return null;
        }
        return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
    }

    @NonNull
    private static File getTokenFile() {
        return new File(LampApp.getInstance().getFilesDir(), NAME);
    }

    @NonNull
    private AlgorithmParameterSpec getAlgorithmParameterSpec() {
        AlgorithmParameterSpec spec;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            spec = new KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setCertificateSubject(new X500Principal(CERTIFICATE_SUBJECT))
                    .setCertificateSerialNumber(CERTIFICATE_SERIAL_NUMBER)
                    .setCertificateNotBefore(new DateTime(2016, 3, 31, 18, 49).toDate())
                    .setCertificateNotAfter(new DateTime(2036, 3, 31, 18, 49).toDate())
                    .build();
        } else {
            //noinspection deprecation
            spec = new KeyPairGeneratorSpec.Builder(LampApp.getInstance())
                    .setAlias(ALIAS)
                    .setStartDate(new DateTime(2016, 3, 31, 18, 49).toDate())
                    .setEndDate(new DateTime(2036, 3, 31, 18, 49).toDate())
                    .setSerialNumber(CERTIFICATE_SERIAL_NUMBER)
                    .setSubject(new X500Principal(CERTIFICATE_SUBJECT))
                    .build();
        }
        return spec;
    }
}
