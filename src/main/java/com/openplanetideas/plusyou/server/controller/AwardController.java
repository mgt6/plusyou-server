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
import com.openplanetideas.plusyou.server.domain.Award;
import com.openplanetideas.plusyou.server.domain.UserAward;
import com.openplanetideas.plusyou.server.domain.response.AwardList;
import com.openplanetideas.plusyou.server.domain.response.UserAwardList;
import com.openplanetideas.plusyou.server.service.AwardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/award")
public class AwardController extends AbstractController {

    private static final String GET_ALL_AWARDS = "/all/facebook-user-id/{facebookId}";
    private static final String POST_EXPLORE_AWARD = "/volonteer/facebook-user-id/{facebookId}/opportunity-id/{opportunityId}";
    private static final String POST_EARLY_BIRD_AWARD = "/early-bird/facebook-user-id/{facebookId}";
    private static final String POST_CHECK_IN_AWARD = "/check-in/facebook-user-id/{facebookId}";
    private static final String POST_PICTURE_AWARD = "/picture/facebook-user-id/{facebookId}";
    private static final String POST_SOCIAL_NETWORK_AWARD = "/social-network/facebook-user-id/{facebookId}";
    private static final String POST_TOTALLY_COMMITTED_AWARD = "/totally-committed/facebook-user-id/{facebookId}";
    private static final String POST_INVITE_FRIENDS_AWARD = "/invite-friends/facebook-user-id/{facebookId}/opportunity-id/{opportunityId}";
    private static final String POST_BUDDY_AWARDS_URL = "/buddy-up/facebook-user-id/{facebookId}/opportunity-id/{opportunityId}";
    
    private static final String POST_FRIEND_ACTIVE_AWARD = "/friend-active/opportunity-id/{opportunityId}";

    @Inject
    private AwardService awardService;

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(value = GET_ALL_AWARDS, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<UserAwardList> getAllAwardsOfUser(@PathVariable final String facebookId) {
        final List<UserAward> allAwardsOfUser = awardService.getAllAwardsOfUser(facebookId);
        userService.flagAwardsAwarded(facebookId);
        if (!isEmpty(allAwardsOfUser)) {
            return new ResponseEntity<UserAwardList>(new UserAwardList(allAwardsOfUser), HttpStatus.OK);
        } else {
            return new ResponseEntity<UserAwardList>(HttpStatus.NO_CONTENT);
        }
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(value = POST_BUDDY_AWARDS_URL, method = POST)
    public ResponseEntity<AwardList> getBuddyAwards(@PathVariable final String facebookId, @PathVariable final Long opportunityId, @RequestBody final String jsonFriendList) {
        List<Award> activeAwards = awardService.assignBuddyAwards(facebookId, opportunityId, jsonFriendList);
        //TODO: candidate for refactor: spring-android rest-client can't handle HttpStatus "NO-CONTENT" atm
        return new ResponseEntity<AwardList>(new AwardList(activeAwards), HttpStatus.OK);
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(value = POST_CHECK_IN_AWARD, method = POST)
    public ResponseEntity getCheckInAward(@PathVariable final String facebookId) {
        return createResponseEntity(awardService.assignCheckInAward(facebookId));
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(value = POST_EARLY_BIRD_AWARD, method = POST)
    public ResponseEntity getEarlyBirdAward(@PathVariable final String facebookId) {
        return createResponseEntity(awardService.assignEarlyBirdAward(facebookId));
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(value = POST_INVITE_FRIENDS_AWARD, method = POST)
    public ResponseEntity getInviteFriendsAward(@PathVariable final String facebookId, @PathVariable final Long opportunityId) {
        return createResponseEntity(awardService.assignInviteFriendAwards(facebookId, opportunityId));
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(value = POST_PICTURE_AWARD, method = POST)
    public ResponseEntity getPictureAward(@PathVariable final String facebookId) {
        return createResponseEntity(awardService.assignPictureAward(facebookId));
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(value = POST_SOCIAL_NETWORK_AWARD, method = POST)
    public ResponseEntity getSocialNetworkAward(@PathVariable final String facebookId) {
        return createResponseEntity(awardService.assignSocialNetworkAward(facebookId));
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(value = POST_TOTALLY_COMMITTED_AWARD, method = POST)
    public ResponseEntity getTotallyCommittedAward(@PathVariable final String facebookId) {
        return createResponseEntity(awardService.assignTotallyCommittedAward(facebookId));
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(value = POST_EXPLORE_AWARD, method = POST)
    public ResponseEntity getVolunteeringAwards(@PathVariable final String facebookId, @PathVariable final Long opportunityId) {
        List<Award> volunteeringAwards = awardService.assignExploreAwards(facebookId, opportunityId);
        Award goodIntentionsAward = awardService.assignGoodIntentionsAward(facebookId);
        if (goodIntentionsAward != null) {
            volunteeringAwards.add(goodIntentionsAward);
        }
        return createResponseEntity(volunteeringAwards);
    }

    @Secured("ROLE_USER")
    @RequestMapping(method = POST, value = POST_FRIEND_ACTIVE_AWARD, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> postFriendActiveAward(@PathVariable final Long opportunityId, @RequestBody final String jsonFriendIds) {
        List<String> friendIds = jsonService.extractFriendIdsFromJson(jsonFriendIds);
        awardService.assignFriendActiveAwards(opportunityId, friendIds);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    private ResponseEntity createResponseEntity(final Award award) {
        if (award != null) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    private ResponseEntity createResponseEntity(final List<Award> awards) {
        if (!isEmpty(awards)) {
            return new ResponseEntity<AwardList>(HttpStatus.OK);
        } else {
            return new ResponseEntity<AwardList>(HttpStatus.NO_CONTENT);
        }
    }
}