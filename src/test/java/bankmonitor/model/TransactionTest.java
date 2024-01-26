package bankmonitor.model;

import bankmonitor.utility.TransactionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;

public class TransactionTest {

    @ParameterizedTest
    @MethodSource("correctJSONProvider")
    void givenCorrectJSON_whenGettersCalled_thenReturnsCorrectly(String data, String expectedReference,
            Integer expectedAmount) {

        Transaction transaction = new Transaction(data);

        Assertions.assertEquals(expectedReference, transaction.getReference());
        Assertions.assertEquals(expectedAmount, transaction.getAmount());

    }

    @ParameterizedTest
    @NullAndEmptySource
    @MethodSource("incorrectJSONProvider")
    void givenIncorrectJSON_whenGetterCalled_thenThrowsJSONException(String data) {
        Transaction transaction = new Transaction(data);

        Assertions.assertThrows(TransactionException.class, transaction::getReference);
        Assertions.assertThrows(TransactionException.class, transaction::getAmount);
    }

    static Stream<Arguments> correctJSONProvider() {
        return Stream.of(Arguments.of("{ }", null, null),
                Arguments.of("{  \"irrelevantKey\": \"irrelevantValue\" }", null, null),
                Arguments.of("{ \"reference\": \"foo\" }", "foo", null),
                Arguments.of("{ \"reference\": \"foo\",  \"irrelevantKey\": \"irrelevantValue\" }", "foo", null),
                Arguments.of("{  \"amount\": 100}", null, 100),
                Arguments.of("{  \"amount\": 100, \"irrelevantKey\": \"irrelevantValue\" }", null, 100),
                Arguments.of("{ \"reference\": \"\", \"amount\": 0 }", "", 0),
                Arguments.of("{ \"reference\": \"\", \"amount\": 0, \"irrelevantKey\": \"irrelevantValue\"}", "", 0),
                Arguments.of("{ \"reference\": \"    \", \"amount\": -1, \"irrelevantKey\": \"irrelevantValue\"}",
                        "    ", -1),
                Arguments.of("{ \"reference\": \" \", \"amount\": -200, \"irrelevantKey\": \"irrelevantValue\"}", " ",
                        -200),
                Arguments.of("{ \"reference\": \"foo\", \"amount\": 200, \"irrelevantKey\": \"irrelevantValue\"}",
                        "foo", 200));
    }

    static Stream<String> incorrectJSONProvider() {
        return Stream.of("   ", " \"reference\": \"foo\", \"amount\": 200, \"irrelevantKey\": \"irrelevantValue\"",
                " \"reference\": \"foo\", \"amount\": 200, \"irrelevantKey\": \"irrelevantValue\"}",
                "{ \"reference\" \"foo\", \"amount\": 200, \"irrelevantKey\": \"irrelevantValue\"",
                "{ \"reference\": \"foo\", \"amount\": 200 \"irrelevantKey\": \"irrelevantValue\"}",
                "{ \"reference\": , \"amount\": 200, \"irrelevanvantValue\"}",
                "{ \"reference\": \"foo\", : 200, \"irrelevantKey\": \"irrelevantValue\"}");
    }

}