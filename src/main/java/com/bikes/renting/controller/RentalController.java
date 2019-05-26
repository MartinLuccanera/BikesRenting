package com.bikes.renting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bike")
public class RentalController {

    @RequestMapping(value = "/rental", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    void saveBikeRental(@RequestParam String username, String name, String email, String bio, String birthdate) {
    }

    //TODO: check this for required params     public Greeting sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
    @RequestMapping(value = "/rental", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    String getResponse() {
        return "Got to endpoint";
    }
}