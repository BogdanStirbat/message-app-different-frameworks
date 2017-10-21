package com.bstirbat.message.reactive;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class ReactiveStreamsTests {

    @Test
    public void testSimpleMonoAndFlux() {
        Mono<String> mono = Mono.just("orange")
                .log()
                .map(String::toUpperCase);

        Flux<String> flux = Flux.just("orange", "apple", "banana")
                .log()
                .map(String::toUpperCase);
    }

    @Test
    public void testSubscribeToMono() {
        Mono.just("simple")
                .log()
                .map(String::toUpperCase)
                .subscribe(System.out::println);
    }

    @Test
    public void testMonoWithLongRunningSuppliers() {
        Mono.fromSupplier(() -> sleepAndReturn(2 * 1000, "delayed 2 seconds"))
                .log()
                .subscribe(System.out::println);
        System.out.println("Statement 1 reached");
    }

    @Test
    public void testMonoWithParallelLongRunningSuppliers() {
        Mono.fromSupplier(() -> sleepAndReturn(12 * 1000, "delayed 12 seconds"))
                .log()
                .subscribeOn(Schedulers.parallel())
                .subscribe(System.out::println);
        System.out.println("Statement 1 reached");

        Mono.fromSupplier(() -> sleepAndReturn(6 * 1000, "delayed 6 seconds"))
                .log()
                .subscribeOn(Schedulers.parallel())
                .subscribe(System.out::println);
        System.out.println("Statement 2 reached");

        String blockedMono = Mono.fromSupplier(() -> sleepAndReturn(18 * 1000, "delayed 18 seconds"))
                .log()
                .subscribeOn(Schedulers.parallel())
                .block();
        System.out.println("Statement 3 reached");
        System.out.println(blockedMono);
    }

    @Test
    public void testFluxWithSubscribe() {
        Flux.just("orange", "apple", "banana")
                .log()
                .map(String::toUpperCase)
                .subscribe(System.out::println);
    }

    @Test
    public void testFluxWithCustomSubscriber() {
        Flux.just("orange", "apple", "banana")
                .log()
                .map(String::toUpperCase)
                .subscribe(new Subscriber<String>() {

                    private long count = 0;
                    private Subscription subscription;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.subscription = subscription;
                        subscription.request(2);
                    }

                    @Override
                    public void onNext(String s) {
                        count++;
                        if (count >= 2) {
                            count = 0;
                            subscription.request(2);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Test
    public void testTwoLongRunningFluxes() throws InterruptedException {
        Flux.interval(Duration.ofMillis(1000))
                .log()
                .take(10)
                .subscribe(l -> System.out.println("First flux: " + l));

        Flux.interval(Duration.ofMillis(2222))
                .log()
                .take(4)
                .subscribe(l -> System.out.println("Second flux: " + l));

        Thread.sleep(11 * 1000);
    }

    private String sleepAndReturn(long milliseconds, String toReturn) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return toReturn;
    }

}
