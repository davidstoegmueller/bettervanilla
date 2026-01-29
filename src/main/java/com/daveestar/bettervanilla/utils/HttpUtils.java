package com.daveestar.bettervanilla.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class HttpUtils {
  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);
  private static final HttpClient CLIENT = HttpClient.newBuilder()
      .connectTimeout(DEFAULT_TIMEOUT)
      .followRedirects(HttpClient.Redirect.NORMAL)
      .build();

  public static JsonElement sendGETRequest(String url, Duration timeout) throws IOException, InterruptedException {
    return sendGETRequest(url, timeout, null);
  }

  public static JsonElement sendGETRequest(String url, Duration timeout, java.util.Map<String, String> headers)
      throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(timeout)
        .GET()
        .build();

    if (headers != null && !headers.isEmpty()) {
      HttpRequest.Builder builder = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .timeout(timeout)
          .GET();

      for (var entry : headers.entrySet()) {
        if (entry.getKey() != null && entry.getValue() != null) {
          builder.header(entry.getKey(), entry.getValue());
        }
      }

      request = builder.build();
    }

    HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    return handleJsonResponse(response, "GET", url);
  }

  public static JsonElement sendPOSTRequest(String url, JsonElement payload, Duration timeout)
      throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(timeout)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(payload == null ? "null" : payload.toString()))
        .build();

    HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    return handleJsonResponse(response, "POST", url);
  }

  private static JsonElement handleJsonResponse(HttpResponse<String> response, String method, String url)
      throws IOException {
    int statusCode = response.statusCode();
    if (statusCode < 200 || statusCode >= 300) {
      throw new IOException(method + " request failed with status " + statusCode + " for " + url);
    }

    return JsonParser.parseString(response.body());
  }
}
