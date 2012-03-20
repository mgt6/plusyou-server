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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;

public class AwardTestBuilder implements Builder<Award> {

    private static final Logger LOG = LoggerFactory.getLogger(AwardTestBuilder.class);

    private Long awardId = -123L;
    private String awardName = "default";
    private Award.AwardLevel awardLevel = Award.AwardLevel.NONE;
    private String imageResource = "image_res";
    private Award.AwardGroup awardGroup = Award.AwardGroup.NONE;
    private Award.AwardCategory awardCategory = Award.AwardCategory.EXPLORE;
    private String descriptionResourceId = "default_res";

    private AwardTestBuilder() {}

    public static AwardTestBuilder anAward() {
        return new AwardTestBuilder();
    }

    @Override
    public Award build() {
        try {
            Award award = getAwardInstance();
            ReflectionTestUtils.setField(award, "id", awardId);
            ReflectionTestUtils.setField(award, "name", awardName);
            ReflectionTestUtils.setField(award, "level", awardLevel);
            ReflectionTestUtils.setField(award, "imageResourceId", imageResource);
            ReflectionTestUtils.setField(award, "group", awardGroup);
            ReflectionTestUtils.setField(award, "category", awardCategory);
            ReflectionTestUtils.setField(award, "descriptionResourceId", descriptionResourceId);
            return award;
        } catch (Exception e) {
            LOG.error("Could not instantiate a new Award: ", e);
            return null;
        }
    }

    public AwardTestBuilder withCategory(Award.AwardCategory category) {
        this.awardCategory = category;
        return this;
    }

    public AwardTestBuilder withDescriptionResourceId(String descriptionResourceId) {
        this.descriptionResourceId = descriptionResourceId;
        return this;
    }

    public AwardTestBuilder withGroup(Award.AwardGroup awardGroup) {
        this.awardGroup = awardGroup;
        return this;
    }

    public AwardTestBuilder withId(Long id) {
        this.awardId = id;
        return this;
    }

    public AwardTestBuilder withImageResource(String imageResource) {
        this.imageResource = imageResource;
        return this;
    }

    public AwardTestBuilder withLevel(Award.AwardLevel awardLevel) {
        this.awardLevel = awardLevel;
        return this;
    }

    public AwardTestBuilder withName(String name) {
        this.awardName = name;
        return this;
    }

    private Award getAwardInstance() throws Exception {
        Constructor<Award> awardConstructor = Award.class.getDeclaredConstructor();
        awardConstructor.setAccessible(true);
        return awardConstructor.newInstance();
    }
}