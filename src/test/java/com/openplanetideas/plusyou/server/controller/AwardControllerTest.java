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
import com.openplanetideas.plusyou.server.domain.Award;
import com.openplanetideas.plusyou.server.domain.UserAward;
import com.openplanetideas.plusyou.server.domain.UserAwardTestBuilder;
import com.openplanetideas.plusyou.server.domain.response.AwardList;
import com.openplanetideas.plusyou.server.domain.response.UserAwardList;
import com.openplanetideas.plusyou.server.service.AwardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static com.openplanetideas.plusyou.server.domain.AwardTestBuilder.anAward;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AwardControllerTest {

    private static final String FACEBOOK_ID = "-1L";

    @Mock
    private AwardService awardService;

    @InjectMocks
    private AwardController awardController;

    @Test
    public void getAllAwardsReturnsGroupedAwardsList_noResults() {
        when(awardService.getAllAwardsOfUser(Matchers.<String>any())).thenReturn(Lists.<UserAward>newArrayList());
        ResponseEntity<UserAwardList> responseEntity = awardController.getAllAwardsOfUser(Matchers.<String>any());

        verify(awardService).getAllAwardsOfUser(Matchers.<String>any());

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void getAllAwardsReturnsGroupedAwardsList_withResults() throws Exception {
        UserAward award = UserAwardTestBuilder.aUserAward().build();
        when(awardService.getAllAwardsOfUser(FACEBOOK_ID)).thenReturn(Lists.<UserAward>newArrayList(award));
        ResponseEntity<UserAwardList> responseEntity = awardController.getAllAwardsOfUser(FACEBOOK_ID);

        verify(awardService).getAllAwardsOfUser(FACEBOOK_ID);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().userAwards.size());
        assertTrue(responseEntity.getBody().userAwards.contains(award));
    }

    @Test
    public void getCheckInAward_returnsAward() throws Exception {
        Award award = anAward().build();

        when(awardService.assignCheckInAward(Matchers.<String>any())).thenReturn(award);
        ResponseEntity<Award> responseEntity = awardController.getCheckInAward(Matchers.<String>any());

        verify(awardService).assignCheckInAward(Matchers.<String>any());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getCheckInAward_returnsNoContent() throws Exception {
        when(awardService.assignCheckInAward(Matchers.<String>any())).thenReturn(null);
        ResponseEntity<Award> responseEntity = awardController.getCheckInAward(Matchers.<String>any());

        verify(awardService).assignCheckInAward(Matchers.<String>any());

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void getEarlyBirdAward_returnsAward() throws Exception {
        Award award = anAward().build();

        when(awardService.assignEarlyBirdAward(Matchers.<String>any())).thenReturn(award);
        ResponseEntity<Award> responseEntity = awardController.getEarlyBirdAward(Matchers.<String>any());

        verify(awardService).assignEarlyBirdAward(Matchers.<String>any());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getEarlyBirdAward_returnsNoContent() throws Exception {
        when(awardService.assignEarlyBirdAward(Matchers.<String>any())).thenReturn(null);
        ResponseEntity<Award> responseEntity = awardController.getEarlyBirdAward(Matchers.<String>any());

        verify(awardService).assignEarlyBirdAward(Matchers.<String>any());

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void getPictureAward_returnsAward() throws Exception {
        Award award = anAward().build();

        when(awardService.assignPictureAward(Matchers.<String>any())).thenReturn(award);
        ResponseEntity<Award> responseEntity = awardController.getPictureAward(Matchers.<String>any());

        verify(awardService).assignPictureAward(Matchers.<String>any());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getPictureAward_returnsNoContent_returnsNoContent() throws Exception {
        when(awardService.assignPictureAward(Matchers.<String>any())).thenReturn(null);
        ResponseEntity<Award> responseEntity = awardController.getPictureAward(Matchers.<String>any());

        verify(awardService).assignPictureAward(Matchers.<String>any());

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void getSocialNetworkAward_returnsAward() throws Exception {
        Award award = anAward().build();

        when(awardService.assignSocialNetworkAward(Matchers.<String>any())).thenReturn(award);
        ResponseEntity<Award> responseEntity = awardController.getSocialNetworkAward(Matchers.<String>any());

        verify(awardService).assignSocialNetworkAward(Matchers.<String>any());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getSocialNetworkAward_returnsNoContent() throws Exception {
        when(awardService.assignSocialNetworkAward(Matchers.<String>any())).thenReturn(null);
        ResponseEntity<Award> responseEntity = awardController.getSocialNetworkAward(Matchers.<String>any());

        verify(awardService).assignSocialNetworkAward(Matchers.<String>any());

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void getVolunteeringAwards_noResults() {
        when(awardService.assignExploreAwards(Matchers.<String>any(), Matchers.<Long>any())).thenReturn(new ArrayList<Award>());
        when(awardService.assignGoodIntentionsAward(Matchers.<String>any())).thenReturn(null);

        ResponseEntity<AwardList> responseEntity = awardController.getVolunteeringAwards(Matchers.<String>any(), Matchers.<Long>any());

        verify(awardService).assignExploreAwards(Matchers.<String>any(), Matchers.<Long>any());
        verify(awardService).assignGoodIntentionsAward(Matchers.<String>any());

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void getVolunteeringAwards_noVolonteeringAward_withGoodIntentionAward() throws Exception {
        Award goodIntentionsAward = anAward().build();

        when(awardService.assignExploreAwards(Matchers.<String>any(), Matchers.<Long>any())).thenReturn(Lists.<Award>newArrayList());
        when(awardService.assignGoodIntentionsAward(Matchers.<String>any())).thenReturn(goodIntentionsAward);

        ResponseEntity<AwardList> responseEntity = awardController.getVolunteeringAwards(Matchers.<String>any(), Matchers.<Long>any());

        verify(awardService).assignExploreAwards(Matchers.<String>any(), Matchers.<Long>any());
        verify(awardService).assignGoodIntentionsAward(Matchers.<String>any());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getVolunteeringAwards_withVolonteeringAwardAndGoodIntentionAward() throws Exception {
        Award volonteeringAward = anAward().withId(-123L).withName("volunteer").build();
        Award goodIntetionAward = anAward().withId(-456L).withName("goodIntention").build();

        when(awardService.assignExploreAwards(Matchers.<String>any(), Matchers.<Long>any())).thenReturn(Lists.<Award>newArrayList(volonteeringAward));
        when(awardService.assignGoodIntentionsAward(Matchers.<String>any())).thenReturn(goodIntetionAward);

        ResponseEntity<AwardList> responseEntity = awardController.getVolunteeringAwards(Matchers.<String>any(), Matchers.<Long>any());

        verify(awardService).assignExploreAwards(Matchers.<String>any(), Matchers.<Long>any());
        verify(awardService).assignGoodIntentionsAward(Matchers.<String>any());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getVolunteeringAwards_withVolonteeringAward_noGoodIntentionAward() throws Exception {
        Award volonteeringAward = anAward().build();

        when(awardService.assignExploreAwards(Matchers.<String>any(), Matchers.<Long>any())).thenReturn(Lists.<Award>newArrayList(volonteeringAward));
        when(awardService.assignGoodIntentionsAward(Matchers.<String>any())).thenReturn(null);

        ResponseEntity<AwardList> responseEntity = awardController.getVolunteeringAwards(Matchers.<String>any(), Matchers.<Long>any());

        verify(awardService).assignExploreAwards(Matchers.<String>any(), Matchers.<Long>any());
        verify(awardService).assignGoodIntentionsAward(Matchers.<String>any());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}