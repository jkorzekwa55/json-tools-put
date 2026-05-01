package pl.put.poznan.jsontools.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pl.put.poznan.jsontools.logic.JsonToolsTransformer;

import java.util.Arrays;

@RestController
@RequestMapping("/{text}")
public class JsonToolsController {

    private static final Logger logger = LoggerFactory.getLogger(JsonToolsController.class);

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public String transformWithGet(
            @PathVariable String text,
            @RequestParam(value = "transforms", defaultValue = "upper,escape") String[] transforms) {

        logger.debug(text);
        logger.debug(Arrays.toString(transforms));

        JsonToolsTransformer transformer = new JsonToolsTransformer(transforms);
        return transformer.transformJson(text);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public String transformWithPost(
            @PathVariable String text,
            @RequestBody String[] transforms) {

        logger.debug(text);
        logger.debug(Arrays.toString(transforms));

        JsonToolsTransformer transformer = new JsonToolsTransformer(transforms);
        return transformer.transformJson(text);
    }
}
