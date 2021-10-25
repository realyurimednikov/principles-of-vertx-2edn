package net.yurimednikov.vertxbook.cashx.models;

import java.util.List;

public final class PagedAccountList {
    
    private final List<Account> accounts;
    private final int total;
    
    public PagedAccountList(List<Account> accounts, int total) {
        this.accounts = accounts;
        this.total = total;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public int getTotal() {
        return total;
    }

    
}
