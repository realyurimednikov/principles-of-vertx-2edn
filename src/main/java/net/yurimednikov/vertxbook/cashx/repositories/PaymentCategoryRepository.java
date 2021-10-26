package net.yurimednikov.vertxbook.cashx.repositories;

import io.vertx.core.Future;
import net.yurimednikov.vertxbook.cashx.models.PaymentCategory;
import net.yurimednikov.vertxbook.cashx.models.PaymentCategoryList;

import java.util.Optional;

public interface PaymentCategoryRepository {

    Future<PaymentCategory> savePaymentCategory (PaymentCategory category);

    Future<Optional<PaymentCategory>> findCategoryById (long id);

    Future<PaymentCategoryList> findAll (long userId);

    Future<Boolean> removePaymentCategory (long id);

}
