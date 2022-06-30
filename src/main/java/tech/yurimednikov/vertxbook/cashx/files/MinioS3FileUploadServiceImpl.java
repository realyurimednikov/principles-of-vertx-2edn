package tech.yurimednikov.vertxbook.cashx.files;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.io.ByteArrayInputStream;

public class MinioS3FileUploadServiceImpl extends AbstractFileService{

    private final Vertx vertx;
    private final MinioClient client;
    private final String bucket;

    public MinioS3FileUploadServiceImpl(Vertx vertx, JsonObject config){
        this.vertx = vertx;
        this.bucket = config.getString("bucket");
        this.client = MinioClient.builder().endpoint(config.getString("endpoint"))
                .credentials(config.getString("access_key"), config.getString("secret"))
                .build();
    }

    @Override
    public Future<Boolean> upload(String name, String format, String content) {
        return encodeData(content).compose(result -> vertx.executeBlocking(promise -> {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(result.getBytes());
            String fileName = getFilename(name, format);
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .stream(byteStream, byteStream.available(), -1)
                    .build();
            try {
                client.putObject(putObjectArgs);
                promise.complete(true);
            }catch (Exception exception){
                System.out.println(exception.getMessage());
                promise.fail(exception);
            }
        })).map(result -> Boolean.TRUE);
    }
}
