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
import com.openplanetideas.plusyou.server.repository.AwardRepository;
import com.openplanetideas.plusyou.server.repository.UserRepository;
import com.openplanetideas.plusyou.server.service.AwardService;
import com.openplanetideas.plusyou.server.service.UserService;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.social.NotAuthorizedException;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.ResponseExtractor;

import java.io.IOException;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.openplanetideas.plusyou.server.domain.UserTestBuilder.aUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test_properties-config.xml"})
public class UserControllerTest {

    private static final String FACEBOOK_ID = "1L";
    private static final String PASSPRASE = "abcdef";
    private static final String HASH_ALGORITM = "SHA-256";

    @Value("${facebook_appId}")
    public String facebookAppId;

    @Value("${facebook_appAccessToken}")
    public String facebookAppAccessToken;

    @Mock
    protected UserService userService;

    @Mock
    private AwardService awardService;

    @Mock
    private UserRepository userRepo;

    @Mock
    private AwardRepository awardRepository;

    @InjectMocks
    private UserController controller;

    private String facebookTestAccountId;
    private String facebookTestAccountAccessToken;

    @Before
    public void initMockito() throws JSONException {
        MockitoAnnotations.initMocks(this);
        createFacebookTestUser();
    }

    @After
    public void cleanUp() {
        cleanUpFacebookTestUser();

    }

    @Test
    public void deleteAllUserData_returns_OK() {
        ResponseEntity response = controller.deleteAllUserData(FACEBOOK_ID);
        verify(userService).deleteAllUserData(FACEBOOK_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void saveWhenFacebookIdAlreadyExists() throws Exception {
        User user = aUser().withFacebookId(facebookTestAccountId).withPassphrase(facebookTestAccountAccessToken).build();
        when(userRepo.findByFacebookId(facebookTestAccountId)).thenReturn(user);
        ResponseEntity response = controller.createOrUpdateUser(facebookTestAccountAccessToken, facebookTestAccountId, hashedAccessToken());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(user.getRegistrationDate());
    }

    @Test
    public void saveWhenFacebookIdDoesNotExists() throws Exception {
        when(userRepo.findByFacebookId(FACEBOOK_ID)).thenReturn(null);

        ResponseEntity response = controller.createOrUpdateUser(facebookTestAccountAccessToken, facebookTestAccountId, hashedAccessToken());
        verify(userService).createNewUser(Matchers.<String>any(), Matchers.<String>any());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = NotAuthorizedException.class)
    public void throwExceptionWhenFacebookInvalidAccessToken() throws Exception {
        ResponseEntity response = controller.createOrUpdateUser("", FACEBOOK_ID, PASSPRASE);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private void cleanUpFacebookTestUser() {
        FacebookTemplate facebookTemplate = new FacebookTemplate();
        StringBuilder url = new StringBuilder("https://graph.facebook.com/")
                .append(facebookTestAccountId)
                .append("?method=delete&access_token=")
                .append(facebookTestAccountAccessToken);
        facebookTemplate.restOperations().execute(url.toString(), HttpMethod.GET, null, null);
    }

    private void createFacebookTestUser() throws JSONException {
        FacebookTemplate facebookTemplate = new FacebookTemplate();
        StringBuilder url = new StringBuilder("https://graph.facebook.com/")
                .append(facebookAppId)
                .append("/accounts/test-users?installed=true&name=Test&permissions=read_stream&method=post&access_token=")
                .append(facebookAppAccessToken);
        String testAccountResponse = facebookTemplate.restOperations().execute(url.toString(), HttpMethod.GET, null, new ResponseExtractor<String>() {

            @Override
            public String extractData(ClientHttpResponse response) throws IOException {
                StringWriter writer = new StringWriter();
                IOUtils.copy(response.getBody(), writer, "UTF-8");
                return writer.toString();
            }
        });

        JSONObject testAccount = new JSONObject(testAccountResponse);
        facebookTestAccountId = testAccount.getString("id");
        facebookTestAccountAccessToken = testAccount.getString("access_token");
    }

    private String hashedAccessToken() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITM);
            messageDigest.update(facebookTestAccountAccessToken.getBytes());

            byte[] digest = messageDigest.digest();
            return String.copyValueOf(Hex.encodeHex(digest));
        } catch (NoSuchAlgorithmException e) {
            return null;
    }
    }
}