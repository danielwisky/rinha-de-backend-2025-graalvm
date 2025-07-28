package br.com.danielwisky.rinhadebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class RinhaDeBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(RinhaDeBackendApplication.class, args);
  }
}