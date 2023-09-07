package blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BlockChain implements Serializable {
    private List<Block> chain;

    public BlockChain() {
        chain = new ArrayList<>();
        // Create the Genesis block (the first block in the BlockChain)
        createGenesisBlock();
    }
 // Create the Genesis block
    private void createGenesisBlock() {
        Block genesisBlock = new Block(0, "0");
        chain.add(genesisBlock);
    }
    // Add a new block to the BlockChain
    public void addBlock(Block newBlock) {
        Block latestBlock = Block.getLatestBlock(chain);

        if (latestBlock != null) {
            newBlock.setPreviousHash(latestBlock.getHash());
            newBlock.setIndex(chain.size());
        } else {
            // Handle the case when the chain is empty (e.g., for the Genesis block)
            newBlock.setPreviousHash("0");
            newBlock.setIndex(0);
        }

        newBlock.setTimestamp(System.currentTimeMillis());
        newBlock.setHash(newBlock.calculateHash());
        chain.add(newBlock);
    }
    // View the entire BlockChain
    public void viewBlockChain() {
        for (Block block : chain) {
            System.out.println("Block #" + block.getIndex());
            System.out.println("Previous Hash: " + block.getPreviousHash());
            System.out.println("Timestamp: " + block.getTimestamp());
            System.out.println("Merkle Root: " + block.getMerkleRoot());
            System.out.println("Block Hash: " + block.getHash());
            System.out.println("Transactions:");

            for (Transaction transaction : block.getTransactions()) {
                System.out.println("  " + transaction.toString());
            }

            System.out.println("--------------------------------");
        }
    }
	public List<Block> getChain() {
		return chain;
	}
	public void setChain(List<Block> chain) {
		this.chain = chain;
	}
	public void addTransaction(Transaction transaction, int level) {
	    if (chain.isEmpty() || Block.getLatestBlock(chain).getTransactions().size() >= 4) {
	        // Create a new block and add it to the chain
	        Block newBlock = new Block(chain.size(), getLastBlockHash());
	        chain.add(newBlock);
	    }

	    boolean levelExists = true;
	    while (levelExists) {
	        Block lastBlock = Block.getLatestBlock(chain);
	        levelExists = false;

	        for (Transaction existingTransaction : lastBlock.getTransactions()) {
	            if (existingTransaction.getLevel() == level) {
	                levelExists = true;
	                break;
	            }
	        }

	        if (levelExists) {
	            // Create a new block and add it to the chain
	            Block newBlock = new Block(chain.size(), getLastBlockHash());
	            chain.add(newBlock);
	        }
	    }

	    // Find the last block in the chain and add the transaction to it
	    Block lastBlock = Block.getLatestBlock(chain);

	    // Sign the transaction before adding it
//	    transaction.signTransaction(privateKey);

	    lastBlock.addTransaction(transaction);
	}

	public String getLastBlockHash() {
        if (!chain.isEmpty()) {
            Block lastBlock = chain.get(chain.size() - 1);
            return lastBlock.getHash();
        } else {
            return "0"; // Return a default hash for the Genesis block
        }
    }
	private Block findBlockByLevel(int level) {
        for (Block block : chain) {
            for (Transaction transaction : block.getTransactions()) {
                if (transaction.getLevel() == level) {
                    return block;
                }
            }
        }
        return null;
    }

}
