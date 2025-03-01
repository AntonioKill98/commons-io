/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io.input;

import static org.apache.commons.io.IOUtils.EOF;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.output.QueueOutputStream;

/**
 * Simple alternative to JDK {@link java.io.PipedInputStream}; queue input stream provides what's written in queue
 * output stream.
 *
 * <p>
 * Example usage:
 * </p>
 * <pre>
 * QueueInputStream inputStream = new QueueInputStream();
 * QueueOutputStream outputStream = inputStream.newQueueOutputStream();
 *
 * outputStream.write("hello world".getBytes(UTF_8));
 * inputStream.read();
 * </pre>
 * <p>
 * Unlike JDK {@link PipedInputStream} and {@link PipedOutputStream}, queue input/output streams may be used safely in a
 * single thread or multiple threads. Also, unlike JDK classes, no special meaning is attached to initial or current
 * thread. Instances can be used longer after initial threads exited.
 * </p>
 * <p>
 * Closing a {@link QueueInputStream} has no effect. The methods in this class can be called after the stream has been
 * closed without generating an {@link IOException}.
 * </p>
 *
 * @see QueueOutputStream
 * @since 2.9.0
 * @deprecated Use {@link AbstractBlockingQueueInputStream.PollingQueueInputStream}
 */
@Deprecated
public class QueueInputStream extends AbstractBlockingQueueInputStream {

    /**
     * Constructs a new instance with no limit to its internal buffer size.
     */
    public QueueInputStream() {
        this(new LinkedBlockingQueue<>());
    }

    /**
     * Constructs a new instance with given queue.
     *
     * @param blockingQueue backing queue for the stream.
     */
    public QueueInputStream(final BlockingQueue<Integer> blockingQueue) {
        super(blockingQueue);
    }

    /**
     * Reads and returns a single byte.
     *
     * @return either the byte read or {@code -1} if the end of the stream has been reached
     */
    @Override
    public int read() {
        final Integer value = getBlockingQueue().poll();
        return value == null ? EOF : 0xFF & value;
    }

}
