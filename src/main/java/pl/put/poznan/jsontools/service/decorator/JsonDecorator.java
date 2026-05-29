package pl.put.poznan.jsontools.service.decorator;

import com.fasterxml.jackson.databind.JsonNode;
import pl.put.poznan.jsontools.service.JsonService;

/**
 * Abstract decorator: delegates {@link #process} to the wrapped {@link JsonService}
 * before subclasses apply their own transformation.
 */
public abstract class JsonDecorator implements JsonService {
    protected final JsonService wrappedService;

    public JsonDecorator(JsonService wrappedService) {
        this.wrappedService = wrappedService;
    }

    @Override
    public JsonNode process(JsonNode input) {
        // Delegates to the next service in the chain
        return wrappedService.process(input);
    }
}
