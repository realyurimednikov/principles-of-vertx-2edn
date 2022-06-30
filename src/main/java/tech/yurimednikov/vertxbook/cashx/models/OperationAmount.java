package tech.yurimednikov.vertxbook.cashx.models;

import java.math.BigDecimal;

public record OperationAmount(String currency, BigDecimal value) {
    
}
