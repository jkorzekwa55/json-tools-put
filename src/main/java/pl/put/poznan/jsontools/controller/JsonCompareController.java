package pl.put.poznan.jsontools.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.jsontools.dto.CompareRequest;
import pl.put.poznan.jsontools.dto.CompareResponse;
import pl.put.poznan.jsontools.dto.DiffLine;
import pl.put.poznan.jsontools.service.CompareService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/json", produces = MediaType.APPLICATION_JSON_VALUE)
public class JsonCompareController {

    private final CompareService compareService;

    public JsonCompareController(CompareService compareService) {
        this.compareService = compareService;
    }

    @PostMapping(path = "/compare")
    public CompareResponse compare(@RequestBody CompareRequest request) {
        String left = request != null ? request.getLeft() : null;
        String right = request != null ? request.getRight() : null;
        List<DiffLine> diffs = compareService.compareLines(left, right);
        CompareResponse response = new CompareResponse();
        response.setDifferences(diffs);
        return response;
    }
}
