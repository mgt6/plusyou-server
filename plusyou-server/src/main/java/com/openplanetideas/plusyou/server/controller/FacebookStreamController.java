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
import com.openplanetideas.plusyou.server.domain.FacebookStream;
import com.openplanetideas.plusyou.server.domain.User;
import com.openplanetideas.plusyou.server.domain.response.FacebookStreamList;
import com.openplanetideas.plusyou.server.exception.PlusYouServerException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

import static com.openplanetideas.plusyou.server.domain.FacebookStream.EntityIdentifier.AWARD;
import static com.openplanetideas.plusyou.server.domain.FacebookStream.EntityIdentifier.OPPORTUNITY;
import static com.openplanetideas.plusyou.server.domain.FacebookStream.FacebookStreamType.MESSAGE;
import static com.openplanetideas.plusyou.server.domain.FacebookStream.FacebookStreamType.PHOTO;

@Controller
@RequestMapping("/facebook")
public class FacebookStreamController extends AbstractController {

    private static final String SAVE_FB_OPPORTUNITY_MSG_URL = "/save/facebook-message-id/{facebookMessageId}/opportunity-id/{opportunityId}/facebook-user-id/{facebookUserId}";
    private static final String SAVE_FB_IMG_URL = "/save/facebook-photo-id/{facebookPhotoId}/opportunity-id/{opportunityId}/facebook-user-id/{facebookUserId}";
    private static final String SAVE_FB_AWARD_MSG_URL = "/save/facebook-message-id/{facebookMessageId}/award-id/{awardId}/facebook-user-id/{facebookUserId}";

    @PreAuthorize("#facebookUserId == authentication.name")
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/messages/facebook-user-id/{facebookUserId}")
    public ResponseEntity<FacebookStreamList> getFacebookFriendsPosts(@PathVariable final String facebookUserId, @RequestBody final String jsonFriendList) {
        List<User> users = jsonService.extractUsersFromJson(jsonFriendList);
        users.add(userRepository.findByFacebookId(facebookUserId));
        
        List<FacebookStream> streams = new ArrayList<FacebookStream>();
        if (!CollectionUtils.isEmpty(users) && users.size() > 0) { /*fix for "unexpected end of subtree": hibernate.onjira.com/browse/HHH-2045*/
            streams = facebookPostRepository.findByUserIn(users, new PageRequest(0, 100));
        }
        return new ResponseEntity<FacebookStreamList>(new FacebookStreamList(streams), HttpStatus.OK);
    }

    @PreAuthorize("#facebookUserId == authentication.name")
    @RequestMapping(method = RequestMethod.POST, value = SAVE_FB_AWARD_MSG_URL)
    public ResponseEntity saveAward(@PathVariable final String facebookMessageId, @PathVariable final Long awardId, @PathVariable final String facebookUserId) throws PlusYouServerException {
        return saveStream(facebookMessageId, awardId, facebookUserId, AWARD, MESSAGE);
    }

    @PreAuthorize("#facebookUserId == authentication.name")
    @RequestMapping(method = RequestMethod.POST, value = SAVE_FB_OPPORTUNITY_MSG_URL)
    public ResponseEntity saveMessage(@PathVariable final String facebookMessageId, @PathVariable final Long opportunityId, @PathVariable final String facebookUserId) throws PlusYouServerException {
        return saveStream(facebookMessageId, opportunityId, facebookUserId, OPPORTUNITY, MESSAGE);
    }

    @PreAuthorize("#facebookUserId == authentication.name")
    @RequestMapping(method = RequestMethod.POST, value = SAVE_FB_IMG_URL)
    public ResponseEntity savePhoto(@PathVariable final String facebookPhotoId, @PathVariable final Long opportunityId, @PathVariable final String facebookUserId) throws PlusYouServerException {
        return saveStream(facebookPhotoId, opportunityId, facebookUserId, OPPORTUNITY, PHOTO);
    }

    private ResponseEntity saveStream(final String facebookMessageId, final Long opportunityId, final String facebookUserId, final FacebookStream.EntityIdentifier entityType, FacebookStream.FacebookStreamType message) throws PlusYouServerException {
        User user = userRepository.findByFacebookId(facebookUserId);
        if (user != null) {
            facebookPostRepository.save(new FacebookStream(facebookMessageId, opportunityId, entityType, user, message));
            return new ResponseEntity<String>(HttpStatus.OK);
        } else {
            throw new PlusYouServerException(FACEBOOK_USER_ID_NOT_FOUND_IN_USER_TABLE);
        }
    }
}