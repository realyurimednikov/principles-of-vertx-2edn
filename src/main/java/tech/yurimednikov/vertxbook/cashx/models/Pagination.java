package tech.yurimednikov.vertxbook.cashx.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagination that = (Pagination) o;
        return page == that.page && limit == that.limit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, limit);
    }
}
