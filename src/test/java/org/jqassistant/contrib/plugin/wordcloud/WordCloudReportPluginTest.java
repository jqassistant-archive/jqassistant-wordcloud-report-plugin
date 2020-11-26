package org.jqassistant.contrib.plugin.wordcloud;


import com.buschmais.jqassistant.core.report.api.ReportContext;
import com.buschmais.jqassistant.core.report.api.ReportException;
import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.rule.api.model.Concept;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WordCloudReportPluginTest {

    private static final File REPORT_DIRECTORY = new File("target/test/report");
    private static final Concept CONCEPT = Concept.builder().id("test").build();

    @Mock
    private WordCloudRenderer wordCloudRenderer;

    @Mock
    private ReportContext reportContext;

    @Captor
    private ArgumentCaptor<List<String>> wordCaptor;

    @InjectMocks
    private WordCloudReportPlugin wordCloudReportPlugin = new WordCloudReportPlugin();

    @BeforeEach
    public void setUp() throws IOException {
        FileUtils.deleteDirectory(REPORT_DIRECTORY);
        doReturn(REPORT_DIRECTORY).when(reportContext).getReportDirectory("wordcloud");
        this.wordCloudReportPlugin.configure(reportContext, emptyMap());
    }

    @Test
    public void scalarResult() throws ReportException {
        // given
        Map<String, Object> row1 = new HashMap<>();
        row1.put("value", "Value1");
        Map<String, Object> row2 = new HashMap<>();
        row2.put("value", "Value2");
        Map<String, Object> row3 = new HashMap<>();
        row3.put("value", "Value3");
        Result<Concept> result = createResult(row1, row2, row3);

        // when
        wordCloudReportPlugin.setResult(result);

        //then
        verifyResult("Value1", "Value2", "Value3");
    }


    @Test
    public void arrayResult() throws ReportException {
        // given
        Map<String, Object> row1 = new HashMap<>();
        row1.put("value", asList("Value1", "Value2"));
        Map<String, Object> row2 = new HashMap<>();
        row2.put("value", asList("Value2", "Value3"));

        Result<Concept> result = createResult(row1, row2);

        // when
        wordCloudReportPlugin.setResult(result);

        //then
        verifyResult("Value1", "Value2", "Value2", "Value3");
    }

    private Result<Concept> createResult(Map<String, Object>... rows) {
        Concept concept = Concept.builder().id("test").build();
        return Result.<Concept>builder().rule(concept).rows(asList(rows)).columnNames(asList("value")).status(Result.Status.SUCCESS).build();
    }

    private void verifyResult(String... expectedValues) {
        assertThat(REPORT_DIRECTORY).exists();
        File expectedWordCloudFile = new File(REPORT_DIRECTORY, "test.png");
        verify(wordCloudRenderer).render(wordCaptor.capture(), eq(expectedWordCloudFile));
        assertThat(wordCaptor.getValue()).containsExactly(expectedValues);
    }
}