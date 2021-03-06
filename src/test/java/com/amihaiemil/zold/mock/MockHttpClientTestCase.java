/**
 * Copyright (c) 2019, Mihai Emil Andronache
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1)Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2)Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3)Neither the name of zold-java-client nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.amihaiemil.zold.mock;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.client5.http.classic.methods.HttpGet;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.URISyntaxException;

/**
 * Tests for {@link MockHttpClient}.
 *
 * @author George Aristy (george.aristy@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class MockHttpClientTestCase {
    /**
     * Should return the given response if the request meets the given
     * condition.
     */
    @Test
    public void returnResponseIfRequestMeetsCondition() {
        final ClassicHttpResponse response =
            new BasicClassicHttpResponse(HttpStatus.SC_OK);
        MatcherAssert.assertThat(
            new MockHttpClient(
                new AssertRequest(
                    response,
                    new Condition(
                        "",
                        // @checkstyle LineLength (1 line)
                        r -> {
                            try {
                                return "http://some.test.com/"
                                    .equals(r.getUri().toString());
                            } catch (final URISyntaxException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    )
                )
            ).execute(new HttpGet("http://some.test.com")),
            Matchers.is(response)
        );
    }

    /**
     * Should fail if the http request does not meet the given condition.
     */
    @Test(expected = AssertionError.class)
    public void failIfRequestDoesNotMeetCondition() {
        new MockHttpClient(
            new AssertRequest(
                null,
                new Condition(
                    "",
                    r -> {
                        try {
                            return "http://some.test.com/"
                                .equals(r.getUri().toString());
                        } catch (final URISyntaxException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                )
            )
        ).execute(new HttpGet("http://test.com"));
    }

    /**
     * The failure message should be equal to the one specified in the
     * condition.
     */
    @Test
    public void failureMsg() {
        final String msg = "Test message";
        try {
            new MockHttpClient(
                new AssertRequest(
                    null,
                    new Condition(
                        msg,
                        // @checkstyle LineLength (1 line)
                        r -> {
                            try {
                                return "http://some.test.com/"
                                    .equals(r.getUri().toString());
                            } catch (final URISyntaxException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    )
                )
            ).execute(new HttpGet("http://test.com"));
        } catch (final AssertionError error) {
            MatcherAssert.assertThat(
                "The failure message must be equal to the one given.",
                error.getMessage(),
                Matchers.is(msg)
            );
        }
    }
}
