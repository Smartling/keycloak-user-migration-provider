/*
 * Copyright 2015 Smartling, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acme.legacy.app.service;

import com.acme.legacy.app.entity.User;
import com.acme.legacy.app.manager.AccessDeniedException;
import com.acme.legacy.app.manager.UserManager;
import com.smartling.keycloak.federation.FederatedUserModel;
import com.smartling.keycloak.federation.FederatedUserService;
import com.smartling.keycloak.federation.UserCredentialsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Simple JAX-RS based legacy user service.
 */
@Component
@Path("/migration")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LegacyUserService implements FederatedUserService
{
    private static final Logger LOG = LoggerFactory.getLogger(LegacyUserService.class);

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private UserManager userManager;

    public LegacyUserService()
    {
        LOG.warn("creating me");
    }

    @Override
    public FederatedUserModel getUserDetails(String username)
    {
        User user = userManager.findByEmail(username);
        return conversionService.convert(user, FederatedUserModel.class);
    }

    @Override
    public Response validateUserExists(String username)
    {
        User user = userManager.findByEmail(username);
        Status status = user != null ? Status.OK : Status.NOT_FOUND;
        return Response.status(status).entity("").build();
    }

    @Override
    public Response validateLogin(String username, UserCredentialsDto credentials)
    {
        Status status = Status.OK;

        try
        {
            userManager.login(username, credentials.getPassword());
            LOG.info("User {} login valid", username);
        }
        catch (AccessDeniedException ex)
        {
            status = Status.UNAUTHORIZED;
            LOG.info("User {} login failed", username);
        }

        return Response.status(status).entity("").build();
    }
}

