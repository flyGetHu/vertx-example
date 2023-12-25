package com.vertx.common.config;

import cn.hutool.core.date.DateUtil;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.SharedData;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
public class FutureTest {

    @Test
    public void testFuture(VertxTestContext testContext) throws InterruptedException {
        final Vertx vertx = Vertx.vertx();
        final SharedData sharedData = vertx.sharedData();
        sharedData.getLocalCounter("test").onSuccess(counter -> {
            final List<List<Future<String>>> taskss = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                final List<Future<String>> tasks = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    tasks.add(task(vertx));
                }
                taskss.add(tasks);
            }
            final List<CompositeFuture> tasks = new ArrayList<>();
            for (List<Future<String>> futures : taskss) {
                final CompositeFuture compositeFuture = Future.all(futures);
                tasks.add(compositeFuture);
            }
            final CompositeFuture compositeFuture = Future.all(tasks).onComplete(ar -> {
                if (ar.succeeded()) {
                    System.out.println("all success");
                } else {
                    System.out.println("all failed");
                }
                testContext.completeNow();
            });
        });
        testContext.awaitCompletion(100, TimeUnit.SECONDS);
    }

    Future<String> task(Vertx vertx) {
        Promise<String> promise = Promise.promise();
        vertx.setTimer(1000, l -> {
            System.out.println(DateUtil.now());
            promise.complete("task");
        });
        return promise.future();
    }
}
