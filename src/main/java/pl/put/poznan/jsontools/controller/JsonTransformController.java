package pl.put.poznan.jsontools.controller;

import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.jsontools.service.JsonService;

@RestController
public class JsonTransformController {

    private final JsonService jsonService;

    public JsonTransformController(JsonService jsonService) {
        this.jsonService = jsonService;
    }
}
