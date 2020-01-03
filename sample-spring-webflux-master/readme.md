## Using Reactive WebClient with Spring WebFlux  [![Twitter](https://img.shields.io/twitter/follow/piotr_minkowski.svg?style=social&logo=twitter&label=Follow%20Me)](https://twitter.com/piotr_minkowski)

Detailed description can be found here: [Using Reactive WebClient with Spring WebFlux](https://piotrminkowski.wordpress.com/2019/11/04/using-reactive-webclient-with-spring-webflux/)

# USING REACTIVE WEBCLIENT WITH SPRING WEBFLUX

Reactive APIs and generally reactive programming become increasingly popular lately. You have a change to observe more and more new frameworks and toolkits supporting reactive programming, or just dedicated for this. Today, in the era of microservices architecture, where the network communication through APIs becomes critical for applications, reactive APIs seems to be an attractive alternative to a traditional, synchronous approach. It should be definitely considered as a primary approach if you are working with large streams of data exchanged via network communication.
Spring supports reactive programming and reactive APIs too. You could have a change to read about it in some of my previous articles where I focused on introducing that support. In the article Reactive Microservices with Spring WebFlux and Spring Cloud you can read more about building microservices architecture using Spring WebFlux together with Spring Cloud projects. In turn, in the articles Introduction to Reactive APIs with Postgres, R2DBC, Spring Data JDBC and Spring WebFlux and Reactive Elasticsearch with Spring Boot I have introduced reactive Spring Data repositories on an example of PostgreSQL and Elasticsearch. Those articles should be treated as an introduction to reactive programming with Spring. Today, I would like to go deeply into that topic and discuss some aspects related to the network communication between service that exposes reactive stream via API and service that consumes this API using Spring WebClient.


#1. ACCESS REACTIVE STREAM USING WEBCLIENT

First, let’s consider the typical scenario of reading reactive API on the consumer side. e have the following implementation of reactive stream containing Person objects on the producer side:

```
@RestController
@RequestMapping("/persons")
public class PersonController {
 
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);
 
    @GetMapping("/json")
    public Flux<Person> findPersonsJson() {
        return Flux.fromStream(this::prepareStream)
                .doOnNext(person -> LOGGER.info("Server produces: {}", person));
    }
}
```

We can easily access it with non-blocking WebClient. The following test starts sample Spring WebFlux application, defines WebClient instance and subscribes to the response stream.

```
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SampleSpringWebFluxTest {
 
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleSpringWebFluxTest.class);
    final WebClient client = WebClient.builder().baseUrl("http://localhost:8080").build();
 
    @Test
    public void testFindPersonsJson() throws TimeoutException, InterruptedException {
        final Waiter waiter = new Waiter();
        Flux<Person> persons = client.get().uri("/persons/json").retrieve().bodyToFlux(Person.class);
        persons.subscribe(person -> {
            waiter.assertNotNull(person);
            LOGGER.info("Client subscribes: {}", person);
            waiter.resume();
        });
        waiter.await(3000, 9);
    }
}
```

In reactive programming with Reactor and Spring WebFlux you first need to subscribe to the stream in order to be able to access emitted objects. Assuming that our test stream has 9 Person elements you will receive the following log output:

Run the test cases

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.2.RELEASE)

