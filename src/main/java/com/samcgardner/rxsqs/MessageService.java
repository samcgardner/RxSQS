package com.samcgardner.rxsqs;

import com.amazonaws.services.sqs.model.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageService {

    private BlockingQueue<Message> messages;
    private List<Thread> threads;

    MessageService(String queueUrl, RxSQSConfiguration config) {
        this.messages = new LinkedBlockingQueue<>(config.getMaxBufferSize());
        this.threads = new LinkedList<>();
        for(int i = 0; i < config.getThreadPoolSize(); i++) {
            threads.add(new Thread(new FetchMessageRunnable(queueUrl, config.getAmazonSQS(), messages)));
            threads.get(i).start();
        }
    }

    Message getNextMessage() throws InterruptedException {
        return messages.take();
    }

    void terminateThreads() {
        for (Thread thread : threads) thread.interrupt();
    }
}
