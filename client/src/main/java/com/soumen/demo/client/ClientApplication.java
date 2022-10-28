package com.soumen.demo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }


    @Bean
    GreetingsClient greetingsClient(HttpServiceProxyFactory proxyFactory) {
        return proxyFactory.createClient(GreetingsClient.class);
    }

    @Bean
    HttpServiceProxyFactory httpServiceProxyFactory(WebClient.Builder builder) {
        var wc = builder.baseUrl("http://localhost:8080");
        return WebClientAdapter.createHttpServiceProxyFactory(wc);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> ready(GreetingsClient gc) {
        return event -> {
            var response = gc.greet("Soumen");
            System.out.println("Response : " + response);
        };

    }

}


interface GreetingsClient {
    @GetExchange("/greetings/{name}")
    Greeting greet(String name);
}

record Greeting(String message) {
}


