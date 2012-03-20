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
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType
@Entity(name = "facebook_streams")
public class FacebookStream extends AbstractEntity {

    private static final long serialVersionUID = 2103022841048700635L;

    @XmlElement(required = true)
    @ManyToOne(optional = false)
    private User user;

    @XmlElement(required = true)
    @Column(nullable = false)
    private Long entityId;

    @XmlElement(required = true)
    @Column(nullable = false, unique = true)
    private String streamId;

    @XmlElement(required = true)
    @Column
    @Enumerated(value = EnumType.STRING)
    private FacebookStreamType type;

    @XmlElement(required = true)
    @Column
    @Enumerated(value = EnumType.STRING)
    private EntityIdentifier entityIdentifier;

    public enum EntityIdentifier {

        OPPORTUNITY, AWARD
    }

    @XmlType
    public enum FacebookStreamType {

        MESSAGE, PHOTO
    }

    protected FacebookStream() {
    }

    public FacebookStream(String streamId, Long entityId, EntityIdentifier entityIdentifier, User user, FacebookStreamType type) {
        this.streamId = streamId;
        this.user = user;
        this.type = type;
        this.entityId = entityId;
        this.entityIdentifier = entityIdentifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj.getClass() != getClass()) {
            return false;
        }

        FacebookStream fbStream = (FacebookStream) obj;
        return new EqualsBuilder()
                .append(user, fbStream.getUser())
                .append(streamId, fbStream.getStreamId())
                .append(entityId, fbStream.getEntityId())
                .append(entityIdentifier, fbStream.getEntityIdentifier())
                .append(type, fbStream.getType())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(user)
                .append(streamId)
                .append(entityId)
                .append(entityIdentifier)
                .append(type)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(user).append(streamId).toString();
    }

    public Long getEntityId() {
        return entityId;
    }

    public EntityIdentifier getEntityIdentifier() {
        return entityIdentifier;
    }

    public String getStreamId() {
        return streamId;
    }

    public FacebookStreamType getType() {
        return type;
    }

    public User getUser() {
        return user;
    }
}