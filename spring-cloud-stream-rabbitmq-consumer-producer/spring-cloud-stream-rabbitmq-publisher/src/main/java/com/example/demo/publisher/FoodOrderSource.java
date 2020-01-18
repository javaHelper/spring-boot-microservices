package com.example.demo.publisher;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface FoodOrderSource {

	@Output("foodOrdersChannel")
	MessageChannel foodOrders();

}