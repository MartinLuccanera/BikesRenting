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
* [kafka using spring boot](https://medium.com/@contactsunny/simple-apache-kafka-producer-and-consumer-using-spring-boot-41be672f4e2b)
* [json serializer deserializer](https://codenotfound.com/spring-kafka-json-serializer-deserializer-example.html)
* [inject static variables](https://www.mkyong.com/spring/spring-inject-a-value-into-static-variables/)
* [spring mvc requestParam binding](https://reversecoding.net/spring-mvc-requestparam-binding-request-parameters/)
* [spring requestParam](https://www.baeldung.com/spring-request-param)
* [kafka tutorial](http://cloudurable.com/blog/kafka-tutorial-kafka-producer/index.html)
* [kafka singleton vs static](https://stackoverflow.com/questions/39459987/kafka-consumer-client-creation-singleton-instance-vs-static-method)
* [spring.io](https://spring.io/guides/tutorials/rest/)
* [overloaded method spring](https://stackoverflow.com/questions/30380498/overload-controller-method-in-java-spring)
* [kafka definitive guide](https://www.oreilly.com/library/view/kafka-the-definitive/9781491936153/ch04.html)
* [Gson string-jsonObject](https://www.baeldung.com/gson-string-to-jsonobject)