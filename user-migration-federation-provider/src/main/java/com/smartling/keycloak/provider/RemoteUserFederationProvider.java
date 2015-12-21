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

import com.smartling.keycloak.federation.FederatedUserModel;
import com.smartling.keycloak.federation.FederatedUserService;
import com.smartling.keycloak.federation.UserCredentialsDto;
import org.apache.http.HttpStatus;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.models.CredentialValidationOutput;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserFederationProvider;
import org.keycloak.models.UserFederationProviderModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Remote API based user federation provider.
 *
 * @author Scott Rossillo
 */
public class RemoteUserFederationProvider implements UserFederationProvider {

    private static final Logger LOG = Logger.getLogger(RemoteUserFederationProvider.class);
    private static final Set<String> supportedCredentialTypes = Collections.singleton(UserCredentialModel.PASSWORD);
    static final String UID_ATTRIBUTE = "uid";

    private KeycloakSession session;
    private UserFederationProviderModel model;
    private final FederatedUserService federatedUserService;

    private static FederatedUserService buildClient(String uri) {

        ResteasyClient client = new ResteasyClientBuilder().disableTrustManager().build();
        ResteasyWebTarget target =  client.target(uri);

        return target
                .proxyBuilder(FederatedUserService.class)
                .classloader(FederatedUserService.class.getClassLoader())
                .build();
    }

    public RemoteUserFederationProvider(KeycloakSession session, UserFederationProviderModel model, String uri) {
        this(session, model, buildClient(uri));
        LOG.info("Using validation base URI: " + uri);
    }

    protected RemoteUserFederationProvider(KeycloakSession session, UserFederationProviderModel model, FederatedUserService federatedUserService) {
        this.session = session;
        this.model = model;
        this.federatedUserService = federatedUserService;
    }

    @Override
    public boolean synchronizeRegistrations() {
        return false;
    }

    @Override
    public UserModel register(RealmModel realm, UserModel user) {
        LOG.warn("In call to register user");
        return null;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        LOG.warn("In call to remove user:" + user.getUsername());
        user.setFederationLink(null);
        session.users().removeUser(realm, user);
        LOG.warn("Removed federation link for user: " + user.getUsername());
        return true;
    }

    private UserModel createUserModel(RealmModel realm, String rawUsername) throws NotFoundException {

        String username = rawUsername.toLowerCase().trim();
        FederatedUserModel remoteUser = federatedUserService.getUserDetails(username);
        LOG.info("Creating user model for: " + username);
        UserModel userModel = session.userStorage().addUser(realm, username);

        if (!username.equals(remoteUser.getEmail())) {
            throw new IllegalStateException(String.format("Local and remote users differ: [%s != %s]", username, remoteUser.getUsername()));
        }

        userModel.setFederationLink(model.getId());
        userModel.setEnabled(remoteUser.isEnabled());
        userModel.setEmail(username);
        userModel.setEmailVerified(remoteUser.isEmailVerified());
        userModel.setFirstName(remoteUser.getFirstName());
        userModel.setLastName(remoteUser.getLastName());

        if (remoteUser.getAttributes() != null) {
            Map<String, List<String>> attributes = remoteUser.getAttributes();
            for (String attributeName : attributes.keySet())
                userModel.setAttribute(attributeName, attributes.get(attributeName));
        }

        if (remoteUser.getRoles() != null) {
            for (String role : remoteUser.getRoles()) {
                RoleModel roleModel = realm.getRole(role);
                if (roleModel != null) {
                    userModel.grantRole(roleModel);
                    LOG.infof("Granted user %s, role %s", username, role);
                }
            }
        }

        return userModel;
    }

    protected boolean exists(RealmModel realm, String username) {

        if (LOG.isInfoEnabled()) {
            LOG.info("User? " + session.userStorage().getUserByUsername(username, realm));
            LOG.info("Email? " + session.userStorage().getUserByEmail(username, realm));
        }

        return session.userStorage().getUserByUsername(username, realm) != null || session.userStorage().getUserByEmail(username, realm) != null;
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        LOG.warn("Get by username: " + username);

        if (exists(realm, username)) {
            LOG.warn(String.format("User %s already exists in realm %s, returning null", username, realm.getName()));
            return null;
        }

        try {
            return this.createUserModel(realm, username);
        } catch (NotFoundException ex) {
            LOG.error("Federated user not found: " + username);
            return null;
        }
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        LOG.warn("Get by email: " + email);

        if (exists(realm, email)) {
            LOG.warn(String.format("Email %s already exists in realm %s, returning null", email, realm.getName()));
            return null;
        }

        try {
            return this.createUserModel(realm, email);
        } catch (NotFoundException ex) {
            LOG.error("Federated user (by email) not found: " + email);
            return null;
        }
    }

    @Override
    public List<UserModel> searchByAttributes(Map<String, String> attributes, RealmModel realm, int maxResults) {
        LOG.warn("In searchByAttributes(): " + attributes);
        return Collections.emptyList();
    }

    @Override
    public void preRemove(RealmModel realm) {
        // no-op
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        // no-op
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group)
    {
        // no-op
    }

    @Override
    public Set<String> getSupportedCredentialTypes(UserModel user) {
        return supportedCredentialTypes;
    }

    @Override
    public Set<String> getSupportedCredentialTypes() {
        return supportedCredentialTypes;
    }

    @Override
    public boolean validCredentials(RealmModel realm, UserModel user, List<UserCredentialModel> input) {

        LOG.info("Validating credentials");

        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("UserCredentialModel list is empty or null!");
        }

        UserCredentialModel credentials = input.get(0);
        Response response = federatedUserService.validateLogin(user.getUsername(), new UserCredentialsDto(credentials.getValue()));
        boolean valid = HttpStatus.SC_OK == response.getStatus();

        if (valid) {
            user.updateCredential(credentials);
            session.userStorage().getUserById(user.getId(), realm).updateCredential(credentials);
            session.userStorage().getUserById(user.getId(), realm).setFederationLink(null);
        }

        return valid;
    }

    @Override
    public boolean validCredentials(RealmModel realm, UserModel user, UserCredentialModel... input) {
        return validCredentials(realm, user, Arrays.asList(input));
    }

    @Override
    public CredentialValidationOutput validCredentials(RealmModel realm, UserCredentialModel credential) {
        return CredentialValidationOutput.failed();
    }

    @Override
    public UserModel validateAndProxy(RealmModel realm, UserModel local)
    {
        return local;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel local)
    {
        LOG.info("Checking if user is valid: " + local.getUsername());
        Response response = federatedUserService.validateUserExists(local.getUsername());
        LOG.warn("Checked if " + local.getUsername() + " is valid: " + response.getStatus());
        return HttpStatus.SC_ACCEPTED == response.getStatus();
    }

    @Override
    public void close() {
        LOG.warn("In call to close()");
    }
}
