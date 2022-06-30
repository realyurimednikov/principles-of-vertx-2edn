package tech.yurimednikov.vertxbook.cashx.config;

import io.vertx.core.Future;
import tech.yurimednikov.vertxbook.cashx.models.ApplicationConfiguration;

public interface ApplicationConfigurationManager {
    
    Future<ApplicationConfiguration> retrieveApplicationConfiguration();
    
}
