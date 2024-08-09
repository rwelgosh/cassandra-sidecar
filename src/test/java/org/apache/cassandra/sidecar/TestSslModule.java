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

package org.apache.cassandra.sidecar;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cassandra.sidecar.auth.authentication.AuthenticatorConfig;
import org.apache.cassandra.sidecar.auth.authorization.AuthorizerConfig;
import org.apache.cassandra.sidecar.config.AuthenticatorConfiguration;
import org.apache.cassandra.sidecar.config.AuthorizerConfiguration;
import org.apache.cassandra.sidecar.config.SslConfiguration;
import org.apache.cassandra.sidecar.config.yaml.AuthenticatorConfigurationImpl;
import org.apache.cassandra.sidecar.config.yaml.AuthorizerConfigurationImpl;
import org.apache.cassandra.sidecar.config.yaml.KeyStoreConfigurationImpl;
import org.apache.cassandra.sidecar.config.yaml.SidecarConfigurationImpl;
import org.apache.cassandra.sidecar.config.yaml.SslConfigurationImpl;

import static org.apache.cassandra.sidecar.common.ResourceUtils.writeResourceToPath;


/**
 * Changes to the TestModule to define SSL dependencies
 */
public class TestSslModule extends TestModule
{
    private static final Logger logger = LoggerFactory.getLogger(TestSslModule.class);
    private final Path certPath;
    private Path keystorePath;
    private Path truststorePath;

    public TestSslModule(Path certPath)
    {
        this.certPath = certPath;
    }

    public TestSslModule(Path certPath, Path keystorePath, Path truststorePath)
    {
        this.certPath = certPath;
        this.keystorePath = keystorePath;
        this.truststorePath = truststorePath;
    }

    @Override
    public SidecarConfigurationImpl abstractConfig()
    {
        ClassLoader classLoader = TestSslModule.class.getClassLoader();

        Path keyStorePath;
        Path trustStorePath;

        if (this.keystorePath != null)
        {
            keyStorePath = this.keystorePath;
        }
        else
        {
            keyStorePath = writeResourceToPath(classLoader, certPath, "certs/test.p12");
        }
        String keyStorePassword = "password";

        if (this.truststorePath != null)
        {
            trustStorePath = this.truststorePath;
        }
        else
        {
            trustStorePath = writeResourceToPath(classLoader, certPath, "certs/ca.p12");
        }

        String trustStorePassword = "password";

        if (!Files.exists(keyStorePath))
        {
            logger.error("JMX password file not found in path={}", keyStorePath);
        }
        if (!Files.exists(trustStorePath))
        {
            logger.error("Trust Store file not found in path={}", trustStorePath);
        }

        SslConfiguration sslConfiguration =
        SslConfigurationImpl.builder()
                            .enabled(true)
                            .useOpenSsl(true)
                            .handshakeTimeoutInSeconds(10L)
                            .clientAuth("NONE")
                            .keystore(new KeyStoreConfigurationImpl(keyStorePath.toAbsolutePath().toString(),
                                                                    keyStorePassword))
                            .truststore(new KeyStoreConfigurationImpl(trustStorePath.toAbsolutePath().toString(),
                                                                      trustStorePassword))
                            .build();

        AuthenticatorConfiguration authenticatorConfiguration =
        AuthenticatorConfigurationImpl.builder()
                                      .authorizedIdentities(Collections.emptySet())
                                      .authConfig(AuthenticatorConfig.AllowAllAuthenticator)
                                      .build();

        AuthorizerConfiguration authorizerConfiguration =
        AuthorizerConfigurationImpl.builder()
                                   .authConfig(AuthorizerConfig.AllowAllAuthorizer)
                                   .build();

        return super.abstractConfig(sslConfiguration, authenticatorConfiguration, authorizerConfiguration);
    }
}
