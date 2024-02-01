package bankmonitor.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bankmonitor.service.TransactionService;
import bankmonitor.utility.TransactionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import bankmonitor.model.Transaction;

@Controller
@RequestMapping("/")
public class TransactionController {

    private final String TRANSACTION_ERROR_KEY = "transactionError";

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    @ResponseBody
    public List<Transaction> getAllTransactions() {
        return this.transactionService.getAllTransactions();
    }

    @PostMapping("/transactions")
    @ResponseBody
    public Transaction createTransaction(@RequestBody String jsonData) {
        return this.transactionService.createTransaction(jsonData);
    }

    @PutMapping("/transactions/{id}")
    @ResponseBody
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody String update) {
        return ResponseEntity.ok(this.transactionService.updateTransaction(id, update));
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<String> handleTransactionException(TransactionException e) throws JsonProcessingException {
        Map<String, String> payload = new HashMap<>();
        payload.put(TRANSACTION_ERROR_KEY, e.getMessage());

        String jsonError = new ObjectMapper().writeValueAsString(payload);

        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(jsonError);
    }
}