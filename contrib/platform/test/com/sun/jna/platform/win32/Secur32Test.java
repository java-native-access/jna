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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.SspiUtil.ManagedSecBufferDesc;
import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.PSecPkgInfo;
import com.sun.jna.platform.win32.Sspi.SecPkgContext_PackageInfo;
import com.sun.jna.platform.win32.Sspi.SecPkgContext_Sizes;
import com.sun.jna.platform.win32.Sspi.SecPkgCredentials_Names;
import com.sun.jna.platform.win32.Sspi.SecPkgInfo;
import com.sun.jna.platform.win32.Sspi.SecPkgInfo.ByReference;
import com.sun.jna.platform.win32.Sspi.TimeStamp;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;
import java.nio.charset.Charset;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class Secur32Test extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Secur32Test.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // Drain last error to prevent test fails from lingering  last error
        // states
        Kernel32.INSTANCE.GetLastError();
    }

    public void testGetUserNameEx() {
        IntByReference len = new IntByReference();
        Secur32.INSTANCE.GetUserNameEx(
                Secur32.EXTENDED_NAME_FORMAT.NameSamCompatible, null, len);
        assertTrue(len.getValue() > 0);
        char[] buffer = new char[len.getValue() + 1];
        assertTrue(Secur32.INSTANCE.GetUserNameEx(
                Secur32.EXTENDED_NAME_FORMAT.NameSamCompatible, buffer, len));
        String username = Native.toString(buffer);
        assertTrue(username.length() > 0);
    }

    public void testAcquireCredentialsHandle() {
        CredHandle phCredential = new CredHandle();
        TimeStamp ptsExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_OUTBOUND, null, null, null,
                null, phCredential, ptsExpiry));
        assertTrue(phCredential.dwLower != null);
        assertTrue(phCredential.dwUpper != null);
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeCredentialsHandle(
                phCredential));
    }

    public void testAcquireCredentialsHandleInvalidPackage() {
        CredHandle phCredential = new CredHandle();
        TimeStamp ptsExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_SECPKG_NOT_FOUND, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "PackageDoesntExist", Sspi.SECPKG_CRED_OUTBOUND, null, null, null,
                null, phCredential, ptsExpiry));
    }

    public void testInitializeSecurityContext() {
        CredHandle phCredential = new CredHandle();
        TimeStamp ptsExpiry = new TimeStamp();
        // acquire a credentials handle
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_OUTBOUND, null, null, null,
                null, phCredential, ptsExpiry));
        // initialize security context
        CtxtHandle phNewContext = new CtxtHandle();
        ManagedSecBufferDesc pbToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
        IntByReference pfContextAttr = new IntByReference();
        int rc = Secur32.INSTANCE.InitializeSecurityContext(phCredential, null,
                Advapi32Util.getUserName(), Sspi.ISC_REQ_CONNECTION, 0,
                Sspi.SECURITY_NATIVE_DREP, null, 0, phNewContext, pbToken,
                pfContextAttr, null);
        assertTrue(rc == W32Errors.SEC_I_CONTINUE_NEEDED || rc == W32Errors.SEC_E_OK);
        assertTrue(phNewContext.dwLower != null);
        assertTrue(phNewContext.dwUpper != null);
        assertTrue(pbToken.getBuffer(0).getBytes().length > 0);
        // release
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.DeleteSecurityContext(
                phNewContext));
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeCredentialsHandle(
                phCredential));
    }

    public void testAcceptSecurityContext() {
        // client ----------- acquire outbound credential handle
        CredHandle phClientCredential = new CredHandle();
        TimeStamp ptsClientExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_OUTBOUND, null, null, null,
                null, phClientCredential, ptsClientExpiry));
        // client ----------- security context
        CtxtHandle phClientContext = new CtxtHandle();
        IntByReference pfClientContextAttr = new IntByReference();
        // server ----------- acquire inbound credential handle
        CredHandle phServerCredential = new CredHandle();
        TimeStamp ptsServerExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_INBOUND, null, null, null,
                null, phServerCredential, ptsServerExpiry));
        // server ----------- security context
        CtxtHandle phServerContext = new CtxtHandle();
        ManagedSecBufferDesc pbServerToken = null;
        IntByReference pfServerContextAttr = new IntByReference();
        int clientRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        int serverRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        do {
            // client ----------- initialize security context, produce a client token
            // client token returned is always new
            ManagedSecBufferDesc pbClientToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
            if (clientRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
                // server token is empty the first time
                ManagedSecBufferDesc pbServerTokenCopy = pbServerToken == null
                        ? null : new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, pbServerToken.getBuffer(0).getBytes());
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
                pbServerToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
                ManagedSecBufferDesc pbClientTokenByValue = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, pbClientToken.getBuffer(0).getBytes());
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

    public void testImpersonateRevertSecurityContext() {
        // client ----------- acquire outbound credential handle
        CredHandle phClientCredential = new CredHandle();
        TimeStamp ptsClientExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_OUTBOUND, null, null, null,
                null, phClientCredential, ptsClientExpiry));
        // client ----------- security context
        CtxtHandle phClientContext = new CtxtHandle();
        IntByReference pfClientContextAttr = new IntByReference();
        // server ----------- acquire inbound credential handle
        CredHandle phServerCredential = new CredHandle();
        TimeStamp ptsServerExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_INBOUND, null, null, null,
                null, phServerCredential, ptsServerExpiry));
        // server ----------- security context
        CtxtHandle phServerContext = new CtxtHandle();
        ManagedSecBufferDesc pbServerToken = null;
        IntByReference pfServerContextAttr = new IntByReference();
        int clientRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        int serverRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        do {
            // client ----------- initialize security context, produce a client token
            // client token returned is always new
            ManagedSecBufferDesc pbClientToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
            if (clientRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
                // server token is empty the first time
                ManagedSecBufferDesc pbServerTokenCopy = pbServerToken == null
                        ? null : new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, pbServerToken.getBuffer(0).getBytes());
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
                pbServerToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
                ManagedSecBufferDesc pbClientTokenByValue = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, pbClientToken.getBuffer(0).getBytes());
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

    public void testEnumerateSecurityPackages() {
        IntByReference pcPackages = new IntByReference();
        PSecPkgInfo.ByReference pPackageInfo = new PSecPkgInfo.ByReference();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.EnumerateSecurityPackages(
                pcPackages, pPackageInfo));
        SecPkgInfo.ByReference[] packagesInfo = pPackageInfo.toArray(
                pcPackages.getValue());
        for (SecPkgInfo.ByReference packageInfo : packagesInfo) {
            assertTrue(packageInfo.Name.length() > 0);
            assertTrue(packageInfo.Comment.length() >= 0);
        }
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeContextBuffer(
                pPackageInfo.pPkgInfo.getPointer()));

    }

    public void testQuerySecurityContextToken() {
        // client ----------- acquire outbound credential handle
        CredHandle phClientCredential = new CredHandle();
        TimeStamp ptsClientExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_OUTBOUND, null, null, null,
                null, phClientCredential, ptsClientExpiry));
        // client ----------- security context
        CtxtHandle phClientContext = new CtxtHandle();
        IntByReference pfClientContextAttr = new IntByReference();
        // server ----------- acquire inbound credential handle
        CredHandle phServerCredential = new CredHandle();
        TimeStamp ptsServerExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_INBOUND, null, null, null,
                null, phServerCredential, ptsServerExpiry));
        // server ----------- security context
        CtxtHandle phServerContext = new CtxtHandle();
        ManagedSecBufferDesc pbServerToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
        IntByReference pfServerContextAttr = new IntByReference();
        int clientRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        int serverRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        do {
            // client token returned is always new
            ManagedSecBufferDesc pbClientToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
            // client ----------- initialize security context, produce a client token
            if (clientRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
                // server token is empty the first time
                clientRc = Secur32.INSTANCE.InitializeSecurityContext(
                        phClientCredential,
                        phClientContext.isNull() ? null : phClientContext,
                        Advapi32Util.getUserName(),
                        Sspi.ISC_REQ_CONNECTION,
                        0,
                        Sspi.SECURITY_NATIVE_DREP,
                        pbServerToken,
                        0,
                        phClientContext,
                        pbClientToken,
                        pfClientContextAttr,
                        null);
                assertTrue(String.format("Unexepected result from InitializeSecurityContext: %1$d / 0x%1$x ", clientRc),
                        clientRc == W32Errors.SEC_I_CONTINUE_NEEDED || clientRc == W32Errors.SEC_E_OK);
            }
            // server ----------- accept security context, produce a server token
            if (serverRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
                serverRc = Secur32.INSTANCE.AcceptSecurityContext(phServerCredential,
                        phServerContext.isNull() ? null : phServerContext,
                        pbClientToken,
                        Sspi.ISC_REQ_CONNECTION,
                        Sspi.SECURITY_NATIVE_DREP,
                        phServerContext,
                        pbServerToken,
                        pfServerContextAttr,
                        ptsServerExpiry);
                assertTrue(String.format("Unexepected result from AcceptSecurityContext: %1$d / 0x%1$x ", serverRc),
                        serverRc == W32Errors.SEC_I_CONTINUE_NEEDED || serverRc == W32Errors.SEC_E_OK);
            }
        } while (serverRc != W32Errors.SEC_E_OK || clientRc != W32Errors.SEC_E_OK);
        // query security context token
        HANDLEByReference phContextToken = new HANDLEByReference();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.QuerySecurityContextToken(
                phServerContext, phContextToken));
        // release security context token
        Kernel32Util.closeHandleRef(phContextToken);
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

    public void testCreateEmptyToken() {
        ManagedSecBufferDesc token = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
        assertEquals(1, token.cBuffers);
        assertEquals(Sspi.SECBUFFER_TOKEN, token.getBuffer(0).BufferType);
        assertEquals(Sspi.MAX_TOKEN_SIZE, token.getBuffer(0).cbBuffer);
    }

    public void testQueryContextAttributes() {
        // client ----------- acquire outbound credential handle
        CredHandle phClientCredential = new CredHandle();
        TimeStamp ptsClientExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_OUTBOUND, null, null, null,
                null, phClientCredential, ptsClientExpiry));
        // client ----------- security context
        CtxtHandle phClientContext = new CtxtHandle();
        IntByReference pfClientContextAttr = new IntByReference();
        // server ----------- acquire inbound credential handle
        CredHandle phServerCredential = new CredHandle();
        TimeStamp ptsServerExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_INBOUND, null, null, null,
                null, phServerCredential, ptsServerExpiry));
        // server ----------- security context
        CtxtHandle phServerContext = new CtxtHandle();
        ManagedSecBufferDesc pbServerToken = null;
        IntByReference pfServerContextAttr = new IntByReference();
        int clientRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        int serverRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        do {
            // client ----------- initialize security context, produce a client token
            // client token returned is always new
            ManagedSecBufferDesc pbClientToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
            if (clientRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
                // server token is empty the first time
                ManagedSecBufferDesc pbServerTokenCopy = pbServerToken == null
                        ? null : new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, pbServerToken.getBuffer(0).getBytes());
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
                pbServerToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
                ManagedSecBufferDesc pbClientTokenByValue = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, pbClientToken.getBuffer(0).getBytes());
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
        // query context attributes
        SecPkgContext_PackageInfo packageinfo = new SecPkgContext_PackageInfo();
        assertEquals(W32Errors.SEC_E_OK,
                Secur32.INSTANCE.QueryContextAttributes(phServerContext, Sspi.SECPKG_ATTR_PACKAGE_INFO, packageinfo));
        ByReference info = packageinfo.PackageInfo;

        assertNotNull(info.Name);
        assertNotNull(info.Comment);

        assertTrue(!info.Name.isEmpty());
        assertTrue(!info.Comment.isEmpty());

        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeContextBuffer(info.getPointer()));

        // release server context
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.DeleteSecurityContext(phServerContext));
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeCredentialsHandle(phServerCredential));
        // release client context
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.DeleteSecurityContext(phClientContext));
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeCredentialsHandle(phClientCredential));
    }

    public void testQuerySecurityPackageInfo() {
        PSecPkgInfo pkgInfo = new PSecPkgInfo();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.QuerySecurityPackageInfo("NTLM", pkgInfo));
        assertEquals(pkgInfo.pPkgInfo.Name, "NTLM");
        assertEquals(pkgInfo.pPkgInfo.fCapabilities & Sspi.SECPKG_FLAG_PRIVACY, Sspi.SECPKG_FLAG_PRIVACY);
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.FreeContextBuffer(pkgInfo.getPointer()));
    }

    public void testQueryCredentialAttribute() {
        // acquire sample credential handle
        CredHandle phClientCredential = new CredHandle();
        TimeStamp ptsClientExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(null, "Negotiate",
                Sspi.SECPKG_CRED_OUTBOUND, null, null, null, null, phClientCredential, ptsClientExpiry));

        SecPkgCredentials_Names names = new SecPkgCredentials_Names();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.QueryCredentialsAttributes(phClientCredential, Sspi.SECPKG_CRED_ATTR_NAMES, names));

        String accountName = names.getUserName();

        assertNotNull(accountName);
        assertTrue(accountName.length() > 0);

        assertEquals(W32Errors.SEC_E_OK, names.free());
    }

    public void testEncryptDecryptMessage() {
        // client ----------- acquire outbound credential handle
        CredHandle phClientCredential = new CredHandle();
        TimeStamp ptsClientExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_OUTBOUND, null, null, null,
                null, phClientCredential, ptsClientExpiry));
        // client ----------- security context
        CtxtHandle phClientContext = new CtxtHandle();
        IntByReference pfClientContextAttr = new IntByReference();
        // server ----------- acquire inbound credential handle
        CredHandle phServerCredential = new CredHandle();
        TimeStamp ptsServerExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_INBOUND, null, null, null,
                null, phServerCredential, ptsServerExpiry));
        // server ----------- security context
        CtxtHandle phServerContext = new CtxtHandle();
        ManagedSecBufferDesc pbServerToken = null;
        IntByReference pfServerContextAttr = new IntByReference();
        int clientRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        int serverRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        do {
            // client ----------- initialize security context, produce a client token
            // client token returned is always new
            ManagedSecBufferDesc pbClientToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
            if (clientRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
                // server token is empty the first time
                ManagedSecBufferDesc pbServerTokenCopy = pbServerToken == null
                        ? null : new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, pbServerToken.getBuffer(0).getBytes());
                clientRc = Secur32.INSTANCE.InitializeSecurityContext(
                        phClientCredential,
                        phClientContext.isNull() ? null : phClientContext,
                        Advapi32Util.getUserName(),
                        Sspi.ISC_REQ_CONNECTION | Sspi.ISC_REQ_CONFIDENTIALITY,
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
                pbServerToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
                ManagedSecBufferDesc pbClientTokenByValue = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, pbClientToken.getBuffer(0).getBytes());
                serverRc = Secur32.INSTANCE.AcceptSecurityContext(phServerCredential,
                        phServerContext.isNull() ? null : phServerContext,
                        pbClientTokenByValue,
                        Sspi.ISC_REQ_CONNECTION | Sspi.ISC_REQ_CONFIDENTIALITY,
                        Sspi.SECURITY_NATIVE_DREP,
                        phServerContext,
                        pbServerToken,
                        pfServerContextAttr,
                        ptsServerExpiry);
                assertTrue(serverRc == W32Errors.SEC_I_CONTINUE_NEEDED || serverRc == W32Errors.SEC_E_OK);
            }
        } while (serverRc != W32Errors.SEC_E_OK || clientRc != W32Errors.SEC_E_OK);

        assertTrue((pfServerContextAttr.getValue() & Sspi.ISC_REQ_CONFIDENTIALITY) == Sspi.ISC_REQ_CONFIDENTIALITY);
        assertTrue((pfClientContextAttr.getValue() & Sspi.ISC_REQ_CONFIDENTIALITY) == Sspi.ISC_REQ_CONFIDENTIALITY);

        // Fetch size limits for crypto functions
        SecPkgContext_Sizes sizes = new SecPkgContext_Sizes();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.QueryContextAttributes(phClientContext, Sspi.SECPKG_ATTR_SIZES, sizes));

        // Create sample input data
        byte[] inputData = "Hallo Welt".getBytes(Charset.forName("ASCII"));

        // Do encryption, buffer 0 holds meta data, buffer 1 holds the
        // clear text data on input and the encrypted data on output.
        // Uses the phClientContext
        ManagedSecBufferDesc encryptBuffers = new ManagedSecBufferDesc(2);

        Memory tokenMemory = new Memory(sizes.cbSecurityTrailer);
        Memory dataMemory = new Memory(inputData.length);
        dataMemory.write(0, inputData, 0, inputData.length);

        encryptBuffers.getBuffer(0).BufferType = Sspi.SECBUFFER_TOKEN;
        encryptBuffers.getBuffer(0).cbBuffer = (int) tokenMemory.size();
        encryptBuffers.getBuffer(0).pvBuffer = tokenMemory;
        encryptBuffers.getBuffer(1).BufferType = Sspi.SECBUFFER_DATA;
        encryptBuffers.getBuffer(1).cbBuffer = (int) dataMemory.size();
        encryptBuffers.getBuffer(1).pvBuffer = dataMemory;

        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.EncryptMessage(phClientContext, 0, encryptBuffers, 0));

        byte[] encryptedTokenData = encryptBuffers.getBuffer(0).getBytes();
        byte[] encryptedData = encryptBuffers.getBuffer(1).getBytes();

        assertNotNull(encryptedTokenData);
        assertNotNull(encryptedData);
        assertTrue(encryptedTokenData.length > 0);
        assertTrue(encryptedData.length > 0);
        assertFalse(Arrays.equals(inputData, encryptedData));

        // Do decryption of data with the pfServerContextAttr
        ManagedSecBufferDesc decryptBuffers = new ManagedSecBufferDesc(2);

        Memory decryptTokenMemory = new Memory(encryptedTokenData.length);
        decryptTokenMemory.write(0, encryptedTokenData, 0, encryptedTokenData.length);
        Memory decryptDataMemory = new Memory(encryptedData.length);
        decryptDataMemory.write(0, encryptedData, 0, encryptedData.length);

        decryptBuffers.getBuffer(0).BufferType = Sspi.SECBUFFER_TOKEN;
        decryptBuffers.getBuffer(0).cbBuffer = (int) decryptTokenMemory.size();
        decryptBuffers.getBuffer(0).pvBuffer = decryptTokenMemory;
        decryptBuffers.getBuffer(1).BufferType = Sspi.SECBUFFER_DATA;
        decryptBuffers.getBuffer(1).cbBuffer = (int) decryptDataMemory.size();
        decryptBuffers.getBuffer(1).pvBuffer = decryptDataMemory;

        IntByReference qosResult = new IntByReference();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.DecryptMessage(phServerContext, decryptBuffers, 0, qosResult));

        byte[] decryptMessageResult = decryptBuffers.getBuffer(1).getBytes();
        assertTrue(Arrays.equals(inputData, decryptMessageResult));

        // Modify message and retry decryption. Decryption is expected to be
        // refused and the buffers should be untouched.
        // Modification is done by injecting a NULL byte into the beginning of
        // the message

        ManagedSecBufferDesc decryptBuffers2 = new ManagedSecBufferDesc(2);

        Memory decryptTokenMemory2 = new Memory(encryptedTokenData.length);
        decryptTokenMemory2.write(0, encryptedTokenData, 0, encryptedTokenData.length);
        Memory decryptDataMemory2 = new Memory(encryptedData.length + 1);
        decryptDataMemory2.write(1, encryptedData, 0, encryptedData.length);

        decryptBuffers2.getBuffer(0).BufferType = Sspi.SECBUFFER_TOKEN;
        decryptBuffers2.getBuffer(0).cbBuffer = (int) decryptTokenMemory2.size();
        decryptBuffers2.getBuffer(0).pvBuffer = decryptTokenMemory2;
        decryptBuffers2.getBuffer(1).BufferType = Sspi.SECBUFFER_DATA;
        decryptBuffers2.getBuffer(1).cbBuffer = (int) decryptDataMemory2.size();
        decryptBuffers2.getBuffer(1).pvBuffer = decryptDataMemory2;

        assertEquals(W32Errors.SEC_E_MESSAGE_ALTERED, Secur32.INSTANCE.DecryptMessage(phServerContext, decryptBuffers2, 0, qosResult));

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

    public void testMakeVerifySignature() {
        // client ----------- acquire outbound credential handle
        CredHandle phClientCredential = new CredHandle();
        TimeStamp ptsClientExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_OUTBOUND, null, null, null,
                null, phClientCredential, ptsClientExpiry));
        // client ----------- security context
        CtxtHandle phClientContext = new CtxtHandle();
        IntByReference pfClientContextAttr = new IntByReference();
        // server ----------- acquire inbound credential handle
        CredHandle phServerCredential = new CredHandle();
        TimeStamp ptsServerExpiry = new TimeStamp();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.AcquireCredentialsHandle(
                null, "Negotiate", Sspi.SECPKG_CRED_INBOUND, null, null, null,
                null, phServerCredential, ptsServerExpiry));
        // server ----------- security context
        CtxtHandle phServerContext = new CtxtHandle();
        ManagedSecBufferDesc pbServerToken = null;
        IntByReference pfServerContextAttr = new IntByReference();
        int clientRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        int serverRc = W32Errors.SEC_I_CONTINUE_NEEDED;
        do {
            // client ----------- initialize security context, produce a client token
            // client token returned is always new
            ManagedSecBufferDesc pbClientToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
            if (clientRc == W32Errors.SEC_I_CONTINUE_NEEDED) {
                // server token is empty the first time
                ManagedSecBufferDesc pbServerTokenCopy = pbServerToken == null
                        ? null : new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, pbServerToken.getBuffer(0).getBytes());
                clientRc = Secur32.INSTANCE.InitializeSecurityContext(
                        phClientCredential,
                        phClientContext.isNull() ? null : phClientContext,
                        Advapi32Util.getUserName(),
                        Sspi.ISC_REQ_CONNECTION | Sspi.ISC_REQ_CONFIDENTIALITY,
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
                pbServerToken = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, Sspi.MAX_TOKEN_SIZE);
                ManagedSecBufferDesc pbClientTokenByValue = new ManagedSecBufferDesc(Sspi.SECBUFFER_TOKEN, pbClientToken.getBuffer(0).getBytes());
                serverRc = Secur32.INSTANCE.AcceptSecurityContext(phServerCredential,
                        phServerContext.isNull() ? null : phServerContext,
                        pbClientTokenByValue,
                        Sspi.ISC_REQ_CONNECTION | Sspi.ISC_REQ_CONFIDENTIALITY,
                        Sspi.SECURITY_NATIVE_DREP,
                        phServerContext,
                        pbServerToken,
                        pfServerContextAttr,
                        ptsServerExpiry);
                assertTrue(serverRc == W32Errors.SEC_I_CONTINUE_NEEDED || serverRc == W32Errors.SEC_E_OK);
            }
        } while(serverRc != W32Errors.SEC_E_OK || clientRc != W32Errors.SEC_E_OK);

        assertTrue((pfServerContextAttr.getValue() & Sspi.ISC_REQ_CONFIDENTIALITY) == Sspi.ISC_REQ_CONFIDENTIALITY);
        assertTrue((pfClientContextAttr.getValue() & Sspi.ISC_REQ_CONFIDENTIALITY) == Sspi.ISC_REQ_CONFIDENTIALITY);

        // Fetch size limits for crypto functions
        SecPkgContext_Sizes sizes = new SecPkgContext_Sizes();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.QueryContextAttributes(phClientContext, Sspi.SECPKG_ATTR_SIZES, sizes));

        // Create sample input data
        byte[] inputData = "Hallo Welt".getBytes(Charset.forName("ASCII"));

        // Make signature, buffer 0 holds signature data, buffer 1 holds the
        // clear text data
        ManagedSecBufferDesc signingBuffers = new ManagedSecBufferDesc(2);

        Memory tokenMemory = new Memory(sizes.cbMaxSignature);
        Memory dataMemory = new Memory(inputData.length);
        dataMemory.write(0, inputData, 0, inputData.length);

        signingBuffers.getBuffer(0).BufferType = Sspi.SECBUFFER_TOKEN;
        signingBuffers.getBuffer(0).cbBuffer = (int) tokenMemory.size();
        signingBuffers.getBuffer(0).pvBuffer = tokenMemory;
        signingBuffers.getBuffer(1).BufferType = Sspi.SECBUFFER_DATA;
        signingBuffers.getBuffer(1).cbBuffer = (int) dataMemory.size();
        signingBuffers.getBuffer(1).pvBuffer = dataMemory;

        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.MakeSignature(phClientContext, 0, signingBuffers, 0));

        byte[] signingData = signingBuffers.getBuffer(0).getBytes();
        byte[] signedData = signingBuffers.getBuffer(1).getBytes();

        assertNotNull(signingData);
        assertNotNull(signedData);
        assertTrue(signingData.length > 0);
        assertTrue(signedData.length > 0);
        assertTrue(Arrays.equals(inputData, signedData));

        // Do verification of data with the pfServerContextAttr
        ManagedSecBufferDesc verificationBuffers = new ManagedSecBufferDesc(2);

        Memory verificationSigningMemory = new Memory(signingData.length);
        verificationSigningMemory.write(0, signingData, 0, signingData.length);
        Memory verificiationSignedMemory = new Memory(signedData.length);
        verificiationSignedMemory.write(0, signedData, 0, signedData.length);

        verificationBuffers.getBuffer(0).BufferType = Sspi.SECBUFFER_TOKEN;
        verificationBuffers.getBuffer(0).cbBuffer = (int) verificationSigningMemory.size();
        verificationBuffers.getBuffer(0).pvBuffer = verificationSigningMemory;
        verificationBuffers.getBuffer(1).BufferType = Sspi.SECBUFFER_DATA;
        verificationBuffers.getBuffer(1).cbBuffer = (int) verificiationSignedMemory.size();
        verificationBuffers.getBuffer(1).pvBuffer = verificiationSignedMemory;

        IntByReference qosResult = new IntByReference();
        assertEquals(W32Errors.SEC_E_OK, Secur32.INSTANCE.VerifySignature(phServerContext, verificationBuffers, 0, qosResult));

        byte[] decryptMessageResult = verificationBuffers.getBuffer(1).getBytes();
        assertTrue(Arrays.equals(inputData, decryptMessageResult));

        // Modify message and retry decryption. Decryption is expected to be
        // refused and the buffers should be untouched.
        // Modification is done by injecting a NULL byte into the beginning of
        // the message

        ManagedSecBufferDesc verificationBuffers2 = new ManagedSecBufferDesc(2);

        Memory verificationSigingMemory2 = new Memory(signingData.length);
        verificationSigingMemory2.write(0, signingData, 0, signingData.length);
        Memory verificationSignedMemory2 = new Memory(signedData.length + 1);
        verificationSignedMemory2.write(1, signedData, 0, signedData.length);

        verificationBuffers2.getBuffer(0).BufferType = Sspi.SECBUFFER_TOKEN;
        verificationBuffers2.getBuffer(0).cbBuffer = (int) verificationSigingMemory2.size();
        verificationBuffers2.getBuffer(0).pvBuffer = verificationSigingMemory2;
        verificationBuffers2.getBuffer(1).BufferType = Sspi.SECBUFFER_DATA;
        verificationBuffers2.getBuffer(1).cbBuffer = (int) verificationSignedMemory2.size();
        verificationBuffers2.getBuffer(1).pvBuffer = verificationSignedMemory2;

        qosResult = new IntByReference();
        assertEquals(W32Errors.SEC_E_MESSAGE_ALTERED, Secur32.INSTANCE.VerifySignature(phServerContext, verificationBuffers2, 0, qosResult));

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

    public void testCompleteAuthToken() {
        /*
        This is not a real test of the function, it just ensures, that it is
        callable and returns a sane error code.
        */
        int result = Secur32.INSTANCE.CompleteAuthToken(null, null);
        assertTrue(String.format("Unexpected error code: 0x%08X%n", result),
                result == WinError.SEC_E_INVALID_HANDLE
                || result == WinError.SEC_E_INVALID_TOKEN);
    }
}
