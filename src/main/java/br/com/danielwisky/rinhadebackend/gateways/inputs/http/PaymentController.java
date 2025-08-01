package br.com.danielwisky.rinhadebackend.gateways.inputs.http;

import static org.springframework.http.HttpStatus.ACCEPTED;

import br.com.danielwisky.rinhadebackend.gateways.inputs.http.resources.request.PaymentRequest;
import br.com.danielwisky.rinhadebackend.gateways.outputs.PaymentMessageGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

  private final PaymentMessageGateway paymentMessageGateway;

  @PostMapping
  @ResponseStatus(ACCEPTED)
  public ResponseEntity<Void> payment(@RequestBody final PaymentRequest paymentRequest) {
    paymentMessageGateway.sendPaymentMessage(paymentRequest.toDomain());
    return ResponseEntity.accepted().build();
  }
}