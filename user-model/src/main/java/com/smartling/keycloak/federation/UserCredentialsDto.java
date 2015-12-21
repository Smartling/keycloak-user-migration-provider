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
package com.smartling.keycloak.federation;

import java.io.Serializable;
import java.util.Objects;

/**
 * Simple password data transfer object.
 *
 * @author Scott Rossillo
 */
public class UserCredentialsDto implements Serializable
{
    private String password;

    public UserCredentialsDto()
    {
    }

    public UserCredentialsDto(String password)
    {
        this.password = password;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof UserCredentialsDto))
        {
            return false;
        }
        UserCredentialsDto that = (UserCredentialsDto) o;
        return Objects.equals(getPassword(), that.getPassword());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getPassword());
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
