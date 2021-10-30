package net.yurimednikov.vertxbook.cashx.files;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import net.yurimednikov.vertxbook.cashx.errors.DependencyCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

@ExtendWith(VertxExtension.class)
class MinioS3FileUploadServiceImplTest {

    private MinioS3FileUploadServiceImpl service;

    @BeforeEach
    void setup(Vertx vertx, VertxTestContext context){
        ConfigStoreOptions store = new ConfigStoreOptions()
                .setType("file")
                .setFormat("properties")
                .setConfig(new JsonObject().put("path", "minio.properties"));

        ConfigRetrieverOptions retrieverOptions = new ConfigRetrieverOptions();
        retrieverOptions.addStore(store);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, retrieverOptions);
        retriever.getConfig().onSuccess(configuration -> {
            if (configuration.isEmpty()) context.failNow(new DependencyCreationException());
            service = new MinioS3FileUploadServiceImpl(vertx, configuration);
            context.completeNow();
        }).onFailure(context::failNow);
    }

    @Test
    void uploadTest(Vertx vertx, VertxTestContext context) {
        String filename = UUID.randomUUID().toString();
        String format = "txt";
        String content = "SGVsbG8sIFdvcmxk";
        service.upload(filename, format, content)
                .onComplete(result -> context.completeNow())
                .onFailure(context::failNow);
    }
}
