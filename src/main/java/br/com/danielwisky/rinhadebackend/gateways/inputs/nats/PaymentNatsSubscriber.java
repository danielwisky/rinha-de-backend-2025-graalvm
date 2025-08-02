package br.com.danielwisky.rinhadebackend.gateways.inputs.nats;

import static java.nio.charset.StandardCharsets.UTF_8;

import br.com.danielwisky.rinhadebackend.gateways.inputs.nats.resources.ProcessPaymentInputResource;
import br.com.danielwisky.rinhadebackend.usecases.ProcessPayment;
import br.com.danielwisky.rinhadebackend.utils.JsonUtils;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentNatsSubscriber {

  private final Connection natsConnection;
  private final JsonUtils jsonUtils;
  private final ProcessPayment processPayment;

  @Value("${nats.subject}")
  private String paymentSubject;

  @Value("${nats.consumer.concurrency:10}")
  private int concurrency;

  @Value("${nats.consumer.queue-group:payment-processors}")
  private String queueGroup;

  private final List<Dispatcher> dispatchers = new ArrayList<>();
  private ExecutorService executorService;

  @PostConstruct
  public void subscribe() {
    log.debug("Initializing NATS consumer with {} concurrent dispatchers", concurrency);
    executorService = Executors.newFixedThreadPool(concurrency);
    for (int i = 0; i < concurrency; i++) {
      final var dispatcher = natsConnection.createDispatcher(this::handlePaymentMessageAsync);
      dispatcher.subscribe(paymentSubject, queueGroup);
      dispatchers.add(dispatcher);
      log.debug("Created dispatcher {} for subject {} with queue group {}", i + 1, paymentSubject, queueGroup);
    }

    log.debug("NATS consumer initialized successfully with {} dispatchers", dispatchers.size());
  }

  @PreDestroy
  public void shutdown() {
    log.debug("Shutting down NATS consumer...");
    dispatchers.forEach(dispatcher -> {
      try {
        dispatcher.unsubscribe(paymentSubject);
      } catch (Exception e) {
        log.warn("Error unsubscribing dispatcher: {}", e.getMessage());
      }
    });

    if (executorService != null && !executorService.isShutdown()) {
      executorService.shutdown();
      log.debug("Executor service shutdown completed");
    }
  }

  private void handlePaymentMessageAsync(final Message message) {
    executorService.submit(() -> handlePaymentMessage(message));
  }

  private void handlePaymentMessage(final Message message) {
    final String messageData = new String(message.getData(), UTF_8);
    try {
      log.debug("Processing payment message: {}", messageData);
      final var paymentResource = jsonUtils.toObject(messageData, ProcessPaymentInputResource.class);
      processPayment.execute(paymentResource.toDomain());
      log.debug("Payment message processed successfully");
    } catch (Exception e) {
      log.debug("Error processing payment message: {} - Error: {}", messageData, e.getMessage(), e);
      try {
        natsConnection.publish(paymentSubject, message.getData());
        log.debug("Message republished for retry");
      } catch (Exception republishError) {
        log.error("Failed to republish message: {}", republishError.getMessage());
      }
    }
  }
}