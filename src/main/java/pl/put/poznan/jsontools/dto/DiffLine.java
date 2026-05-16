package pl.put.poznan.jsontools.dto;

/** Represents a single line difference between two texts. */
public class DiffLine {

    private int line;
    private String left;
    private String right;

    public DiffLine() {
    }

    public DiffLine(int line, String left, String right) {
        this.line = line;
        this.left = left;
        this.right = right;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

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
