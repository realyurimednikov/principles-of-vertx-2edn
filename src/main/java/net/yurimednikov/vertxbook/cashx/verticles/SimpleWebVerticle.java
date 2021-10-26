package net.yurimednikov.vertxbook.cashx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

class SimpleWebVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> promise) throws Exception{
        HttpServer server = vertx.createHttpServer(); // 1
        Router router = Router.router(vertx); //2
        router.get("/hello") //3
                .handler(context -> {
                    String message = """
                            <h1>Hello from the Vertx</h1>
                            <p>This is just a simple example, that shows how to build a
                             web application with Vertx</p>
                            <p> :) </p>
                            """; //4
                    context.response()
                            .putHeader("Content-Type", "text/html")
                            .setStatusCode(200) //5
                            .end(message); //6
                });

        server.requestHandler(router); //7
        server.listen(8080) //8
                .onFailure(promise::fail)
                .onSuccess(result -> promise.complete());
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        SimpleWebVerticle verticle = new SimpleWebVerticle();
        vertx.deployVerticle(verticle).onFailure(err -> System.out.println(err.getMessage()))
                .onSuccess(result -> System.out.println("Verticle deployed with id: " + result));
    }
}
