package org.jqassistant.contrib.plugin.wordcloud;

import com.buschmais.jqassistant.core.analysis.api.AnalyzerConfiguration;
import com.buschmais.jqassistant.core.analysis.impl.AnalyzerImpl;
import com.buschmais.jqassistant.core.report.api.ReportPlugin;
import com.buschmais.jqassistant.core.report.impl.CompositeReportPlugin;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

public class WordCloudIT extends AbstractPluginIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordCloudIT.class);

    @BeforeEach
    public void setUp() {
        Map<String, ReportPlugin> reportPlugins = new HashMap<>(getReportPlugins(emptyMap()));
        CompositeReportPlugin compositeReportPlugin = new CompositeReportPlugin(reportPlugins);
        this.analyzer = new AnalyzerImpl(new AnalyzerConfiguration(), this.store, getRuleInterpreterPlugins(), compositeReportPlugin, LOGGER);
    }

    @Test
    public void wordCloudReportFile() throws RuleException {
        applyConcept("wordcloud-test:ScalarValues");
        File outputDirectory = reportContext.getOutputDirectory();
        File expectedReportDirectory = new File(outputDirectory, "report/wordcloud");
        assertThat(expectedReportDirectory).exists();
        File expectedWordCloud = new File(expectedReportDirectory, "wordcloud-test_ScalarValues.png");
        assertThat(expectedWordCloud).exists();
    }
}
