package pl.put.poznan.jsontools.dto;

import javax.validation.constraints.NotNull;

/**
 * Request body for compare: { "left": "...", "right": "..." }
 */
public class CompareRequest {

    @NotNull(message = "left is required")
    private String left;

    @NotNull(message = "right is required")
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
