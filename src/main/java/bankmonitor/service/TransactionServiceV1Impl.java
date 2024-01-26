package bankmonitor.service;

import bankmonitor.model.Transaction;
import bankmonitor.repository.TransactionRepository;
import bankmonitor.utility.TransactionException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@Qualifier("v1")
public class TransactionServiceV1Impl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceV1Impl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return this.transactionRepository.findAll();
    }

    @Override
    public Transaction createTransaction(String jsonData) {
        Transaction newTransaction = new Transaction(getJsonObject(jsonData));

        return transactionRepository.save(newTransaction);
    }

    @Override
    public Transaction updateTransaction(Long id, String updateJSON) {
        Optional<Transaction> optionalTransaction = this.transactionRepository.findById(id);

        Transaction currentTransaction = optionalTransaction
                .orElseThrow(() -> new TransactionException("No transaction found with the given id!"));

        JSONObject updateJson = getJsonObject(updateJSON);
        JSONObject newTransactionData = getJsonObject(currentTransaction.getData());

        if (updateJson.has(Transaction.AMOUNT_KEY)) {
            newTransactionData.put(Transaction.AMOUNT_KEY, updateJson.getInt(Transaction.AMOUNT_KEY));
        }

        if (updateJson.has(Transaction.REFERENCE_KEY)) {
            newTransactionData.put(Transaction.REFERENCE_KEY, updateJson.getString(Transaction.REFERENCE_KEY));
        }

        currentTransaction.setData(newTransactionData.toString());

        return this.transactionRepository.save(currentTransaction);
    }

    private JSONObject getJsonObject(String jsonData) {
        TransactionException invalidDataException = new TransactionException("Provided JSON data is invalid!");

        if (jsonData == null) {
            throw invalidDataException;
        }

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
        } catch (JSONException exception) {
            throw invalidDataException;
        }

        return jsonObject;
    }
}
