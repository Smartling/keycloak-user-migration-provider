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
package com.smartling.keycloak.provider;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserFederationProvider;
import org.keycloak.models.UserFederationProviderFactory;
import org.keycloak.models.UserFederationProviderModel;
import org.keycloak.models.UserFederationSyncResult;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * Remote user federation provider factory.
 *
 * @author Scott Rossillo
 */
public class RemoteUserFederationProviderFactory implements UserFederationProviderFactory {

    public static final String PROVIDER_NAME = "User Migration API Provider";
    public static final String PROP_USER_VALIDATION_URL = "User Validation Host Base URI";

    private static final Logger LOG = Logger.getLogger(RemoteUserFederationProviderFactory.class);

    @Override
    public UserFederationProvider getInstance(KeycloakSession session, UserFederationProviderModel model) {
        return new RemoteUserFederationProvider(session, model, model.getConfig().get(PROP_USER_VALIDATION_URL));
    }

    @Override
    public Set<String> getConfigurationOptions() {
        LOG.warn("Returning configuration options");
        return Collections.singleton(PROP_USER_VALIDATION_URL);
    }

    @Override
    public String getId() {
        return PROVIDER_NAME;
    }

    @Override
    public UserFederationProvider create(KeycloakSession session) {
        return null;
    }

    @Override
    public void init(Scope config) {
        // no-op
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // no-op
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public UserFederationSyncResult syncAllUsers(KeycloakSessionFactory sessionFactory, String realmId, UserFederationProviderModel model)
    {
        throw new UnsupportedOperationException("This federation provider doesn't support syncAllUsers()");
    }

    @Override
    public UserFederationSyncResult syncChangedUsers(KeycloakSessionFactory sessionFactory, String realmId, UserFederationProviderModel model, Date lastSync)
    {
        throw new UnsupportedOperationException("This federation provider doesn't support syncChangedUsers()");
    }
}
