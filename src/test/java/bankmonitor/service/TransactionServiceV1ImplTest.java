package bankmonitor.service;

import bankmonitor.model.Transaction;
import bankmonitor.repository.TransactionRepository;
import bankmonitor.utility.TransactionException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.stream.Stream;

class TransactionServiceV1ImplTest {

    TransactionRepository mockedTransactionRepository;

    TransactionServiceV1Impl transactionServiceV1;

    @BeforeEach
    void setup() {
        this.mockedTransactionRepository = Mockito.mock(TransactionRepository.class);
        this.transactionServiceV1 = new TransactionServiceV1Impl(this.mockedTransactionRepository);
    }

    @Test
    void getAllTransactions() {
        this.transactionServiceV1.getAllTransactions();

        Mockito.verify(this.mockedTransactionRepository, Mockito.times(1)).findAll();
    }

    @ParameterizedTest
    @MethodSource("incorrectJSONProvider")
    @NullAndEmptySource
    void createTransaction_whenIncorrectJSONProvided_thenThrowsTransactionException(String jsonData) {

        Assertions.assertThrows(TransactionException.class, () -> this.transactionServiceV1.createTransaction(jsonData),
                "Invalid JSON provided!");
    }

    @ParameterizedTest
    @MethodSource("correctJSONProvider")
    void createTransaction_whenCorrectJSONProvided_thenRepositorySaveCalled(String jsonData) {
        this.transactionServiceV1.createTransaction(jsonData);

        Mockito.verify(this.mockedTransactionRepository, Mockito.times(1)).save(Mockito.any());
    }

