package tech.yurimednikov.vertxbook.cashx.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class PaymentOperation {
    private final long operationId;
    private final long userId;
    private final String name;
    private final Account account;
    private final PaymentCategory category;
    private final BigDecimal amount;
    private final String currency;
    private final LocalDate date;

    public PaymentOperation(long operationId, long userId, String name, Account account, PaymentCategory category, BigDecimal amount, String currency, LocalDate date) {
        this.operationId = operationId;
        this.userId = userId;
        this.account = account;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
    }

    public static PaymentOperation withId (long id, PaymentOperation old){
        return new PaymentOperation(id, old.userId, old.name, old.account, old.category, old.amount, old.currency, old.date);
    }

    public long getOperationId() {
        return operationId;
    }

    public long getUserId() {
        return userId;
    }

    public Account getAccount() {
        return account;
    }

    public PaymentCategory getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getName() {
        return name;
    }
}
