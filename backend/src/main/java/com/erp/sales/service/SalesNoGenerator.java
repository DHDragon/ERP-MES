package com.erp.sales.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class SalesNoGenerator {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public String next(String prefix) {
        int n = ThreadLocalRandom.current().nextInt(100, 999);
        return prefix + LocalDateTime.now().format(FMT) + n;
    }
}
