package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.FoodOrder;
import com.example.demo.publisher.FoodOrderSource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "food-order", description = "Food Order API")
@RestController
public class FoodOrderController {

	@Autowired
	FoodOrderSource foodOrderSource;

	@Operation(summary = "Save FoodOrder", description = "Save FoodOrder", tags = { "food-order" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = FoodOrder.class)))) })	
	@PostMapping(value = "/order")
	@ResponseBody
	public String orderFood(@RequestBody FoodOrder foodOrder) {
		foodOrderSource.foodOrders().send(MessageBuilder.withPayload(foodOrder).build());
		System.out.println(foodOrder.toString());
		return "food ordered!";
	}
}