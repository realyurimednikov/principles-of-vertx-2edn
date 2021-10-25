package net.yurimednikov.vertxbook.cashx.files;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
class LocalFileUploadServiceImplTest {
    
    @Test
    void uploadTest(Vertx vertx, VertxTestContext context){
        LocalFileUploadServiceImpl service = new LocalFileUploadServiceImpl(vertx);
        String filename = UUID.randomUUID().toString();
        String content = "SGVsbG8sIFdvcmxk";
        FileSystem fileSystem = vertx.fileSystem();
        Checkpoint saveCheckpoint = context.checkpoint();
        Checkpoint existCheckpoint = context.checkpoint();
        
        context.verify(() -> {
            service.upload(filename, "txt", content)
                .compose(result -> {
                    Assertions.assertTrue(result);
                    saveCheckpoint.flag();
                    return fileSystem.exists(filename + ".txt");
                })
                .compose(result -> {
                    Assertions.assertTrue(result);
                    existCheckpoint.flag();
                    return fileSystem.delete(filename + ".txt");
                })
                .onSuccess(r -> {
                    context.completeNow();
                })
                .onFailure(context::failNow);
        });
    }
}
