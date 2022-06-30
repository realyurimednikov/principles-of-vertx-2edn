package tech.yurimednikov.vertxbook.cashx.models;

import java.util.List;

public final class PaymentOperationList {

    private final List<PaymentOperation> operations;

    public PaymentOperationList(List<PaymentOperation> operations) {
        this.operations = operations;
    }

    public List<PaymentOperation> getOperations() {
        return operations;
    }
}
