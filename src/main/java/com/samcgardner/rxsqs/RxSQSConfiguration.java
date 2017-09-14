package com.samcgardner.rxsqs;

import com.amazonaws.services.sqs.AmazonSQS;

public class RxSQSConfiguration {

    private final int maxBufferSize;
    private final int threadPoolSize;
    private final boolean deleteOnReceive;
    private final String dlqUrl;

    public String getDlqUrl() {
        return dlqUrl;
    }

    public boolean isDeleteOnReceive() {
        return deleteOnReceive;
    }

    AmazonSQS getAmazonSQS() {
        return amazonSQS;
    }

    private final AmazonSQS amazonSQS;

    RxSQSConfiguration(AmazonSQS amazonSQS, int maxBufferSize, int threadPoolSize, boolean deleteOnReceive, String dlqUrl) {
        this.amazonSQS = amazonSQS;
        this.maxBufferSize = maxBufferSize;
        this.threadPoolSize = threadPoolSize;
        this.deleteOnReceive = deleteOnReceive;
        this.dlqUrl = dlqUrl;
    }

    int getMaxBufferSize() {
        return maxBufferSize;
    }

    int getThreadPoolSize() {
        return threadPoolSize;
    }
}
