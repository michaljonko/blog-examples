/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Michał Jonko
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

package pl.coffeepower.blog.messagebus.util;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.lmax.disruptor.EventFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

public final class BytesEventFactory implements EventFactory<BytesEventFactory.BytesEvent> {

  public static final int BUFFER_SIZE = 1024;

  @Override
  public BytesEvent newInstance() {
    return new BytesEvent(BUFFER_SIZE);
  }

  @Getter
  @EqualsAndHashCode
  public static final class BytesEvent {

    private final byte[] buffer;
    private int currentLength;

    BytesEvent(int capacity) {
      Preconditions.checkArgument(capacity > 0, "Capacity has to be greater than 0");
      buffer = new byte[capacity];
    }

    public void copyToBuffer(@NonNull byte[] data) {
      copyToBuffer(data, data.length);
    }

    public void copyToBuffer(@NonNull byte[] data, int length) {
      Preconditions.checkArgument(length <= buffer.length, "Data to copy has length greater than buffer");
      Preconditions.checkArgument(length >= 0, "Length argument is less then zero");
      System.arraycopy(data, 0, buffer, 0, currentLength = length);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("bufferSize", buffer.length)
          .add("currentLength", currentLength)
          .toString();
    }
  }
}
