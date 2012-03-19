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

package com.openplanetideas.plusyou.server.service;

import com.google.common.collect.Lists;
import com.openplanetideas.plusyou.server.AbstractTest;
import com.openplanetideas.plusyou.server.domain.User;
import com.openplanetideas.plusyou.server.domain.UserInvite;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UserServiceTest extends AbstractTest {

    private static final String FACEBOOK_ID = "123456789";

    @Test
    public void deleteAllUserData_dataDeleted() {
        User user = userRepository.findByFacebookId(FACEBOOK_ID);
        userService.deleteAllUserData(FACEBOOK_ID);
        assertEquals(2, userRepository.count());
        assertNull(userRepository.findByFacebookId(FACEBOOK_ID));
        assertEquals(0, facebookPostRepository.findByUserIn(Lists.newArrayList(user), null).size());
        assertEquals(0, userOpportunityRepository.findByUser(user).size());
        assertNotNull(awardRepository.findByName(user.getAwards().get(0).getName()));
    }

    @Test
    public void deleteAllUserData_userNotFound() {
        User user = userRepository.findByFacebookId("-1");
        userService.deleteAllUserData("-1");
        assertNull(user);
    }

    @Test
    public void saveUserInvites_OK() {
        String facebookId = "123456789";
        Long opportunityId = 8501L;
        List<String> friendIds = Lists.newArrayList("12L", "5");

        userService.saveUserInvites(facebookId, opportunityId, friendIds);

        User user = userRepository.findByFacebookId(facebookId);
        List<UserInvite> userInvites = userInviteRepository.findByUser(user);
        assertNotNull(userInvites);
        assertEquals(2, userInvites.size());
    }
}