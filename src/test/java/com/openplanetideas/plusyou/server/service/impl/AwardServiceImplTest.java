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
import com.openplanetideas.plusyou.provider.InterestCategory;
import com.openplanetideas.plusyou.provider.Opportunity;
import com.openplanetideas.plusyou.server.common.AwardGroupInterestCategoryMapper;
import com.openplanetideas.plusyou.server.domain.Award;
import com.openplanetideas.plusyou.server.domain.User;
import com.openplanetideas.plusyou.server.domain.UserAward;
import com.openplanetideas.plusyou.server.domain.UserAwardPk;
import com.openplanetideas.plusyou.server.domain.UserAwardTestBuilder;
import com.openplanetideas.plusyou.server.domain.UserInvite;
import com.openplanetideas.plusyou.server.domain.UserOpportunity;
import com.openplanetideas.plusyou.server.repository.AwardRepository;
import com.openplanetideas.plusyou.server.repository.UserAwardRepository;
import com.openplanetideas.plusyou.server.repository.UserInviteRepository;
import com.openplanetideas.plusyou.server.repository.UserOpportunityRepository;
import com.openplanetideas.plusyou.server.repository.UserRepository;
import com.openplanetideas.plusyou.server.webservice.ProviderRestHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.openplanetideas.plusyou.server.domain.AwardTestBuilder.anAward;
import static com.openplanetideas.plusyou.server.domain.UserAwardTestBuilder.aUserAward;
import static com.openplanetideas.plusyou.server.domain.UserInviteTestBuilder.aUserInvite;
import static com.openplanetideas.plusyou.server.domain.UserOpportunityTestBuilder.aDate;
import static com.openplanetideas.plusyou.server.domain.UserOpportunityTestBuilder.aUserOpportunity;
import static com.openplanetideas.plusyou.server.domain.UserTestBuilder.aUser;
import static com.openplanetideas.plusyou.server.domain.xsd.InterestBuilder.anInterest;
import static com.openplanetideas.plusyou.server.domain.xsd.OpportunityTestBuilder.anOpportunity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AwardServiceImplTest {

    private static final String FB_ID = "-895465432L";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserOpportunityRepository userOpportunityRepository;

    @Mock
    private AwardRepository awardRepository;

    @Mock
    private ProviderRestHandler providerRestHandler;

    @Mock
    private UserAwardRepository userAwardRepository;

    @Mock
    private UserInviteRepository userInviteRepository;

    @InjectMocks
    private AwardServiceImpl awardService;

    @Test
    public void assignFriendActiveAwards() {
        Award lowAward = anAward().withCategory(Award.AwardCategory.SHARE).withGroup(Award.AwardGroup.ACTIVE).withLevel(Award.AwardLevel.LOW).build();
        Award highAward = anAward().withCategory(lowAward.getCategory()).withGroup(lowAward.getGroup()).withLevel(Award.AwardLevel.HIGH).build();

        User firstFriend = aUser().build();
        User secondFriend = aUser().build();
        User thirdFriend = aUser().build();
        List<String> friendIds = Lists.newArrayList(firstFriend.getFacebookId(), secondFriend.getFacebookId(), thirdFriend.getFacebookId());

        List<UserOpportunity> friendOpportunities = new ArrayList<UserOpportunity>();
        friendOpportunities.add(aUserOpportunity().withUser(firstFriend).build());
        friendOpportunities.add(aUserOpportunity().withUser(secondFriend).build());
        friendOpportunities.add(aUserOpportunity().withUser(thirdFriend).build());
        UserOpportunity firstFriendOpportunity = friendOpportunities.get(0);
        UserOpportunity[] nextFriendOpportunities = friendOpportunities.subList(1, friendOpportunities.size()).toArray(new UserOpportunity[friendOpportunities.size() - 1]);

        List<UserAward> friendAwards = new ArrayList<UserAward>();
        friendAwards.add(aUserAward().withUser(firstFriend).withTimesWon(1).build());
        friendAwards.add(aUserAward().withUser(secondFriend).withTimesWon(7).build());
        friendAwards.add(aUserAward().withUser(thirdFriend).withTimesWon(5).build());
        UserAward firstFriendAward = friendAwards.get(0);
        UserAward[] nextFriendAwards = friendAwards.subList(1, friendAwards.size()).toArray(new UserAward[friendAwards.size() - 1]);

        when(awardRepository.findByCategoryAndLevelAndGroup(any(Award.AwardCategory.class), any(Award.AwardLevel.class), any(Award.AwardGroup.class))).thenReturn(lowAward);
        when(userRepository.findByFacebookId(anyString())).thenReturn(firstFriend, secondFriend, thirdFriend);
        when(userOpportunityRepository.findByUserAndOpportunityId(any(User.class), anyLong())).thenReturn(firstFriendOpportunity, nextFriendOpportunities);
        when(userAwardRepository.findByPk(any(UserAwardPk.class))).thenReturn(firstFriendAward, nextFriendAwards);
        when(awardRepository.findByGroupAndLevel(any(Award.AwardGroup.class), any(Award.AwardLevel.class))).thenReturn(highAward);

        Map<User, Award> expectedFriendAwards = new HashMap<User, Award>();
        expectedFriendAwards.put(firstFriend, lowAward);
        expectedFriendAwards.put(secondFriend, null);
        expectedFriendAwards.put(thirdFriend, highAward);

        Map<User, Award> actualFriendAwards = awardService.assignFriendActiveAwards(firstFriendOpportunity.getOpportunityId(), friendIds);
        assertEquals(expectedFriendAwards, actualFriendAwards);
    }

    @Test
    public void assignInviteFriendAwards_invite2Friends() {
        assignInviteFriendAwards(2, false);
    }

    @Test
    public void assignInviteFriendAwards_invite5Friends() {
        assignInviteFriendAwards(5, true);
    }

    @Test
    public void assignInviteFriendAwards_invite7Friends() {
        assignInviteFriendAwards(7, true);
    }

    @Test
    public void testAssignCheckInAward_returnsAmazingDeedsAward() throws Exception {
        final User user = aUser().withFacebookId(FB_ID).build();
        final List<UserOpportunity> userOpportunities = Lists.newArrayList(
                aUserOpportunity().withUser(user).withOpportunityId(-123L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-456L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-789L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-147L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-258L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-369L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-987L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-654L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-321L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-753L).withCheckInDate(aDate()).build()
        );
        final String expectedAwardName = "Amazing Deeds";   //TODO hardcoded

        final Award expectedAward = anAward().withName(expectedAwardName).build();
        UserAward expectedUserAward = aUserAward().withAward(expectedAward).withUser(user).build();


        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(userOpportunityRepository.findByUserAndCheckInDateNotNull(user)).thenReturn(userOpportunities);
        when(awardRepository.findByName(expectedAwardName)).thenReturn(expectedAward);

        final Award testResult = awardService.assignCheckInAward(FB_ID);
        verify(userOpportunityRepository).findByUserAndCheckInDateNotNull(user);
        verify(awardRepository).findByName(expectedAwardName);

        assertNotNull(testResult);
        assertEquals(expectedAward, testResult);
        assertTrue(user.getUserAwards().contains(expectedUserAward));
    }

    @Test
    public void testAssignCheckInAward_returnsGoodDeedAward() throws Exception {
        final User user = aUser().withFacebookId(FB_ID).build();
        final List<UserOpportunity> userOpportunities = Lists.newArrayList(
                aUserOpportunity().withUser(user).withOpportunityId(-4141214L).withCheckInDate(aDate()).build()
        );
        final String expectedAwardName = "Good Deed";   //TODO hardcoded

        final Award expectedAward = anAward().withName(expectedAwardName).build();
        UserAward expectedUserAward = aUserAward().withAward(expectedAward).withUser(user).build();


        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(userOpportunityRepository.findByUserAndCheckInDateNotNull(user)).thenReturn(userOpportunities);
        when(awardRepository.findByName(expectedAwardName)).thenReturn(expectedAward);

        final Award testResult = awardService.assignCheckInAward(FB_ID);
        verify(userOpportunityRepository).findByUserAndCheckInDateNotNull(user);
        verify(awardRepository).findByName(expectedAwardName);

        assertNotNull(testResult);
        assertEquals(expectedAward, testResult);
        assertTrue(user.getUserAwards().contains(expectedUserAward));
    }

    @Test
    public void testAssignCheckInAward_returnsSuperUAward() throws Exception {
        final User user = aUser().withFacebookId(FB_ID).build();
        final List<UserOpportunity> userOpportunities = Lists.newArrayList(
                aUserOpportunity().withUser(user).withOpportunityId(-123L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-789L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-456L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-741L).withCheckInDate(aDate()).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-852L).withCheckInDate(aDate()).build()
        );
        final String expectedAwardName = "Super U!";   //TODO hardcoded

        final Award expectedAward = anAward().withName(expectedAwardName).build();
        UserAward expectedUserAward = aUserAward().withAward(expectedAward).withUser(user).build();


        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(userOpportunityRepository.findByUserAndCheckInDateNotNull(user)).thenReturn(userOpportunities);
        when(awardRepository.findByName(expectedAwardName)).thenReturn(expectedAward);

        final Award testResult = awardService.assignCheckInAward(FB_ID);
        verify(userOpportunityRepository).findByUserAndCheckInDateNotNull(user);
        verify(awardRepository).findByName(expectedAwardName);

        assertNotNull(testResult);
        assertEquals(expectedAward, testResult);
        assertTrue(user.getUserAwards().contains(expectedUserAward));
    }

    @Test
    public void testAssignEarlyBirdAward() throws Exception {
        User user = aUser().withFacebookId(FB_ID).build();
        String expectedAwardName = "Early Bird"; //TODO hardcoded

        Award expectedAward = anAward().withName(expectedAwardName).build();
        UserAward expectedUserAward = aUserAward().withAward(expectedAward).withUser(user).build();

        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(awardRepository.findByName(expectedAwardName)).thenReturn(expectedAward);

        Award testResult = awardService.assignEarlyBirdAward(FB_ID);

        assertNotNull(testResult);
        assertEquals(expectedAward, testResult);
        assertTrue(user.getUserAwards().contains(expectedUserAward));
    }

    @Test
    public void testAssignExploreAwards_oneHighLevelToWin_oneAlreadyWon4Times() throws Exception {
        Long opportunityId = new Double(Math.random()).longValue();
        Opportunity opportunity = anOpportunity()
                .withAnInterest(anInterest().wihtInterestCategory(InterestCategory.ART))
                .build();

        List<Award> awardsToWinList = new ArrayList<Award>();
        Award expectedAward = anAward().withId(-1L).withCategory(Award.AwardCategory.EXPLORE).withGroup(Award.AwardGroup.ACTIVE).withLevel(Award.AwardLevel.HIGH).build();
        Award awardAlreadyWon = anAward().withId(-2L).withCategory(Award.AwardCategory.EXPLORE).withGroup(Award.AwardGroup.ACTIVE).withLevel(Award.AwardLevel.LOW).build();

        UserAwardTestBuilder userAwardAlreadyWon = aUserAward().withAward(awardAlreadyWon).withTimesWon(5);

        User user = aUser()
                .withFacebookId(FB_ID)
                .withUserAward(userAwardAlreadyWon)
                .build();

        awardsToWinList.add(awardAlreadyWon);

        List<Award.AwardGroup> awardGroupList = new AwardGroupInterestCategoryMapper(opportunity.getInterests().getInterest()).getAwardGroupList();

        UserAwardPk userAwardAlreadyWonPk = new UserAwardPk(awardAlreadyWon, user);

        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(providerRestHandler.handleGetForEntity("/opportunities/{id}", Opportunity.class, opportunityId)).thenReturn(opportunity);
        when(awardRepository.findByCategoryAndLevelAndGroupIn(Award.AwardCategory.EXPLORE, Award.AwardLevel.LOW, awardGroupList)).thenReturn(awardsToWinList);

        when(userAwardRepository.findByPk(userAwardAlreadyWonPk)).thenReturn(userAwardAlreadyWon.withUser(user).build());
        when(awardRepository.findByGroupAndLevel(awardAlreadyWon.getGroup(), Award.AwardLevel.HIGH)).thenReturn(expectedAward);

        List<Award> awardsWon = awardService.assignExploreAwards(FB_ID, opportunityId);
        verify(userRepository).findByFacebookId(FB_ID);
        verify(providerRestHandler).handleGetForEntity("/opportunities/{id}", Opportunity.class, opportunityId);
        verify(awardRepository).findByCategoryAndLevelAndGroupIn(Award.AwardCategory.EXPLORE, Award.AwardLevel.LOW, awardGroupList);
        verify(awardRepository).findByGroupAndLevel(awardAlreadyWon.getGroup(), Award.AwardLevel.HIGH);

        assertEquals(1, awardsWon.size());
        assertTrue(awardsWon.contains(expectedAward));
    }

    @Test
    public void testAssignExploreAwards_oneLowLevelAwardToWin_noneWonBefore() throws Exception {
        Long opportunityId = new Double(Math.random()).longValue();
        Opportunity opportunity = anOpportunity()
                .withAnInterest(anInterest().wihtInterestCategory(InterestCategory.ART))
                .build();

        List<Award> awardToWinList = new ArrayList<Award>();
        Award awardToWin = anAward().withCategory(Award.AwardCategory.EXPLORE).withGroup(Award.AwardGroup.ART).withLevel(Award.AwardLevel.LOW).build();
        User user = aUser().withFacebookId(FB_ID).build();

        awardToWinList.add(awardToWin);

        List<Award.AwardGroup> awardGroupList = new AwardGroupInterestCategoryMapper(opportunity.getInterests().getInterest()).getAwardGroupList();

        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(providerRestHandler.handleGetForEntity("/opportunities/{id}", Opportunity.class, opportunityId)).thenReturn(opportunity);
        when(awardRepository.findByCategoryAndLevelAndGroupIn(Award.AwardCategory.EXPLORE, Award.AwardLevel.LOW, awardGroupList)).thenReturn(awardToWinList);

        for (Award award : awardToWinList) {
            UserAwardPk userAwardPk = new UserAwardPk(award, user);
            when(userAwardRepository.findByPk(userAwardPk)).thenReturn(aUserAward().withUser(user).withAward(award).build());
        }

        List<Award> awardsWon = awardService.assignExploreAwards(FB_ID, opportunityId);
        verify(userRepository).findByFacebookId(FB_ID);
        verify(providerRestHandler).handleGetForEntity("/opportunities/{id}", Opportunity.class, opportunityId);
        verify(awardRepository).findByCategoryAndLevelAndGroupIn(Award.AwardCategory.EXPLORE, Award.AwardLevel.LOW, awardGroupList);

        assertEquals(1, awardsWon.size());
        assertTrue(awardsWon.contains(awardToWin));
    }

    @Test
    public void testAssignExploreAwards_twoToWin_oneAlreadyWon() throws Exception {
        Long opportunityId = new Double(Math.random()).longValue();
        Opportunity opportunity = anOpportunity()
                .withAnInterest(anInterest().wihtInterestCategory(InterestCategory.ART))
                .build();

        List<Award> awardsToWinList = new ArrayList<Award>();
        Award expectedAward = anAward().withId(-1L).withCategory(Award.AwardCategory.EXPLORE).withGroup(Award.AwardGroup.ART).withLevel(Award.AwardLevel.LOW).build();
        Award awardAlreadyWon = anAward().withId(-2L).withCategory(Award.AwardCategory.EXPLORE).withGroup(Award.AwardGroup.ACTIVE).withLevel(Award.AwardLevel.LOW).build();
        User user = aUser().withFacebookId(FB_ID).withAward(awardAlreadyWon).build();

        awardsToWinList.add(expectedAward);
        awardsToWinList.add(awardAlreadyWon);

        List<Award.AwardGroup> awardGroupList = new AwardGroupInterestCategoryMapper(opportunity.getInterests().getInterest()).getAwardGroupList();

        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(providerRestHandler.handleGetForEntity("/opportunities/{id}", Opportunity.class, opportunityId)).thenReturn(opportunity);
        when(awardRepository.findByCategoryAndLevelAndGroupIn(Award.AwardCategory.EXPLORE, Award.AwardLevel.LOW, awardGroupList)).thenReturn(awardsToWinList);

        UserAward expectedUserAward = aUserAward().withUser(user).withAward(expectedAward).withTimesWon(1).build();
        when(userAwardRepository.findByPk(new UserAwardPk(expectedAward, user))).thenReturn(expectedUserAward);
        when(userAwardRepository.findByPk(new UserAwardPk(awardAlreadyWon, user))).thenReturn(aUserAward().withUser(user).withAward(awardAlreadyWon).withTimesWon(2).build());

        List<Award> awardsWon = awardService.assignExploreAwards(FB_ID, opportunityId);
        verify(userRepository).findByFacebookId(FB_ID);
        verify(providerRestHandler).handleGetForEntity("/opportunities/{id}", Opportunity.class, opportunityId);
        verify(awardRepository).findByCategoryAndLevelAndGroupIn(Award.AwardCategory.EXPLORE, Award.AwardLevel.LOW, awardGroupList);

        assertEquals(1, awardsWon.size());
        assertTrue(awardsWon.contains(expectedAward));
        assertTrue(user.getUserAwards().contains(expectedUserAward));
    }

    @Test
    public void testAssignGoodIntentionsAward_firstOpportunityJoined_awardAssigned() throws Exception {
        User user = aUser().withFacebookId(FB_ID).build();
        List<UserOpportunity> userOpportunities = Lists.newArrayList(new UserOpportunity(user, -14254L));
        String expectedAwardName = "Good Intentions"; //TODO hardcoded

        Award expectedAward = anAward().withName(expectedAwardName).build();
        UserAward expectedUserAward = aUserAward().withAward(expectedAward).withUser(user).build();

        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(userOpportunityRepository.findByUser(user)).thenReturn(userOpportunities);
        when(awardRepository.findByName(expectedAwardName)).thenReturn(expectedAward);

        Award testResult = awardService.assignGoodIntentionsAward(FB_ID);
        verify(userOpportunityRepository).findByUser(user);
        verify(awardRepository).findByName(expectedAwardName);

        assertNotNull(testResult);
        assertEquals(expectedAward, testResult);
        assertTrue(user.getUserAwards().contains(expectedUserAward));
    }

    @Test
    public void testAssignGoodIntentionsAward_secondOpportunityJoined_awardAssigned() throws Exception {
        final User user = aUser().withFacebookId(FB_ID).build();
        final List<UserOpportunity> userOpportunities = Lists.newArrayList(
                aUserOpportunity().withUser(user).withOpportunityId(-4141214L).build(),
                aUserOpportunity().withUser(user).withOpportunityId(-1513232L).build()
        );
        final String expectedAwardName = "Good Intentions";   //TODO hardcoded

        final Award expectedAward = null;

        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(userOpportunityRepository.findByUser(user)).thenReturn(userOpportunities);
        when(awardRepository.findByName(expectedAwardName)).thenReturn(expectedAward);

        final Award testResult = awardService.assignGoodIntentionsAward(FB_ID);
        verify(userOpportunityRepository).findByUser(user);
        verify(awardRepository, times(0)).findByName(expectedAwardName);

        assertNull(testResult);
        assertNull(expectedAward);
        assertEquals(0, user.getUserAwards().size());
    }

    @Test
    public void testAssignNewbieAward() throws Exception {
        User user = aUser().build();
        String expectedAwardName = "Newbie";  //TODO hardcoded

        Award expectedAward = anAward().withName(expectedAwardName).build();
        UserAward expectedUserAward = aUserAward().withAward(expectedAward).withUser(user).build();

        when(awardRepository.findByName(expectedAwardName)).thenReturn(expectedAward);

        Award testResult = awardService.assignNewbieAward(user);

        assertNotNull(testResult);
        assertEquals(expectedAward, testResult);
        assertTrue(user.getUserAwards().contains(expectedUserAward));
    }

    @Test
    public void testAssignPictureAward() throws Exception {
        User user = aUser().withFacebookId(FB_ID).build();
        String expectedAwardName = "Super Snapper";   //TODO hardcoded

        Award expectedAward = anAward().withName(expectedAwardName).build();
        UserAward expectedUserAward = aUserAward().withAward(expectedAward).withUser(user).build();

        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(awardRepository.findByName(expectedAwardName)).thenReturn(expectedAward);

        Award testResult = awardService.assignPictureAward(FB_ID);

        assertNotNull(testResult);
        assertEquals(expectedAward, testResult);
        assertTrue(user.getUserAwards().contains(expectedUserAward));
    }

    @Test
    public void testAssignSocialNetworkAward_highLevelAwardAssigned() throws Exception {
        String allReadyAssignedAwardName = "Spread the Word";   //TODO hardcoded
        Award allReadyAssignedAward = anAward().withName(allReadyAssignedAwardName).build();
        User user = aUser()
                .withFacebookId(FB_ID)
                .withUserAward(aUserAward().withAward(allReadyAssignedAward).withTimesWon(4))
                .build();

        String expectedAwardName = "Spread the Love";   //TODO hardcoded

        Award expectedAward = anAward().withName(expectedAwardName).build();

        UserAward expectedUserAward = aUserAward().withAward(expectedAward).withUser(user).build();

        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(awardRepository.findByName(expectedAwardName)).thenReturn(expectedAward);
        when(awardRepository.findByName(allReadyAssignedAwardName)).thenReturn(allReadyAssignedAward);

        Award testResult = awardService.assignSocialNetworkAward(FB_ID);

        assertNotNull(testResult);
        assertEquals(expectedAward, testResult);
        assertTrue(user.getUserAwards().contains(expectedUserAward));
    }

    @Test
    public void testAssignSocialNetworkAward_lowLevelAwardAssigned() throws Exception {
        User user = aUser().withFacebookId(FB_ID).build();
        String expectedAwardName = "Spread the Word";   //TODO hardcoded

        Award expectedAward = anAward().withName(expectedAwardName).build();
        UserAward expectedUserAward = aUserAward().withAward(expectedAward).withUser(user).build();

        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(awardRepository.findByName(expectedAwardName)).thenReturn(expectedAward);

        Award testResult = awardService.assignSocialNetworkAward(FB_ID);

        assertNotNull(testResult);
        assertEquals(expectedAward, testResult);
        assertTrue(user.getUserAwards().contains(expectedUserAward));
    }

    @Test
    public void testGetAllAwardsOfUser_userHasNoAwards() throws Exception {
        User user = aUser().withFacebookId(FB_ID).build();

        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);
        when(awardRepository.findAll()).thenReturn(compileHighLowNoneAwards());

        List<UserAward> allAwardsOfUser = awardService.getAllAwardsOfUser(FB_ID);
        verify(userRepository).findByFacebookId(FB_ID);
        verify(awardRepository).findAll();

        assertEquals(2, allAwardsOfUser.size());

        for (UserAward award : allAwardsOfUser) {
            assertEquals(Award.DEFAULT_ICON, award.getAward().getImageResourceId());
        }
    }

    @Test
    public void testGetAllAwardsOfUser_userHasOneAward() throws Exception {
        final User user = aUser().withFacebookId(FB_ID).build();
        final Award anAward = anAward().withName("high_award").withLevel(Award.AwardLevel.HIGH).withGroup(Award.AwardGroup.ART).build();
        final UserAward userAward =aUserAward()
                .withAward(anAward)
                .withUser(user)
                .build();

        user.assignAward(anAward);

        when(userRepository.findByFacebookId(FB_ID)).thenReturn(user);

        final Award noneLevelAward = anAward().withId(-3L).withName("none_award").withLevel(Award.AwardLevel.NONE).build();
        UserAward noneLevelUserAward = aUserAward()
                .withAward(noneLevelAward)
                .withUser(null)
                .build();
        when(awardRepository.findAll()).thenReturn(compileHighLowNoneAwards());

        List<UserAward> allAwardsOfUser = awardService.getAllAwardsOfUser(FB_ID);
        verify(userRepository).findByFacebookId(FB_ID);
        verify(awardRepository).findAll();

        assertEquals(2, allAwardsOfUser.size());
        assertTrue(allAwardsOfUser.contains(userAward));
        assertTrue(allAwardsOfUser.contains(noneLevelUserAward));
    }

    private void assignInviteFriendAwards(int nbrOfInvites, boolean highAwardExpected) {
        User user = aUser().withFacebookId(FB_ID).build();
        Award lowAward = anAward().withCategory(Award.AwardCategory.SHARE).withGroup(Award.AwardGroup.INVITE).withLevel(Award.AwardLevel.LOW).build();
        Award highAward = anAward().withCategory(Award.AwardCategory.SHARE).withGroup(Award.AwardGroup.INVITE).withLevel(Award.AwardLevel.HIGH).build();
        Long opportunityId = 1L;

        UserAward userAward = aUserAward().withAward(lowAward).withUser(user).withTimesWon(1).build();
        List<UserAward> nextUserAwards = new ArrayList<UserAward>();
        List<UserInvite> userInvites = new ArrayList<UserInvite>();
        for (int i = 1; i <= nbrOfInvites; i++) {
            UserAward nextUserAward = aUserAward().withAward(userAward.getAward()).withUser(userAward.getUser()).withTimesWon(userAward.getTimesWon() + i).build();
            nextUserAwards.add(nextUserAward);

            userInvites.add(aUserInvite().build());
        }

        when(userRepository.findByFacebookId(user.getFacebookId())).thenReturn(user);
        when(awardRepository.findByCategoryAndLevelAndGroup(lowAward.getCategory(), lowAward.getLevel(), lowAward.getGroup())).thenReturn(lowAward);
        when(userInviteRepository.findByUserAndOpportunityId(user, opportunityId)).thenReturn(userInvites);
        when(awardRepository.findByGroupAndLevel(highAward.getGroup(), highAward.getLevel())).thenReturn(highAward);
        when(userAwardRepository.findByPk(new UserAwardPk(lowAward, user))).thenReturn(userAward, (UserAward[]) nextUserAwards.toArray(new UserAward[nextUserAwards.size()]));

        List<Award> expectedAwards = Lists.newArrayList(lowAward);
        if (highAwardExpected) {
            expectedAwards.add(highAward);
        }
        List<Award> actualAwards = awardService.assignInviteFriendAwards(user.getFacebookId(), opportunityId);
        assertEquals(expectedAwards, actualAwards);
    }

    private List<Award> compileHighLowNoneAwards() {
        List<Award> awards = Lists.newArrayList();
        awards.add(anAward().withId(-1L).withName("high_award").withLevel(Award.AwardLevel.HIGH).withGroup(Award.AwardGroup.ART).build());
        awards.add(anAward().withId(-2L).withName("low_award").withLevel(Award.AwardLevel.LOW).withGroup(Award.AwardGroup.ART).build());
        awards.add(anAward().withId(-3L).withName("none_award").withLevel(Award.AwardLevel.NONE).build());

        return awards;
    }
}