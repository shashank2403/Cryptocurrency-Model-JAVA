package DSCoinPackage;

import HelperClasses.*;

public class TransactionBlock 
{

  public Transaction[] trarray;
  public TransactionBlock previous;
  public TransactionBlock next;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) 
  {
    trarray = t.clone();
    previous = null;
    Tree = new MerkleTree();
    trsummary = Tree.Build(t);
    dgst = null;
  } 

  public boolean checkTransaction (Transaction t) 
  {    
    if (t.coinsrc_block == null)
      return true;
    TransactionBlock temp = this;
    while(temp!=t.coinsrc_block && temp!=null)
    {
      for(int i = 0;i<temp.trarray.length;i++)
        if (temp.trarray[i].coinID.equals(t.coinID))
          return false;    
      temp = temp.previous;
    }
    boolean found = false;
    for (int i = 0;i<t.coinsrc_block.trarray.length && !found;i++)
      if (t.coinID.equals(t.coinsrc_block.trarray[i].coinID) && t.Source.equals(t.coinsrc_block.trarray[i].Destination))
        found = true;
    if (found)
      return true;
    return false;
  }
}