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
import com.openplanetideas.plusyou.server.domain.response.UserList;
import com.openplanetideas.plusyou.server.exception.PlusYouServerException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/user")
public class UserController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private static final String ACTION_SAVE = "/save";
    private static final String ACTION_DELETE = "/delete/{facebookId}";
    private static final String ACTION_FIND_ALL = "/find";

    @RequestMapping(method = POST, value = ACTION_SAVE)
    @Transactional
    public ResponseEntity createOrUpdateUser(@RequestBody String facebookToken, @RequestHeader("facebookId") final String facebookId, @RequestHeader("passphrase") final String passphrase) throws PlusYouServerException {
        User user = userRepository.findByFacebookId(facebookId);
        LOG.info("Validating user...");
        FacebookTemplate facebookTemplate = new FacebookTemplate(facebookToken);
        final String facebookIdOfToken = facebookTemplate.userOperations().getUserProfile().getId();

        if (!facebookId.equals(facebookIdOfToken)) {
            LOG.error("Unacceptable user claim: Claimed fb-id = " + facebookId + " actual fb-id = " + facebookIdOfToken);
            return new ResponseEntity(UNAUTHORIZED);
        }

        LOG.info("User claim accepted: " + facebookId);
        if (user == null) {
            userService.createNewUser(facebookId, passphrase);
        } else if (!user.getPassword().equals(passphrase)) {
            LOG.info("Updating user password");
            user.updatePassphrase(passphrase);
        }

        return new ResponseEntity(OK);
    }

    @PreAuthorize("#facebookId == authentication.name")
    @RequestMapping(method = POST, value = ACTION_DELETE)
    public ResponseEntity deleteAllUserData(@PathVariable final String facebookId) {
        userService.deleteAllUserData(facebookId);
        return new ResponseEntity(OK);
    }

    @RequestMapping(method = POST, value = ACTION_FIND_ALL)
    public ResponseEntity<UserList> findUsers(@RequestBody final String facebookFriendList) throws JSONException {
        List<User> users = jsonService.extractUsersFromJson(facebookFriendList);
        //TODO NO_CONTENT results in crash at client site because Spring-REST doen't handle NO-CONTENT correctly (fix in 3.1 RC)
        return new ResponseEntity<UserList>(new UserList(users), OK);
    }
}