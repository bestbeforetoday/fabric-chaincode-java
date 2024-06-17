/*
 * Copyright 2019 IBM DTCC All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.fabric.shim.ext.sbe.impl;

import org.hyperledger.fabric.protos.common.MSPRole.MSPRoleType;
import org.hyperledger.fabric.shim.ext.sbe.StateBasedEndorsement;
import org.hyperledger.fabric.shim.ext.sbe.StateBasedEndorsement.RoleType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StateBasedEndorsementImplTest {

    @Test
    public void addOrgs() {
        // add an org
        final StateBasedEndorsement ep = StateBasedEndorsementFactory.getInstance().newStateBasedEndorsement(null);
        ep.addOrgs(RoleType.RoleTypePeer, "Org1");

        final byte[] epBytes = ep.policy();
        assertThat(epBytes).isNotNull();
        assertThat(epBytes).hasSizeGreaterThan(0);
        final byte[] expectedEPBytes = StateBasedEndorsementUtils.signedByFabricEntity("Org1", MSPRoleType.PEER).toByteString().toByteArray();
        assertThat(epBytes).isEqualTo(expectedEPBytes);
    }

    @Test
    public void delOrgs() {

        final byte[] initEPBytes = StateBasedEndorsementUtils.signedByFabricEntity("Org1", MSPRoleType.PEER).toByteString().toByteArray();
        final StateBasedEndorsement ep = StateBasedEndorsementFactory.getInstance().newStateBasedEndorsement(initEPBytes);
        final List<String> listOrgs = ep.listOrgs();

        assertThat(listOrgs).isNotNull();
        assertThat(listOrgs).containsExactly("Org1");

        ep.addOrgs(RoleType.RoleTypeMember, "Org2");
        ep.delOrgs("Org1");

        final byte[] epBytes = ep.policy();

        assertThat(epBytes).isNotNull();
        assertThat(epBytes).hasSizeGreaterThan(0);
        final byte[] expectedEPBytes = StateBasedEndorsementUtils.signedByFabricEntity("Org2", MSPRoleType.MEMBER).toByteString().toByteArray();
        assertThat(epBytes).isEqualTo(expectedEPBytes);
    }

    @Test
    public void listOrgs() {
        final byte[] initEPBytes = StateBasedEndorsementUtils.signedByFabricEntity("Org1", MSPRoleType.PEER).toByteString().toByteArray();
        final StateBasedEndorsement ep = StateBasedEndorsementFactory.getInstance().newStateBasedEndorsement(initEPBytes);
        final List<String> listOrgs = ep.listOrgs();

        assertThat(listOrgs).isNotNull();
        assertThat(listOrgs).containsExactly("Org1");
    }
}
