package com.samcgardner.rxsqs;

import com.amazonaws.services.sqs.model.Message;
import rx.Observer;
import rx.observables.SyncOnSubscribe;

public class MessageOnSubscribe extends SyncOnSubscribe<MessageService, Message> {

    private final RxSQSConfiguration config;
    private String queueUrl;

    MessageOnSubscribe(String queueUrl, RxSQSConfiguration config) {
        this.queueUrl = queueUrl;
        this.config = config;
    }

    @Override
    protected MessageService generateState() {
        return new MessageService(queueUrl, config);
    }

    @Override
    protected MessageService next(MessageService state, Observer<? super Message> observer) {
        try {
            observer.onNext(state.getNextMessage());
        } catch (InterruptedException e) {
            // We don't expect to be interrupted here as we aren't exposing the ability to interrupt our blocking wait,
            // so treat being interrupted as an error to be propagated
            observer.onError(e);
        }
        return state;
    }

    @Override
    protected void onUnsubscribe(MessageService state) {
        state.terminateThreads();
    }
}
