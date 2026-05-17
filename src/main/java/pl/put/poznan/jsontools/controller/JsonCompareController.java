package pl.put.poznan.jsontools.controller;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.jsontools.dto.CompareRequest;
import pl.put.poznan.jsontools.dto.CompareResponse;
import pl.put.poznan.jsontools.dto.DiffLine;
import pl.put.poznan.jsontools.service.CompareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping(path = "/api/json", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class JsonCompareController {

    private static final Logger logger = LoggerFactory.getLogger(JsonCompareController.class);

    private final CompareService compareService;

    @PostMapping(path = "/compare")
    public CompareResponse compare(@Valid @RequestBody CompareRequest request) {
        logger.info("Received compare request");
        String left = request.getLeft();
        String right = request.getRight();
        logger.debug("Compare request left length - {}, right length - {}", left.length(), right.length());
        List<DiffLine> diffs = compareService.compareLines(left, right);
        CompareResponse response = new CompareResponse();
        response.setDifferences(diffs);
        logger.info("Returning compare response");
        return response;
    }
}
