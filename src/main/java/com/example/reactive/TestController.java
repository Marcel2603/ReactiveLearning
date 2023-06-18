package com.example.reactive;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final Scheduler piScheduler;
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping(path = "/crash")
    public ResponseEntity<Mono<Double>> crash(@RequestParam(name = "numbers") int numbers) {
        return ResponseEntity.ok(Mono.just(createLoad(numbers, false)));
    }

    @GetMapping(path = "/atLeastYouTried")
    public ResponseEntity<Mono<Double>> atLeastYouTried(@RequestParam(name = "numbers") int numbers) {
        return ResponseEntity.ok(Mono.just(createLoad(numbers, false)).publishOn(Schedulers.boundedElastic()));
    }

    @GetMapping(path = "/better")
    public ResponseEntity<Mono<Double>> better(@RequestParam(name = "numbers") int numbers) {
        return ResponseEntity.ok(Mono.just(numbers).publishOn(Schedulers.boundedElastic()).map(n -> createLoad(n, false)));
    }

    @GetMapping(path = "/safe")
    public ResponseEntity<Mono<Double>> safe(@RequestParam(name = "numbers") int numbers, @RequestParam(name = "log", required = false, defaultValue = "false") Boolean log) {
        return ResponseEntity.ok(Mono.just(numbers).publishOn(piScheduler).map(n -> createLoad(n, log)));
    }

    private static double createLoad(double n, boolean withLog) {
        double num = 0.0;
        logger.info("Start with number {}", n);
        for (int i = 1; i < n; i++) {
            if (withLog) logger.info("Calc number {}", num);
            num += Math.pow(-1, i + 1) / (2 * i - 1);
        }
        logger.info("Pi finished {}", num);
        return num * 4;
    }

}
