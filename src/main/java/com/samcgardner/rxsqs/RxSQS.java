package com.samcgardner.rxsqs;

import com.amazonaws.services.sqs.model.Message;
import rx.Observable;

public class RxSQS {

    public static Observable<Message> getMessageObservable(String queueUrl) {
        return getMessageObservable(queueUrl, RxSQSConfigurationBuilder.defaultConfiguration());
    }

    public static Observable<Message> getMessageObservable(String queueUrl, RxSQSConfiguration config) {
        Observable<Message> messageObservable = Observable.create(new MessageOnSubscribe(queueUrl, config));

        if (config.isDeleteOnReceive()) {
            messageObservable = messageObservable.doOnNext(
                    message -> config.getAmazonSQS().deleteMessage(queueUrl, message.getReceiptHandle()));
        }

        if (!config.getDlqUrl().isEmpty()) {
            messageObservable = messageObservable.doOnError(
                    error -> config.getAmazonSQS().sendMessage(config.getDlqUrl(), error.getMessage()));
        }

        return messageObservable;
    }

}
