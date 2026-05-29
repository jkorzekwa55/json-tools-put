package pl.put.poznan.jsontools.controller;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/** REST API for comparing two JSON (or plain text) documents line by line. */
@Slf4j
@RestController
@RequestMapping(path = "/api/json", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class JsonCompareController {
    private final CompareService compareService;

    @PostMapping(path = "/compare")
    public CompareResponse compare(@Valid @RequestBody CompareRequest request) {
        log.info("Received compare request");
        String left = request.getLeft();
        String right = request.getRight();
        log.debug("Compare request left length - {}, right length - {}", left.length(), right.length());
        List<DiffLine> diffs = compareService.compareLines(left, right);
        CompareResponse response = new CompareResponse();
        response.setDifferences(diffs);
        log.info("Returning compare response");
        return response;
    }
}
