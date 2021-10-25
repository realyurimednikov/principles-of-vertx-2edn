package net.yurimednikov.vertxbook.cashx.repositories;

import java.util.Optional;

import io.vertx.core.Future;
import net.yurimednikov.vertxbook.cashx.models.Account;
import net.yurimednikov.vertxbook.cashx.models.AccountList;
import net.yurimednikov.vertxbook.cashx.models.PagedAccountList;

public interface AccountRepository {
    
    Future<Account> saveAccount (Account account);

    Future<Optional<Account>> findAccountById (Long accountId);

    Future<AccountList> findAccounts (Long userId);

    Future<PagedAccountList> findAccountsWithPagination (Long userId, Pagination pagination);

    Future<Boolean> removeAccount (Long accountId);

    Future<Account> updateAccount (Account account);

    Future<AccountList> saveManyAccounts (AccountList accounts);

}
