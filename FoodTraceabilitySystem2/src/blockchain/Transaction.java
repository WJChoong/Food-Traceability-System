package blockchain;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;

import util.DigitalSignature;
import util.HashUtil;

public class Transaction implements Serializable {
//	private String data;
//    private String sender;
//    private String receiver;
    private int level;
    private String digitalSignature;
    private String location;
    private LocalDateTime timeIn;
    private LocalDateTime timeOut;
    private String userId;
    private String testResult;
    private String certifications;

    public Transaction(int level, String location, LocalDateTime timeIn, LocalDateTime timeOut,
            String userId, String testResult, String certifications) {
		this.level = level;
		this.location = location;
		this.timeIn = timeIn;
		this.timeOut = timeOut;
		this.userId = userId;
		this.testResult = testResult;
		this.certifications = certifications;
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
    
}