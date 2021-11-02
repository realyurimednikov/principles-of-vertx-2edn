package net.yurimednikov.vertxbook.cashx.verticles;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import net.yurimednikov.vertxbook.cashx.config.ApplicationConfigurationManager;
import net.yurimednikov.vertxbook.cashx.config.EmbeddedApplicationConfigurationManagerImpl;
import net.yurimednikov.vertxbook.cashx.errors.AccessDeniedException;
import net.yurimednikov.vertxbook.cashx.errors.ValidatorException;
import net.yurimednikov.vertxbook.cashx.modules.SecuredApplicationModule;
import net.yurimednikov.vertxbook.cashx.services.AuthService;
import net.yurimednikov.vertxbook.cashx.web.AuthController;
import net.yurimednikov.vertxbook.cashx.web.SecuredAccountController;

public class SecuredAccountsRestVerticle extends AbstractVerticle {

    private AuthController authController;
    private SecuredAccountController accountController;
    private AuthService authService;

    public SecuredAccountsRestVerticle(AuthService authService, AuthController authController, SecuredAccountController accountController){
        this.authController = authController;
        this.accountController = accountController;
        this.authService = authService;
    }

    @Override
    public void start(Promise<Void> promise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/api/*").handler(BodyHandler.create());
        router.route("/api/*").handler(CorsHandler.create("*"));
        router.route("/api/secured/*").handler(authService::authenticate);
        router.post("/api/secured/accounts").handler(accountController::createAccount);
        router.get("/api/secured/accounts").handler(accountController::findAccounts);
        router.put("/api/secured/accounts/:id").handler(accountController::updateAccount);
        router.get("/api/secured/accounts/:id").handler(accountController::findAccountById);
        router.delete("/api/secured/accounts/:id").handler(accountController::removeAccount);
        router.post("/api/auth/login").handler(authController::loginEndpoint);
        router.post("/api/auth/signup").handler(authController::signupEndpoint);
        router.route("/api/*").failureHandler(context -> {
            if (context.failed()){
                Throwable reason = context.failure();
                if (reason instanceof ValidatorException){
                    ValidatorException exception = (ValidatorException) reason;
                    JsonArray validatorMessages = exception.getMessages();
                    System.out.println(validatorMessages.encode());
                    context.response().setStatusCode(400).end(validatorMessages.encode());
                } else if (reason instanceof AccessDeniedException) {
                    context.response().setStatusCode(403).end("Access Denied");
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
        ApplicationConfigurationManager configurationManager = new EmbeddedApplicationConfigurationManagerImpl(vertx);
        configurationManager.retrieveApplicationConfiguration()
                .map(configuration -> {
                    SecuredApplicationModule module = new SecuredApplicationModule(vertx, configuration);
                    return module;
                })
                .map(module -> {
                    Injector injector = Guice.createInjector(module);
                    SecuredAccountsRestVerticle verticle = injector.getInstance(SecuredAccountsRestVerticle.class);
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
