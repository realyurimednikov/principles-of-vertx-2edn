package tech.yurimednikov.vertxbook.cashx.tokens;

import io.vertx.core.Future;
import tech.yurimednikov.vertxbook.cashx.errors.AccessDeniedException;
import tech.yurimednikov.vertxbook.cashx.models.UserPrincipal;

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
        if (principal.getIssuedAt().isBefore(LocalDateTime.now().minusDays(30))) return Future.failedFuture(new AccessDeniedException());
        return Future.succeededFuture(principal);
    }

    @Override
    public Future<String> encodeToken(long userId, List<String> permissions) {
        String token = UUID.randomUUID().toString();
        LocalDateTime validUntil = LocalDateTime.now().plusDays(30);
        UserPrincipal principal = new UserPrincipal(userId, permissions, validUntil);
        tokens.put(token, principal);
        return Future.succeededFuture(token);
    }
}
