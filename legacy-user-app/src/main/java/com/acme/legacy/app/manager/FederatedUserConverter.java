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
package com.acme.legacy.app.manager;

import com.acme.legacy.app.entity.User;
import com.smartling.keycloak.federation.FederatedUserModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts legacy a {@link User} into a {@link FederatedUserModel}.
 *
 * @author Scott Rossillo
 */
@Component
public class FederatedUserConverter implements Converter<User,FederatedUserModel>
{
    @Override
    public FederatedUserModel convert(User source)
    {
        if (source == null)
            return null;

        FederatedUserModel model = new FederatedUserModel();
        Map<String,List<String>> attributes = new HashMap<>();

        model.setAttributes(attributes);

        model.setUsername(source.getEmail());
        model.setEmail(source.getEmail());
        model.setFirstName(source.getFirstName());
        model.setLastName(source.getLastName());

        model.setEnabled(source.isEnabled());
        model.setEmailVerified(source.isEmailVerified());

        // map other data to attributes
        attributes.put("title", Collections.singletonList(source.getTitle()));

        // map roles
        model.setRoles(source.getRoles());

        return model;
    }
}
