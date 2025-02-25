package com.fool.gamearchivemanager.module.test.controller;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class TestSubscriber {

    // 创建一个自定义的Subscriber
    static class MySubscriber implements Flow.Subscriber<Integer> {
        private Flow.Subscription subscription;

        private final CountDownLatch latch;

        MySubscriber(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            System.out.println("Subscribed!");
            subscription.request(5); // 请求5个数据项
        }

        @Override
        public void onNext(Integer item) {
            System.out.println("Consumed: " + item);
            try {
                // 模拟消费过程
                Thread.sleep(500);  // 模拟处理延迟
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            latch.countDown();
            // 消费完一个数据后，再请求下一个数据项
            subscription.request(1); // 控制背压，减少请求的频率
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.println("Completed!");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建一个 SubmissionPublisher，作为 Publisher
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();

        CountDownLatch countDownLatch = new CountDownLatch(20);
        // 创建一个订阅者
        MySubscriber subscriber = new MySubscriber(countDownLatch);

        // 将订阅者与发布者绑定
        publisher.subscribe(subscriber);

        // 启动发布者生产数据
        for (int i = 0; i < 20; i++) {
            publisher.submit(i);  // 发布数据
            System.out.println("Produced: " + i);
            Thread.sleep(100);  // 模拟生产的速度
        }

        // 等待一段时间，确保消费者处理完数据

        countDownLatch.await();
        // 关闭发布者
        publisher.close();


    }
}
