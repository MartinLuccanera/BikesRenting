# BikeRental

###### Project to model bike rentals.

## Characteristics:

A company rents bikes under following options:
1. Rental by hour, charging $5 per hour
2. Rental by day, charging $20 a day
3. Rental by week, changing $60 a week
4. Family Rental, is a promotion that can include from 3 to 5 Rentals (of any type) with a discount
of 30% of the total price
5. The values could change in the future if the client decides at any time
6: Implement a persistence model for the rents information
7. Implement a kind of message queue model to send and receive the information of the rents that the user creates
8. At the moment of the project, no providers for data persistence nor message exchange have been defined

## Rental model 
Defined by a common interface "Rental" which defines a common method to calculate rental cost.
Each implementation overrides the calculation to suit its needs.

## Patterns

**Composite pattern** to define the required characteristic of having a rental-type which includes other rentals.
Family rental can contain any of the non-composed rentals [Hourly, Daily, Weekly]. The total sum for composed rental is
the sum of the children to which a discount is applied.

**Factory pattern** to instantiate each of the rental types.

**MVC** Even though this app does not have a view, it was separated in **Model** and **Controller** 

All configurations are stored in 

## Configuration classes:

**_RentalTypes_** -> Stores all types of rentals.

**_PricingConstants_** -> Price for each rental_type.

**_JsonKeyConstants_** -> Messages are sent as Json. Keys used are stored here.

**_ParametersConstants_** -> Parameters received via endpoints are stored here.

## App description:

Data (rental information) is received via REST API endpoint:

> bike/rental

Messages are sent in-app via a kafka message model.

## App flow

To start the app we can use an IDE and run **Application** or from console

```
mvn exec:java
```

When the app starts, it immediately creates an endpoint (RentalController.java) with POST enabled for messages to be submitted.
We can do this from console with

```
curl -X POST 'localhost:8080/bike/rental?rentalType=family&subRental=day&subRental=week&subRental=hour&quantity=2&quantity=1&quantity=3'
```
This will create a composed rental "family" comprised of 3 rentals ["day", week", "hour"] and 3 lengths of rental [2,1,3] respectively.
To create a single rental, the curl would be
```
curl -X POST 'localhost:8080/bike/rental?rentalType=day&quantity=2'
```   

At the same time, a Kafka consumer will be created and sent into its own thread to listen
for possible messages in a loop.

In-app the message received via API is converted to Json with the following layout:

## Json layout


JSON structure for regular rentals
```json
{
    "topic": "rentalType",
    "units": 0
}
```

For family (which is a composed rentalType)
```json
{
    "topic": "family",
    "rentals": [
        {
            "topic": "rentalType",
            "units": 1
        },
        {
            "topic": "rentalType",
            "units": 2
        },
        {
            "topic": "rentalType",
            "units": 3
        },
        {
            "topic": "rentalType",
            "units": 4
        },
        {
            "topic": "rentalType",
            "units": 5
        }
    ]
}
```

After validating the fields/parameters a Kafka producer is created which will send the 
message to kafka for queueing and delivery [**__NOTE:__** I did my best, but the connection between producer/consumer is **NOT** working :( ].

At this point, we are ready to instantiate the rentalTypes and calculate the cost of rental and be persisted :D.

**__Disclaimer__**: As the process cannot be completely seen just by running the app, I created a bunch of tests so that 
the behaviour can be put to the test (also many utilities like JsonParsing, messages creation and validation, etc), hence:

## Tests

Tests can be run classically by 
```
mvn test
```
I have included 2 separate reports regarding tests

1- Surefire report
```
mvn surefire-report:report
```

Which can be located at 
> reports\surefire\site

2- Intelli-j code coverage report (based on JaCoCo)

Which can be located at 
> reports\intelli-j_code_coverage_report

**__DevNote__**: Is this an overkill? probably. But I had quite some fun doing it :D

Sources: 
* [Kafka using spring boot](https://medium.com/@contactsunny/simple-apache-kafka-producer-and-consumer-using-spring-boot-41be672f4e2b)
* [Json serializer deserializer](https://codenotfound.com/spring-kafka-json-serializer-deserializer-example.html)
* [Inject static variables](https://www.mkyong.com/spring/spring-inject-a-value-into-static-variables/)
* [Spring mvc requestParam binding](https://reversecoding.net/spring-mvc-requestparam-binding-request-parameters/)
* [Spring requestParam](https://www.baeldung.com/spring-request-param)
* [Kafka tutorial](http://cloudurable.com/blog/kafka-tutorial-kafka-producer/index.html)
* [Kafka singleton vs static](https://stackoverflow.com/questions/39459987/kafka-consumer-client-creation-singleton-instance-vs-static-method)
* [Spring.io](https://spring.io/guides/tutorials/rest/)
* [Overloaded method spring](https://stackoverflow.com/questions/30380498/overload-controller-method-in-java-spring)
* [Kafka definitive guide](https://www.oreilly.com/library/view/kafka-the-definitive/9781491936153/ch04.html)
* [Gson string-jsonObject](https://www.baeldung.com/gson-string-to-jsonobject)
* [AssertThat vs Assert*](https://objectpartners.com/2013/09/18/the-benefits-of-using-assertthat-over-other-assert-methods-in-unit-tests/)
* [Spring testing](https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#spring-testing-annotation-contextconfiguration)
* [Unit testing endpoints](https://www.freecodecamp.org/news/unit-testing-services-endpoints-and-repositories-in-spring-boot-4b7d9dc2b772/)
* [Advanced kafka examples](http://cloudurable.com/blog/kafka-tutorial-kafka-producer-advanced-java-examples/index.html)
* [JsonAssert](https://www.programcreek.com/java-api-examples/?class=net.javacrumbs.jsonunit.JsonAssert&method=assertJsonEquals)
* [JsonAssert (other)](https://www.baeldung.com/jsonassert)
* [Assert exceptions](https://stackoverflow.com/questions/40268446/junit-5-how-to-assert-an-exception-is-thrown)
* [Junit report tool](https://stackoverflow.com/questions/2846493/is-there-a-decent-html-junit-report-plugin-for-maven)
* [Surefire plugin](https://maven.apache.org/surefire/maven-surefire-report-plugin/usage.html)
* [Testing spring](https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html)
* [Testing spring (other)](https://www.baeldung.com/integration-testing-in-spring)
* [Restful Service](https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-configuration)
* [Spring boot](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-auto-configuration)
* [Spring test annotation](https://stackoverflow.com/questions/39417530/what-is-the-proper-annotation-since-springapplicationconfiguration-webintegra/39417657)
* [Get running port on spring test with RANDOM_PORT](https://stackoverflow.com/questions/30312058/spring-boot-how-to-get-the-running-port)
* [Spring Response](https://www.baeldung.com/spring-response-entity)
