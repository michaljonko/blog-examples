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

import com.google.common.base.Stopwatch;
import com.google.common.base.Verify;
import com.google.common.primitives.Longs;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.ExcerptTailer;
import net.openhft.chronicle.tools.ChronicleTools;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.stream.LongStream;

import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public final class ChronicleStorageExample implements Closeable {

    public static final String PATH = System.getProperty("java.io.tmpdir") + File.separator + "sequence";
    private final Chronicle chronicle;
    private final long startSequenceValue = 0L;
    private final long endSequenceValue = 1_000_000L;

    public ChronicleStorageExample(@NonNull String path) throws IOException {
        log.info("Chronicle path: " + path);
        this.chronicle = ChronicleQueueBuilder
                .indexed(path)
                .dataBlockSize(2 * 1024 * 1024)
                .build();
        ChronicleTools.deleteOnExit(path);
    }

    public void writeDataSet() throws IOException {
        try (ExcerptAppender appender = chronicle.createAppender()) {
            LongStream.range(startSequenceValue, endSequenceValue).forEach(value -> {
                appender.startExcerpt(Longs.BYTES);
                appender.write(Longs.toByteArray(value));
                appender.finish();
            });
        }
    }

    public void verifyDataSet() throws IOException {
        try (ExcerptTailer tailer = chronicle.createTailer()) {
            long expectedSequence = startSequenceValue;
            byte[] buffer = new byte[Longs.BYTES];
            while (expectedSequence <= endSequenceValue && tailer.nextIndex()) {
                tailer.read(buffer);
                tailer.finish();
                long value = Longs.fromByteArray(buffer);
                Verify.verify(value == expectedSequence++, "Long value (%s) does not equal to expected value (%s)", value, expectedSequence);
            }
            Verify.verify(expectedSequence == endSequenceValue, "Did not read all data (%s)", expectedSequence - 1);
        }
    }

    public void brokenVerifyDataSet() throws IOException {
        try (ExcerptTailer tailer = chronicle.createTailer()) {
            long expectedSequence = startSequenceValue + 2 * endSequenceValue / 3;
            tailer.index(expectedSequence);
            byte[] buffer = new byte[Longs.BYTES];
            while (expectedSequence <= endSequenceValue && tailer.nextIndex()) {
                tailer.read(buffer);
                tailer.finish();
                long value = Longs.fromByteArray(buffer);
                Verify.verify(value == expectedSequence++, "Long value (%s) does not equal to expected value (%s)", value, expectedSequence);
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (chronicle != null) {
            chronicle.close();
        }
    }

    public static void main(String[] args) throws IOException {
        log.info("WarmUp...");
        ChronicleTools.warmup();
        log.info("Starting...");
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (ChronicleStorageExample example = new ChronicleStorageExample(PATH)) {
            example.writeDataSet();
            example.verifyDataSet();
            example.brokenVerifyDataSet();
        }
        stopwatch.stop();
        log.info("Finished.");
        log.info("All operations took " + stopwatch);
    }
}
