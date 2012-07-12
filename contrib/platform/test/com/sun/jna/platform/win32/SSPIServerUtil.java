package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.Sspi.TimeStamp;
import com.sun.jna.ptr.IntByReference;

public class SSPIServerUtil {

    private static SSPIServerUtil INSTANCE;

    public static SSPIServerUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SSPIServerUtil();
        }
        return INSTANCE;
    }

    private SecBufferDesc clientToken = null;
    private SecBufferDesc serverToken = null;
    private boolean initialized = false;
    private CredHandle phServerCredential = null;
    private TimeStamp ptsServerExpiry = null;
    private CtxtHandle phServerContext = null;
    private IntByReference pfServerContextAttr = null;

    private SSPIServerUtil() {
        this.init();
    }

    private void init() {

        phServerCredential = new CredHandle();
        ptsServerExpiry = new TimeStamp();
        phServerContext = new CtxtHandle();
        pfServerContextAttr = new IntByReference();

        this.serverToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN,
                Sspi.MAX_TOKEN_SIZE);
        this.clientToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN,
                Sspi.MAX_TOKEN_SIZE);

        try {
            if (W32Errors.SEC_E_OK != Secur32.INSTANCE
                    .AcquireCredentialsHandle(null, "Kerberos", new Long(
                            Sspi.SECPKG_CRED_INBOUND).longValue(), null, null,
                            null, null, phServerCredential, ptsServerExpiry)) {
                throw new RuntimeException("AcquireCredentialsHandle");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void update(SecBufferDesc token) {
        this.clientToken = token;
        try {
            int serverRc = -1;
            serverRc = Secur32.INSTANCE.AcceptSecurityContext(
                    phServerCredential, phServerContext.isNull() ? null
                            : phServerContext, clientToken, new Long(
                            Sspi.ISC_REQ_CONNECTION).longValue(), new Long(
                            Sspi.SECURITY_NATIVE_DREP).longValue(),
                    phServerContext, serverToken, pfServerContextAttr,
                    ptsServerExpiry);
            if (serverRc == W32Errors.SEC_E_OK) {
                this.initialized = true;
                return;
            }
            if (serverRc != W32Errors.SEC_I_CONTINUE_NEEDED) {
                throw new RuntimeException("InitializeSecurityContext");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] setClientToken(byte[] token) {
        if (token != null && token.length > 0) {
            this.clientToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, token);
            this.update(this.clientToken);
        }
        return this.getServerToken();
    }

    public byte[] getServerToken() {
        return this.serverToken.getBytes();
    }

    public boolean isSecContextEstablished() {
        return this.initialized;
    }

    public String getPrincipal() {
        IntByReference len = new IntByReference();
        Secur32.INSTANCE.GetUserNameEx(
                Secur32.EXTENDED_NAME_FORMAT.NameUserPrincipal, null, len);
        char[] buffer = new char[len.getValue() + 1];
        Secur32.INSTANCE.GetUserNameEx(
                Secur32.EXTENDED_NAME_FORMAT.NameUserPrincipal, buffer, len);
        String username = Native.toString(buffer);
        return username;
    }

    public void disposeContext() {
        Secur32.INSTANCE.DeleteSecurityContext(this.phServerContext);
        Secur32.INSTANCE.FreeCredentialsHandle(this.phServerCredential);
    }

}
