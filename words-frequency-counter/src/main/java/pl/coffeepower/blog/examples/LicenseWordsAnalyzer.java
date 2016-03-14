/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Michał Jonko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pl.coffeepower.blog.examples;

import com.google.common.io.Resources;

import lombok.extern.log4j.Log4j2;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import pl.coffeepower.blog.examples.counters.SimpleWordsFrequencyCounter;

import java.io.IOException;
import java.util.Map;

@Log4j2
public final class LicenseWordsAnalyzer {

    public static final String TOKEN_MODEL = "en-token.bin";
    public static final int TOP_WORDS_LIMIT = 3;
    public static final String LICENSE = "The MIT License (MIT)\n" +
            "Copyright (c) 2015 Michał Jonko\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
            "of this software and associated documentation files (the \"Software\"), to deal\n" +
            "in the Software without restriction, including without limitation the rights\n" +
            "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
            "copies of the Software, and to permit persons to whom the Software is\n" +
            "furnished to do so, subject to the following conditions:\n" +
            "The above copyright notice and this permission notice shall be included in all\n" +
            "copies or substantial portions of the Software.\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
            "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
            "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
            "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
            "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
            "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
            "SOFTWARE.";
    private final Tokenizer tokenizer;

    public LicenseWordsAnalyzer() throws IOException {
        this.tokenizer = new TokenizerME(new TokenizerModel(Resources.getResource(TOKEN_MODEL)));
    }

    public static void main(String[] args) throws IOException {
        new LicenseWordsAnalyzer().calculateWordsFrequency()
                .entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue() - entry1.getValue())
                .limit(TOP_WORDS_LIMIT)
                .forEach(entry -> log.info("{} : {}", entry.getKey(), entry.getValue()));
    }

    public final Map<String, Integer> calculateWordsFrequency() {
        WordsFrequencyCounter wordsFrequencyCounter = new SimpleWordsFrequencyCounter();
        for (String word : tokenizer.tokenize(LICENSE)) {
            wordsFrequencyCounter.increase(word);
        }
        return wordsFrequencyCounter.wordsFrequency();
    }
}
