package pl.put.poznan.jsontools.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.put.poznan.jsontools.dto.DiffLine;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompareServiceTest {

    private CompareService compareService;

    @BeforeEach
    void setUp() {
        compareService = new CompareService();
    }

    @Test
    void shouldReturnNoDifferencesForIdenticalTexts() {
        List<DiffLine> diffs = compareService.compareLines("line 1\nline 2\nline 3", "line 1\nline 2\nline 3");

        assertTrue(diffs.isEmpty(), "Identical texts should produce no differences");
    }

    @Test
    void shouldReturnSingleDifferenceForPartialMismatch() {
        List<DiffLine> diffs = compareService.compareLines("line 1\nline 2\nline 3", "line 1\nLINE 2\nline 3");

        assertEquals(1, diffs.size());
        assertEquals(2, diffs.get(0).getLine());
        assertEquals("line 2", diffs.get(0).getLeft());
        assertEquals("LINE 2", diffs.get(0).getRight());
    }

    @Test
    void shouldMarkMissingLinesWhenTextsHaveDifferentLengths() {
        List<DiffLine> diffs = compareService.compareLines("line 1\nline 2\nline 3", "line 1\nline 2");

        assertEquals(1, diffs.size());
        assertEquals(3, diffs.get(0).getLine());
        assertEquals("line 3", diffs.get(0).getLeft());
        assertEquals("", diffs.get(0).getRight());
    }

    @Test
    void shouldReturnNoDifferencesForBlankTexts() {
        List<DiffLine> diffs = compareService.compareLines("", "");

        assertTrue(diffs.isEmpty(), "Blank texts should produce no differences");
    }

    @Test
    void shouldTreatOneBlankTextAsDifferenceForAllLinesOfTheOtherText() {
        List<DiffLine> diffs = compareService.compareLines("line 1\nline 2", "");

        assertEquals(2, diffs.size());
        assertEquals(1, diffs.get(0).getLine());
        assertEquals("line 1", diffs.get(0).getLeft());
        assertEquals("", diffs.get(0).getRight());
        assertEquals(2, diffs.get(1).getLine());
        assertEquals("line 2", diffs.get(1).getLeft());
        assertEquals("", diffs.get(1).getRight());
    }

    @Test
    void shouldTreatNullInputsAsBlankTexts() {
        List<DiffLine> diffs = compareService.compareLines(null, null);

        assertTrue(diffs.isEmpty(), "Null inputs should behave like blank texts");
    }
}
