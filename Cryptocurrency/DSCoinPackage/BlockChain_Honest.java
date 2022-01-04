package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest 
{

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock)
  {    
    CRF crf = new CRF(64);
    int num = 1000000000;
    String nonce = "1000000001";
    
    if (lastBlock == null) 
    {
      while (!nonce.substring(0, 4).equals("0000"))      
        nonce = crf.Fn(start_string + "#" + newBlock.trsummary + "#" + Integer.toString(++num));
      newBlock.nonce = Integer.toString(num);
      newBlock.dgst = nonce;
      newBlock.previous = null;
      lastBlock = newBlock;
    }
    else
    {
      while (!nonce.substring(0, 4).equals("0000")) 
        nonce = crf.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + Integer.toString(++num));      
      newBlock.nonce = Integer.toString(num);
      newBlock.dgst = nonce;
      newBlock.previous = lastBlock;
      lastBlock = newBlock;
    }
    return;
  }
}