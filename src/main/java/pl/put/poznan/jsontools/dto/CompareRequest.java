package pl.put.poznan.jsontools.dto;

/**
 * Request body for compare: { "left": "...", "right": "..." }
 */
public class CompareRequest {

    private String left;
    private String right;

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }
}
