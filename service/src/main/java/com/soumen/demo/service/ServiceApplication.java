package com.soumen.demo.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}

@RestController
class GreetingHttpController {

    private final ObservationRegistry registry;

    GreetingHttpController(ObservationRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/greetings/{name}")
    public Greeting greet(@PathVariable String name) {
        if (!(StringUtils.hasText(name) && Character.isUpperCase(name.charAt(0)))) {
            throw new IllegalArgumentException("The name must start with a capital letter");
        }
        return Observation
                .createNotStarted("greetings.name", this.registry)
                .observe(() -> new Greeting("Hello " + name + "!"));
    }
}

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onException(HttpServletRequest request) {
        request
                .getAttributeNames()
                .asIterator()
                .forEachRemaining(attribute ->
                        System.out.println("Attribute name : " + attribute));
        return ProblemDetail
                .forStatusAndDetail(HttpStatusCode.valueOf(404), "the name is invalid");
    }
}


record Greeting(String message) {
}
