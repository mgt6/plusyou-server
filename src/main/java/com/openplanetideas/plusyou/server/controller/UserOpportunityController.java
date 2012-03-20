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

package com.openplanetideas.plusyou.server.controller;

import com.openplanetideas.plusyou.server.controller.common.AbstractController;
import com.openplanetideas.plusyou.server.domain.User;
import com.openplanetideas.plusyou.server.domain.UserOpportunity;
import com.openplanetideas.plusyou.server.domain.response.UserOpportunityList;
import com.openplanetideas.plusyou.server.exception.PlusYouServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.UserOperations;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.List;

import static com.openplanetideas.plusyou.server.domain.UserOpportunity.Status.JOINED;
import static com.openplanetideas.plusyou.server.domain.UserOpportunity.Status.WITHDRAWED;

@Controller
@RequestMapping("/user-opportunity")
public class UserOpportunityController extends AbstractController {

    public static final String ACTION_SAVE = "/save/facebook-user-id/{facebookId}/opportunity-id/{opportunityId}";
    public static final String ACTION_DELETE_USER_EVENT = "/delete/facebook-user-id/{facebookId}/opportunity-id/{opportunityId}";
    public static final String ACTION_DELETE_BY_USER = "/delete/facebook-user-id/{facebookId}";
    public static final String ACTION_GET_BY_FB_ID = "/get/facebook-user-id/{facebookId}";
    public static final String ACTION_POST_CHECK_IN = "/check-in/facebook-user-id/{facebookId}/opportunity-id/{opportunityId}";

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(value = ACTION_POST_CHECK_IN, method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Date> checkInOpportunity(@PathVariable final String facebookId, @PathVariable Long opportunityId) throws PlusYouServerException {
        Date now = new Date();
        User user = findUserByFacebookId(facebookId);
        UserOpportunity userOpportunity = userOpportunityRepository.findByUserAndOpportunityId(user, opportunityId);
        userOpportunity.checkIn(now);

        return new ResponseEntity<Date>(now, HttpStatus.OK);
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(ACTION_DELETE_USER_EVENT)
    @Transactional
    public ResponseEntity delete(@PathVariable final String facebookId, @PathVariable final Long opportunityId) throws PlusYouServerException {
        User user = findUserByFacebookId(facebookId);
        UserOpportunity userOpportunity = userOpportunityRepository.findByUserAndOpportunityId(user, opportunityId);
        if (userOpportunity != null) {
            userOpportunity.setRegistrationStatus(WITHDRAWED);
            userOpportunityRepository.save(userOpportunity);
        }

        sendWithdrawEventEmail(facebookId, opportunityId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(ACTION_DELETE_BY_USER)
    public ResponseEntity deleteAllOfUser(@PathVariable final String facebookId) throws PlusYouServerException {
        userOpportunityRepository.deleteByUser(findUserByFacebookId(facebookId));
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(ACTION_GET_BY_FB_ID)
    public ResponseEntity<UserOpportunityList> getByFacebookId(@PathVariable final String facebookId) throws PlusYouServerException {
        User user = findUserByFacebookId(facebookId);
        List<UserOpportunity> userOpportunities = userOpportunityRepository.findByUser(user);
        return new ResponseEntity<UserOpportunityList>(new UserOpportunityList(userOpportunities), HttpStatus.OK);
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(method = RequestMethod.POST, value = ACTION_SAVE)
    @Transactional
    public ResponseEntity<Boolean> save(@PathVariable final String facebookId, @PathVariable Long opportunityId) throws PlusYouServerException {
        User user = findUserByFacebookId(facebookId);
        UserOpportunity userOpportunity = userOpportunityRepository.findByUserAndOpportunityId(user, opportunityId);
        boolean newUserOpportunity = false;
        if (userOpportunity != null) {
            userOpportunity.setRegistrationStatus(JOINED);
        } else {
            userOpportunity = new UserOpportunity(user, opportunityId);
            userOpportunityRepository.save(userOpportunity);
            newUserOpportunity = true;
        }

        sendJoinEventEmail(facebookId, opportunityId);
        return new ResponseEntity<Boolean>(newUserOpportunity, HttpStatus.OK);
    }

    private User findUserByFacebookId(final String facebookId) throws PlusYouServerException {
        User user = userRepository.findByFacebookId(facebookId);
        if (user == null) {
            throw new PlusYouServerException(FACEBOOK_USER_ID_NOT_FOUND_IN_USER_TABLE);
        }
        return user;
    }

    private String getFacebookProfileName(String facebookId) {
        FacebookTemplate facebookTemplate = new FacebookTemplate();
        UserOperations userOperations = facebookTemplate.userOperations();
        FacebookProfile facebookProfile = userOperations.getUserProfile(facebookId);
        return facebookProfile.getName();
    }

    private void sendJoinEventEmail(final String facebookId, final Long opportunityId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String user = getFacebookProfileName(facebookId);
                providerRestHandler.isSuccessfulPost("/sendEventNotificationEmail/join/{user}/{opportunityId}", user, opportunityId);
            }
        }).start();
    }

    private void sendWithdrawEventEmail(final String facebookId, final Long opportunityId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String user = getFacebookProfileName(facebookId);
                providerRestHandler.isSuccessfulPost("/sendEventNotificationEmail/withdraw/{user}/{opportunityId}", user, opportunityId);
            }
        }).start();
    }
}