    @ParameterizedTest
    @MethodSource("correctJSONProvider")
    void createTransaction_whenCorrectJSONProvided_thenDataCorrectlySaved(String jsonData) {
        Transaction expectedTransaction = new Transaction(jsonData);
        Mockito.when(mockedTransactionRepository.save(Mockito.any())).thenReturn(expectedTransaction);

        Transaction actualTransaction = this.transactionServiceV1.createTransaction(jsonData);

        Assertions.assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    void updateTransaction_whenTransactionNotFound_thenThrowsTransactionException() {
        Mockito.when(mockedTransactionRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(TransactionException.class, () -> transactionServiceV1.updateTransaction(1L, ""),
                "No transaction found with the given id!");
    }

    @ParameterizedTest
    @MethodSource("incorrectJSONProvider")
    void updateTransaction_whenIncorrectJSONProvided_ThrowsTransactionException(String incorrectUpdateJSON) {
        Assertions.assertThrows(TransactionException.class,
                () -> transactionServiceV1.updateTransaction(1L, incorrectUpdateJSON),
                "Provided JSON data is invalid!");
    }

    @ParameterizedTest
    @MethodSource("updateAmountProvider")
    void updateTransaction_whenCorrectJSONProvided_thenUpdatesAmount(Long id, String updateJSON,
            Integer expectedAmount) {
        // GIVEN
        Transaction oldTransaction = new Transaction(
                "{ \"reference\": \"oldReference\", \"amount\": 0, \"irrelevantKey\": \"irrelevantValue\"}");
        Mockito.when(mockedTransactionRepository.findById(id)).thenReturn(Optional.of(oldTransaction));

        Transaction updatedTransaction = new JSONObject(updateJSON).has(Transaction.AMOUNT_KEY)
                ? new Transaction(updateJSON) : oldTransaction;
        Mockito.when(mockedTransactionRepository.save(Mockito.any())).thenReturn(updatedTransaction);

        // WHEN
        Integer actualAmount = transactionServiceV1.updateTransaction(id, updateJSON).getAmount();

        // THEN
        Assertions.assertEquals(expectedAmount, actualAmount);
    }

    @ParameterizedTest
    @MethodSource("updateReferenceProvider")
    void updateTransaction_whenCorrectJSONProvided_thenUpdatesReference(Long id, String updateJSON,
            String expectedReference) {
        // GIVEN
        Transaction oldTransaction = new Transaction(
                "{ \"reference\": \"oldReference\", \"amount\": 0, \"irrelevantKey\": \"irrelevantValue\"}");
        Mockito.when(mockedTransactionRepository.findById(id)).thenReturn(Optional.of(oldTransaction));

        Transaction updatedTransaction = new JSONObject(updateJSON).has(Transaction.REFERENCE_KEY)
                ? new Transaction(updateJSON) : oldTransaction;
        Mockito.when(mockedTransactionRepository.save(Mockito.any())).thenReturn(updatedTransaction);

        // WHEN
        String actualReference = transactionServiceV1.updateTransaction(id, updateJSON).getReference();

        // THEN
        Assertions.assertEquals(expectedReference, actualReference);
    }

    static Stream<String> incorrectJSONProvider() {
        return Stream.of("   ", " \"reference\": \"foo\", \"amount\": 200, \"irrelevantKey\": \"irrelevantValue\"",
                " \"reference\": \"foo\", \"amount\": 200, \"irrelevantKey\": \"irrelevantValue\"}",
                "{ \"reference\" \"foo\", \"amount\": 200, \"irrelevantKey\": \"irrelevantValue\"",
                "{ \"reference\": \"foo\", \"amount\": 200 \"irrelevantKey\": \"irrelevantValue\"}",
                "{ \"reference\": , \"amount\": 200, \"irrelevanvantValue\"}",
                "{ \"reference\": \"foo\", : 200, \"irrelevantKey\": \"irrelevantValue\"}");
    }

    static Stream<String> correctJSONProvider() {
        return Stream.of("{ }", "{  \"irrelevantKey\": \"irrelevantValue\" }", "{ \"reference\": \"foo\" }",
                "{ \"reference\": \"foo\",  \"irrelevantKey\": \"irrelevantValue\" }", "{  \"amount\": 100}",
                "{  \"amount\": 100, \"irrelevantKey\": \"irrelevantValue\" }",
                "{ \"reference\": \"\", \"amount\": 0 }",
                "{ \"reference\": \"\", \"amount\": 0, \"irrelevantKey\": \"irrelevantValue\"}",
                "{ \"reference\": \"    \", \"amount\": -1, \"irrelevantKey\": \"irrelevantValue\"}",
                "{ \"reference\": \" \", \"amount\": -200, \"irrelevantKey\": \"irrelevantValue\"}",
                "{ \"reference\": \"foo\", \"amount\": 200, \"irrelevantKey\": \"irrelevantValue\"}");
    }

    static Stream<Arguments> updateAmountProvider() {
        return Stream.of(
                Arguments.of(1L,
                        "{\"reference\": \"oldReference\", \"amount\": 0, \"irrelevantKey\": \"irrelevantValue\"}", 0),
                Arguments.of(2L, "{\"reference\": \"oldReference\", \"irrelevantKey\": \"irrelevantValue\"}", 0),
                Arguments.of(1L,
                        "{\"reference\": \"oldReference\", \"amount\": 200, \"irrelevantKey\": \"irrelevantValue\"}",
                        200),
                Arguments.of(3L,
                        "{\"reference\": \"oldReference\", \"amount\": -200, \"irrelevantKey\": \"irrelevantValue\"}",
                        -200));
    }

    static Stream<Arguments> updateReferenceProvider() {
        return Stream.of(
                Arguments.of(1L,
                        "{\"reference\": \"oldReference\", \"amount\": 0, \"irrelevantKey\": \"irrelevantValue\"}",
                        "oldReference"),
                Arguments.of(1L, "{\"amount\": 0, \"irrelevantKey\": \"irrelevantValue\"}", "oldReference"),
                Arguments.of(2L,
                        "{\"reference\": \"newReference\", \"amount\": 0, \"irrelevantKey\": \"irrelevantValue\"}",
                        "newReference"),
                Arguments.of(3L, "{\"reference\": \"\", \"amount\": -0, \"irrelevantKey\": \"irrelevantValue\"}", ""));
    }
}
