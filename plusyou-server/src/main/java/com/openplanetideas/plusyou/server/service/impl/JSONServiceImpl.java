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
import com.openplanetideas.plusyou.server.domain.User;
import com.openplanetideas.plusyou.server.repository.UserRepository;
import com.openplanetideas.plusyou.server.service.JSONService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class JSONServiceImpl implements JSONService {

    private static final Logger LOG = LoggerFactory.getLogger(JSONServiceImpl.class);

    @Inject
    private UserRepository userRepository;

    @Override
    public List<String> extractFriendIdsFromJson(String jsonFriendIds) {
        try {
            JSONObject json = new JSONObject(jsonFriendIds);
            JSONArray friends = json.getJSONArray("friends");

            List<String> ids = new ArrayList<String>();
            for (int i = 0; i < friends.length(); i++) {
                JSONObject friend = friends.getJSONObject(i);
                ids.add(friend.getString("id"));
            }
            return ids;
        }
        catch (JSONException e) {
            LOG.error("Error parsing jsonFriendIds", e);
            return Lists.newArrayList();
        }
    }

    @Override
    public List<User> extractUsersFromJson(String jsonFriendList) {
        try {
            LOG.info("Parsing JSON: " + jsonFriendList);
            JSONObject facebookFriendsObj = new JSONObject(jsonFriendList);
            JSONArray facebookFriendArrayObj = facebookFriendsObj.getJSONArray("data");
            List<User> users = new ArrayList<User>(facebookFriendArrayObj.length());
            for (int i = 0; i < facebookFriendArrayObj.length(); i++) {
                JSONObject friendObj = facebookFriendArrayObj.getJSONObject(i);
                User facebookFriend = userRepository.findByFacebookId(friendObj.getString("id"));

                if (facebookFriend != null) {
                    users.add(facebookFriend);
                }
            }
            return users;
        } catch (JSONException e) {
            LOG.error("Error parsing jsonFriendList", e);
            return Lists.newArrayList();
        }
    }
}