package net.yurimednikov.vertxbook.cashx.repositories;

import java.util.Optional;

import io.vertx.core.Future;
import net.yurimednikov.vertxbook.cashx.models.SimpleOperation;
import net.yurimednikov.vertxbook.cashx.models.SimpleOperationList;

public interface SimpleOperationRepository {
    
    Future<SimpleOperation> saveOperation (SimpleOperation operation);

    Future<Optional<SimpleOperation>> findOperationById (long id);

    Future<SimpleOperationList> findOperations (long userId);

    Future<Boolean> removeOperation (long id);
    
}
