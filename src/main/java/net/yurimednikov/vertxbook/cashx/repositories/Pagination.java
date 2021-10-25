package net.yurimednikov.vertxbook.cashx.repositories;

public final class Pagination {
    
    private final int page;
    private final int limit;
    
    public Pagination(int page, int limit) {
        this.page = page;
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    
}
