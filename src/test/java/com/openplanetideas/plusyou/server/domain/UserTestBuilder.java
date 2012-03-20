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

package com.openplanetideas.plusyou.server.domain;

import com.openplanetideas.plusyou.server.exception.PlusYouServerException;
import org.apache.commons.lang3.builder.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserTestBuilder implements Builder<User> {

    private static final Logger LOG = LoggerFactory.getLogger(UserTestBuilder.class);

    private String facebookId = "-123456L";
    private String passphrase = "abcdef";
    private List<Award> awards = new ArrayList<Award>();
    private List<UserAward> userAwards = new ArrayList<UserAward>();
    private List<UserAwardTestBuilder> userAwardTestBuilders = new ArrayList<UserAwardTestBuilder>();
    private Date registrationDate = createRegistrationDate();

    private UserTestBuilder() {
    }

    public static UserTestBuilder aUser() {
        return new UserTestBuilder();
    }

    @Override
    public User build() {
        User user = null;
        try {
            Long id = -1L;
            user = new User(facebookId, passphrase);
            ReflectionTestUtils.setField(user, "id", id);
            ReflectionTestUtils.setField(user, "registrationDate", registrationDate);

            for (Award award : awards) {
                UserAward userAward = new UserAward(user, award);
                userAwards.add(userAward);
            }

            for (UserAwardTestBuilder userAwardTestBuilder : userAwardTestBuilders) {
                userAwardTestBuilder.withUser(user);
                userAwards.add(userAwardTestBuilder.build());
            }

            ReflectionTestUtils.setField(user, "userAwards", userAwards);
        } catch (PlusYouServerException e) {
            LOG.error("TestUser could not be created", e);
        }

        return user;
    }

    public UserTestBuilder withAward(Award award) {
        awards.add(award);
        return this;
    }

    public UserTestBuilder withFacebookId(String facebookId) {
        this.facebookId = facebookId;
        return this;
    }

    public UserTestBuilder withUserAward(UserAwardTestBuilder userAwardTestBuilder) {
        userAwardTestBuilders.add(userAwardTestBuilder);
        return this;
    }

    public UserTestBuilder withPassphrase(String accessToken) {
        this.passphrase = accessToken;
        return this;
    }

    private Date createRegistrationDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 12, 1);
        return cal.getTime();
    }
}