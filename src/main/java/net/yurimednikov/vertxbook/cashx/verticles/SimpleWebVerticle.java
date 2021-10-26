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
                            <h1>Hello from Vertx 4!</h1>
                            <h2>This is just a simple example, that shows how to build a
                             web application with Vertx</h2>
                            <p> :) </p>
                            """; //4
                    context.response()
                            .putHeader("Content-Type", "text/html") //5
                            .setStatusCode(200) //6
                            .end(message); //7
                });

        server.requestHandler(router); //8
        server.listen(8080) //9
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
