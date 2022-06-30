package tech.yurimednikov.vertxbook.cashx.files;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import tech.yurimednikov.vertxbook.cashx.models.Base64Data;

public class FtpFileUploadServiceImpl extends AbstractFileService{

    private final Vertx vertx;
    private final FTPClient ftpClient;
    private final String server;
    private final String user;
    private final String password;
    private final int port;

    public FtpFileUploadServiceImpl(Vertx vertx, JsonObject config){
        this.vertx = vertx;
        this.ftpClient = new FTPClient();
        this.server = config.getString("server");
        this.password = config.getString("password");
        this.user = config.getString("username");
        this.port = config.getInteger("port");
    }

    @Override
    public Future<Boolean> upload(String name, String format, String content) {
        return vertx.executeBlocking(promise -> {
            encodeData(content).onSuccess(data -> {
                String path = "/" + getFilename(name, format);
                boolean result = connectAndUpload(path, data);
                promise.complete(result);
            });
        }).compose(result -> Future.succeededFuture(Boolean.parseBoolean(result.toString())));
        // return null;
    }

    private boolean connectAndUpload(String path, Base64Data content){
        try {
            // ftpClient.ope
            ftpClient.connect(server, port);
            int connectStatus = ftpClient.getReplyCode();
            if (FTPReply.isPositiveCompletion(connectStatus)){
                System.out.println("Client connected");
                boolean logged = ftpClient.login(user, password);

                
                
                // System.out.println("Client logged");
                if (logged) {
                    System.out.println("Client uploading");
                    InputStream inputStream = new ByteArrayInputStream(content.getBytes());
                    boolean result = ftpClient.storeFile(path, inputStream);
                    inputStream.close();
                    return result;
                } else {
                    ftpClient.disconnect();
                    System.out.println("Cannot login");
                    // throw new RuntimeException();
                    return false;
                }
            } else {
                ftpClient.disconnect();
                // throw new RuntimeException();
                // System.out.println();
                System.out.println("Cannot connect");
                return false;
            }
        } catch (Exception ex){
            //
            System.out.println(ex.getClass().toGenericString());
            System.out.println(ex.getMessage());
            return false;
        }
        // } finally {
        //     if (ftpClient.isConnected()) {
        //         try {
        //             ftpClient.disconnect();
        //         } catch (Exception ex){
        //             //
        //         }
        //     }

        //     //
        // }

        // return false;

    }
    
}
