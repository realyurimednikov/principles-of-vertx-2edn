package tech.yurimednikov.vertxbook.cashx.tokens;

import io.vertx.core.Future;
import tech.yurimednikov.vertxbook.cashx.models.UserPrincipal;

import java.util.List;

public interface TokenManager {

    Future<UserPrincipal> decodeToken (String token);

    Future<String> encodeToken (long userId, List<String> permissions);
}
