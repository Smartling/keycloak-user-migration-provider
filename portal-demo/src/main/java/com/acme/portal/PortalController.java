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
package com.acme.portal;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Simple controller demonstrating the migration of user data from the legacy
 * user store to Keycloak, on demand.
 *
 * @author Scott Rossillo
 */
@Controller
public class PortalController
{
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String handleWelcomeRequest()
    {
        return "home";
    }

    @RequestMapping(value = "/info/user", method = RequestMethod.GET)
    public String handlerUserInfoRequest(Model model)
    {
        KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        IDToken token = authentication.getAccount().getKeycloakSecurityContext().getIdToken();

        model.addAttribute("token", token);
        model.addAttribute("claims", token.getOtherClaims());

        return "info";
    }

    @ModelAttribute("serviceName")
    public String populateServiceName()
    {
        return "Demo Portal";
    }
}
