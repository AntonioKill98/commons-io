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

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

import org.apache.commons.io.build.AbstractStreamBuilder;

/**
 * This class is an example for using an {@link ObservableInputStream}. It creates its own {@link org.apache.commons.io.input.ObservableInputStream.Observer},
 * which calculates a checksum using a MessageDigest, for example an MD5 sum.
 * <p>
 * See the MessageDigest section in the <a href= "https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest"> Java
 * Cryptography Architecture Standard Algorithm Name Documentation</a> for information about standard algorithm names.
 * </p>
 * <p>
 * <em>Note</em>: Neither {@link ObservableInputStream}, nor {@link MessageDigest}, are thread safe. So is {@link MessageDigestCalculatingInputStream}.
 * </p>
 * <p>
 * TODO Rename to MessageDigestInputStream in 3.0.
 * </p>
 */
public class MessageDigestCalculatingInputStream extends ObservableInputStream {

    /**
     * Builds a new {@link ReaderInputStream} instance.
     * <p>
     * For example:
     * </p>
     * <pre>{@code
     * MessageDigestCalculatingInputStream s = MessageDigestCalculatingInputStream.builder()
     *   .setPath(path)
     *   .setMessageDigest("SHA-512")
     *   .get()}
     * </pre>
     * <p>
     * @since 2.12.02
     */
    public static class Builder extends AbstractStreamBuilder<MessageDigestCalculatingInputStream, Builder> {

        private MessageDigest messageDigest;

        public Builder() {
            try {
                this.messageDigest = getDefaultMessageDigest();
            } catch (final NoSuchAlgorithmException e) {
                // Should not happen.
                throw new IllegalStateException(e);
            }
        }

        @SuppressWarnings("resource")
        @Override
        public MessageDigestCalculatingInputStream get() throws IOException {
            return new MessageDigestCalculatingInputStream(getOrigin().getInputStream(), messageDigest);
        }

        /**
         * Sets the message digest.
         *
         * @param messageDigest the message digest.
         */
        public void setMessageDigest(final MessageDigest messageDigest) {
            this.messageDigest = messageDigest;
        }

        /**
         * Sets the name of the name of the message digest algorithm.
         *
         * @param algorithm the name of the algorithm. See the MessageDigest section in the
         *                  <a href= "https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest"> Java Cryptography
         *                  Architecture Standard Algorithm Name Documentation</a> for information about standard algorithm names.
         * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation for the specified algorithm.
         */
        public void setMessageDigest(final String algorithm) throws NoSuchAlgorithmException {
            this.messageDigest = MessageDigest.getInstance(algorithm);
        }

    }

    /**
     * Constructs a new {@link Builder}.
     *
     * @return a new {@link Builder}.
     * @since 2.12.0
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Maintains the message digest.
     */
    public static class MessageDigestMaintainingObserver extends Observer {
        private final MessageDigest messageDigest;

        /**
         * Creates an MessageDigestMaintainingObserver for the given MessageDigest.
         *
         * @param messageDigest the message digest to use
         */
        public MessageDigestMaintainingObserver(final MessageDigest messageDigest) {
            this.messageDigest = messageDigest;
        }

        @Override
        public void data(final byte[] input, final int offset, final int length) throws IOException {
            messageDigest.update(input, offset, length);
        }

        @Override
        public void data(final int input) throws IOException {
            messageDigest.update((byte) input);
        }
    }

    /**
     * The default message digest algorithm.
     * <p>
     * The MD5 cryptographic algorithm is weak and should not be used.
     * </p>
     */
    /**
     * VIENE MODIFICATO IN QUESTO PUNTO IL DEFAULT_ALGORITHM, IN ORIGINE MD5, in SHA-512
     * QUESTO PER CORREGGERE IL SECURITY HOTSPOT RILEVATO DA SonarCube.
     *
     * private static final String DEFAULT_ALGORITHM = "MD5"; -->
     * private static final String DEFAULT_ALGORITHM = "SHA-512";
     */
    private static final String DEFAULT_ALGORITHM = "SHA-512";

    /**
     * Gets a MessageDigest object that implements the default digest algorithm.
     *
     * @return a Message Digest object that implements the default algorithm.
     * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation.
     * @see Provider
     */
    static MessageDigest getDefaultMessageDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(DEFAULT_ALGORITHM);
    }

    private final MessageDigest messageDigest;

    /**
     * Creates a new instance, which calculates a signature on the given stream, using a {@link MessageDigest} with the "MD5" algorithm.
     * <p>
     * The MD5 algorithm is weak and should not be used.
     * </p>
     *
     * @param inputStream the stream to calculate the message digest for
     * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation for the specified algorithm.
     * @deprecated Use {@link #builder()}.
     */
    @Deprecated
    public MessageDigestCalculatingInputStream(final InputStream inputStream) throws NoSuchAlgorithmException {
        this(inputStream, getDefaultMessageDigest());
    }

    /**
     * Creates a new instance, which calculates a signature on the given stream, using the given {@link MessageDigest}.
     *
     * @param inputStream   the stream to calculate the message digest for
     * @param messageDigest the message digest to use
     * @deprecated Use {@link #builder()}.
     */
    @Deprecated
    public MessageDigestCalculatingInputStream(final InputStream inputStream, final MessageDigest messageDigest) {
        super(inputStream, new MessageDigestMaintainingObserver(messageDigest));
        this.messageDigest = messageDigest;
    }

    /**
     * Creates a new instance, which calculates a signature on the given stream, using a {@link MessageDigest} with the given algorithm.
     *
     * @param inputStream the stream to calculate the message digest for
     * @param algorithm   the name of the algorithm requested. See the MessageDigest section in the
     *                    <a href= "https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest"> Java Cryptography
     *                    Architecture Standard Algorithm Name Documentation</a> for information about standard algorithm names.
     * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation for the specified algorithm.
     * @deprecated Use {@link #builder()}.
     */
    @Deprecated
    public MessageDigestCalculatingInputStream(final InputStream inputStream, final String algorithm) throws NoSuchAlgorithmException {
        this(inputStream, MessageDigest.getInstance(algorithm));
    }

    /**
     * Gets the {@link MessageDigest}, which is being used for generating the checksum.
     * <p>
     * <em>Note</em>: The checksum will only reflect the data, which has been read so far. This is probably not, what you expect. Make sure, that the complete
     * data has been read, if that is what you want. The easiest way to do so is by invoking {@link #consume()}.
     * </p>
     *
     * @return the message digest used
     */
    public MessageDigest getMessageDigest() {
        return messageDigest;
    }
}
