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
import com.openplanetideas.plusyou.provider.Interest;
import com.openplanetideas.plusyou.provider.Opportunity;
import com.openplanetideas.plusyou.server.common.AwardGroupInterestCategoryMapper;
import com.openplanetideas.plusyou.server.domain.Award;
import com.openplanetideas.plusyou.server.domain.User;
import com.openplanetideas.plusyou.server.domain.UserAward;
import com.openplanetideas.plusyou.server.domain.UserAwardPk;
import com.openplanetideas.plusyou.server.domain.UserInvite;
import com.openplanetideas.plusyou.server.domain.UserOpportunity;
import com.openplanetideas.plusyou.server.service.AwardService;
import com.openplanetideas.plusyou.server.service.common.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AwardServiceImpl extends AbstractService implements AwardService {

    private static final int HIGH_AWARD_CONDITION = 5;
    private static final Date LAUNCH_DATE_PLUS_28 = setLaunchDate();

    private static Date setLaunchDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 1, 1);
        calendar.add(Calendar.DAY_OF_MONTH, 28);
        return calendar.getTime();
    }

    @Override
    @Transactional
    public List<Award> assignBuddyAwards(final String facebookId, final Long opportunityId, final String jsonFriendList) {
        User user = userRepository.findByFacebookId(facebookId);
        List<User> fbFriends = jsonService.extractUsersFromJson(jsonFriendList);

        final String buddyUpAwardName = "Buddy Up";
        final String trueFriendsAwardName = "True Friends";
        Award buddyUpAward = null;

        for (User friend : fbFriends) {
            UserOpportunity userOpportunity = userOpportunityRepository.findByUserAndOpportunityId(friend, opportunityId);
            if (userOpportunity != null) {
                buddyUpAward = assignAwardToUser(user, buddyUpAwardName, true);
                assignAwardToUser(friend, buddyUpAwardName, true);
                assignHighAward(trueFriendsAwardName, buddyUpAward, friend);
            }
        }

        List<Award> awards = new ArrayList<Award>(2);
        UserAward userAward = userAwardRepository.findByPk(new UserAwardPk(buddyUpAward, user));
        if (userAward != null && userAward.getTimesWon() == 1) {
            awards.add(buddyUpAward);
        }

        Award award = assignHighAward(trueFriendsAwardName, buddyUpAward, user);
        if (award != null) {
            awards.add(award);
        }

        return awards;
    }

    @Override
    @Transactional
    public Award assignCheckInAward(final String facebookId) {
        User user = getUser(facebookId);
        List<UserOpportunity> userOpportunities = userOpportunityRepository.findByUserAndCheckInDateNotNull(user);

        int checkedInOpportunityCount = 0;
        for (UserOpportunity userOpportunity : userOpportunities) {
            if (userOpportunity.getCheckInDate() != null) {
                checkedInOpportunityCount++;
            }
        }

        switch (checkedInOpportunityCount) {
            case 1:
                return assignGoodDeedAward(user);
            case 5:
                return assignSuperUAward(user);
            case 10:
                return assignAmazingDeedsAward(user);
            default:
                return null;
        }
    }

    @Override
    @Transactional
    public Award assignEarlyBirdAward(final String facebookId) {
        User user = getUser(facebookId);
        return assignAwardToUser(user, "Early Bird"); //TODO unhardcode (name as enum)
    }

    @Override
    @Transactional
    public List<Award> assignExploreAwards(final String facebookId, final Long opportunityId) {
        Opportunity opportunity = providerRestHandler.handleGetForEntity("/opportunities/{id}", Opportunity.class, opportunityId);
        List<Interest> interests = opportunity.getInterests().getInterest();
        List<Award.AwardGroup> interestsAsAwardGroupList = new AwardGroupInterestCategoryMapper(interests).getAwardGroupList();

        return assignVolunteeringAwards(facebookId, Award.AwardCategory.EXPLORE, Award.AwardLevel.LOW, interestsAsAwardGroupList, HIGH_AWARD_CONDITION);
    }

    @Override
    @Transactional
    public Award assignFoundingMemberAward(final User user) {
        Date now = new Date();
        if (now.before(LAUNCH_DATE_PLUS_28) && userRepository.count() < 25) {
            return assignAwardToUser(user, "Founding Member");
        }

        return null;
    }

    @Override
    @Transactional
    public Map<User, Award> assignFriendActiveAwards(final Long opportunityId, final List<String> friendIds) {
        Award awardToWin = awardRepository.findByCategoryAndLevelAndGroup(Award.AwardCategory.SHARE, Award.AwardLevel.LOW, Award.AwardGroup.ACTIVE);

        Map<User, Award> friendAwards = new HashMap<User, Award>();
        for (String friendId : friendIds) {
            User friend = getUser(friendId);
            if (friend != null) {
                UserOpportunity friendOpportunity = userOpportunityRepository.findByUserAndOpportunityId(friend, opportunityId);
                if (friendOpportunity != null) {
                    friend.assignAward(awardToWin);
                    Award award = determineAwardWon(friend, awardToWin, HIGH_AWARD_CONDITION);
                    friendAwards.put(friend, award);
                }
            }
        }
        return friendAwards;
    }

    @Override
    @Transactional
    public Award assignGoodIntentionsAward(final String facebookId) {
        User user = getUser(facebookId);
        List<UserOpportunity> userOpportunities = userOpportunityRepository.findByUser(user);
        if (userOpportunities.size() == 1) {
            return assignAwardToUser(user, "Good Intentions"); //TODO remove hardcoded name (enum name?)
        }
        return null;
    }

    @Override
    @Transactional
    public List<Award> assignInviteFriendAwards(final String facebookId, final Long opportunityId) {
        User user = getUser(facebookId);
        Award awardToWin = awardRepository.findByCategoryAndLevelAndGroup(Award.AwardCategory.SHARE, Award.AwardLevel.LOW, Award.AwardGroup.INVITE);

        List<Award> awardsWon = new ArrayList<Award>();
        List<UserInvite> userInvites = userInviteRepository.findByUserAndOpportunityId(user, opportunityId);
        for (UserInvite userInvite : userInvites) {
            if (!userInvite.isProcessedForAward()) {
                user.assignAward(awardToWin);
                Award awardWon = determineAwardWon(user, awardToWin, HIGH_AWARD_CONDITION);
                if (awardWon != null) {
                    awardsWon.add(awardWon);
                }
                userInvite.setProcessedForAward(true);
            }
        }
        return awardsWon;
    }

    @Override
    @Transactional
    public Award assignNewbieAward(final User user) {
        return assignAwardToUser(user, "Newbie"); //TODO unhardcode (name as enum)
    }

    @Override
    @Transactional
    public Award assignPictureAward(final String facebookId) {
        return assignSuperSnapperAward(getUser(facebookId));
    }

    @Override
    @Transactional
    public Award assignSocialNetworkAward(final String facebookId) {
        User user = getUser(facebookId);
        Award award = assignAwardToUser(user, "Spread the Word", true);

        final List<Award> awardList = extractAwardList(user.getUserAwards());
        UserAward socialNetworkAward = user.getUserAwards().get(awardList.indexOf(award));

        if (socialNetworkAward.getTimesWon() == 1) {
            return award;
        } else if (socialNetworkAward.getTimesWon() == 5) {
            return assignAwardToUser(user, "Spread the Love");
        }

        return null;
    }

    @Override
    @Transactional
    //TODO Refactor
    public Award assignTotallyCommittedAward(final String facebookId) {
        User user = getUser(facebookId);
        List<UserOpportunity> userOpportunities = userOpportunityRepository.findByUser(user);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getRegistrationDate());
        calendar.add(Calendar.MONTH, 6);
        Date regDatePlus6Months = calendar.getTime();

        if (regDatePlus6Months.after(new Date()) && userOpportunities.size() >= 2) {   //TODO && sharedAwards >= 1
            return assignAwardToUser(user, "Totally Committed");
        }

        return null;
    }

    @Override
    public List<UserAward> getAllAwardsOfUser(final String facebookId) {
        List<UserAward> userAwards = getUser(facebookId).getUserAwards();
        List<UserAward> allAwards = setIconsToDefault(awardRepository.findAll());
        userAwards = keepAwardsOfLevel(userAwards, Award.AwardLevel.HIGH);
        allAwards = keepAwardsOfLevel(allAwards, Award.AwardLevel.LOW);

        allAwards = mergeAwardsWithAchievedAwards(allAwards, userAwards);
        allAwards = keepAwardsOfLevel(allAwards, Award.AwardLevel.HIGH);

        return allAwards;
    }

    private Award assignAmazingDeedsAward(final User user) {
        return assignAwardToUser(user, "Amazing Deeds"); //TODO unhardcode (name as enum)
    }

    private Award assignAwardToUser(final User user, final String awardName) {
        return assignAwardToUser(user, awardName, false);
    }

    private Award assignAwardToUser(final User user, final String awardName, final boolean allowDuplicateAwards) {
        Award award = awardRepository.findByName(awardName);
        if (allowDuplicateAwards || !user.getAwards().contains(award)) {
            user.assignAward(award);
            return award;
        }
        return null;
    }

    private Award assignGoodDeedAward(final User user) {
        return assignAwardToUser(user, "Good Deed"); //TODO unhardcode (name as enum)
    }

    private Award assignHighAward(final String trueFriendsAwardName, final Award award, final User user) {
        UserAward userAward = userAwardRepository.findByPk(new UserAwardPk(award, user));
        if (userAward != null && userAward.getTimesWon() >= HIGH_AWARD_CONDITION) {
            return assignAwardToUser(user, trueFriendsAwardName);
        }
        return null;
    }

    private Award assignSuperSnapperAward(User user) {
        return assignAwardToUser(user, "Super Snapper"); //TODO unhardcode (name as enum)
    }

    private Award assignSuperUAward(User user) {
        return assignAwardToUser(user, "Super U!"); //TODO unhardcode (name as enum)
    }

    private List<Award> assignVolunteeringAwards(String facebookId, Award.AwardCategory category, Award.AwardLevel level, List<Award.AwardGroup> groups, int highAwardCondition) {
        User user = getUser(facebookId);
        List<Award> awardsToWin = awardRepository.findByCategoryAndLevelAndGroupIn(category, level, groups);
        return determineAwardsWon(user, awardsToWin, highAwardCondition);
    }

    private Award determineAwardWon(User user, Award awardToWin, final int highAwardCondition) {
        UserAwardPk userAwardPk = new UserAwardPk(awardToWin, user);
        UserAward userAward = userAwardRepository.findByPk(userAwardPk);

        int timesWon = userAward.getTimesWon();
        if (timesWon < highAwardCondition) {
            return timesWon == 1 ? awardToWin : null;
        } else {
            Award highAward = awardRepository.findByGroupAndLevel(awardToWin.getGroup(), Award.AwardLevel.HIGH);
            user.assignAward(highAward);
            return timesWon == highAwardCondition ? highAward : null;
        }
    }

    private List<Award> determineAwardsWon(User user, List<Award> awardsToWin, int highAwardCondition) {
        List<Award> newAwardsWon = new ArrayList<Award>(awardsToWin.size());

        for (Award lowAward : awardsToWin) {
            user.assignAward(lowAward);
            UserAward userAward = userAwardRepository.findByPk(new UserAwardPk(lowAward, user));
            if (userAward.getTimesWon() == 1) {
                newAwardsWon.add(lowAward);
            } else if (userAward.getTimesWon() == highAwardCondition) {
                Award highAward = awardRepository.findByGroupAndLevel(lowAward.getGroup(), Award.AwardLevel.HIGH);
                user.assignAward(highAward);
                newAwardsWon.add(highAward);
            }
        }

        return newAwardsWon;
    }

    private List<Award> extractAwardList(List<UserAward> userAwards) {
        List<Award> awards = new ArrayList<Award>(userAwards.size());
        for (UserAward userAward : userAwards) {
            awards.add(userAward.getAward());
        }
        return awards;
    }

    private User getUser(String facebookId) {
        return userRepository.findByFacebookId(facebookId);
    }

    private List<UserAward> keepAwardsOfLevel(List<UserAward> userAwards, final Award.AwardLevel level) {
        Map<Award.AwardGroup, List<UserAward>> awardGroupListMap = mapAwardsWithAwardGroup(userAwards);
        List<UserAward> awardsToReturn = new ArrayList<UserAward>(userAwards.size());
        for (Award.AwardGroup group : awardGroupListMap.keySet()) {
            List<UserAward> awardsOfGroup = awardGroupListMap.get(group);
            List<UserAward> filteredAwards = new ArrayList<UserAward>(awardsOfGroup);
            if (awardsOfGroup.size() > 1) {
                for (UserAward userAward : awardsOfGroup) {
                    if (userAward.getAward().getLevel() != Award.AwardLevel.NONE && userAward.getAward().getLevel() != level) {
                        filteredAwards.remove(userAward);
                    }
                }
            }
            awardsToReturn.addAll(filteredAwards);
        }
        return awardsToReturn;
    }

    private Map<Award.AwardGroup, List<UserAward>> mapAwardsWithAwardGroup(List<UserAward> userAwards) {
        Map<Award.AwardGroup, List<UserAward>> userAwardMap = new HashMap<Award.AwardGroup, List<UserAward>>();
        for (UserAward userAward : userAwards) {
            List<UserAward> awardsOfGroup = userAwardMap.get(userAward.getAward().getGroup());
            if (awardsOfGroup == null) {
                awardsOfGroup = new ArrayList<UserAward>();
                awardsOfGroup.add(userAward);
            } else {
                awardsOfGroup.add(userAward);
            }
            userAwardMap.put(userAward.getAward().getGroup(), awardsOfGroup);
        }
        return userAwardMap;
    }

    private List<UserAward> mergeAwardsWithAchievedAwards(final List<UserAward> allAwards, final List<UserAward> achievedAwards) {
        Set<UserAward> mergedAwards = new HashSet<UserAward>(achievedAwards);
        for (UserAward award : allAwards) {
            mergedAwards.add(award);
        }
        return Lists.newArrayList(mergedAwards);
    }

    private List<UserAward> setIconsToDefault(List<Award> awards) {
        List<UserAward> userAwards = Lists.newArrayList();

        for (Award award : awards) {
            award.setImageResourceId(Award.DEFAULT_ICON);
            userAwards.add(new UserAward(null, award));
        }

        return userAwards;
    }
}