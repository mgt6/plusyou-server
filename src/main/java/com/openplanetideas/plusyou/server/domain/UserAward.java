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

import com.openplanetideas.plusyou.server.domain.jaxb.DateTimeXmlAdapter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "user_awards")
@AssociationOverrides({
        @AssociationOverride(name = "pk.award", joinColumns = @JoinColumn(name = "award_id")),
        @AssociationOverride(name = "pk.user", joinColumns = @JoinColumn(name = "user_id"))
})
@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.NONE)
public class UserAward implements Serializable {

    private static final long serialVersionUID = 7494508587959256301L;

    @EmbeddedId
    private UserAwardPk pk = new UserAwardPk();
    @Column(nullable = false)
    private Date assignedDate;
    @Column(nullable = false)
    private Integer timesWon = 0;

    protected UserAward() {
    }

    public UserAward(User user, Award award, Date assignedDate) {
        this(user, award);
        this.assignedDate = assignedDate;
    }

    public UserAward(User user, Award award) {
        pk.setUser(user);
        pk.setAward(award);
        timesWon = 1;
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

        UserAward userAward = (UserAward) obj;
        return new EqualsBuilder()
                /*ATTENTION: When User becomes important for a UserAward to be equal,
                * AwardService.getAllAwardsOfUser() needs to be rewritten. This logic will break
                * when User is taken into account in this equals.
                * .append(getUser(), userAward.getUser())
                * */
                .append(getAward(), userAward.getAward())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                //.append(getUser())
                .append(getAward())
                .hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(getUser())
                .append(getAward())
                .toString();
    }

    public void addTimesWon() {
        this.timesWon++;
    }


    @XmlElement(required = true)
    @XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    public Date getAssignedDate() {
        return assignedDate;
    }

    @Transient
    @XmlElement(required = true)
    public Award getAward() {
        return pk.getAward();
    }

    public Integer getTimesWon() {
        return timesWon;
    }

    @Transient
    public User getUser() {
        return pk.getUser();
    }
}