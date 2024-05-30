/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
package com.sun.jna.platform.win32;

import com.sun.jna.ptr.IntByReference;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


public class Secur32_Impersonate_Test {

    @Test
    public void testImpersonateRevertSecurityContext() {
        // client ----------- acquire outbound credential handle
        Sspi.CredHandle phClientCredential = new Sspi.CredHandle();
        Sspi.TimeStamp ptsClientExpiry = new Sspi.TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_OUTBOUND, null, null, null,
                null, phClientCredential, ptsClientExpiry));
        // client ----------- security context
        Sspi.CtxtHandle phClientContext = new Sspi.CtxtHandle();
        IntByReference pfClientContextAttr = new IntByReference();
        // server ----------- acquire inbound credential handle
        Sspi.CredHandle phServerCredential = new Sspi.CredHandle();
        Sspi.TimeStamp ptsServerExpiry = new Sspi.TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_INBOUND, null, null, null,
                null, phServerCredential, ptsServerExpiry));
        // server ----------- security context
        Sspi.CtxtHandle phServerContext = new Sspi.CtxtHandle();
        SspiUtil.ManagedSecBufferDesc pbServerToken = null;
        IntByReference pfServerContextAttr = new IntByReference();
        int clientRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        int serverRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        do {
            // client ----------- initialize security context, produce a client token
            // client token returned is always new
            SspiUtil.ManagedSecBufferDesc pbClientToken = new SspiUtil.ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
            if (clientRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
                // server token is empty the first time
                SspiUtil.ManagedSecBufferDesc pbServerTokenCopy = pbServerToken == null
                        ? null : new SspiUtil.ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, pbServerToken.getBuffer(0).getBytes());
                clientRc = Secur32.INSTANCE.InitializeSecurityContext(
                        phClientCredential,
                        phClientContext.isNull() ? null : phClientContext,
                        Advapi32Util.getUserName(),
                        Sspi.ISC_REQ_CONNECTION,
                        0,
                        Sspi.SECURITY_NATIVE_DREP,
                        pbServerTokenCopy,
                        0,
                        phClientContext,
                        pbClientToken,
                        pfClientContextAttr,
                        null);
                assertTrue(clientRc == W32Errors.SEC_I_CONTINUE_NEEDED || clientRc == W32Errors.SEC_E_OK);
            }
            // server ----------- accept security context, produce a server token
            if (serverRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
                pbServerToken = new SspiUtil.ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
                SspiUtil.ManagedSecBufferDesc pbClientTokenByValue = new SspiUtil.ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, pbClientToken.getBuffer(0).getBytes());
                serverRc = Secur32.INSTANCE.AcceptSecurityContext(phServerCredential,
                        phServerContext.isNull() ? null : phServerContext,
                        pbClientTokenByValue,
                        Sspi.ISC_REQ_CONNECTION,
                        Sspi.SECURITY_NATIVE_DREP,
                        phServerContext,
                        pbServerToken,
                        pfServerContextAttr,
                        ptsServerExpiry);
                assertTrue(serverRc == W32Errors.SEC_I_CONTINUE_NEEDED || serverRc == W32Errors.SEC_E_OK);
            }
        } while (serverRc != W32Errors.SEC_E_OK || clientRc != W32Errors.SEC_E_OK);
        // impersonate
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.ImpersonateSecurityContext(
                phServerContext));
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.RevertSecurityContext(
                phServerContext));
        // release server context
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.DeleteSecurityContext(
                phServerContext));
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeCredentialsHandle(
                phServerCredential));
        // release client context
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.DeleteSecurityContext(
                phClientContext));
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeCredentialsHandle(
                phClientCredential));
    }
}
