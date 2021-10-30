package net.yurimednikov.vertxbook.cashx.files;


import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
class FtpFileUploadServiceImplTest {
    
    FakeFtpServer ftpServer;
    
    final String USERNAME = "user";
    final String PASSWORD = "password";

    @BeforeEach
    void setup () throws Exception {
        ftpServer = new FakeFtpServer();
        ftpServer.addUserAccount(new UserAccount(USERNAME, PASSWORD, "/data"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));
        ftpServer.setFileSystem(fileSystem);
        
        ftpServer.start();
        System.out.println("Server is running: " + ftpServer.getSystemStatus());
    }

    @Test
    void uploadTest(Vertx vertx, VertxTestContext context){

        String filename = UUID.randomUUID().toString();
        String format = "txt";
        String content = "SGVsbG8sIFdvcmxk";

        JsonObject config = new JsonObject();
        config.put("username", USERNAME);
        config.put("password", PASSWORD);
        config.put("server", "localhost");
        config.put("port", ftpServer.getServerControlPort());

        FtpFileUploadServiceImpl service = new FtpFileUploadServiceImpl(vertx, config);
        context.verify(() -> {
            System.out.println("Server status: " + ftpServer.getSystemStatus());
            service.upload(filename, format, content)
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    Assertions.assertTrue(result);
                    context.completeNow();
                });
        });
    }

    @AfterEach
    void shutdown () throws Exception{
        ftpServer.stop();
    }
}
