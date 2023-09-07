package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
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
    
    public static void savePrivateKeyToFile(String filePath, PrivateKey privateKey) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PrivateKey loadPrivateKeyFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (PrivateKey) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
