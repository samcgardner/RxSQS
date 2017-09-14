package com.samcgardner.rxsqs;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class RxSQSConfigurationBuilder {

    private AmazonSQS amazonSQS = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    private int maxBufferSize = 200;
    private int threadPoolSize = 1;
    private boolean deleteOnReceive = true;
    private String dlqUrl = "";

    private RxSQSConfigurationBuilder() {
    }

    public static RxSQSConfiguration defaultConfiguration() {
        return standard().build();
    }

    public static RxSQSConfigurationBuilder standard() {
        return new RxSQSConfigurationBuilder();
    }

    public RxSQSConfigurationBuilder withAmazonSQS(AmazonSQS sqs) {
        this.amazonSQS = sqs;
        return this;
    }

    public RxSQSConfigurationBuilder withMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
        return this;
    }

    public RxSQSConfigurationBuilder withThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        return this;
    }

    public RxSQSConfigurationBuilder withDeleteOnReceive(boolean deleteOnReceive) {
        this.deleteOnReceive = deleteOnReceive;
        return this;
    }

    public RxSQSConfigurationBuilder withDlqUrl(String dlqUrl) {
        this.dlqUrl = dlqUrl;
        return this;
    }


    public RxSQSConfiguration build() {
        return new RxSQSConfiguration(amazonSQS, maxBufferSize, threadPoolSize, deleteOnReceive, dlqUrl);
    }
}
