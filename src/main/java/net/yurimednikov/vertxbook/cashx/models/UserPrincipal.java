package net.yurimednikov.vertxbook.cashx.models;

import java.time.LocalDateTime;
import java.util.List;

public final class UserPrincipal {

    private final long userid;
    private final List<String> permissions;
    private final LocalDateTime issuedAt;

    public UserPrincipal(long userid, List<String> permissions, LocalDateTime issuedAt) {
        this.userid = userid;
        this.permissions = permissions;
        this.issuedAt = issuedAt;
    }

    public long getUserid() {
        return userid;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
}
