package pl.put.poznan.jsontools.service;

import org.springframework.stereotype.Service;
import pl.put.poznan.jsontools.dto.DiffLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CompareService {
    private static final Logger logger = LoggerFactory.getLogger(CompareService.class);
    /**
     * Compares two texts line-by-line and returns a list of differing lines.
     * Lines are 1-based.
     */
    public List<DiffLine> compareLines(String left, String right) {
        logger.info("Starting JSON comparison");
        logger.debug("Comparison input lengths - left={}, right={}", lengthOf(left), lengthOf(right));
        if (left == null) left = "";
        if (right == null) right = "";

        if (left.isEmpty() && right.isEmpty()) {
            logger.info("Finished line-by-line comparison with 0 differences");
            return Collections.emptyList();
        }

        String[] leftLines = left.split("\\r?\\n", -1);
        String[] rightLines = right.split("\\r?\\n", -1);

        int max = Math.max(leftLines.length, rightLines.length);
        List<DiffLine> diffs = new ArrayList<>();

        for (int i = 0; i < max; i++) {
            String l = i < leftLines.length ? leftLines[i] : "";
            String r = i < rightLines.length ? rightLines[i] : "";
            if (!Objects.equals(l, r)) {
                diffs.add(new DiffLine(i + 1, l, r));
            }
        }
        logger.info("Finished comparison with {} differences", diffs.size());
        return diffs;
    }
    private static int lengthOf(String text) {
        return text != null ? text.length() : 0;
    }
}
