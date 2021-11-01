package net.yurimednikov.vertxbook.cashx.tokens;

import io.vertx.core.Future;
import net.yurimednikov.vertxbook.cashx.models.UserPrincipal;

import java.util.List;

public interface TokenManager {

    Future<UserPrincipal> decodeToken (String token);

    Future<String> encodeToken (long userId, List<String> permissions);
}
