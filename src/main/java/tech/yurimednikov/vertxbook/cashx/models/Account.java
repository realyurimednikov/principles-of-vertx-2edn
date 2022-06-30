package tech.yurimednikov.vertxbook.cashx.models;

public final class Account {

    private final long id;
    private final String name;
    private final String currency;
    private final long userId;
    
    public Account(long id, String name, String currency, long userId) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public long getUserId() {
        return userId;
    }

    public static Account withId (long id, Account old){
        return new Account(id, old.getName(), old.getCurrency(), old.getUserId());
    }
}
