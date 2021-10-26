package net.yurimednikov.vertxbook.cashx.repositories;

import io.vertx.sqlclient.Row;
import net.yurimednikov.vertxbook.cashx.models.Account;
import net.yurimednikov.vertxbook.cashx.models.PaymentCategory;
import net.yurimednikov.vertxbook.cashx.models.PaymentOperation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Function;

class PaymentOperationRowMapper implements Function<Row, PaymentOperation> {

    @Override
    public PaymentOperation apply(Row row) {
        PaymentCategoryRowMapper categoryRowMapper = new PaymentCategoryRowMapper();
        AccountRowMapper accountRowMapper = new AccountRowMapper();
        Account account = accountRowMapper.apply(row);
        PaymentCategory category = categoryRowMapper.apply(row);
        long id = row.getLong("operation_id");
        long userId = row.getLong("operation_userid");
        String name = row.getString("operation_name");
        String currency = row.getString("operation_currency");
        BigDecimal amount = row.getBigDecimal("operation_amount");
        LocalDate date = row.getLocalDate("operation_date");
        return new PaymentOperation(id, userId, name, account, category, amount, currency, date);
    }
}
