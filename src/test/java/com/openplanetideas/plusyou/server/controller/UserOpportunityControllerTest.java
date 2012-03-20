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

import com.openplanetideas.plusyou.server.domain.User;
import com.openplanetideas.plusyou.server.domain.UserOpportunity;
import com.openplanetideas.plusyou.server.domain.response.UserOpportunityList;
import com.openplanetideas.plusyou.server.exception.PlusYouServerException;
import com.openplanetideas.plusyou.server.repository.UserOpportunityRepository;
import com.openplanetideas.plusyou.server.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static com.openplanetideas.plusyou.server.domain.UserOpportunityTestBuilder.aUserOpportunity;
import static com.openplanetideas.plusyou.server.domain.UserTestBuilder.aUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserOpportunityControllerTest {

    private static final String FACEBOOK_ID = "1L";
    private static final Long OPPORTUNITY_ID = 100L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserOpportunityRepository userOpportunityRepository;

    @InjectMocks
    private UserOpportunityController controller = new UserOpportunityController();

    @Test
    public void deleteAllOfUser() throws Exception {
        User user = aUser().withFacebookId(FACEBOOK_ID).build();

        when(userRepository.findByFacebookId(FACEBOOK_ID)).thenReturn(user);

        ResponseEntity responseEntity = controller.deleteAllOfUser(FACEBOOK_ID);
        verify(userOpportunityRepository).deleteByUser(user);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void deleteWhenFacebookIdFoundTest() throws PlusYouServerException {
        User user = aUser().withFacebookId(FACEBOOK_ID).build();
        UserOpportunity userOpportunity = aUserOpportunity().withUser(user).withOpportunityId(OPPORTUNITY_ID).build();

        when(userRepository.findByFacebookId(FACEBOOK_ID)).thenReturn(user);
        when(userOpportunityRepository.findByUserAndOpportunityId(user, OPPORTUNITY_ID)).thenReturn(userOpportunity);
        ResponseEntity<String> response = controller.delete(FACEBOOK_ID, OPPORTUNITY_ID);
        verify(userRepository).findByFacebookId(FACEBOOK_ID);
        verify(userOpportunityRepository).findByUserAndOpportunityId(user, OPPORTUNITY_ID);
        verify(userOpportunityRepository).save(userOpportunity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test(expected = PlusYouServerException.class)
    public void deleteWhenFacebookIdNotFoundTest() throws PlusYouServerException {
        when(userRepository.findByFacebookId(FACEBOOK_ID)).thenReturn(null);
        ResponseEntity responseEntity = controller.delete(FACEBOOK_ID, OPPORTUNITY_ID);
        verify(userRepository).findByFacebookId(FACEBOOK_ID);
        verify(userOpportunityRepository, times(0)).findByUserAndOpportunityId(any(User.class), anyLong());
        verify(userOpportunityRepository, times(0)).delete(any(UserOpportunity.class));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void getUserOpportunitiesByFacebookIdReturns2UserOpportunitiesTest() throws PlusYouServerException {
        User user = aUser().withFacebookId(FACEBOOK_ID).build();
        List<UserOpportunity> userOpportunities = new ArrayList<UserOpportunity>(5);
        userOpportunities.add(aUserOpportunity().withUser(user).withOpportunityId(1L).build());
        userOpportunities.add(aUserOpportunity().withUser(user).withOpportunityId(2L).build());
        userOpportunities.add(aUserOpportunity().withUser(user).withOpportunityId(3L).build());
        userOpportunities.add(aUserOpportunity().withUser(user).withOpportunityId(4L).build());
        userOpportunities.add(aUserOpportunity().withUser(user).withOpportunityId(5L).build());

        when(userRepository.findByFacebookId(FACEBOOK_ID)).thenReturn(user);
        when(userOpportunityRepository.findByUser(user)).thenReturn(userOpportunities);

        ResponseEntity<UserOpportunityList> responseEntity = controller.getByFacebookId(FACEBOOK_ID);
        verify(userRepository).findByFacebookId(FACEBOOK_ID);
        verify(userOpportunityRepository).findByUser(user);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userOpportunities, responseEntity.getBody().getUserOpportunities());
    }

    @Test
    public void postUserOpportunityCheckIn() throws Exception{
        User user = aUser().withFacebookId(FACEBOOK_ID).build();
        UserOpportunity userOpportunity = aUserOpportunity().withUser(user).withOpportunityId(OPPORTUNITY_ID).build();

        when(userRepository.findByFacebookId(FACEBOOK_ID)).thenReturn(user);
        when(userOpportunityRepository.findByUserAndOpportunityId(user, OPPORTUNITY_ID)).thenReturn(userOpportunity);

        ResponseEntity responseEntity = controller.checkInOpportunity(FACEBOOK_ID, OPPORTUNITY_ID);
        verify(userRepository).findByFacebookId(FACEBOOK_ID);
        verify(userOpportunityRepository).findByUserAndOpportunityId(user, OPPORTUNITY_ID);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void saveWhenFacebookIdFoundTest() throws PlusYouServerException {
        User user = aUser().withFacebookId(FACEBOOK_ID).build();
        UserOpportunity userOpportunity = aUserOpportunity().withUser(user).withOpportunityId(OPPORTUNITY_ID).build();

        when(userRepository.findByFacebookId(FACEBOOK_ID)).thenReturn(user);
        when(userOpportunityRepository.save(userOpportunity)).thenReturn(userOpportunity);

        ResponseEntity<Boolean> response = controller.save(FACEBOOK_ID, OPPORTUNITY_ID);
        verify(userRepository).findByFacebookId(FACEBOOK_ID);
        verify(userOpportunityRepository).save(userOpportunity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        assertNotNull(userOpportunity.getRegistrationDate());
        assertNull(userOpportunity.getCheckInDate());
    }

    @Test(expected = PlusYouServerException.class)
    public void saveWhenFacebookIdNotFoundTest() throws PlusYouServerException {
        when(userRepository.findByFacebookId(FACEBOOK_ID)).thenReturn(null);
        controller.save(FACEBOOK_ID, OPPORTUNITY_ID);
        verify(userOpportunityRepository, times(0)).save(any(UserOpportunity.class));
    }
}