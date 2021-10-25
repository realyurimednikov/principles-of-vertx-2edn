package net.yurimednikov.vertxbook.cashx.auth;

import java.util.List;

public final class UserPrincipal {
    
    private final long userId;
    private final List<AuthenticationPermission> permissions;
    
    public UserPrincipal(long userId, List<AuthenticationPermission> permissions) {
        this.userId = userId;
        this.permissions = permissions;
    }

    public long getUserId() {
        return userId;
    }

    public List<AuthenticationPermission> getPermissions() {
        return permissions;
    }

    
}
