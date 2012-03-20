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
import com.openplanetideas.plusyou.server.exception.PlusYouServerException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@XmlType
@Entity(name = "users")
public class User extends AbstractEntity {

    private static final Logger LOG = LoggerFactory.getLogger(User.class);
    private static final long serialVersionUID = 5622499901870683126L;

    @XmlElement(required = true)
    @Column(nullable = false, unique = true)
    private String facebookId;

    @Column(nullable = false)
    @XmlTransient
    private String password;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pk.user", cascade = CascadeType.ALL)
    @XmlTransient
    private List<UserAward> userAwards = new LinkedList<UserAward>();

    @Temporal(TemporalType.DATE)
    @XmlTransient
    private Date registrationDate;

    protected User() {
    }

    public User(String facebookId, String password) throws PlusYouServerException {
        this.facebookId = facebookId;
        this.password = password;
        this.registrationDate = new Date();
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

        User user = (User) obj;
        return new EqualsBuilder()
                .append(facebookId, user.getFacebookId())
                .append(registrationDate, user.getRegistrationDate())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(facebookId)
                .append(registrationDate)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(facebookId)
                .append(registrationDate)
                .toString();
    }

    //TODO write test in UserRepositoryCrudTest
    public void assignAward(Award award) {
        UserAward newUserAward = new UserAward(this, award, new Date());
        if (userAwards.contains(newUserAward)) {
            UserAward userAward = userAwards.get(userAwards.indexOf(newUserAward));
            userAward.addTimesWon();
        } else {
            userAwards.add(newUserAward);
        }
        LOG.info("Award assigned: " + award);
    }

    //TODO write test in UserRepositoryCrudTest
    @JsonIgnore
    public List<Award> getAwards() {
        List<Award> awards = new ArrayList<Award>(userAwards.size());
        for (UserAward userAward : userAwards) {
            awards.add(userAward.getAward());
        }
        return awards;
    }

    public String getFacebookId() {
        return facebookId;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    public Date getRegistrationDate() {
        return registrationDate;
    }

    @JsonIgnore
    public List<UserAward> getUserAwards() {
        return userAwards;
    }

    public void updatePassphrase(String passphrase) {
        this.password = passphrase;
    }
}