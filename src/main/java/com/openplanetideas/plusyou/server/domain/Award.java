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

import com.openplanetideas.plusyou.server.domain.common.AbstractEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType
@Entity(name = "awards")
public class Award extends AbstractEntity {

    public static final String DEFAULT_ICON = "award_ic_default";

    private static final long serialVersionUID = -6821190206074070455L;

    @XmlElement(required = true)
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AwardCategory category;

    @XmlElement(required = true, name = "awardGroup")
    @Column(nullable = false, name = "awardGroup")
    @Enumerated(value = EnumType.STRING)
    private AwardGroup group = AwardGroup.NONE;

    //TODO: make enum?
    @XmlElement(required = true)
    @Column(nullable = false, unique = true)
    private String name;

    @XmlElement(required = true)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AwardLevel level = AwardLevel.NONE;

    @XmlElement(required = true)
    @Column(nullable = false, unique = true)
    private String imageResourceId;

    @XmlElement(required = true)
    @Column(nullable = false, unique = true)
    private String descriptionResourceId;

    @XmlElement(required = true)
    @Column(nullable = false, unique = true)
    private String messageResourceId;

    @XmlElement(required = true)
    @Column(nullable = false, unique = true)
    private String facebookMessageResourceId;

    @XmlType
    public enum AwardCategory {

        EXPLORE, TRY, SHARE, BUILD, LOVE
    }

    @XmlType
    public enum AwardGroup {

        SPORT, ART, POLITICS, DISABILITY, ELDERLY, MUSIC, EDUCATION, INVITE, BRING, ACTIVE, BUDDY, INFORMER, MEMBER, COMMUNITY, LOCAL, COMMUNITY_SERVICES, ENVIRONMENT, NONE
    }

    @XmlType
    public enum AwardLevel {

        NONE,
        LOW,
        HIGH
    }

    protected Award() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        Award award = (Award) obj;
        return new EqualsBuilder()
                .append(category, award.getCategory())
                .append(group, award.getGroup())
                .append(name, award.getName())
                .append(level, award.getLevel())
                .append(messageResourceId, award.getMessageResourceId())
                .append(descriptionResourceId, award.getDescriptionResourceId())
                .append(facebookMessageResourceId, award.getFacebookMessageResourceId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(category)
                .append(group)
                .append(name)
                .append(level)
                .append(messageResourceId)
                .append(descriptionResourceId)
                .append(facebookMessageResourceId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(category)
                .append(group)
                .append(name)
                .append(level)
                .append(imageResourceId)
                .append(messageResourceId)
                .append(descriptionResourceId)
                .toString();
    }

    public AwardCategory getCategory() {
        return category;
    }

    public String getDescriptionResourceId() {
        return descriptionResourceId;
    }

    public String getFacebookMessageResourceId() {
        return facebookMessageResourceId;
    }

    public AwardGroup getGroup() {
        return group;
    }

    public String getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(String imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public AwardLevel getLevel() {
        return level;
    }

    public String getMessageResourceId() {
        return messageResourceId;
    }

    public String getName() {
        return name;
    }
}