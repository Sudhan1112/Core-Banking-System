package com.cbs.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TransactionIdGenerator {
    
    private final AtomicLong sequence = new AtomicLong(0);
    private final Random random = new Random();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    public String generateTransactionId() {
        String timestamp = LocalDateTime.now().format(formatter);
        String sequenceNumber = String.format("%04d", sequence.getAndIncrement() % 10000);
        String randomDigits = String.format("%04d", random.nextInt(10000));
        
        return "TXN" + timestamp + sequenceNumber + randomDigits;
    }
}