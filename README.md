# RxSQS
An RxJava based library for consuming messages from Amazon's Simple Queue Service. Use can be as simple as this
trivial one-liner:
```java
Observable<Message> messages = RxSQS.getMessageObservable($QUEUE_URL);
```

Which yields a backpressured Observable that supports take operations yielding Amazon's own Message model class. The library
will by default clean up messages from the queue as they are read and makes no use of a DLQ. For more specialised usage, 
the library is configurable via a fluent builder API with a DLQ to redrive errors to, your own implementation of an SQS client,
etc. For example, to supply a DLQ to use for errors and a specific SQS client:
```java
RxSQSConfiguration config = RxSQSConfigurationBuilder.standard()
  .withAmazonSQS($SQS_CLIENT)
  .withDlqUrl($DLQ_URL)
  .build();
  
Observable<Message> messages = RxSQS.getMessageObservable($QUEUE_URL, config);
```  
