package bankmonitor.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONException;
import org.json.JSONObject;

@Entity
@Table(name = "transaction")
public class Transaction {

    public static final String REFERENCE_KEY = "reference";
    public static final String AMOUNT_KEY = "amount";
    public static final String PROVIDED_DATA_IS_NULL = "Provided data is null!";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "created_at")
    private LocalDateTime timestamp;

    @Column(name = "data")
    private String data;

    public Transaction() {
    }

    public Transaction(String jsonData) {
        this.timestamp = LocalDateTime.now();
        this.data = jsonData;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getAmount() {
        JSONObject jsonData = getJSONData();
        return jsonData.has(AMOUNT_KEY) ? jsonData.getInt(AMOUNT_KEY) : null;
    }

    public String getReference() {
        JSONObject jsonData = getJSONData();
        return jsonData.has(REFERENCE_KEY) ? jsonData.getString(REFERENCE_KEY) : null;
    }

    private JSONObject getJSONData() {
        if (this.data == null) {
            throw new JSONException(PROVIDED_DATA_IS_NULL);
        }

        return new JSONObject(this.data);
    }
}