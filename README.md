# BikeRental
JSON structure for regular rentals
{
    "topic": "",
    "units": 
}
For family
{
    "topic": "family",
    "rentals": [
        {
            "topic": ""
            "units": 
        },
        {
            "topic": ""
            "units": 
        },
        {
            "topic": ""
            "units": 
        },
        {
            "topic": ""
            "units": 
        },
        {
            "topic": ""
            "units": 
        }
    ]
}

Example curl to hit endpoints

curl -X GET 'localhost:8080/bike/rental?rentalType=family&subRental=day&subRental=week&quantity=2&quantity=1'
curl -X GET 'localhost:8080/bike/rental?rentalType=family&topic=day,week&units=1,2'

Sources: 
https://medium.com/@contactsunny/simple-apache-kafka-producer-and-consumer-using-spring-boot-41be672f4e2b
https://codenotfound.com/spring-kafka-json-serializer-deserializer-example.html
https://www.mkyong.com/spring/spring-inject-a-value-into-static-variables/
https://reversecoding.net/spring-mvc-requestparam-binding-request-parameters/
https://www.baeldung.com/spring-request-param
http://cloudurable.com/blog/kafka-tutorial-kafka-producer/index.html
https://stackoverflow.com/questions/39459987/kafka-consumer-client-creation-singleton-instance-vs-static-method
https://spring.io/guides/tutorials/rest/
