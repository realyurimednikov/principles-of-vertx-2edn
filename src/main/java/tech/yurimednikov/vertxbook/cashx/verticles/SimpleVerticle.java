package tech.yurimednikov.vertxbook.cashx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class SimpleVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        SimpleVerticle verticle = new SimpleVerticle();
        vertx.deployVerticle(verticle)
                .compose(id -> {
                    System.out.println("Verticle was deployed with id: " + id);
                    return vertx.undeploy(id);
                })
                .onSuccess(result -> {
                    System.out.println("Verticle undeployed");
                    vertx.close().onSuccess(r -> System.out.println("Vertx is closed"));
                })
                .onFailure(err -> System.out.println(err.getMessage()));
    }
}
