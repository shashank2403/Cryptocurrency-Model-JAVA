package DSCoinPackage;

import java.util.*;
import HelperClasses.*;

public class Members
{
  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;
  int trans_num;

  Members()
  {
    trans_num = 0;
    in_process_trans = new Transaction[100];
  }

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) 
  {
    String coinID = mycoins.get(0).first;
    TransactionBlock coinsrc_block = mycoins.get(0).second;
    mycoins.remove(0);
    Transaction tobj = new Transaction();
    tobj.coinID = coinID;
    tobj.Source = this;
    tobj.coinsrc_block = coinsrc_block;
    tobj.next = null;
    for (int i = 0;i<DSobj.memberlist.length;i++)
      if (DSobj.memberlist[i].UID.equals(destUID))
      {
        tobj.Destination = DSobj.memberlist[i];
        break;
      }
    in_process_trans[trans_num++] = tobj;
    DSobj.pendingTransactions.AddTransactions(tobj);
  }

  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) 
  {
    String coinID = mycoins.get(0).first;
    TransactionBlock coinsrc_block = mycoins.get(0).second;
    mycoins.remove(0);
    Transaction tobj = new Transaction();
    tobj.coinID = coinID;
    tobj.Source = this;
    tobj.coinsrc_block = coinsrc_block;
    tobj.next = null;
    for (int i = 0;i<DSobj.memberlist.length;i++)
      if (DSobj.memberlist[i].UID.equals(destUID))
      {
        tobj.Destination = DSobj.memberlist[i];
        break;
      }
    in_process_trans[trans_num++] = tobj;
    DSobj.pendingTransactions.AddTransactions(tobj);
  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException 
  {
    TransactionBlock B = DSObj.bChain.lastBlock;
    TransactionBlock tB = null;
    boolean found = false;

    while (!found)
    {      
      for (int i = 0; i < B.trarray.length; i++)
        if (B.trarray[i].equals(tobj)) 
        {
          tB = B;
          found = true;
          break;
        }
      if (found)
        break;        
      if (B.previous == null && !found)
        throw new MissingTransactionException();
      if (B.previous != null)
        B = B.previous;
    }
    
    int index = 0;
    for (int i = 0; i < tB.trarray.length; i++)
      if (tB.trarray[i] == tobj) 
      {
        index = i;
        break;
      }
    
    TreeNode node = new TreeNode();
    node = tB.Tree.rootnode;

    int len = tB.trarray.length;
    while (len > 1) 
    {
      if (index < len / 2)
        node = node.left;
      else
        node = node.right;
      len = len / 2;
      index = index % len;
    }

    //SCPTR
    List<Pair<String, String>> list1 = new LinkedList<Pair<String, String>>();
    List<Pair<String, String>> list2 = new LinkedList<Pair<String, String>>();

    while (node != tB.Tree.rootnode) 
    {
      if (node.parent.left == node) 
        list1.add(new Pair<String, String>(node.val, node.parent.right.val));
      else 
        list1.add(new Pair<String, String>(node.parent.left.val, node.val));
      node = node.parent;
    }
    list1.add(new Pair<String, String>(tB.Tree.rootnode.val, null));
    
    List<Pair<String, String>> tlist = new LinkedList<Pair<String, String>>();
    
    TransactionBlock temp =  DSObj.bChain.lastBlock;
    while (temp != tB)
    {
      tlist.add(new Pair<String, String>(temp.dgst, temp.previous.dgst + "#" + temp.trsummary + "#" + temp.nonce));
      temp = temp.previous;
    }
    tlist.add(new Pair<String, String>(temp.dgst, temp.previous.dgst + "#" + temp.trsummary + "#" + temp.nonce));
    tlist.add(new Pair<String, String>(tB.previous.dgst, null));

    for (int i = tlist.size()- 1; i >=0 ; i--)
      list2.add(tlist.get(i));   

    //Remove tobj from in_process_trans
    for (int i = 0; i < trans_num; i++)
      if (in_process_trans[i] == tobj) 
      {
        for (int j = i; j < trans_num - 1; j++)
          in_process_trans[i] = in_process_trans[i+1];
        in_process_trans[trans_num-1] = null;
        break;
      }
    trans_num--;

    for (int i = 0; i < tobj.Destination.mycoins.size(); i++)
      if (tobj.Destination.mycoins.get(i).first.compareTo(tobj.coinID) > 0) 
      {
        tobj.Destination.mycoins.add(i, new Pair<String, TransactionBlock>(tobj.coinID, tB));
        break;
      }
    return new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(list1, list2);
  }

  public void MineCoin(DSCoin_Honest DSObj) 
  {
    Transaction[] trarray = new Transaction[DSObj.bChain.tr_count];
    int count = 0;
    while(count<DSObj.bChain.tr_count-1 && DSObj.pendingTransactions.firstTransaction!=null)
    {
      try
      {
        Transaction t = DSObj.pendingTransactions.RemoveTransaction();
        boolean found = false;
        for (int i = 0;i<count && !found;i++)
          if (trarray[i].coinID.equals(t.coinID))
            found = true;
        if (!found && DSObj.bChain.lastBlock.checkTransaction(t))
          trarray[count++] = t;
      }
      catch(Exception e){}
    }

    Transaction minerRewardTransaction = new Transaction();
    DSObj.latestCoinID = Integer.toString(Integer.valueOf(DSObj.latestCoinID)+1);
    minerRewardTransaction.coinID = DSObj.latestCoinID;
    minerRewardTransaction.Source = null;
    minerRewardTransaction.Destination = this;
    minerRewardTransaction.coinsrc_block = null;
    trarray[count++] = minerRewardTransaction;
    TransactionBlock tB = new TransactionBlock(trarray);
    DSObj.bChain.InsertBlock_Honest(tB);
    mycoins.add(new Pair<String, TransactionBlock>(DSObj.latestCoinID, tB));
    return;
  }  

  public void MineCoin(DSCoin_Malicious DSObj) 
  {
    Transaction[] trarray = new Transaction[DSObj.bChain.tr_count];
    int count = 0;
    while(count<DSObj.bChain.tr_count-1)
    {
      try
      {
        Transaction t = DSObj.pendingTransactions.RemoveTransaction();
        boolean found = false;
        for (int i = 0;i<count && !found;i++)
          if (trarray[i].coinID.equals(t.coinID))
            found =true;
        TransactionBlock tb = DSObj.bChain.FindLongestValidChain();
        if (!found)
          if (tb == null)
            trarray[count++] = t;
          else if (tb.checkTransaction(t))
            trarray[count++] = t;
      }
      catch(EmptyQueueException e){
        break;
      }
    }

    Transaction minerRewardTransaction = new Transaction();
    DSObj.latestCoinID = Integer.toString(Integer.valueOf(DSObj.latestCoinID)+1);
    minerRewardTransaction.coinID = DSObj.latestCoinID;
    minerRewardTransaction.Source = null;
    minerRewardTransaction.Destination = this;
    minerRewardTransaction.coinsrc_block = null;
    trarray[count++] = minerRewardTransaction;
    TransactionBlock tB = new TransactionBlock(trarray);
    DSObj.bChain.InsertBlock_Malicious(tB);
    mycoins.add(new Pair<String, TransactionBlock>(DSObj.latestCoinID, tB));
    return;
  }  
}