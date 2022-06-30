package tech.yurimednikov.vertxbook.cashx.verticles;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import tech.yurimednikov.vertxbook.cashx.config.ApplicationConfigurationManager;
import tech.yurimednikov.vertxbook.cashx.errors.DependencyCreationException;
import tech.yurimednikov.vertxbook.cashx.modules.ApplicationModule;

public class AppVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
//        ApplicationConfigurationManager configurationManager = new EmbeddedApplicationConfigurationManagerImpl(vertx);
//        configurationManager.retrieveApplicationConfiguration()
//                .compose(configuration -> {
//                    try {
//                        ApplicationModule module = new ApplicationModule(vertx, configuration);
//                        return Future.succeededFuture(module);
//                    } catch (DependencyCreationException ex){
//                        return Future.failedFuture(ex);
//                    }
//                })
//                .map(module -> {
//                    Injector injector = Guice.createInjector(module);
//                    AccountsRestVerticle verticle = injector.getInstance(AccountsRestVerticle.class);
//                    return verticle;
//                })
//                .compose(vertx::deployVerticle)
//                .onFailure(err -> {
//                    System.out.println(err.getMessage());
//                    startPromise.fail(err);
//                })
//                .onSuccess(result -> {
//                    System.out.println("Verticle deployed with id " + result);
//                    startPromise.complete();
//                });
    }
}
