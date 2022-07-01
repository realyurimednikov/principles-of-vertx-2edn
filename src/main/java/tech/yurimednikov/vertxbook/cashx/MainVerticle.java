package tech.yurimednikov.vertxbook.cashx;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import tech.yurimednikov.vertxbook.cashx.config.ApplicationConfigurationManager;
import tech.yurimednikov.vertxbook.cashx.config.BasicApplicationConfigurationManagerImpl;
import tech.yurimednikov.vertxbook.cashx.modules.ApplicationModule;
import tech.yurimednikov.vertxbook.cashx.verticles.AccountsRestVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ApplicationConfigurationManager configurationManager = new BasicApplicationConfigurationManagerImpl(vertx);
    configurationManager.retrieveApplicationConfiguration().map(config -> {
      ApplicationModule module = new ApplicationModule(vertx, config);
      Injector injector = Guice.createInjector(module);
      AccountsRestVerticle verticle = injector.getInstance(AccountsRestVerticle.class);
      return verticle;
    })
    .compose(verticle -> vertx.deployVerticle(verticle))
    .onFailure(err->{
      System.out.println("Fail to initialize an application");
      System.out.println(err.getMessage());
      startPromise.fail(err);
    })
    .onSuccess(result->{
      System.out.println("Application is up and running");
      startPromise.complete();
    });
  }
}
