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

package com.openplanetideas.plusyou.server.service.impl;

import com.google.common.collect.Lists;
import com.openplanetideas.plusyou.server.controller.common.AbstractController;
import com.openplanetideas.plusyou.server.domain.FacebookStream;
import com.openplanetideas.plusyou.server.domain.User;
import com.openplanetideas.plusyou.server.domain.UserInvite;
import com.openplanetideas.plusyou.server.domain.UserOpportunity;
import com.openplanetideas.plusyou.server.exception.PlusYouServerException;
import com.openplanetideas.plusyou.server.service.AwardService;
import com.openplanetideas.plusyou.server.service.UserService;
import com.openplanetideas.plusyou.server.service.common.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends AbstractService implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Inject
    protected AwardService awardService;

    @Override
    @Transactional
    public void deleteAllUserData(final String facebookId) {
        User user = userRepository.findByFacebookId(facebookId);

        if (user != null) {
            List<FacebookStream> facebookStreams = facebookPostRepository.findByUserIn(Lists.newArrayList(user), null);
            facebookPostRepository.delete(facebookStreams);

            List<UserOpportunity> userOpportunities = userOpportunityRepository.findByUser(user);
            userOpportunityRepository.delete(userOpportunities);

            List<UserInvite> userInvites = userInviteRepository.findByUser(user);
            userInviteRepository.delete(userInvites);

            userRepository.delete(user);
        } else {
            String msg = String.format("%s: %s", AbstractController.FACEBOOK_USER_ID_NOT_FOUND_IN_USER_TABLE, facebookId);
            LOG.warn(msg);
        }
    }

    @Override
    @Transactional
    public void saveUserInvites(final String facebookId, final Long opportunityId, final List<String> friendIds) {
        User user = userRepository.findByFacebookId(facebookId);
        if (user == null) {
            String msg = String.format("%s: %s", AbstractController.FACEBOOK_USER_ID_NOT_FOUND_IN_USER_TABLE, facebookId);
            LOG.warn(msg);
        } else {
            List<UserInvite> userInvites = new ArrayList<UserInvite>();
            for (final String friendId : friendIds) {
                UserInvite userInvite = userInviteRepository.findByUserAndOpportunityIdAndFriendId(user, opportunityId, friendId);
                if (userInvite == null) {
                    userInvite = new UserInvite(user, opportunityId, friendId);
                    userInvites.add(userInvite);
                }
            }

            userInviteRepository.save(userInvites);
        }
    }

    @Override
    @Transactional
    public void createNewUser(String facebookId, String password) throws PlusYouServerException {
        User user = new User(facebookId, password);
        awardService.assignNewbieAward(user);
        awardService.assignFoundingMemberAward(user);
        userRepository.save(user);
        LOG.info("User created: " + user);
    }
}