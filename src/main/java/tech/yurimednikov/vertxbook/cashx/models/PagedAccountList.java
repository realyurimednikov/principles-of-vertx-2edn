package tech.yurimednikov.vertxbook.cashx.models;

import java.util.List;

public final class PagedAccountList {
    
    private final List<Account> accounts;
    private final int numberOfPages;
    private final int currentPage;
    private final int total;


    public PagedAccountList(List<Account> accounts, int numberOfPages, int currentPage, int total) {
        this.accounts = accounts;
        this.numberOfPages = numberOfPages;
        this.currentPage = currentPage;
        this.total = total;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotal() {
        return total;
    }
}
