package net.yurimednikov.vertxbook.cashx.models;

public final class PaymentCategory {

    private final long categoryId;
    private final long userId;
    private final String name;
    private final String type;

    public PaymentCategory(long categoryId, long userId, String name, String type) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.name = name;
        this.type = type;
    }

    public static PaymentCategory withId (long newId, PaymentCategory old){
        return new PaymentCategory(newId, old.getUserId(), old.getName(), old.getType());
    }

    public long getCategoryId() {
        return categoryId;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
