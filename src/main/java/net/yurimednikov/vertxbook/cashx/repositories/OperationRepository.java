package net.yurimednikov.vertxbook.cashx.repositories;

import java.util.Optional;

import io.vertx.core.Future;
import net.yurimednikov.vertxbook.cashx.models.Operation;
import net.yurimednikov.vertxbook.cashx.models.OperationList;

public interface OperationRepository {
    
    Future<Operation> saveOperation (Operation operation);

    Future<Optional<Operation>> findOperationById (String id);

    Future<Boolean> removeOperation (String id);

    Future<OperationList> findOperations (String userId);
}
