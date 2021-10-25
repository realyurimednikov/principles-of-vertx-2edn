package net.yurimednikov.vertxbook.cashx.files;

import java.util.Base64;

import io.vertx.core.Future;
import net.yurimednikov.vertxbook.cashx.models.Base64Data;

abstract class AbstractFileService implements FileUploadService {
    
    Future<Base64Data> encodeData(String data) {
        try {
            byte[] bytes = Base64.getEncoder().encode(data.getBytes());
            Base64Data result = new Base64Data(bytes);
            return Future.succeededFuture(result);
        } catch(Exception ex){
            return Future.failedFuture(new RuntimeException());
        }
    }

    String getFilename (String name, String format){
        return name + "." + format;
    }
}
