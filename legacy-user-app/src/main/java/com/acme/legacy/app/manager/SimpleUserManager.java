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
import com.acme.legacy.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Simple user manager implementation.
 *
 * @author Scott Rossillo
 */
@Service
public class SimpleUserManager implements UserManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUserManager.class);
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByEmail(String email)
    {
        Assert.notNull(email, "email address required");
        return userRepository.findByEmail(email);
    }

    @Override
    public User login(String email, String password) throws AccessDeniedException
    {
        Assert.notNull(email, "email address required");
        Assert.notNull(password, "password required");
        User user = userRepository.findByEmail(email);
        String encodedPassword;

        if (user == null)
            throw new AccessDeniedException();

        encodedPassword = passwordEncoder.encode(password, user.getSalt());

        if (!encodedPassword.equals(user.getPassword()))
            throw new AccessDeniedException();

        return user;
    }
}
