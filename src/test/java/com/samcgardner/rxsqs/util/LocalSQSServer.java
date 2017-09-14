package com.samcgardner.rxsqs.util;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.typesafe.config.ConfigFactory;
import org.elasticmq.server.ElasticMQServer;
import org.elasticmq.server.config.ElasticMQServerConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LocalSQSServer {

    // tried to use custom port but exceedingly difficult with elasticMQServer :(
    public static final int LOCAL_SQS_PORT = 9324;
    public static final String DEFAULT_LOCAL_SQS_ENDPOINT = String.format("http://localhost:%d", LOCAL_SQS_PORT);

    public static AmazonSQSClient SQS_CLIENT;

    public LocalSQSServer() {
        if (SQS_CLIENT == null) {
            SQS_CLIENT = createLocalSqsClient();
            start();
        }
    }

    private AmazonSQSClient createLocalSqsClient() {
        AmazonSQSClient amazonSQSClient;
        amazonSQSClient = new AmazonSQSClient(new BasicAWSCredentials("x", "x"));
        amazonSQSClient.setEndpoint(DEFAULT_LOCAL_SQS_ENDPOINT);
        return amazonSQSClient;
    }

    public LocalSQSServer start() {
        ElasticMQServer elasticMQServer = new ElasticMQServer(new ElasticMQServerConfig(ConfigFactory.load()));
        elasticMQServer.start();
        return this;
    }

    public void createQueue(String queueName) {
        SQS_CLIENT.createQueue(queueName);
    }

    public void sendMessage(String queueName, String message) {
        String queueUrl = SQS_CLIENT.getQueueUrl(queueName).getQueueUrl();
        SQS_CLIENT.sendMessage(queueUrl, message);
    }

    public List<Message> receiveMessage(String queueName) {
        String queueUrl = SQS_CLIENT.getQueueUrl(queueName).getQueueUrl();
        GetQueueAttributesResult approximateNumberOfMessages = SQS_CLIENT.getQueueAttributes(queueUrl, Collections.singletonList("ApproximateNumberOfMessages"));
        String noOfMessages = approximateNumberOfMessages.getAttributes().get("ApproximateNumberOfMessages");
        List<ReceiveMessageResult> collect = IntStream.range(0, Integer.parseInt(noOfMessages)).mapToObj(i -> SQS_CLIENT.receiveMessage(queueUrl)).collect(Collectors.toList());
        return collect.stream().map(ReceiveMessageResult::getMessages).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public void deleteMessage(String queueName) {
        String queueUrl = SQS_CLIENT.getQueueUrl(queueName).getQueueUrl();
        for (Message message : receiveMessage(queueName)) {
            SQS_CLIENT.deleteMessage(queueUrl, message.getReceiptHandle());
        }
    }
}
