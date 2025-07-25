package br.com.danielwisky.rinhadebackend.gateways.inputs.http;

import static org.springframework.http.HttpStatus.ACCEPTED;

import br.com.danielwisky.rinhadebackend.gateways.inputs.http.resources.request.PaymentRequest;
import br.com.danielwisky.rinhadebackend.usecases.ProcessPayment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

  private final ProcessPayment processPayment;

  @PostMapping
  @ResponseStatus(ACCEPTED)
  public ResponseEntity<Void> payment(@RequestBody @Valid final PaymentRequest paymentRequest) {
    log.debug("payment request: {}", paymentRequest);
    processPayment.execute(paymentRequest.toDomain());
    return ResponseEntity.accepted().build();
  }
}