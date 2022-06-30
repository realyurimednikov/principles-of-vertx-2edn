package tech.yurimednikov.vertxbook.cashx.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import tech.yurimednikov.vertxbook.cashx.models.ApplicationConfiguration;

public class BasicApplicationConfigurationManagerImpl implements ApplicationConfigurationManager {

    private final ConfigRetriever retriever;

    public BasicApplicationConfigurationManagerImpl(Vertx vertx){
        this.retriever = ConfigRetriever.create(vertx);
    }

    @Override
    public Future<ApplicationConfiguration> retrieveApplicationConfiguration() {
        return retriever.getConfig().compose(raw -> {
            if (raw == null) return Future.failedFuture(new RuntimeException());
            String dbUrl = raw.getString("DATABASE_URL");
            return Future.succeededFuture(new ApplicationConfiguration(dbUrl));
        });
    }
    
}
