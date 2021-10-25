package net.yurimednikov.vertxbook.cashx.files;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

public class LocalFileUploadServiceImpl extends AbstractFileService {

    private Vertx vertx;

    public LocalFileUploadServiceImpl(Vertx vertx){
        this.vertx = vertx;
    }

    @Override
    public Future<Boolean> upload(String name, String format, String content) {
        String filename = getFilename(name, format);
        FileSystem fileSystem = vertx.fileSystem();
        return fileSystem.createFile(filename)
            .compose(r -> encodeData(content))
            .compose(data -> {
                Buffer buffer = Buffer.buffer(data.getBytes());
                return fileSystem.writeFile(filename, buffer);
            })
            .compose(r -> Future.succeededFuture(true));
    }
    
}
