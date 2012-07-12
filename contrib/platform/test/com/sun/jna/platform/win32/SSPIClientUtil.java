package com.sun.jna.platform.win32;

import com.sun.jna.platform.win32.Sspi.CredHandle;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.Sspi.TimeStamp;
import com.sun.jna.ptr.IntByReference;

public class SSPIClientUtil {

    private static SSPIClientUtil INSTANCE;

    public static SSPIClientUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SSPIClientUtil();
        }
        return INSTANCE;
    }

    private SecBufferDesc clientToken = null;
    private SecBufferDesc serverToken = null;
    private boolean initialized = false;
    private CredHandle phClientCredential = null;
    private TimeStamp ptsClientExpiry = null;
    private CtxtHandle phClientContext = null;
    private IntByReference pfClientContextAttr = null;

    private SSPIClientUtil() {
        this.init();
    }

    private void init() {

        phClientCredential = new CredHandle();
        ptsClientExpiry = new TimeStamp();
        phClientContext = new CtxtHandle();
        pfClientContextAttr = new IntByReference();

        this.clientToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN,
                Sspi.MAX_TOKEN_SIZE);
        this.serverToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN,
                Sspi.MAX_TOKEN_SIZE);

        try {
            if (W32Errors.SEC_E_OK != Secur32.INSTANCE
                    .AcquireCredentialsHandle(Advapi32Util.getUserName(),
                            "Kerberos",
                            new Long(Sspi.SECPKG_CRED_OUTBOUND).longValue(),
                            null, null, null, null, phClientCredential,
                            ptsClientExpiry)) {
                throw new RuntimeException("AcquireCredentialsHandle");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.update(this.serverToken);
    }

    private void update(SecBufferDesc serverToken) {
        this.serverToken = serverToken;
        try {
            int clientRc = -1;
            this.clientToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN,
                    Sspi.MAX_TOKEN_SIZE);
            clientRc = Secur32.INSTANCE.InitializeSecurityContext(
                    phClientCredential, phClientContext.isNull() ? null
                            : phClientContext, Advapi32Util.getUserName(),
                    new Long(Sspi.ISC_REQ_CONNECTION).longValue(), new Long(0)
                            .longValue(), new Long(Sspi.SECURITY_NATIVE_DREP)
                            .longValue(), serverToken, new Long(0).longValue(),
                    phClientContext, clientToken, pfClientContextAttr, null);

            if (clientRc == W32Errors.SEC_E_OK) {
                this.initialized = true;
                return;
            }
            if (clientRc != W32Errors.SEC_I_CONTINUE_NEEDED) {
                throw new RuntimeException("InitializeSecurityContext");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getClientToken() {
        return this.clientToken.getBytes();
    }

    public byte[] setServerToken(byte[] serverToken) {
        if (serverToken != null && serverToken.length > 0) {
            this.serverToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN,
                    serverToken);
            this.update(this.serverToken);
        }
        return this.getClientToken();
    }

    public boolean isSecContextEstablished() {
        return this.initialized;
    }

    public void disposeContext() {
        Secur32.INSTANCE.DeleteSecurityContext(this.phClientContext);
        Secur32.INSTANCE.FreeCredentialsHandle(this.phClientCredential);
    }

}