16:45:54.833 --- [           main] : Starting SampleSpringWebFluxTest on 841DTN2 with PID 5164 (started by e081155 in C:\Learnings\spring-boot-microservices\sample-spring-webflux-master)
16:45:54.835 --- [           main] : No active profile set, falling back to default profiles: default
16:45:56.799 --- [           main] : Netty started on port(s): 8080
16:45:56.802 --- [           main] : Started SampleSpringWebFluxTest in 2.462 seconds (JVM running for 4.407)
16:45:57.387 --- [     parallel-1] : Server produces: Person(id=1, firstName=Name01, lastName=Surname01, age=11)
16:45:57.469 --- [ctor-http-nio-2] : Client subscribes: Person(id=1, firstName=Name01, lastName=Surname01, age=11)
16:45:57.516 --- [     parallel-1] : Server produces: Person(id=2, firstName=Name02, lastName=Surname02, age=22)
16:45:57.516 --- [     parallel-1] : Server produces: Person(id=3, firstName=Name03, lastName=Surname03, age=33)
16:45:57.517 --- [     parallel-1] : Server produces: Person(id=4, firstName=Name04, lastName=Surname04, age=44)
16:45:57.517 --- [ctor-http-nio-2] : Client subscribes: Person(id=2, firstName=Name02, lastName=Surname02, age=22)
16:45:57.517 --- [     parallel-1] : Server produces: Person(id=5, firstName=Name05, lastName=Surname05, age=55)
16:45:57.517 --- [ctor-http-nio-2] : Client subscribes: Person(id=3, firstName=Name03, lastName=Surname03, age=33)
16:45:57.517 --- [     parallel-1] : Server produces: Person(id=6, firstName=Name06, lastName=Surname06, age=66)
16:45:57.518 --- [ctor-http-nio-2] : Client subscribes: Person(id=4, firstName=Name04, lastName=Surname04, age=44)
16:45:57.518 --- [     parallel-1] : Server produces: Person(id=7, firstName=Name07, lastName=Surname07, age=77)
16:45:57.518 --- [     parallel-1] : Server produces: Person(id=8, firstName=Name08, lastName=Surname08, age=88)
16:45:57.518 --- [ctor-http-nio-2] : Client subscribes: Person(id=5, firstName=Name05, lastName=Surname05, age=55)
16:45:57.518 --- [     parallel-1] : Server produces: Person(id=9, firstName=Name09, lastName=Surname09, age=99)
16:45:57.518 --- [ctor-http-nio-2] : Client subscribes: Person(id=6, firstName=Name06, lastName=Surname06, age=66)
16:45:57.519 --- [ctor-http-nio-2] : Client subscribes: Person(id=7, firstName=Name07, lastName=Surname07, age=77)
16:45:57.519 --- [ctor-http-nio-2] : Client subscribes: Person(id=8, firstName=Name08, lastName=Surname08, age=88)
16:45:57.519 --- [ctor-http-nio-2] : Client subscribes: Person(id=9, firstName=Name09, lastName=Surname09, age=99)
16:45:57.535 --- [ctor-http-nio-3] : Server produces: Person(id=1, firstName=Name01, lastName=Surname01, age=11)
16:45:57.535 --- [ctor-http-nio-3] : Server produces: Person(id=2, firstName=Name02, lastName=Surname02, age=22)
16:45:57.535 --- [ctor-http-nio-3] : Server produces: Person(id=3, firstName=Name03, lastName=Surname03, age=33)
16:45:57.535 --- [ctor-http-nio-3] : Server produces: Person(id=4, firstName=Name04, lastName=Surname04, age=44)
16:45:57.535 --- [ctor-http-nio-3] : Server produces: Person(id=5, firstName=Name05, lastName=Surname05, age=55)
16:45:57.535 --- [ctor-http-nio-3] : Server produces: Person(id=6, firstName=Name06, lastName=Surname06, age=66)
16:45:57.535 --- [ctor-http-nio-3] : Server produces: Person(id=7, firstName=Name07, lastName=Surname07, age=77)
16:45:57.535 --- [ctor-http-nio-3] : Server produces: Person(id=8, firstName=Name08, lastName=Surname08, age=88)
16:45:57.535 --- [ctor-http-nio-3] : Server produces: Person(id=9, firstName=Name09, lastName=Surname09, age=99)
16:45:57.541 --- [ctor-http-nio-2] : Client subscribes: Person(id=1, firstName=Name01, lastName=Surname01, age=11)
16:45:57.542 --- [ctor-http-nio-2] : Client subscribes: Person(id=2, firstName=Name02, lastName=Surname02, age=22)
16:45:57.542 --- [ctor-http-nio-2] : Client subscribes: Person(id=3, firstName=Name03, lastName=Surname03, age=33)
16:45:57.542 --- [ctor-http-nio-2] : Client subscribes: Person(id=4, firstName=Name04, lastName=Surname04, age=44)
16:45:57.542 --- [ctor-http-nio-2] : Client subscribes: Person(id=5, firstName=Name05, lastName=Surname05, age=55)
16:45:57.542 --- [ctor-http-nio-2] : Client subscribes: Person(id=6, firstName=Name06, lastName=Surname06, age=66)
16:45:57.543 --- [ctor-http-nio-2] : Client subscribes: Person(id=7, firstName=Name07, lastName=Surname07, age=77)
16:45:57.543 --- [ctor-http-nio-2] : Client subscribes: Person(id=8, firstName=Name08, lastName=Surname08, age=88)
16:45:57.543 --- [ctor-http-nio-2] : Client subscribes: Person(id=9, firstName=Name09, lastName=Surname09, age=99)
16:45:57.655 --- [     parallel-2] : Server produces: Person(id=1, firstName=Name01, lastName=Surname01, age=11)
16:45:57.749 --- [ctor-http-nio-2] : Client subscribes: Person(id=1, firstName=Name01, lastName=Surname01, age=11)
16:45:57.758 --- [     parallel-3] : Server produces: Person(id=2, firstName=Name02, lastName=Surname02, age=22)
16:45:57.850 --- [ctor-http-nio-2] : Client subscribes: Person(id=2, firstName=Name02, lastName=Surname02, age=22)
16:45:57.859 --- [     parallel-4] : Server produces: Person(id=3, firstName=Name03, lastName=Surname03, age=33)
16:45:57.951 --- [ctor-http-nio-2] : Client subscribes: Person(id=3, firstName=Name03, lastName=Surname03, age=33)
16:45:57.960 --- [     parallel-5] : Server produces: Person(id=4, firstName=Name04, lastName=Surname04, age=44)
16:45:58.051 --- [ctor-http-nio-2] : Client subscribes: Person(id=4, firstName=Name04, lastName=Surname04, age=44)
16:45:58.061 --- [     parallel-6] : Server produces: Person(id=5, firstName=Name05, lastName=Surname05, age=55)
16:45:58.152 --- [ctor-http-nio-2] : Client subscribes: Person(id=5, firstName=Name05, lastName=Surname05, age=55)
16:45:58.162 --- [     parallel-7] : Server produces: Person(id=6, firstName=Name06, lastName=Surname06, age=66)
16:45:58.254 --- [ctor-http-nio-2] : Client subscribes: Person(id=6, firstName=Name06, lastName=Surname06, age=66)
16:45:58.263 --- [     parallel-8] : Server produces: Person(id=7, firstName=Name07, lastName=Surname07, age=77)
16:45:58.355 --- [ctor-http-nio-2] : Client subscribes: Person(id=7, firstName=Name07, lastName=Surname07, age=77)
16:45:58.365 --- [     parallel-1] : Server produces: Person(id=8, firstName=Name08, lastName=Surname08, age=88)
16:45:58.456 --- [ctor-http-nio-2] : Client subscribes: Person(id=8, firstName=Name08, lastName=Surname08, age=88)
16:45:58.466 --- [     parallel-2] : Server produces: Person(id=9, firstName=Name09, lastName=Surname09, age=99)
16:45:58.557 --- [ctor-http-nio-2] : Client subscribes: Person(id=9, firstName=Name09, lastName=Surname09, age=99)
```

3. IMPLEMENTING BACKPRESSURE
Backpressure is one of the most important reason you would decide to use reactive programming. Following Spring WebFlux documentation it supports backpressure, since Project Reactor is a Reactive Streams library and, therefore, all of its operators support non-blocking back pressure. The whole sentence is of course conform to the truth, but only on the server-side. Maybe the next fragment of documentation shall shed some light on things: Reactor has a strong focus on server-side Java.. We should remember that Spring WebClient and WebFlux uses TCP transport for communication between a client and the server. And therefore, a client is not able to regulate the frequency of elements emission on the server side. I don’t want to go into the details right now, for the explanation of that situation you may refer to the following post https://stackoverflow.com/questions/52244808/backpressure-mechanism-in-spring-web-flux.
Ok, before proceeding let’s recap the definition of backpressure term. Backpressure (or back pressure) is a resistance or force opposing the desired flow of data through software. In simple words, if a producer send more events than a consumer is able to handle in the specific period of time, the consumer should be able to regulate the frequency of sending events on the producer side. Let’s consider the following test example.


```
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SampleSpringWebFluxTest {
 
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleSpringWebFluxTest.class);
    final WebClient client = WebClient.builder().baseUrl("http://localhost:8080").build();
 
    @Test
    public void testFindPersonsStreamBackPressure() throws TimeoutException, InterruptedException {
        final Waiter waiter = new Waiter();
        Flux<Person> persons = client.get().uri("/persons/stream/back-pressure").retrieve().bodyToFlux(Person.class);
        persons.map(this::doSomeSlowWork).subscribe(person -> {
            waiter.assertNotNull(person);
            LOGGER.info("Client subscribes: {}", person);
            waiter.resume();
        });
        waiter.await(3000, 9);
    }
 
    private Person doSomeSlowWork(Person person) {
        try {
            Thread.sleep(90);
        }
        catch (InterruptedException e) { }
        return person;
    }
}
```


After receiving a stream of elements our test calls a time-expensive mapping method on each element. So, it is not able to handle so many elements as has been sent by the producer. In this case the only way to somehow “regulate” backpressure is through delayElements method on the server side. I also tried to use limitRate method on the service side and implement my own custom Subscriber on the client side, but I wasn’t successful. Here’s the current implementation of our API method for returning stream of Person objects.

```
@RestController
@RequestMapping("/persons")
public class PersonController {
 
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);
 
    @GetMapping(value = "/stream/back-pressure", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Person> findPersonsStreamBackPressure() {
        return Flux.fromStream(this::prepareStream).delayElements(Duration.ofMillis(100))
                .doOnNext(person -> LOGGER.info("Server produces: {}", person));
    }
}
```



![Alt text](1.png?raw=true "Title")



