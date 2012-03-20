/*
 * Copyright (c) 2012, Sony Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the Sony Corporation.
 */

package com.openplanetideas.plusyou.server.repository;

import com.google.common.collect.Lists;
import com.openplanetideas.plusyou.server.AbstractTest;
import com.openplanetideas.plusyou.server.domain.FacebookStream;
import com.openplanetideas.plusyou.server.domain.User;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FacebookPostRepositoryTest extends AbstractTest {
    
    @Test
    public void delete() {
        FacebookStream facebookStream = facebookPostRepository.findByStreamId("123456789_8501");
        facebookPostRepository.delete(facebookStream);
        assertEquals(8, facebookPostRepository.count());
        assertNull(facebookPostRepository.findByStreamId("123456789_8501"));
        assertNotNull(userRepository.findByFacebookId(facebookStream.getUser().getFacebookId()));
    }

    @Test
    public void findByEntityId() {
        FacebookStream facebookStream = facebookPostRepository.findByStreamId("123456789_8501");
        assertNotNull(facebookStream);
    }

    @Test
    public void findByUserIn() {
        List<User> users = Lists.newArrayList(userRepository.findByFacebookId("123456789"), userRepository.findByFacebookId("987654321"));
        List<FacebookStream> facebookStreams = facebookPostRepository.findByUserIn(users, new PageRequest(0, 100));
        assertEquals(6, facebookStreams.size());
    }
}