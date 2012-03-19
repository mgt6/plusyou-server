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

package com.openplanetideas.plusyou.server.domain.xsd;

import com.openplanetideas.plusyou.provider.Interest;
import com.openplanetideas.plusyou.provider.Opportunity;
import org.apache.commons.lang3.builder.Builder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

public class OpportunityTestBuilder implements Builder<Opportunity> {

    private Opportunity.Interests opportunityInterests = new Opportunity.Interests();
    private String title = "anOpportunity";
    private List<Interest> interests = new ArrayList<Interest>();

    private OpportunityTestBuilder() {
    }

    public static OpportunityTestBuilder anOpportunity() {
        return new OpportunityTestBuilder();
    }

    @Override
    public Opportunity build() {
        Opportunity opportunity = new Opportunity();
        opportunity.setTitle(title);
        opportunity.setInterests(opportunityInterests);
        return opportunity;
    }

    public OpportunityTestBuilder withAnInterest(Interest interest) {
        this.interests.add(interest);
        setInterests();
        return this;
    }

    public OpportunityTestBuilder withAnInterest(InterestBuilder interestBuilder) {
        this.interests.add(interestBuilder.build());
        setInterests();
        return this;
    }

    public OpportunityTestBuilder withInterests(List<Interest> interests) {
        ReflectionTestUtils.setField(opportunityInterests, "interest", interests);
        return this;
    }

    public OpportunityTestBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    private void setInterests() {
        ReflectionTestUtils.setField(opportunityInterests, "interest", interests);
    }
}