package net.yurimednikov.vertxbook.cashx.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import net.yurimednikov.vertxbook.cashx.errors.EntityAlreadyExistsException;
import net.yurimednikov.vertxbook.cashx.models.SimpleOperation;
import net.yurimednikov.vertxbook.cashx.models.SimpleOperationList;

public class SimpleOperationReactivePgRepositoryImpl implements SimpleOperationRepository {

    private final SqlClient client;

    public SimpleOperationReactivePgRepositoryImpl(SqlClient client){
        this.client = client;
    }

    @Override
    public Future<SimpleOperation> saveOperation(SimpleOperation operation) {
        String sql = """
        INSERT INTO simple_operations (so_description, so_userid, so_currency, so_amount, 
        so_datetime, so_accountid, so_category)
         VALUES ($1, $2, $3, $4, $5, $6, $7) 
         RETURNING so_id;
        """;

        Tuple parameters = Tuple.of(
            operation.getDescription(),
            operation.getUserId(),
            operation.getCurrency(),
            operation.getAmount(),
            operation.getDateTime(),
            operation.getAccount().getId(),
            operation.getCategory()
        );

        return client.preparedQuery(sql).execute(parameters)
        .flatMap(rows -> {
            if (rows.rowCount() == 0){
                return Future.failedFuture(new EntityAlreadyExistsException());
            }
            Row row = rows.iterator().next();
            long id = row.getLong("so_id");
            SimpleOperation saved = SimpleOperation.withId(id, operation);
            return Future.succeededFuture(saved);
        });
    }

    @Override
    public Future<Optional<SimpleOperation>> findOperationById(long id) {
        String sql = """
        SELECT
         simple_operations.so_id AS so_id,
        simple_operations.so_description AS so_description,
        simple_operations.so_currency AS so_currency,
        simple_operations.so_amount AS so_amount,
        simple_operations.so_datetime AS so_datetime,
        simple_operations.so_category AS so_category,
        simple_operations.so_userid AS so_userid,
        accounts.account_id AS account_id,
        accounts.account_userid AS account_userid,
        accounts.account_name AS account_name,
        accounts.account_currency AS account_currency
         FROM simple_operations
         INNER JOIN accounts
         ON accounts.account_id = simple_operations.so_accountid
         WHERE simple_operations.so_id = $1;
        """;

        Tuple params = Tuple.of(id);
        

        Future<Optional<SimpleOperation>> future = client.preparedQuery(sql).execute(params).flatMap(rows -> {
            if (rows.rowCount() == 0){
                return Future.succeededFuture(Optional.empty());
            }

            Row row = rows.iterator().next();

            Mapper.SimpleOperationMapper mapper = new Mapper.SimpleOperationMapper();
            SimpleOperation operation = mapper.toEntity(row);
            Optional<SimpleOperation> result = Optional.of(operation);

            return Future.succeededFuture(result);
        });
        return future;
    }

    @Override
    public Future<SimpleOperationList> findOperations(long userId) {
        String sql = """
        SELECT
         simple_operations.so_id AS so_id,
        simple_operations.so_description AS so_description,
        simple_operations.so_currency AS so_currency,
        simple_operations.so_amount AS so_amount,
        simple_operations.so_datetime AS so_datetime,
        simple_operations.so_category AS so_category,
        simple_operations.so_userid AS so_userid,
        accounts.account_id AS account_id,
        accounts.account_userid AS account_userid,
        accounts.account_name AS account_name,
        accounts.account_currency AS account_currency
         FROM simple_operations
         INNER JOIN accounts
         ON accounts.account_id = simple_operations.so_accountid
         WHERE simple_operations.so_userid = $1;
        """;

        Tuple params = Tuple.of(userId);

        Future<SimpleOperationList> result = client.preparedQuery(sql).execute(params).flatMap(rows -> {
            List<SimpleOperation> operations = new ArrayList<>();
            Mapper.SimpleOperationMapper mapper = new Mapper.SimpleOperationMapper();

            for (Row row : rows) {

                // long account_id = row.getLong("account_id");
                // String name = row.getString("account_name");
                // String account_currency = row.getString("account_currency");
                // long account_userId = row.getLong("account_userid");
                // Account account = new Account(account_id, name, account_currency, account_userId);

                // long so_id = row.getLong("so_id");
                // long so_userid = row.getLong("so_userid");
                // String so_description = row.getString("so_description");
                // String so_currency = row.getString("so_currency");
                // String so_category = row.getString("so_category");
                
                // BigDecimal so_amount = row.getBigDecimal("so_amount");
                // LocalDateTime so_datetime = row.getLocalDateTime("so_datetime");

                // SimpleOperation operation = new SimpleOperation(so_id, so_userid, so_description, so_category, so_currency, so_amount, so_datetime, account);
                SimpleOperation operation = mapper.toEntity(row);
                operations.add(operation);
                
            }

            SimpleOperationList list = new SimpleOperationList(operations);
            return Future.succeededFuture(list);
        });


        return result;
    }

    @Override
    public Future<Boolean> removeOperation(long id) {
        String sql = "DELETE FROM simple_operations WHERE so_id = $1 RETURNING so_id;";
        Tuple tuple = Tuple.of(id);        
        return client.preparedQuery(sql)
            .execute(tuple)
            .flatMap(rows -> Future.succeededFuture(rows.rowCount() != 0));
    }
    
}
