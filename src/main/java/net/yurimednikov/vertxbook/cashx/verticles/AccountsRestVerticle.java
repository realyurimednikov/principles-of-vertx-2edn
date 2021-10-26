package net.yurimednikov.vertxbook.cashx.verticles;


import com.google.inject.Guice;
import com.google.inject.Injector;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import net.yurimednikov.vertxbook.cashx.config.ApplicationConfigurationManager;
import net.yurimednikov.vertxbook.cashx.config.BasicApplicationConfigurationManagerImpl;
import net.yurimednikov.vertxbook.cashx.errors.DependencyCreationException;
import net.yurimednikov.vertxbook.cashx.errors.EntityAlreadyExistsException;
import net.yurimednikov.vertxbook.cashx.errors.ValidatorException;
import net.yurimednikov.vertxbook.cashx.modules.ApplicationModule;
import net.yurimednikov.vertxbook.cashx.web.AccountController;

public class AccountsRestVerticle extends AbstractVerticle {
    
    private final AccountController controller;

    public AccountsRestVerticle(AccountController controller){
        this.controller = controller;
    }


    @Override
    public void start(Promise<Void> promise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/api/*").handler(BodyHandler.create());
        router.route("/api/*").handler(CorsHandler.create("*"));
        router.post("/api/accounts/:userId").handler(controller::createAccount);
        router.get("/api/accounts/:userId").handler(controller::findAccounts);
        router.put("/api/account/:id").handler(controller::updateAccount);
        router.get("/api/account/:id").handler(controller::findAccountById);
        router.delete("/api/account/:id").handler(controller::removeAccount);
        router.route("/api/*").failureHandler(context -> {
            if (context.failed()){
                Throwable reason = context.failure();
                if (reason instanceof ValidatorException){
                    ValidatorException exception = (ValidatorException) reason;
                    JsonArray validatorMessages = exception.getMessages();
                    System.out.println(validatorMessages.encode());
                    context.response().setStatusCode(400).end(validatorMessages.encode());
                } else if (reason instanceof EntityAlreadyExistsException) {
                    context.response().setStatusCode(403).end();
                } else {
                    context.response().setStatusCode(500).end("Error happened");
                }
            }
        });
        server.requestHandler(router);
        server.listen(8080)
            .onFailure(promise::fail)
            .onSuccess(result -> promise.complete());
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        ApplicationConfigurationManager configurationManager = new BasicApplicationConfigurationManagerImpl(vertx);
        configurationManager.retrieveApplicationConfiguration()
            .compose(configuration -> {
                try {
                    ApplicationModule module = new ApplicationModule(vertx, configuration);
                    return Future.succeededFuture(module);
                } catch (DependencyCreationException ex){
                    return Future.failedFuture(ex);
                }
            })
            .map(module -> {
                Injector injector = Guice.createInjector(module);
                AccountsRestVerticle verticle = injector.getInstance(AccountsRestVerticle.class);
                return verticle;
            })
            .compose(vertx::deployVerticle)
            .onFailure(err -> {
                System.out.println(err.getMessage());
                vertx.close();
            })
            .onSuccess(result -> {
                System.out.println("Verticle deployed with id " + result);
            });
    }
}
