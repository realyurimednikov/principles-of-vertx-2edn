package net.yurimednikov.vertxbook.cashx.files;

import io.vertx.core.Future;

public interface FileUploadService {
    
    Future<Boolean> upload (String name, String format, String content);
}
