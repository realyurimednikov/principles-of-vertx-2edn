package net.yurimednikov.vertxbook.cashx.modules;

import com.google.inject.AbstractModule;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;
import net.yurimednikov.vertxbook.cashx.models.ApplicationConfiguration;
import net.yurimednikov.vertxbook.cashx.repositories.AccountReactivePgRepositoryImpl;
import net.yurimednikov.vertxbook.cashx.repositories.UserReactivePgRepositoryImpl;
import net.yurimednikov.vertxbook.cashx.services.AccountService;
import net.yurimednikov.vertxbook.cashx.services.AccountServiceImpl;
import net.yurimednikov.vertxbook.cashx.services.AuthService;
import net.yurimednikov.vertxbook.cashx.services.AuthServiceImpl;
import net.yurimednikov.vertxbook.cashx.tokens.SessionTokenManager;
import net.yurimednikov.vertxbook.cashx.tokens.TokenManager;
import net.yurimednikov.vertxbook.cashx.verticles.SecuredAccountsRestVerticle;
import net.yurimednikov.vertxbook.cashx.web.AuthController;
import net.yurimednikov.vertxbook.cashx.web.SecuredAccountController;

public class SecuredApplicationModule extends AbstractModule {

    private AuthController authController;
    private SecuredAccountController accountController;
    private AuthService authService;

    public SecuredApplicationModule(Vertx vertx, ApplicationConfiguration configuration){
        SqlClient client = PgPool.client(vertx, configuration.getDatabaseUrl());
        UserReactivePgRepositoryImpl userRepository = new UserReactivePgRepositoryImpl(vertx, client);
        userRepository.createTable().onFailure(System.out::println).onSuccess(r -> System.out.println("User repository: a table was created"));
        AccountReactivePgRepositoryImpl accountRepository = new AccountReactivePgRepositoryImpl(vertx, client);
        accountRepository.createTable().onFailure(System.out::println).onSuccess(r -> System.out.println("Account repository: a table was created"));
        TokenManager tokenManager = new SessionTokenManager();
        authService = new AuthServiceImpl(userRepository, tokenManager);
        AccountService accountService = new AccountServiceImpl(accountRepository);
        authController = new AuthController(authService);
        accountController = new SecuredAccountController(accountService, authService);
    }

    @Override
    protected void configure() {
        bind(SecuredAccountsRestVerticle.class)
                .toInstance(new SecuredAccountsRestVerticle(authService, authController, accountController));
    }
}
