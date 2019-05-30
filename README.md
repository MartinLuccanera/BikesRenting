# BikeRental

JSON structure for regular rentals
```json
{
    "topic": "rentalType",
    "units": 0
}
```
The list of rental types is: ["hour","day","week","family"].

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
Example curl to hit endpoints

```
curl -X GET 'localhost:8080/bike/rental?rentalType=family&subRental=day&subRental=week&quantity=2&quantity=1'
```

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