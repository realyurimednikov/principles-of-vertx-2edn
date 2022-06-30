package tech.yurimednikov.vertxbook.cashx.models;

import java.time.LocalDateTime;

public record Operation(String id, String userId, String name, LocalDateTime dateTime, OperationAmount amount, Category category, String accountId) {
    
    public static Operation withId (String newId, Operation old){
       return new Operation(newId, old.userId, old.name, old.dateTime, old.amount, old.category, old.accountId);
    }
}
