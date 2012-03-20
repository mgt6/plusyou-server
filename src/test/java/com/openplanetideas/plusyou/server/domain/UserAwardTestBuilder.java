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

import org.apache.commons.lang3.builder.Builder;
import org.springframework.test.util.ReflectionTestUtils;

import static com.openplanetideas.plusyou.server.domain.AwardTestBuilder.anAward;
import static com.openplanetideas.plusyou.server.domain.UserTestBuilder.aUser;

public class UserAwardTestBuilder implements Builder<UserAward> {

    private User user = aUser().build();
    private Award award = anAward().build();
    private int timesWon = 1;

    private UserAwardTestBuilder() {
    }

    public static UserAwardTestBuilder aUserAward() {
        return new UserAwardTestBuilder();
    }

    @Override
    public UserAward build() {
        UserAward userAward = new UserAward(user, award);
        ReflectionTestUtils.setField(userAward, "timesWon", timesWon);
        return userAward;
    }

    public UserAwardTestBuilder withAward(Award award) {
        this.award = award;
        return this;
    }

    public UserAwardTestBuilder withTimesWon(int timesWon) {
        this.timesWon = timesWon;
        return this;
    }

    public UserAwardTestBuilder withUser(User user) {
        this.user = user;
        return this;
    }
}