package br.com.danielwisky.rinhadebackend.templates.resources;

import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.response.PaymentResponse;

public class PaymentResponseTemplate {

  public static PaymentResponse valid() {
    final var response = new PaymentResponse();
    response.setMessage("payment processed successfully");
    return response;
  }
}