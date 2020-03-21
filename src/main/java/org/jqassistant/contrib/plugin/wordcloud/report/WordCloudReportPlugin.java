package org.jqassistant.contrib.plugin.wordcloud.report;

import com.buschmais.jqassistant.core.report.api.ReportContext;
import com.buschmais.jqassistant.core.report.api.ReportException;
import com.buschmais.jqassistant.core.report.api.ReportHelper;
import com.buschmais.jqassistant.core.report.api.ReportPlugin;
import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.rule.api.model.ExecutableRule;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.normalize.CharacterStrippingNormalizer;
import com.kennycason.kumo.nlp.normalize.TrimToEmptyNormalizer;
import com.kennycason.kumo.palette.ColorPalette;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class WordCloudReportPlugin implements ReportPlugin {

    private ReportContext reportContext;

    private File reportDirectory;

    @Override
    public void configure(ReportContext reportContext, Map<String, Object> properties) {
        this.reportContext = reportContext;
        reportDirectory = reportContext.getReportDirectory("wordcloud");
        reportDirectory.mkdirs();
    }

    @Override
    public void setResult(Result<? extends ExecutableRule> result) throws ReportException {
        List<String> words = result.getRows().stream().flatMap(row -> row.values().stream()).map(value -> ReportHelper.getLabel(value)).collect(toList());
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.clearNormalizers();
        frequencyAnalyzer.setNormalizer(new TrimToEmptyNormalizer());
        frequencyAnalyzer.addNormalizer(new CharacterStrippingNormalizer());
        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(words);
        final Dimension dimension = new Dimension(600, 600);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
        wordCloud.setPadding(0);
        wordCloud.setBackground(new RectangleBackground(dimension));
        wordCloud.setBackgroundColor(Color.WHITE);
        wordCloud.setColorPalette(new ColorPalette(Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        ExecutableRule<?> rule = result.getRule();
        String fileName = rule.getId().replaceAll("\\:", "_");
        File file = new File(reportDirectory, fileName + ".png");
        wordCloud.writeToFile(file.getAbsolutePath());
        try {
            reportContext.addReport("wordcloud", rule, ReportContext.ReportType.IMAGE, file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new ReportException("Cannot convert file '" + file.getAbsolutePath() + "' to URL");
        }
    }
}
