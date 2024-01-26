package bankmonitor.service;

import bankmonitor.model.Transaction;

import java.util.List;

public interface TransactionService {

    List<Transaction> getAllTransactions();

    Transaction createTransaction(String jsonData);

    Transaction updateTransaction(Long id, String updateJSON);
}
