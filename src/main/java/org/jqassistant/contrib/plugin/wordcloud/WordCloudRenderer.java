package org.jqassistant.contrib.plugin.wordcloud;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.normalize.TrimToEmptyNormalizer;
import com.kennycason.kumo.palette.ColorPalette;

import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Word cloud renderer.
 */
public class WordCloudRenderer {

    /**
     * Render a list of words to word cloud in a PNG file.
     *
     * @param words The words.
     * @param file  The PNG file to write to.
     */
    public void render(List<String> words, File file) {
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.clearNormalizers();
        frequencyAnalyzer.setNormalizer(new TrimToEmptyNormalizer());
        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(words);
        final Dimension dimension = new Dimension(600, 600);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
        wordCloud.setPadding(0);
        wordCloud.setBackground(new RectangleBackground(dimension));
        wordCloud.setBackgroundColor(Color.WHITE);
        wordCloud.setColorPalette(new ColorPalette(new Color(87, 141, 0)));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile(file.getAbsolutePath());
    }
}
