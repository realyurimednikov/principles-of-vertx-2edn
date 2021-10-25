package net.yurimednikov.vertxbook.cashx.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.vertx.sqlclient.Row;
import net.yurimednikov.vertxbook.cashx.models.Account;
import net.yurimednikov.vertxbook.cashx.models.SimpleOperation;

public interface Mapper<T> {
    
    T toEntity (Row row);
    
    class AccountMapper implements Mapper<Account> {

        @Override
        public Account toEntity(Row row) {
            long id = row.getLong("account_id");
            String name = row.getString("account_name");
            String currency = row.getString("account_currency");
            long userId = row.getLong("account_userid");
            return new Account(id, name, currency, userId);
        }

    }

    class SimpleOperationMapper implements Mapper<SimpleOperation> {

        @Override
        public SimpleOperation toEntity(Row row) {
            // create account mapper
            AccountMapper accountMapper = new AccountMapper();
            Account account = accountMapper.toEntity(row);

            // map fields for simple operation
            long so_id = row.getLong("so_id");
            long so_userid = row.getLong("so_userid");
            String so_description = row.getString("so_description");
            String so_currency = row.getString("so_currency");
            String so_category = row.getString("so_category"); 
            BigDecimal so_amount = row.getBigDecimal("so_amount");
            LocalDateTime so_datetime = row.getLocalDateTime("so_datetime");
            
            SimpleOperation operation = new SimpleOperation(so_id, so_userid, so_description, so_category, so_currency, so_amount, so_datetime, account);
        
            // return results
            return operation;
        }

    }
}
