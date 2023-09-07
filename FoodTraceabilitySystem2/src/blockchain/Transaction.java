package blockchain;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.crypto.Cipher;

import util.DigitalSignature;
import util.HashUtil;

public class Transaction implements Serializable {
    private int level;
    private String digitalSignature;
    private String location;
    private LocalDateTime timeIn;
    private LocalDateTime timeOut;
    private String userId;
    private String testResult;
    private String certifications;
    private KeyPair keyPair;
	private String hashedPrivateKey;
	private String privateKeyFilePath;

    public Transaction(int level, String location, LocalDateTime timeIn, LocalDateTime timeOut,
            String userId, String testResult, String certifications, KeyPair keyPair) {
		this.level = level;
		this.location = location;
		this.timeIn = timeIn;
		this.timeOut = timeOut;
		this.userId = userId;
		this.testResult = testResult;
		this.certifications = certifications;
		this.keyPair = keyPair;
	}
 
    public String calculateHash() {
        String dataToHash = level + location + timeIn + timeOut + userId + testResult + certifications;
        return HashUtil.sha3(dataToHash);
    }
    // Method to sign the transaction data
    public void signTransaction(PrivateKey privateKey) {
        String dataToSign = level + location + timeIn + timeOut + userId + testResult + certifications;
        digitalSignature = DigitalSignature.sign(dataToSign, privateKey);
    }
    // Method to verify the signature
    public boolean verifySignature(PublicKey publicKey) {
        String dataToVerify = level + location + timeIn + timeOut + userId + testResult + certifications;
        return DigitalSignature.verify(dataToVerify, digitalSignature, publicKey);
    }
    public String encryptData(String data, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptData(String encryptedData, PublicKey publicKey) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
 // Override toString for display
    @Override
    public String toString() {
        return "Transaction [level=" + level +
               ", location=" + location + ", timeIn=" + timeIn +
               ", timeOut=" + timeOut + ", userId=" + userId +
               ", testResult=" + testResult + ", certifications=" + certifications + "]";
    }
    
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getDigitalSignature() {
        return digitalSignature;
    }
    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDateTime getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(LocalDateTime timeIn) {
		this.timeIn = timeIn;
	}

	public LocalDateTime getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(LocalDateTime timeOut) {
		this.timeOut = timeOut;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTestResult() {
		return testResult;
	}

	public void setTestResult(String testResult) {
		this.testResult = testResult;
	}

	public String getCertifications() {
		return certifications;
	}

	public void setCertifications(String certifications) {
		this.certifications = certifications;
	}
	
	public KeyPair getKeyPair() {
		return keyPair;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	public String getHashedPrivateKey() {
		return hashedPrivateKey;
	}

	public void setHashedPrivateKey(String hashedPrivateKey) {
		this.hashedPrivateKey = hashedPrivateKey;
	}

	public void generateHashedPrivateKey() {
        if (keyPair != null) {
            String privateKeyString = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            this.hashedPrivateKey = HashUtil.sha3(privateKeyString);
        }
    }

    public String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    public void setPrivateKeyFilePath(String privateKeyFilePath) {
        this.privateKeyFilePath = privateKeyFilePath;
    }
    
}