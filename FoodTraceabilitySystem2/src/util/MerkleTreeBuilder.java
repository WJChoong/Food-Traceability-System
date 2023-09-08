package util;

import java.util.ArrayList;
import java.util.List;

public class MerkleTreeBuilder {

    public static String buildMerkleTree(List<String> transactionHashes) {
        if (transactionHashes == null || transactionHashes.isEmpty()) {
            return null; // Handle empty input or return a default hash
        }

        while (transactionHashes.size() > 1) {
            List<String> newLevel = new ArrayList<>();
            for (int i = 0; i < transactionHashes.size(); i += 2) {
                String leftHash = transactionHashes.get(i);
                String rightHash = (i + 1 < transactionHashes.size()) ? transactionHashes.get(i + 1) : leftHash;
                String combinedHash = hashConcatenate(leftHash, rightHash);
                newLevel.add(combinedHash);
            }
            transactionHashes = newLevel;
        }

        return transactionHashes.get(0); // Return the root hash
    }

    private static String hashConcatenate(String leftHash, String rightHash) {
        String concatenatedData = leftHash + rightHash;
        return concatenatedData;
    }
}
