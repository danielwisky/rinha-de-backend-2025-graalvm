package br.com.danielwisky.rinhadebackend.gateways.outputs;

import br.com.danielwisky.rinhadebackend.domains.Payment;

public interface ExternalPaymentGateway {

  Payment payment(Payment payment);
}
