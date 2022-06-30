package tech.yurimednikov.vertxbook.cashx.modules;

import com.google.inject.AbstractModule;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;
import tech.yurimednikov.vertxbook.cashx.errors.DependencyCreationException;
import tech.yurimednikov.vertxbook.cashx.models.ApplicationConfiguration;
import tech.yurimednikov.vertxbook.cashx.repositories.AccountReactivePgRepositoryImpl;
import tech.yurimednikov.vertxbook.cashx.services.AccountService;
import tech.yurimednikov.vertxbook.cashx.services.AccountServiceImpl;
import tech.yurimednikov.vertxbook.cashx.verticles.AccountsRestVerticle;
import tech.yurimednikov.vertxbook.cashx.web.AccountController;

public class ApplicationModule extends AbstractModule {

    private final AccountReactivePgRepositoryImpl repository;
    private final AccountService service;
    private final AccountController controller;

    public ApplicationModule(Vertx vertx, ApplicationConfiguration configuration) throws DependencyCreationException {
        SqlClient client = PgPool.client(vertx, configuration.getDatabaseUrl());
        this.repository = new AccountReactivePgRepositoryImpl(vertx, client);
        this.repository.createTable()
                .onFailure(System.out::println)
                .onSuccess(r -> System.out.println("Account Repository: A table created"));
        this.service = new AccountServiceImpl(repository);
        this.controller = new AccountController(service);
    }
    
    @Override
    protected void configure() {
        bind(AccountsRestVerticle.class).toInstance(new AccountsRestVerticle(controller));
    }
}
