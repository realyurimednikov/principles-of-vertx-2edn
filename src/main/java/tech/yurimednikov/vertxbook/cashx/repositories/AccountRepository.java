package tech.yurimednikov.vertxbook.cashx.repositories;

import java.util.Optional;

import io.vertx.core.Future;
import tech.yurimednikov.vertxbook.cashx.models.*;

public interface AccountRepository {
    
    Future<Account> saveAccount (Account account);

    Future<Optional<Account>> findAccountById (Long accountId);

    Future<AccountList> findAccounts (Long userId);

    Future<Boolean> removeAccount (Long accountId);

    Future<Account> updateAccount (Account account);

    Future<AccountList> saveManyAccounts (AccountList accounts);

    Future<PagedAccountList> findAndPaginate (Long userId, Pagination pagination);

    Future<AccountList> findAndSort (Long userId, Sort sort);
}
