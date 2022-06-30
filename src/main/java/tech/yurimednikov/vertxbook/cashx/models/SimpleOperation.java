package tech.yurimednikov.vertxbook.cashx.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SimpleOperation {
    
    private final long id;
    private final long userId;
    private final String description;
    private final String category;
    private final String currency;
    private final BigDecimal amount;
    private final LocalDateTime dateTime;
    private final Account account;
    
    public SimpleOperation(long id, long userId, String description, String category, String currency,
            BigDecimal amount, LocalDateTime dateTime, Account account) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.category = category;
        this.currency = currency;
        this.amount = amount;
        this.dateTime = dateTime;
        this.account = account;
    }

    public SimpleOperation(long id, long userId, String description, String category, String currency,
            BigDecimal amount, LocalDateTime dateTime, long accountId) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.category = category;
        this.currency = currency;
        this.amount = amount;
        this.dateTime = dateTime;
        this.account = new Account(accountId, null, null, userId);
    }

    public static SimpleOperation withId (long id, SimpleOperation old){
        return new SimpleOperation(id, old.getUserId(), old.getDescription(), old.getCategory(), old.getCurrency(), old.getAmount(), old.getDateTime(), old.getAccount());
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Account getAccount() {
        return account;
    }

    
}
