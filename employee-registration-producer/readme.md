# Spring Cloud Stream Tutorial - Publish Message to RabbitMQ Simple Example

In this tutorial, we understand what is Spring Cloud Stream and its various terms. We then implement a simple example to publish message to RabbitMQ messaging using Spring Cloud Stream.

# What is Spring Cloud Stream ? Need for it.

In previous examples we had implemented examples for integrating Spring Boot Applications with Messaging Systems like Apache Kafka (https://www.javainuse.com/spring/spring-boot-apache-kafka-hello-world) and RabbitMQ (https://www.javainuse.com/spring/spring-boot-rabbitmq-hello-world). If you look at these examples these required a lot of configuration code which was Broker specific. For example in case of RabbitMQ integration with Spring Boot (https://www.javainuse.com/spring/spring-boot-rabbitmq-hello-world) we had to write code to create AmqpTemplate Template and Bindings. So if tomorrow the Messaging System changes we will also need to make application code changes.
Spring Cloud helps solve this problem using Spring Cloud Stream. Using Spring Cloud Stream we can develop applications where we do not need to specify the implementation details of the messaging system we want to use. We just need to specify the required binding dependencies and Spring Cloud Stream will the integrate the messaging systems to Spring Boot Application.

![Alt text](images/d1.jpg?raw=true "Optional Title")

# Spring Cloud Concepts-

- Binder - Depending upon the messaging system we will have to specify a the messaging platform dependency, which in this case is RabbitMQ
<dependency> <groupId>org.springframework.cloud</groupId> <artifactId>spring-cloud-starter-stream-rabbit</artifactId> </dependency>

- Source - When a message is needed to be published it is done using Source. The Source is an interface having a method annotated with @Output. The @Output annotation is used to identify output channels. The Source takes a POJO object, serializes it and then publishes it to the output channel.

```
public interface EmployeeRegistrationSource {
    @Output("employeeRegistrationChannel")
    MessageChannel employeeRegistration();

}
```

- Channel - A channel represents an input and output pipe between the Spring Cloud Stream Application and the Middleware Platform. A channel abstracts the queue that will either publish or consume the message. A channel is always associated with a queue. With this approach, we do not need to use the queue name in the application code. So if tomorrow the queue needs to be changed, we dont need to change the application code.
For example in the EmployeeRegistrationSource we have specified the channel name as employeeRegistrationChannel. In application.properties we have associated this channel with a RabbitMQ Exchange.

Next define the Source class. This will simply be an interface that defines ways of obtaining the MessageChannel object needed to send the message. Here we define the output channel named as employeeRegistrationChannel.

```
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EmployeeRegistrationSource {
	@Output("employeeRegistrationChannel")
	MessageChannel employeeRegistration();
}
```

Next we define a simple controller that will make use of the above defined classes to publish the message upon receiving the employeeRegistration request. Here the @EnableBinding annotation tells Spring Cloud Stream that you want to bind the Controller to a message broker.

```
@RestController
@EnableBinding(EmployeeRegistrationSource.class)
public class EmployeeRegistrationController {

	@Autowired
	EmployeeRegistrationSource employeeRegistrationSource;

	@RequestMapping("/register")
	@ResponseBody
	public String orderFood(@RequestBody Employee employee) {
		employeeRegistrationSource.employeeRegistration().send(MessageBuilder.withPayload(employee).build());
		System.out.println(employee.toString());
		return "Employee Registered";
	}
}
```

Send the above request, it trigger the message to be sent to the RabbitMQ.
Next go to the RabbitMQ console ``http://localhost:15672/``. We can see in the Exchange section, an exchange named employeeRegistration gets created and it has one message.

Launch the Swagger UI:  `http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config`

You should be able to see the 'employeeRegistrations' in the exchange section, make sure you post the sample Employee details.

RabbitMQ login: `http://localhost:15672/` Username and password = guest
