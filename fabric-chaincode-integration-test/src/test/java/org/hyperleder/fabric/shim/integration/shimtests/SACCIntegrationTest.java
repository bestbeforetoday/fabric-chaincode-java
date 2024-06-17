/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/
package org.hyperleder.fabric.shim.integration.shimtests;

import org.hyperleder.fabric.shim.integration.util.FabricState;
import org.hyperleder.fabric.shim.integration.util.InvokeHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic Java Chaincode Test
 *
 */
public class SACCIntegrationTest {

    @BeforeAll
    public static void setUp() throws Exception {
        FabricState.getState().start();       

    }

   @Test
    public void TestLedger(){

        InvokeHelper helper = InvokeHelper.newHelper("shimcc", "sachannel");
        String text = helper.invoke("org1", "putBulkStates");
        assertThat(text).contains("success");
        
        text = helper.invoke("org1", "getByRange","key120","key170");
        assertThat(text).contains("50");

        text = helper.invoke("org1", "getByRangePaged","key120","key170","10","");
        System.out.println(text);
        assertThat(text).contains("key130");

        text = helper.invoke("org1", "getMetricsProviderName");
        System.out.println(text);
        assertThat(text).contains("org.hyperledger.fabric.metrics.impl.DefaultProvider");
    }

}
