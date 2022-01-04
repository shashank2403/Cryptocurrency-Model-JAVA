package DSCoinPackage;

import HelperClasses.*;

public class Moderator
{

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) 
  {
    Members Moderator = new Members();
    Moderator.UID = "Moderator";

    int initial = 100000;

    for (int i = 0; i < coinCount; i = i + DSObj.bChain.tr_count)
    {
      Transaction[] tra = new Transaction[DSObj.bChain.tr_count];
      for (int j = 0; j < DSObj.bChain.tr_count; j++) 
      {
        Transaction t = new Transaction();
        t.coinID = Integer.toString(initial + i + j);
        t.Source = Moderator;
        t.Destination = DSObj.memberlist[(i + j) % DSObj.memberlist.length];
        t.coinsrc_block = null;
        tra[j] = t;
      }
      
      TransactionBlock tB = new TransactionBlock(tra);
      DSObj.bChain.InsertBlock_Honest(tB);

      for (int j = 0; j < DSObj.bChain.tr_count; j++)
        tra[j].Destination.mycoins.add(new Pair<String, TransactionBlock>(tra[j].coinID, tB));
    }
    DSObj.latestCoinID = Integer.toString(initial+coinCount-1);
  } 

  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
    int trc = DSObj.bChain.tr_count; 
    int membercount = DSObj.memberlist.length;

    Members Moderator = new Members();
    Moderator.UID = "Moderator";

    int initial = 100000;

    for (int i = 0; i < coinCount; i = i + trc) 
    {
      Transaction[] tra = new Transaction[trc];
      for (int j = 0; j < trc; j++) 
      {
        Transaction t = new Transaction();
        t.coinID = Integer.toString(initial + i + j);
        t.Source = Moderator;
        t.Destination = DSObj.memberlist[(i + j) % membercount];
        t.coinsrc_block = null;
        tra[j] = t;
      }

      TransactionBlock tB = new TransactionBlock(tra);
      DSObj.bChain.InsertBlock_Malicious(tB);

      for (int j = 0; j < trc; j++)
        tra[j].Destination.mycoins.add(new Pair<String, TransactionBlock>(tra[j].coinID, tB));
    }
    DSObj.latestCoinID = Integer.toString(initial+coinCount-1);
  }
}