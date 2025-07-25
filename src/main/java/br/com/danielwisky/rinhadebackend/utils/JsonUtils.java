package br.com.danielwisky.rinhadebackend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonUtils {

  private final ObjectMapper objectMapper;

  public String toJson(final Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }

  public <T> T toObject(final String content, final Class<T> clazz) {
    try {
      return objectMapper.readValue(content, clazz);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }
}