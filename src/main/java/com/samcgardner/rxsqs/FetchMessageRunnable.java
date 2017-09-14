package com.samcgardner.rxsqs;

import com.amazonaws.AbortedException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class FetchMessageRunnable extends Thread {

    private final String queueUrl;
    private final AmazonSQS sqs;
    private final BlockingQueue<Message> messages;

    FetchMessageRunnable(String queueUrl, AmazonSQS sqs, BlockingQueue<Message> messages) {
        this.queueUrl = queueUrl;
        this.messages = messages;
        this.sqs = sqs;
    }

    @Override
    public void run() {
        while(!this.isInterrupted()) {
            ReceiveMessageRequest request = new ReceiveMessageRequest()
                    .withWaitTimeSeconds(20)
                    .withMaxNumberOfMessages(10)
                    .withQueueUrl(queueUrl);

            // We can safely instantiate this to null as all caught exceptions will result in a return
            List<Message> fetched;

            try {
                fetched = sqs.receiveMessage(request).getMessages();
            }
            catch (AbortedException e) {
                // Presumably, if we are aborting then we don't intend to fetch any more
                return;
            }

            for (Message msg : fetched) {
                try {
                    messages.put(msg);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
