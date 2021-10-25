package net.yurimednikov.vertxbook.cashx.models;

import java.util.List;

public final class SimpleOperationList {
    
    private final List<SimpleOperation> operations;

    public SimpleOperationList(List<SimpleOperation> operations) {
        this.operations = operations;
    }



    public List<SimpleOperation> getOperations() {
        return operations;
    }

    
}
