package blockchain;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import util.DigitalSignature;
import util.HashUtil;
import util.MerkleTreeBuilder;

public class Block implements Serializable {
	private int index;
    private String previousHash;
    private long timestamp;
    private List<Transaction> transactions;
    private String merkleRoot;
    private String hash;
    private int nonce; // Add a nonce field for proof-of-work
    private String digitalSignature;

    public Block(int index, String previousHash) {
        this.index = index;
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.transactions = new ArrayList<>();
        this.merkleRoot = calculateMerkleRoot();
        this.nonce = 0; // Initialize nonce to 0
        this.hash = calculateHash();
    }

    // Calculate the hash of the current block
    public String calculateHash() {
        String data = index + previousHash + timestamp + merkleRoot + nonce;
        return HashUtil.sha3(data); // Use a suitable hash function (e.g., SHA-256)
    }

 // Calculate the Merkle root of the transactions using a basic concatenation approach
    private String calculateMerkleRoot() {
        List<String> transactionHashes = new ArrayList<>();
        for (Transaction tx : transactions) {
            transactionHashes.add(tx.calculateHash());
        }
        return MerkleTreeBuilder.buildMerkleTree(transactionHashes);
    }


//    // Build a Merkle tree from a list of transaction hashes
//    private String constructMerkleTree(List<String> transactionHashes) {
//        if (transactionHashes.isEmpty()) {
//            return "0"; // Return a default hash for an empty tree
//        }
//        if (transactionHashes.size() == 1) {
//            return transactionHashes.get(0); // Return the hash of the single transaction
//        }
//
//        List<String> newLevel = new ArrayList<>();
//        // Combine adjacent hashes and hash them together
//        for (int i = 0; i < transactionHashes.size(); i += 2) {
//            String leftHash = transactionHashes.get(i);
//            String rightHash = (i + 1 < transactionHashes.size()) ? transactionHashes.get(i + 1) : leftHash;
//            newLevel.add(HashUtil.sha3(leftHash + rightHash)); // Concatenate and hash
//        }
//
//        // Recursively build the next level of the Merkle tree
//        return constructMerkleTree(newLevel);
//    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); // Create a target with leading zeros
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block mined: " + hash);
    } 
    public static Block getLatestBlock(List<Block> chain) {
        if (chain.isEmpty()) {
            return null; // Return null if the chain is empty
        }
        return chain.get(chain.size() - 1);
    }

    // Getters and setters for the Block class
    public List<Transaction> getTransactions() {
        return transactions;
    }
    public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getPreviousHash() {
		return previousHash;
	}
	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getMerkleRoot() {
		return merkleRoot;
	}
	public void setMerkleRoot(String merkleRoot) {
		this.merkleRoot = merkleRoot;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public int getNonce() {
		return nonce;
	}
	public void setNonce(int nonce) {
		this.nonce = nonce;
	}
	 public String getDigitalSignature() {
        return digitalSignature;
    }
    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }
	
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        merkleRoot = calculateMerkleRoot(); // Recalculate the Merkle root when adding a transaction
    }
	// Method to sign the block's data
    public void signBlock(PrivateKey privateKey) {
        String dataToSign = index + previousHash + timestamp + merkleRoot + nonce;
        digitalSignature = DigitalSignature.sign(dataToSign, privateKey);
    }
    // Method to verify the signature
    public boolean verifySignature(PublicKey publicKey) {
        String dataToVerify = index + previousHash + timestamp + merkleRoot + nonce;
        return DigitalSignature.verify(dataToVerify, digitalSignature, publicKey);
    }
}