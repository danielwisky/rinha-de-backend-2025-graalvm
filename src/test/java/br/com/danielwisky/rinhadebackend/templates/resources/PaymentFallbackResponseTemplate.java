package br.com.danielwisky.rinhadebackend.templates.resources;

import br.com.danielwisky.rinhadebackend.gateways.outputs.http.client.resources.response.PaymentFallbackResponse;

public class PaymentFallbackResponseTemplate {

  public static PaymentFallbackResponse valid() {
    final var response = new PaymentFallbackResponse();
    response.setMessage("payment processed successfully");
    return response;
  }
}