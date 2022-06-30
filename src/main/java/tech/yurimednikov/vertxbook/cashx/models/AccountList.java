package tech.yurimednikov.vertxbook.cashx.models;

import java.util.List;

public final class AccountList {
    
    private final List<Account> accounts;


    public AccountList(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    
}
