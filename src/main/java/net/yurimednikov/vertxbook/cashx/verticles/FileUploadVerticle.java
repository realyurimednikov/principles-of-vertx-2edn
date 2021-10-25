package net.yurimednikov.vertxbook.cashx.verticles;

// import com.google.inject.Guice;
// import com.google.inject.Injector;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
// import net.yurimednikov.vertxbook.cashx.config.ApplicationConfigurationManager;
import net.yurimednikov.vertxbook.cashx.files.FileUploadService;
// import net.yurimednikov.vertxbook.cashx.modules.FileUploadVerticleModule;

class FileUploadVerticle extends AbstractVerticle {

    private final FileUploadService service;

    FileUploadVerticle(FileUploadService service){
        this.service = service;
    }
    
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/api/*").handler(BodyHandler.create());
        router.post("/api/upload").handler(ctx -> {
            JsonObject body = ctx.getBodyAsJson();
            String name = body.getString("name");
            String content = body.getString("content");
            String format = body.getString("format");
            service.upload(name, format, content)
                .onSuccess(result -> {
                    JsonObject responseBody = new JsonObject();
                    responseBody.put("success", result);
                    ctx.response().setStatusCode(201).end(responseBody.encode());
                })
                .onFailure(ctx::fail);
        });
        server.requestHandler(router);
        server.listen(8080).onSuccess(r -> startPromise.complete()).onFailure(startPromise::fail);
    }

}
