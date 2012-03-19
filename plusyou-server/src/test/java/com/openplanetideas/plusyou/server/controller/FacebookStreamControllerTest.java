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

import com.google.common.collect.Lists;
import com.openplanetideas.plusyou.server.domain.FacebookStream;
import com.openplanetideas.plusyou.server.domain.User;
import com.openplanetideas.plusyou.server.domain.response.FacebookStreamList;
import com.openplanetideas.plusyou.server.exception.PlusYouServerException;
import com.openplanetideas.plusyou.server.repository.FacebookPostRepository;
import com.openplanetideas.plusyou.server.repository.UserRepository;
import com.openplanetideas.plusyou.server.service.JSONService;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static com.openplanetideas.plusyou.server.domain.UserTestBuilder.aUser;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FacebookStreamControllerTest {

    private static final String FACEBOOK_ID = "1L";
    private static final Long OPPORTUNITY_ID = 1L;
    private static final String FACEBOOK_POST_ID = "abc123";
    private static final String VALID_JSON = "{\"data\":[{\"name\":\"Tim Plus You\",\"id\":\"100002326584474\"},{\"name\":\"Tim Plus You\",\"id\":\"100002559204493\"}]}";
    private static final String[] FB_USER_IDS = {"100002326584474", "100002559204493"};

    @Mock
    private UserRepository userRepo;

    @Mock
    private FacebookPostRepository facebookPostRepository;

    @Mock
    private JSONService jsonService;

    @InjectMocks
    private FacebookStreamController controller;

    @Test
    public void getFacebookFriendsPostsWithValidJsonAndKnownFbUsers() throws PlusYouServerException, JSONException {
        final User user = aUser().withFacebookId(FACEBOOK_ID).build();
        when(jsonService.extractUsersFromJson(anyString())).thenReturn(Lists.newArrayList(user));
        when(facebookPostRepository.findByUserIn(anyListOf(User.class), any(PageRequest.class))).thenReturn(getListOf2FacebookPosts());
        ResponseEntity<FacebookStreamList> responseBody = controller.getFacebookFriendsPosts(FACEBOOK_ID, VALID_JSON);
        verify(facebookPostRepository, times(1)).findByUserIn(anyListOf(User.class), any(Pageable.class));

        assertEquals(HttpStatus.OK, responseBody.getStatusCode());
        assertEquals(2, responseBody.getBody().getFacebookStreams().size());
    }

    @Test
    public void getFacebookFriendsPostsWithValidJsonAndUnknownFbUsers() throws PlusYouServerException, JSONException {
        when(jsonService.extractUsersFromJson(anyString())).thenReturn(Lists.<User>newArrayList());
        ResponseEntity<FacebookStreamList> responseBody = controller.getFacebookFriendsPosts(FACEBOOK_ID, VALID_JSON);

        assertEquals(HttpStatus.OK, responseBody.getStatusCode());
        assertEquals(0, responseBody.getBody().getFacebookStreams().size());
    }

    @Test
    public void saveFacebookMessageWithKnownFbId() throws PlusYouServerException {
        User user = aUser().withFacebookId(FACEBOOK_ID).build();
        when(userRepo.findByFacebookId(FACEBOOK_ID)).thenReturn(user);
        ResponseEntity<String> response = controller.saveMessage(FACEBOOK_POST_ID, OPPORTUNITY_ID, FACEBOOK_ID);
        verify(facebookPostRepository, times(1)).save(any(FacebookStream.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = PlusYouServerException.class)
    public void saveFacebookMessageWithUnknownFbId() throws PlusYouServerException {
        when(userRepo.findByFacebookId(FACEBOOK_ID)).thenReturn(null);
        controller.saveMessage(FACEBOOK_POST_ID, OPPORTUNITY_ID, FACEBOOK_ID);
        verify(facebookPostRepository, times(0)).save(any(FacebookStream.class));
    }

    @Test
    public void saveFacebookPhotoWithKnownFbId() throws PlusYouServerException, JSONException {
        User user = aUser().withFacebookId(FACEBOOK_ID).build();
        when(userRepo.findByFacebookId(FACEBOOK_ID)).thenReturn(user);
        ResponseEntity<String> response = controller.savePhoto(FACEBOOK_POST_ID, OPPORTUNITY_ID, FACEBOOK_ID);
        verify(facebookPostRepository, times(1)).save(any(FacebookStream.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = PlusYouServerException.class)
    public void saveFacebookPhotoWithUnknownFbId() throws PlusYouServerException {
        when(userRepo.findByFacebookId(FACEBOOK_ID)).thenReturn(null);
        controller.savePhoto(FACEBOOK_POST_ID, OPPORTUNITY_ID, FACEBOOK_ID);
        verify(facebookPostRepository, times(0)).save(any(FacebookStream.class));
    }

    private List<FacebookStream> getListOf2FacebookPosts() {
        List<FacebookStream> facebookStreams = new ArrayList<FacebookStream>(2);
        for (String fbUserId : FB_USER_IDS) {
            final User user = aUser().withFacebookId(fbUserId).build();
            FacebookStream stream = new FacebookStream("abc_" + fbUserId + "",  OPPORTUNITY_ID, FacebookStream.EntityIdentifier.OPPORTUNITY, user, FacebookStream.FacebookStreamType.MESSAGE);
            facebookStreams.add(stream);
        }
        return facebookStreams;
    }
}
