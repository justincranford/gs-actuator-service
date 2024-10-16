package com.example.springboot;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({KafkaIT.MyListener.class})
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = { KafkaIT.Constants.TOPIC })
@Slf4j
@SuppressWarnings({ "nls", "static-method" })
public class KafkaIT {
	@Autowired
	public KafkaTemplate<String, String> kafkaTemplate;

    public static CountDownLatch LATCH;

    @BeforeEach
    public void beforeEach() {
    	LATCH = new CountDownLatch(1);
    }

    @Test
	public void testPublic() throws Exception {
    	final String message = "Hello World";
		CompletableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(Constants.TOPIC, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
            }
        });
        final boolean success = LATCH.await(3, TimeUnit.SECONDS);
        Assertions.assertThat(success).isTrue();
	}

    @Component
    public static class MyListener {
    	@KafkaListener(topics={Constants.TOPIC}, groupId=Constants.GROUP)
    	public void listener(String value) {
    		log.info("Received: {}", value);
    		LATCH.countDown();
    	}
    }

	public static class Constants {
		private static final String GROUP = "group1";
		private static final String TOPIC = "topic1";
	}
}
