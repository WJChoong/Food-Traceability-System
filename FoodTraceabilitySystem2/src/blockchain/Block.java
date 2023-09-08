package blockchain;

import java.io.Serializable;
import java.security.KeyPair;
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
    private int nonce; 
    private String digitalSignature;

    public Block(int index, String previousHash) {
        this.index = index;
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.transactions = new ArrayList<>();
        this.merkleRoot = calculateMerkleRoot();
        this.nonce = 0; 
        this.hash = calculateHash();
    }

    // Calculate the hash of the current block
    public String calculateHash() {
        String data = index + previousHash + timestamp + merkleRoot + nonce;
        return HashUtil.sha3(data);
    }

    // Calculate the Merkle root of the transactions using a basic concatenation approach
    private String calculateMerkleRoot() {
        List<String> transactionHashes = new ArrayList<>();
        for (Transaction tx : transactions) {
            transactionHashes.add(tx.calculateHash());
        }
        return MerkleTreeBuilder.buildMerkleTree(transactionHashes);
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        for (Transaction tx : transactions) {
            KeyPair keyPair = DigitalSignature.generateKeyPair();
            tx.setKeyPair(keyPair);
            tx.generateHashedPrivateKey();

            // Save private key to a file
            String privateKeyFilePath = "private_key_" + tx.getUserId() + ".dat";
            DigitalSignature.savePrivateKeyToFile(privateKeyFilePath, keyPair.getPrivate());

            // Store the file path in the transaction data
            tx.setPrivateKeyFilePath(privateKeyFilePath);
        }

        System.out.println("Block mined: " + hash);
    } 
    public static Block getLatestBlock(List<Block> chain) {
        if (chain.isEmpty()) {
            return null; 
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
