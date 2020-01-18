package com.example.demo.publisher;

import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(FoodOrderSource.class)
public class FoodOrderPublisher {
}