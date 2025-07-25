package br.com.danielwisky.rinhadebackend.gateways.inputs.http;

import static org.springframework.http.HttpStatus.OK;

import br.com.danielwisky.rinhadebackend.gateways.inputs.http.resources.response.PaymentSummaryResponse;
import br.com.danielwisky.rinhadebackend.usecases.GetPaymentSummary;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments-summary")
public class PaymentSummaryController {

  private final GetPaymentSummary getPaymentSummary;

  @GetMapping
  @ResponseStatus(OK)
  public PaymentSummaryResponse summary(
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) final LocalDateTime from,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) final LocalDateTime to) {
    return new PaymentSummaryResponse(getPaymentSummary.execute(from, to));
  }
}