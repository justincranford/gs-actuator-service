package com.example.springboot;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({JmsIT.MyListener.class})
@DirtiesContext
@ActiveProfiles(profiles={"test"})
@Slf4j
@SuppressWarnings({ "nls", "static-method" })
public class JmsIT {
	@Autowired
	public JmsProducer jmsProducer;

    public static CountDownLatch LATCH;

    @BeforeEach
    public void beforeEach() {
    	LATCH = new CountDownLatch(1);
    }

    @Test
	public void testPublic() throws Exception {
    	final String message = "Hello World";
    	this.jmsProducer.sendMessage(Constants.QUEUE, message);
        final boolean success = LATCH.await(3, TimeUnit.SECONDS);
        Assertions.assertThat(success).isTrue();
	}

    @Component
    public static class MyListener {
        @JmsListener(destination = Constants.QUEUE)
        public void receiveMessage(String value) {
    		log.info("Received: {}", value);
    		LATCH.countDown();
        }
    }

	public static class Constants {
		private static final String QUEUE = "queue1";
	}
}
