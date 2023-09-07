package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import blockchain.BlockChain;
import blockchain.Transaction;
import util.DigitalSignature;

public class Main {
	static Scanner scanner = new Scanner(System.in);
    static int userLevel = 0;
    static String userId = null;
    static List<String[]> userData = new ArrayList<>();

    public static void main(String[] args) {
        loadUserData();

        while (true) {
            if (login()) {
                displayMainMenu();
                int choice = getValidChoice();
                scanner.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        addInformation();
                        break;
                    case 2:
                        viewBlockchain();
                        break;
                    case 3: 
                        logout();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
        		System.out.println("Invalid credentials. Please try again.");
            }
        }
    }

    static void loadUserData() {
        File file = new File("user.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));

                // Default user data
                String defaultUsers = "1,Supplier,supplier,supplier123,Location1,1,Active\n" +
                        "2,Warehouse,warehouse,warehouse123,Location2,2,Active\n" +
                        "3,Transportation,transportation,transportation123,Location3,3,Active\n" +
                        "4,Retail,retail,retail123,Location4,4,Active";

                writer.write(defaultUsers);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("user.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDataArray = line.split(",");
                userData.add(userDataArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static boolean login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader("user.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDataArray = line.split(",");
                if (userDataArray[2].equals(username) && userDataArray[3].equals(password)) {
                    userId = userDataArray[0];
                    userLevel = Integer.parseInt(userDataArray[5]);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
    static void logout() {
        userLevel = 0;
        userId = null;
        userData.clear(); // Clear user data on logout
    }
    static void returnToLogin() {
        userData.clear(); // Clear user data on logout
    }
    static void displayMainMenu() {
        System.out.println("Main Menu");
        System.out.println("1. Add Information");
        System.out.println("2. View Blockchain");
        System.out.println("3. Log Out");
    } 
    static int getValidChoice() {
        int choice;
        while (true) {
            try {
                System.out.print("Enter your choice: ");
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < 1 || choice > 3) {
                    throw new NumberFormatException();
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number (1 or 2).");
            }
        }
        // Consume the remaining newline character
        scanner.nextLine();
        return choice;
    }
    static void addInformation() {
        if (userLevel >= 1) {
            System.out.println("Adding information as Supplier");

            // Create a blockchain and add some transactions
            BlockChain blockchain = new BlockChain();
            if (new File("blockchain.bin").exists()) {
                blockchain = deserializeBlockchain("blockchain.bin");
            }

            // Generate key pair for digital signature
            KeyPair keyPair = DigitalSignature.generateKeyPair();

         // Get input from user for location, testResult, and certifications
            System.out.print("Enter location: ");
            String location = scanner.nextLine();
            System.out.print("Enter test result: ");
            String testResult = scanner.nextLine();
            System.out.print("Enter certifications: ");
            String certifications = scanner.nextLine();

            // Add transactions to the blockchain with user input
            LocalDateTime now = LocalDateTime.now();

            Transaction transaction = new Transaction(userLevel,
                    location, null, now,
                    userId, testResult, certifications
            );
            
            // Sign the transactions
            transaction.signTransaction(keyPair.getPrivate());

            blockchain.addTransaction(transaction, 1);

            // Display the initial blockchain
            System.out.println("Initial Blockchain:");
            blockchain.viewBlockChain();

            // Serialize and save the blockchain to a binary file
            serializeBlockchain(blockchain, "blockchain.bin");
        } else {
            System.out.println("You don't have permission to add information.");
        }
    }
    static void viewBlockchain() {
        if (userLevel >= 1) {
            System.out.println("Viewing blockchain");
            // Load the blockchain from the binary file
            BlockChain loadedBlockchain = deserializeBlockchain("blockchain.bin");

            // Display the loaded blockchain
            System.out.println("\nLoaded Blockchain:");
            loadedBlockchain.viewBlockChain();
        } else {
            System.out.println("You don't have permission to view blockchain.");
        }
    }
 // Serialize and save the blockchain to a binary file
    private static void serializeBlockchain(BlockChain blockchain, String filename) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            outputStream.writeObject(blockchain);
            System.out.println("Blockchain serialized and saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Load the blockchain from a binary file
    private static BlockChain deserializeBlockchain(String filename) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            return (BlockChain) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
 // Save the private key to a keystore file
    private static void savePrivateKeyToKeystore(PrivateKey privateKey, String filename, String password) {
        DigitalSignature.savePrivateKey(filename, privateKey, password);
    }
    // Load the private key from the keystore file
    private static PrivateKey loadPrivateKeyFromKeystore(String filename, String password) {
        return DigitalSignature.loadPrivateKey(filename, password);
    }
}
