package com.samcgardner.rxsqs;

import com.amazonaws.services.sqs.model.Message;
import com.samcgardner.rxsqs.util.LocalSQSServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MessageProviderTest {

    public static String TEST_QUEUE = "test";

    public static LocalSQSServer sqsServer;

    @BeforeClass
    public static void startSQSServer() {
        sqsServer = new LocalSQSServer();
        sqsServer.createQueue(TEST_QUEUE);
    }

    @Test
    public void readMessage() throws Exception {

        sqsServer.sendMessage(TEST_QUEUE, "test");

        RxSQSConfiguration config = RxSQSConfigurationBuilder.standard()
                .withAmazonSQS(LocalSQSServer.SQS_CLIENT)
                .build();

        String queueUrl = LocalSQSServer.SQS_CLIENT.getQueueUrl(TEST_QUEUE).getQueueUrl();

        String body = RxSQS.getMessageObservable(queueUrl, config)
                .take(1)
                .toBlocking()
                .single()
                .getBody();

        assertEquals("test", body);

        List<Message> messages = sqsServer.receiveMessage(TEST_QUEUE);
        assertEquals(0, messages.size());

    }

}