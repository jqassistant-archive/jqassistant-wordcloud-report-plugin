package org.jqassistant.contrib.plugin.wordcloud;

import com.buschmais.jqassistant.core.report.api.ReportContext;
import com.buschmais.jqassistant.core.report.api.ReportException;
import com.buschmais.jqassistant.core.report.api.ReportHelper;
import com.buschmais.jqassistant.core.report.api.ReportPlugin;
import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.rule.api.model.ExecutableRule;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class WordCloudReportPlugin implements ReportPlugin {

    private ReportContext reportContext;

    private File reportDirectory;

    private WordCloudRenderer wordCloudRenderer;

    /**
     * Constructor.
     */
    public WordCloudReportPlugin() {
        this.wordCloudRenderer = new WordCloudRenderer();
    }

    @Override
    public void configure(ReportContext reportContext, Map<String, Object> properties) {
        this.reportContext = reportContext;
        reportDirectory = reportContext.getReportDirectory("wordcloud");
        reportDirectory.mkdirs();
    }

    @Override
    public void setResult(Result<? extends ExecutableRule> result) throws ReportException {
        ExecutableRule<?> rule = result.getRule();
        String fileName = rule.getId().replaceAll("\\:", "_");
        List<String> words = result.getRows().stream().flatMap(row -> row.values().stream()).flatMap(value -> convert(value).stream()).collect(toList());
        File file = new File(reportDirectory, fileName + ".png");
        wordCloudRenderer.render(words, file);
        try {
            reportContext.addReport("wordcloud", rule, ReportContext.ReportType.IMAGE, file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new ReportException("Cannot convert file '" + file.getAbsolutePath() + "' to URL");
        }
    }

    private List<String> convert(Object value) {
        if (value instanceof Iterable<?>) {
            List<String> values = new ArrayList<>();
            for (Object element : (Iterable<?>) value) {
                values.addAll(convert(element));
            }
            return values;
        }
        return singletonList(ReportHelper.getLabel(value));
    }
}
