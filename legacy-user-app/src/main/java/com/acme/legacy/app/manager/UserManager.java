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

/**
 * Provides a user manager.
 *
 * @author Scott Rossillo
 */
public interface UserManager
{
    /**
     * Returns the user identified by the given email address.
     *
     * @param email the email address for the user to find
     * @return the <code>user</code> identified by the given <code>email</code> if found;
     * <code>null</code> otherwise
     */
    User findByEmail(String email);

    /**
     * Logs in the user with the given email and password.
     *
     * @param email the user's email (required)
     * @param password the user's password (required)
     * @return the logged in <code>user</code>
     * @throws AccessDeniedException if the given <code>email</code> and <code>password</code> are invalid
     */
    User login(String email, String password) throws AccessDeniedException;
}
