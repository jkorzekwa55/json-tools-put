package pl.put.poznan.jsontools.dto;

import java.util.List;

public class CompareResponse {

    private List<DiffLine> differences;

    public List<DiffLine> getDifferences() {
        return differences;
    }

    public void setDifferences(List<DiffLine> differences) {
        this.differences = differences;
    }
}
