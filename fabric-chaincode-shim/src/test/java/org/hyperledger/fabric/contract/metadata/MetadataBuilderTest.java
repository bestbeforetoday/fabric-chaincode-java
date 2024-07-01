/*
 * Copyright 2019 IBM All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.fabric.contract.metadata;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.internal.DefaultSchemaClient;
import org.hyperledger.fabric.contract.ChaincodeStubNaiveImpl;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.routing.ContractDefinition;
import org.hyperledger.fabric.contract.routing.impl.ContractDefinitionImpl;
import org.hyperledger.fabric.contract.systemcontract.SystemContract;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import contract.SampleContract;

public class MetadataBuilderTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final String expectedMetadataString = "    {\n" + "       \"components\": {\"schemas\": {}},\n"
            + "       \"$schema\": \"https://fabric-shim.github.io/contract-schema.json\",\n" + "       \"contracts\": {\"SampleContract\": {\n"
            + "          \"name\": \"SampleContract\",\n" + "          \"transactions\": [],\n" + "          \"info\": {\n"
            + "             \"license\": {\"name\": \"\"},\n" + "             \"description\": \"\",\n" + "             \"termsOfService\": \"\",\n"
            + "             \"title\": \"\",\n" + "             \"version\": \"\",\n" + "             \"contact\": {\"email\": \"fred@example.com\"}\n"
            + "          }\n" + "       }},\n" + "       \"info\": {\n" + "          \"license\": {\"name\": \"\"},\n" + "          \"description\": \"\",\n"
            + "          \"termsOfService\": \"\",\n" + "          \"title\": \"\",\n" + "          \"version\": \"\",\n"
            + "          \"contact\": {\"email\": \"fred@example.com\"}\n" + "       }\n" + "    }\n" + "";

    // fields are private, so use reflection to bypass this for unit testing
    @Before
    @After
    public void beforeAndAfterEach() {

        MetadataBuilder.componentMap = new HashMap<>();
        MetadataBuilder.contractMap = new HashMap<>();
        MetadataBuilder.overallInfoMap = new HashMap<>();
        MetadataBuilder.schemaClient = new DefaultSchemaClient();

    }

    @Test
    public void systemContract() {

        final SystemContract system = new SystemContract();
        final ChaincodeStub stub = new ChaincodeStubNaiveImpl();
        system.getMetadata(new Context(stub));
    }

    @Test
    public void defaultSchemasNotLoadedFromNetwork() {
        final ContractDefinition contractDefinition = new ContractDefinitionImpl(SampleContract.class);
        MetadataBuilder.addContract(contractDefinition);
        MetadataBuilder.schemaClient = new SchemaClient() {

            @Override
            public InputStream get(final String uri) {
                throw new RuntimeException("Refusing to load schema: " + uri);
            }

        };
        MetadataBuilder.validate();
    }

}
