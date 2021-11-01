package net.yurimednikov.vertxbook.cashx.tokens;

import io.vertx.core.Future;
import net.yurimednikov.vertxbook.cashx.errors.AccessDeniedException;
import net.yurimednikov.vertxbook.cashx.models.UserPrincipal;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SessionTokenManager implements TokenManager{

    private HashMap<String, UserPrincipal> tokens = new HashMap<>();

    @Override
    public Future<UserPrincipal> decodeToken(String token) {
        UserPrincipal principal = tokens.get(token);
        if (principal == null) return Future.failedFuture(new AccessDeniedException());
        return Future.succeededFuture(principal);
    }

    @Override
    public Future<String> encodeToken(long userId, List<String> permissions) {
        String token = UUID.randomUUID().toString();
        UserPrincipal principal = new UserPrincipal(userId, permissions, LocalDateTime.now());
        tokens.put(token, principal);
        return Future.succeededFuture(token);
    }
}
