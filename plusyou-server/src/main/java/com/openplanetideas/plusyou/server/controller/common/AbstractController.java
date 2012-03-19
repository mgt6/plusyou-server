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

package com.openplanetideas.plusyou.server.controller.common;

import com.openplanetideas.plusyou.server.repository.FacebookPostRepository;
import com.openplanetideas.plusyou.server.repository.UserOpportunityRepository;
import com.openplanetideas.plusyou.server.repository.UserRepository;
import com.openplanetideas.plusyou.server.service.JSONService;
import com.openplanetideas.plusyou.server.service.UserService;
import com.openplanetideas.plusyou.server.webservice.ProviderRestHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.social.NotAuthorizedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.inject.Inject;

@Controller
public abstract class AbstractController {

    public static final String FACEBOOK_USER_ID_NOT_FOUND_IN_USER_TABLE = "facebookUserId not found in User table";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

    @Inject
    protected UserRepository userRepository;

    @Inject
    protected UserOpportunityRepository userOpportunityRepository;

    @Inject
    protected FacebookPostRepository facebookPostRepository;

    @Inject
    protected UserService userService;

    @Inject
    protected JSONService jsonService;

    @Inject
    protected ProviderRestHandler providerRestHandler;

    @ExceptionHandler
    public ResponseEntity exceptionHandler(RuntimeException e) {
        LOG.error(ExceptionUtils.getRootCauseMessage(e), e);

        if (e instanceof NotAuthorizedException || e instanceof AccessDeniedException) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}