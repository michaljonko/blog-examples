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

import com.google.common.primitives.Longs;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.ExcerptTailer;
import net.openhft.chronicle.tools.ChronicleTools;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Log
public class IndexedChronicleTest {

    private Chronicle chronicle;

    @Before
    public void setUp() throws Exception {
        File chronicleFilePath = createChronicleFilePath();
        log.info("chronicleFilePath=" + chronicleFilePath.getAbsolutePath());
        chronicle = ChronicleQueueBuilder
                .indexed(chronicleFilePath)
                .dataBlockSize(2 * 1_024 * 1_024)
                .build();
        ChronicleTools.deleteOnExit(chronicleFilePath.getAbsolutePath());
    }

    @After
    public void tearDown() throws Exception {
        if (chronicle != null) {
            chronicle.close();
        }
    }

    @Test
    public void notWorkingTest() throws Exception {
        createQueue();

        byte[] buffer = new byte[Longs.BYTES];
        ExcerptTailer tailer = chronicle.createTailer().toStart();

        readQueue(tailer, value -> {
            tailer.index(value - 1);
            assertTrue("Cannot move index to position " + value, tailer.nextIndex() || tailer.nextIndex());
            assertEquals("Different length. Expected " + Longs.BYTES, tailer.read(buffer), Longs.BYTES);
            assertEquals("Different data. Expected " + value, Longs.fromByteArray(buffer), value);
        });
    }

    @Test
    public void workingTest() throws Exception {
        createQueue();

        byte[] buffer = new byte[Longs.BYTES];
        ExcerptTailer tailer = chronicle.createTailer().toStart();

        readQueue(tailer, value -> {
            assertTrue("Cannot move index to position " + value, tailer.nextIndex() || tailer.nextIndex());
            assertEquals("Different length. Expected " + Longs.BYTES, tailer.read(buffer), Longs.BYTES);
            assertEquals("Different data. Expected " + value, Longs.fromByteArray(buffer), value);
        });
    }

    private void readQueue(ExcerptTailer tailer, LongConsumer consumer) throws IOException {
        LongStream
                .rangeClosed(Fixtures.START_SEQUENCE_VALUE, Fixtures.END_SEQUENCE_VALUE)
                .onClose(tailer::close)
                .sorted()
                .forEach(consumer);
    }

    private void createQueue() throws IOException {
        ExcerptAppender appender = chronicle.createAppender();
        LongStream
                .rangeClosed(Fixtures.START_SEQUENCE_VALUE, Fixtures.END_SEQUENCE_VALUE)
                .onClose(appender::close)
                .sorted()
                .forEach(value -> {
                    appender.startExcerpt(Longs.BYTES);
                    appender.write(Longs.toByteArray(value));
                    appender.finish();
                });
    }

    private File createChronicleFilePath() {
        return new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Fixtures {
        public static final long START_SEQUENCE_VALUE = 0L;
        public static final long END_SEQUENCE_VALUE = 300_000L;
    }
}