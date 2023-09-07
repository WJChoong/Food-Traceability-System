package util;

import java.io.*;
import java.security.*;

public class KeyFileManager {
    private static final String KEY_DIRECTORY = "keys/";

    public static void storeKeyPair(KeyPair keyPair, String userId) throws IOException {
        File directory = new File(KEY_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(KEY_DIRECTORY + userId + "_keypair.ser"))) {
            oos.writeObject(keyPair);
        }
    }

    public static KeyPair loadKeyPair(String userId) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(KEY_DIRECTORY + userId + "_keypair.ser"))) {
            return (KeyPair) ois.readObject();
        }
    }
}

