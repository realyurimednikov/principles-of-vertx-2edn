package tech.yurimednikov.vertxbook.cashx.repositories;

import io.vertx.core.Future;
import tech.yurimednikov.vertxbook.cashx.models.PaymentOperation;
import tech.yurimednikov.vertxbook.cashx.models.PaymentOperationList;

import java.util.Optional;

public interface PaymentOperationRepository {

    Future<PaymentOperation> createOperation (PaymentOperation operation);

    Future<Optional<PaymentOperation>> findOperationById (long id);

    Future<PaymentOperationList> findAll (long userId);

    Future<Boolean> removeOperation (long id);
}
