package net.yurimednikov.vertxbook.cashx.config;

import io.vertx.core.Future;
import net.yurimednikov.vertxbook.cashx.models.ApplicationConfiguration;

public interface ApplicationConfigurationManager {
    
    Future<ApplicationConfiguration> retrieveApplicationConfiguration();
    
}
