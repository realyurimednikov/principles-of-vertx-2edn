package tech.yurimednikov.vertxbook.cashx.repositories;

import java.util.Optional;

import io.vertx.core.Future;
import tech.yurimednikov.vertxbook.cashx.models.SimpleOperation;
import tech.yurimednikov.vertxbook.cashx.models.SimpleOperationList;

public interface SimpleOperationRepository {
    
    Future<SimpleOperation> saveOperation (SimpleOperation operation);

    Future<Optional<SimpleOperation>> findOperationById (long id);

    Future<SimpleOperationList> findOperations (long userId);

    Future<Boolean> removeOperation (long id);
    
}
