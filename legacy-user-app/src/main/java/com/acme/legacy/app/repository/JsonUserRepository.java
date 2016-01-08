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
package com.acme.legacy.app.repository;

import com.acme.legacy.app.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Trivial, read-only file based user repository.
 */
@Repository
public class JsonUserRepository implements UserRepository
{
    private Map<String,User> users;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() throws Exception {
        Resource resource = new ClassPathResource("users.json");
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        List<User> userList = mapper.readValue(resource.getInputStream(), new TypeReference<List<User>>() { });
        Map<String,User> userMap = new TreeMap<>();

        for (User user : userList)
            userMap.put(user.getEmail(), user);

        this.users = Collections.unmodifiableMap(userMap);
    }

    @Override
    public User findByEmail(String email)
    {
        return users.get(email.toLowerCase());
    }
}
