package pl.put.poznan.jsontools.logic;

/**
 * Example class name aligned with JsonTools project.
 */
public class JsonToolsTransformer {

    private final String[] transforms;

    public JsonToolsTransformer(String[] transforms) {
        this.transforms = transforms;
    }

    public String transformJson(String text) {
        // This remains a simple example implementation for now.
        return text.toUpperCase();
    }
}
