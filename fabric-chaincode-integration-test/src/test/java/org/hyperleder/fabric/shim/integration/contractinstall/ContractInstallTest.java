/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/
package org.hyperleder.fabric.shim.integration.contractinstall;

import org.hyperleder.fabric.shim.integration.util.FabricState;
import org.hyperleder.fabric.shim.integration.util.InvokeHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic Java Chaincode Test
 *
 */
public class ContractInstallTest {

   @BeforeAll
    public static void setUp() throws Exception {
        FabricState.getState().start();
        
    }

   @Test
    public void TestInstall(){

        InvokeHelper helper = InvokeHelper.newHelper("baregradlecc","sachannel");        
        String text = helper.invoke("org1", "whoami");
        assertThat(text).contains("BareGradle");
        
        helper = InvokeHelper.newHelper("baremaven","sachannel");        
        text = helper.invoke("org1", "whoami");
        assertThat(text).contains("BareMaven");
        
        helper = InvokeHelper.newHelper("wrappermaven","sachannel");        
        text = helper.invoke("org1", "whoami");
        assertThat(text).contains("WrapperMaven");
    }

}