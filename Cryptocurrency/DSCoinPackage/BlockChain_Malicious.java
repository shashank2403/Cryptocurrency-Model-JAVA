package DSCoinPackage;

import HelperClasses.*;

public class BlockChain_Malicious 
{

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;
  
  public static boolean checkTransactionBlock (TransactionBlock tB) {
    if (!tB.dgst.substring(0, 4).equals("0000"))
      return false;
    CRF crf = new CRF(64);
    if (tB.previous == null)
    {
      if (!tB.dgst.equals(crf.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce)))
        return false;
    }
    else
    {
      if (!tB.dgst.equals(crf.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce)))
        return false;
    }
    MerkleTree tree = new MerkleTree();
    tree.Build(tB.trarray);
    if (!tB.trsummary.equals(tree.rootnode.val))
      return false;
    for (int i = 0;i<tB.trarray.length;i++)
      if (tB.checkTransaction(tB.trarray[i]) == false)
        return false;
    return true;
  }

  public TransactionBlock FindLongestValidChain () 
  {  
    int max = -1;
    TransactionBlock lBlock = null;
    int i = 0;
    while(lastBlocksList[i]!=null)
    {
      TransactionBlock tB = lastBlocksList[i];
      TransactionBlock validP = null;
      int count = 0;
      while(tB!=null)
      {
        if (!checkTransactionBlock(tB))
        {
          count = 0;
          validP = null;
        }
        else 
        {
          if (count==0)
            validP = tB;
          count++;
        }
        tB = tB.previous;
      }
      if (count>max)
      {
        lBlock = validP;
        max = count;
      }
      i++;
    }
    return lBlock;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) 
  {
    TransactionBlock lastBlock = FindLongestValidChain();
    
    int t = 1000000000;
    String d = "10101010";
    CRF c = new CRF(64);
    if (lastBlock == null) 
    {
      while (!d.substring(0, 4).equals("0000")) 
        d = c.Fn(start_string + "#" + newBlock.trsummary + "#" + Integer.toString(++t));
      newBlock.previous = null;      
    } 
    else 
    {
      while (!d.substring(0, 4).equals("0000")) 
        d = c.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + Integer.toString(++t));
      newBlock.previous = lastBlock;
    }    
    newBlock.nonce = Integer.toString(t);
    newBlock.dgst = d;
    int i=0;
	  while(lastBlocksList[i]!=null)
    {
		  if(lastBlock==lastBlocksList[i])
      {
			  lastBlocksList[i]=newBlock;
			  return;
		  }
		  i++;
	  }
	  lastBlocksList[i]=newBlock;
    return;
  }
}