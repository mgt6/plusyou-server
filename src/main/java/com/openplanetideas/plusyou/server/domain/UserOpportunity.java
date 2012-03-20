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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

import static com.openplanetideas.plusyou.server.domain.UserOpportunity.Status.JOINED;

@XmlRootElement
@XmlType
@Entity(name = "user_opportunities")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "opportunityId"}))
public class UserOpportunity extends AbstractEntity {

    private static final long serialVersionUID = 8693920851869021335L;

    @ManyToOne(optional = false)
    @XmlElement(required = true)
    private User user;

    @Column(nullable = false)
    @XmlElement(required = true)
    private Long opportunityId;

    @XmlElement(required = false)
    @XmlSchemaType(name = "dateTime")
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    @XmlElement(required = false)
    @XmlSchemaType(name = "dateTime")
    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Date checkInDate;

    @XmlElement(required = true)
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Status registrationStatus;

    public enum Status {

        JOINED,
        WITHDRAWED
    }

    protected UserOpportunity() {
    }

    public UserOpportunity(User user, Long opportunityId) {
        this.user = user;
        this.opportunityId = opportunityId;
        this.registrationDate = new Date();
        this.registrationStatus = JOINED;
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

        UserOpportunity userOpportunity = (UserOpportunity) obj;
        return new EqualsBuilder()
                .append(user, userOpportunity.getUser())
                .append(opportunityId, userOpportunity.getOpportunityId())
                .append(registrationStatus, userOpportunity.getRegistrationStatus())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(user)
                .append(opportunityId)
                .append(registrationStatus)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(user)
                .append(opportunityId)
                .append(registrationDate)
                .append(checkInDate)
                .append(registrationStatus)
                .toString();
    }

    public void checkIn(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public Long getOpportunityId() {
        return opportunityId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public Status getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(Status registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public User getUser() {
        return user;
    }
}