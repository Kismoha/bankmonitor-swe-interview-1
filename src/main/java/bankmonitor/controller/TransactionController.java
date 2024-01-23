package bankmonitor.controller;

import java.util.List;
import java.util.Optional;

import bankmonitor.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.json.JSONObject;

import bankmonitor.model.Transaction;
import bankmonitor.repository.TransactionRepository;

@Controller
@RequestMapping("/")
public class TransactionController {

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
}