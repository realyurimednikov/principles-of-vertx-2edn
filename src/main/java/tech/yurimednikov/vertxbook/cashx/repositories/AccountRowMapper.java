package tech.yurimednikov.vertxbook.cashx.repositories;

import java.util.function.Function;

import io.vertx.sqlclient.Row;
import tech.yurimednikov.vertxbook.cashx.models.Account;

class AccountRowMapper implements Function<Row, Account>{

    @Override
    public Account apply(Row row) {
        long id = row.getLong("account_id");
        String name = row.getString("account_name");
        String currency = row.getString("account_currency");
        long userId = row.getLong("account_userid");
        return new Account(id, name, currency, userId);
    }
    
}
