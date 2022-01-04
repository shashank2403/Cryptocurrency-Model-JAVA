package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  TransactionQueue()
  {
    numTransactions = 0;
  }

  public void AddTransactions (Transaction transaction) 
  {
    if (firstTransaction == null)
    {
      firstTransaction = transaction;
      lastTransaction = transaction;
      numTransactions++;
      return;
    }
    lastTransaction.next = transaction;
    transaction.previous = lastTransaction;
    lastTransaction = transaction;
    numTransactions++;
    return;
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException 
  {
    if (numTransactions == 0)
      throw new EmptyQueueException();
    if (numTransactions == 1)
    {
      Transaction temp = firstTransaction;
      firstTransaction = null;
      lastTransaction = null;
      numTransactions--;
      return temp;
    }
    Transaction temp = firstTransaction;
    firstTransaction = firstTransaction.next;
    firstTransaction.previous = null;
    temp.next = null;
    numTransactions--;
    return temp;
  }

  public int size() 
  {
    return numTransactions; 
  }
}