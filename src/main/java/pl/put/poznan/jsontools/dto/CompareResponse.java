package pl.put.poznan.jsontools.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompareResponse {

    private List<DiffLine> differences;
}
