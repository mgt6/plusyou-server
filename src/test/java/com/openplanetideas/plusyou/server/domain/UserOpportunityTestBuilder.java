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

import java.util.Calendar;
import java.util.Date;

public class UserOpportunityTestBuilder implements Builder {

    private User user;
    private Long opportunityId = -1L;
    private Date registrationDate = aDate();
    private Date checkInDate;

    private UserOpportunityTestBuilder() {
    }

    public static Date aDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2001, 12, 01);
        return cal.getTime();
    }

    public static UserOpportunityTestBuilder aUserOpportunity() {
        return new UserOpportunityTestBuilder();
    }

    @Override
    public UserOpportunity build() {
        if (user == null) {
            user = UserTestBuilder.aUser().build();
        }

        final UserOpportunity userOpportunity = new UserOpportunity(user, opportunityId);
        ReflectionTestUtils.setField(userOpportunity, "registrationDate", registrationDate);
        userOpportunity.checkIn(checkInDate);

        return userOpportunity;
    }

    public UserOpportunityTestBuilder withCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
        return this;
    }

    public UserOpportunityTestBuilder withOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
        return this;
    }

    public UserOpportunityTestBuilder withUser(User user) {
        this.user = user;
        return this;
    }
}