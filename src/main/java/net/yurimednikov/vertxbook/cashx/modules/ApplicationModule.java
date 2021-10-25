package net.yurimednikov.vertxbook.cashx.modules;

import com.google.inject.AbstractModule;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;
import net.yurimednikov.vertxbook.cashx.errors.DependencyCreationException;
import net.yurimednikov.vertxbook.cashx.models.ApplicationConfiguration;
import net.yurimednikov.vertxbook.cashx.repositories.AccountRepository;
import net.yurimednikov.vertxbook.cashx.repositories.AccountReactivePgRepositoryImpl;
import net.yurimednikov.vertxbook.cashx.services.AccountService;
import net.yurimednikov.vertxbook.cashx.services.AccountServiceImpl;
import net.yurimednikov.vertxbook.cashx.verticles.AccountsRestVerticle;
import net.yurimednikov.vertxbook.cashx.web.AccountController;

public class ApplicationModule extends AbstractModule {

    private final AccountRepository repository;
    private final AccountService service;
    private final AccountController controller;

    public ApplicationModule(Vertx vertx, ApplicationConfiguration configuration) throws DependencyCreationException{
        SqlClient client = PgPool.client(vertx, configuration.getDatabaseUrl());
        this.repository = new AccountReactivePgRepositoryImpl(client);
        this.service = new AccountServiceImpl(repository);
        this.controller = new AccountController(service);
    }
    
    @Override
    protected void configure() {
        bind(AccountsRestVerticle.class).toInstance(new AccountsRestVerticle(controller));
    }
}
