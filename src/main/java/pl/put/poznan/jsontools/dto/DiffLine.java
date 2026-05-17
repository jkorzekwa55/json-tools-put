package pl.put.poznan.jsontools.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Represents a single line difference between two texts. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiffLine {

    private int line;
    private String left;
    private String right;
}
