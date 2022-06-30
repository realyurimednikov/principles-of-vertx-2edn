package tech.yurimednikov.vertxbook.cashx.services;

import java.util.Optional;

import io.vertx.core.Future;
import tech.yurimednikov.vertxbook.cashx.models.Account;
import tech.yurimednikov.vertxbook.cashx.models.AccountList;
import tech.yurimednikov.vertxbook.cashx.models.PagedAccountList;
import tech.yurimednikov.vertxbook.cashx.models.Pagination;

public interface AccountService {
    
    Future<Account> createAccount (Account account);

    Future<Optional<Account>> findAccountById (long id);

    Future<AccountList> findAccounts (long userId);

    Future<Account> updateAccount (Account account);

    Future<Boolean> removeAccount (long id);

    Future<PagedAccountList> findAccountsWithPagination(long userId, Pagination pagination);
}
