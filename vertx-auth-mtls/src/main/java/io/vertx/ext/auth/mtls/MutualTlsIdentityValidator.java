/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vertx.ext.auth.mtls;

import io.vertx.ext.auth.User;

/**
 * Interface to represent an identity validator for mTLS authentication and authorization.
 * This will allow users to validate that an identity is authenticated and also create a user
 * from the identity in the certificate.
 */
public interface MutualTlsIdentityValidator
{
    /**
     * Validates the identity of the client as extracted from the certificate.
     *
     * @param identity - {@code String} representation of the identity of a client
     * @return - {@code true} if the identity is authenticated and {@code false} if the
     * identity is not authenticated.
     */
    boolean isValidIdentity(String identity);

    /**
     * Creates a {@code User} object from the identity
     *
     * @param identity - {@code String} representation of the identity of a client
     * @return - {@code User} object representing the client
     */
    User userFromIdentity(String identity);
}
