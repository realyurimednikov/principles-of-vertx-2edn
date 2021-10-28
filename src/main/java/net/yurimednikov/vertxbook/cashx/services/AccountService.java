package net.yurimednikov.vertxbook.cashx.services;

import java.util.Optional;

import io.vertx.core.Future;
import net.yurimednikov.vertxbook.cashx.models.Account;
import net.yurimednikov.vertxbook.cashx.models.AccountList;
import net.yurimednikov.vertxbook.cashx.models.PagedAccountList;
import net.yurimednikov.vertxbook.cashx.models.Pagination;

public interface AccountService {
    
    Future<Account> createAccount (Account account);

    Future<Optional<Account>> findAccountById (long id);

    Future<AccountList> findAccounts (long userId);

    Future<Account> updateAccount (Account account);

    Future<Boolean> removeAccount (long id);

    Future<PagedAccountList> findAccountsWithPagination(long userId, Pagination pagination);
}
