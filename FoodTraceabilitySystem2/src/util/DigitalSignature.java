package util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Certificate;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.util.Base64;

public class DigitalSignature {

	public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String sign(String data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA512withRSA"); // Changed to SHA512
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            byte[] signatureBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verify(String data, String signature, PublicKey publicKey) {
        try {
            Signature verifier = Signature.getInstance("SHA512withRSA"); // Changed to SHA512
            verifier.initVerify(publicKey);
            verifier.update(data.getBytes());
            byte[] signatureBytes = Base64.getDecoder().decode(signature);
            return verifier.verify(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void savePrivateKey(String filePath, PrivateKey privateKey, String password) {
        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(null, null);

            // Instead of creating a certificate, we directly add the private key
            keystore.setKeyEntry("privateKey", privateKey, password.toCharArray(), null);

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                keystore.store(fos, password.toCharArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PrivateKey loadPrivateKey(String filePath, String password) {
        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(filePath);
            keystore.load(fis, password.toCharArray());

            Key key = keystore.getKey("privateKey", password.toCharArray());
            return (PrivateKey) key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
