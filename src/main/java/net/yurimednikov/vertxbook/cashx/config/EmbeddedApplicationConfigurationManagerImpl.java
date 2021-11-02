package net.yurimednikov.vertxbook.cashx.config;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import net.yurimednikov.vertxbook.cashx.models.ApplicationConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

public class EmbeddedApplicationConfigurationManagerImpl implements ApplicationConfigurationManager{

    private Vertx vertx;
    private PostgreSQLContainer<?> container;

    public EmbeddedApplicationConfigurationManagerImpl(Vertx vertx){
        this.vertx = vertx;
    }

    @Override
    public Future<ApplicationConfiguration> retrieveApplicationConfiguration() {
        return vertx.executeBlocking(promise -> {
           try {
               JsonObject result = runContainer();
               promise.complete(result);
           } catch (Exception ex){
               promise.fail(ex);
           }
        }).map(data -> {
            JsonObject containerData = JsonObject.mapFrom(data);
            System.out.println("Container is alive: " + container.isRunning());
            ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(containerData.getString("db_url"));
            return applicationConfiguration;
        });
    }

    private JsonObject runContainer() throws Exception{
        try {
            container = new PostgreSQLContainer<>("postgres:11-alpine")
                    .withDatabaseName("cashxdb").withUsername("user").withPassword("secret");
            container.start();
            int port = container.getFirstMappedPort();
            String uri = "postgresql://user:secret@localhost:" + port + "/cashxdb";
            System.out.println("Container created: " + uri);
            JsonObject config = new JsonObject();
            config.put("db_url", uri);
            return config;
        } catch (Exception ex){
            throw new RuntimeException("Unable to create a PostgreSQL container\n" + ex.getMessage());
        }
    }
}
