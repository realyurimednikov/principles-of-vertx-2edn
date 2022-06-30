package tech.yurimednikov.vertxbook.cashx.schemas;

import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.Conditions;
import com.healthmarketscience.sqlbuilder.DeleteQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;


import tech.yurimednikov.vertxbook.cashx.models.Account;

public class AccountSchema implements Schema<Account> {

    private DbTable accountsTable;
    private DbColumn idColumn;
    private DbColumn nameColumn;
    private DbColumn userColumn;
    private DbColumn currencyColumn;

    public AccountSchema(DbSchema schema){
        accountsTable = schema.addTable("accounts");
        idColumn = accountsTable.addColumn("account_id", "number", null);
        nameColumn = accountsTable.addColumn("account_name", "varchar", 255);
        userColumn = accountsTable.addColumn("account_userid", "number", null);
        currencyColumn = accountsTable.addColumn("account_currency", "varchar", 255);
        accountsTable.primaryKey("account_id", "account_id");        
    }

    @Override
    public String getInsertQuery(Account entity) {
        InsertQuery query = new InsertQuery(accountsTable)
            .addColumn(nameColumn, entity.getName())
            .addColumn(userColumn, entity.getUserId())
            .addColumn(currencyColumn, entity.getCurrency());
        return query.validate().toString();
    }

    @Override
    public String getUpdateQuery(Account entity) {
        Condition condition = Conditions.equalTo(idColumn, entity.getId());
        UpdateQuery query = new UpdateQuery(accountsTable)
            .addCondition(condition)
            .addSetClause(nameColumn, entity.getName())
            .addSetClause(currencyColumn, entity.getCurrency());
        return query.validate().toString();
    }

    @Override
    public String getDeleteQuery(Account entity) {
        Condition condition = Conditions.equalTo(idColumn, entity.getId());
        DeleteQuery query = new DeleteQuery(accountsTable).addCondition(condition);
        return query.validate().toString();
    }

    @Override
    public String getSelectOneByIdQuery(Account entity) {
        Condition condition = Conditions.equalTo(idColumn, entity.getId());
        SelectQuery query = new SelectQuery(true)
            .addFromTable(accountsTable)
            .addAllTableColumns(accountsTable)
            .addCondition(condition);
        return query.validate().toString();
    }

    @Override
    public String getSelectManyByUserQuery(Account entity) {
        Condition condition = Conditions.equalTo(userColumn, entity.getUserId());
        SelectQuery query = new SelectQuery()
            .addFromTable(accountsTable)
            .addAllTableColumns(accountsTable)
            .addCondition(condition);
        return query.validate().toString();
    }
    
    public static void main(String[] args) {
        DbSpec specification = new DbSpec();
        DbSchema dbSchema = specification.addDefaultSchema();
        AccountSchema schema = new AccountSchema(dbSchema);
        Account entity = new Account(0, "My bank account", "EUR", 1);
        System.out.println(schema.getUpdateQuery(entity));
        System.err.println(schema.getInsertQuery(entity));
        System.out.println(schema.getSelectOneByIdQuery(entity));
        System.out.println(schema.getSelectManyByUserQuery(entity));
        System.out.println(schema.getDeleteQuery(entity)); 
    }
}
