package tech.yurimednikov.vertxbook.cashx.models;

import java.util.List;

public final class PaymentCategoryList {

    private final List<PaymentCategory> categories;

    public PaymentCategoryList(List<PaymentCategory> categories) {
        this.categories = categories;
    }

    public List<PaymentCategory> getCategories() {
        return categories;
    }
}
