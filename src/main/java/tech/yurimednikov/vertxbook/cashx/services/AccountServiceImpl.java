package tech.yurimednikov.vertxbook.cashx.services;

import java.util.Optional;

import io.vertx.core.Future;
import tech.yurimednikov.vertxbook.cashx.models.Account;
import tech.yurimednikov.vertxbook.cashx.models.AccountList;
import tech.yurimednikov.vertxbook.cashx.models.PagedAccountList;
import tech.yurimednikov.vertxbook.cashx.models.Pagination;
import tech.yurimednikov.vertxbook.cashx.repositories.AccountRepository;

public class AccountServiceImpl implements AccountService{
    
    private final AccountRepository repository;

    public AccountServiceImpl(AccountRepository repository){
        this.repository = repository;
    }

    @Override
    public Future<Account> createAccount(Account account) {
        return repository.saveAccount(account);
    }

    @Override
    public Future<Optional<Account>> findAccountById(long id) {
        return repository.findAccountById(id);
    }

    @Override
    public Future<AccountList> findAccounts(long userId) {
        return repository.findAccounts(userId);
    }

    @Override
    public Future<Account> updateAccount(Account account) {
        return repository.updateAccount(account);
    }

    @Override
    public Future<Boolean> removeAccount(long id) {
        return repository.removeAccount(id);
    }

    @Override
    public Future<PagedAccountList> findAccountsWithPagination(long userId, Pagination pagination) {
        return repository.findAndPaginate(userId, pagination);
    }
}
