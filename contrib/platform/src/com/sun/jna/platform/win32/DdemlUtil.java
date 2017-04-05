/*
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
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.DWORD_PTR;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.Ddeml.CONVCONTEXT;
import com.sun.jna.platform.win32.Ddeml.CONVINFO;
import com.sun.jna.platform.win32.Ddeml.HCONV;
import com.sun.jna.platform.win32.Ddeml.HCONVLIST;
import com.sun.jna.platform.win32.Ddeml.HDDEDATA;
import com.sun.jna.platform.win32.Ddeml.HSZ;
import com.sun.jna.platform.win32.Ddeml.HSZPAIR;
import com.sun.jna.platform.win32.User32Util.MessageLoopThread;
import com.sun.jna.platform.win32.User32Util.MessageLoopThread.Handler;
import com.sun.jna.win32.W32APIOptions;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DdemlUtil defines helper classes, that help with manageing DDE communications.
 */
public abstract class DdemlUtil {
    /**
     * StandaloneDdeClient is a convenience class, that wraps a DdeClient and
     * a {@link User32Util.MessageLoopThread}. The DdeClient needs a running
     * windows message loop, which is supplied by the MessageLoopThread.
     */
    public static class StandaloneDdeClient implements IDdeClient, Closeable {

        private final MessageLoopThread messageLoop = new MessageLoopThread();
        private final IDdeClient ddeClient;
        private final IDdeClient clientDelegate;

        public StandaloneDdeClient() {
            ddeClient = new DdeClient();
            IDdeClient messageLoopHandler = (IDdeClient) Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(),
                    new Class[]{IDdeClient.class},
                    messageLoop.new Handler(ddeClient));
            clientDelegate = (IDdeClient) Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(),
                    new Class[]{IDdeClient.class},
                    new MessageLoopWrapper(messageLoop, messageLoopHandler));
            messageLoop.setDaemon(true);
            messageLoop.start();
        }
        
        public Integer getInstanceIdentitifier() {
            return ddeClient.getInstanceIdentitifier();
        }
        
        public void initialize(int afCmd) throws DdemlException {
            clientDelegate.initialize(afCmd);
        }

        public Ddeml.HSZ createStringHandle(String value) throws DdemlException {
            return clientDelegate.createStringHandle(value);
        }

        public void nameService(Ddeml.HSZ name, int afCmd) throws DdemlException {
            clientDelegate.nameService(name, afCmd);
        }

        public int getLastError() {
            return clientDelegate.getLastError();
        }

        public IDdeConnection connect(Ddeml.HSZ service, Ddeml.HSZ topic, Ddeml.CONVCONTEXT convcontext) {
            return clientDelegate.connect(service, topic, convcontext);
        }

        public String queryString(Ddeml.HSZ value) throws DdemlException {
            return clientDelegate.queryString(value);
        }

        @Override
        public Ddeml.HDDEDATA createDataHandle(Pointer pSrc, int cb, int cbOff, Ddeml.HSZ hszItem, int wFmt, int afCmd) {
            return clientDelegate.createDataHandle(pSrc, cb, cbOff, hszItem, wFmt, afCmd);
        }

        @Override
        public void freeDataHandle(Ddeml.HDDEDATA hData) {
            clientDelegate.freeDataHandle(hData);
        }

        @Override
        public Ddeml.HDDEDATA addData(Ddeml.HDDEDATA hData, Pointer pSrc, int cb, int cbOff) {
            return clientDelegate.addData(hData, pSrc, cb, cbOff);
        }

        @Override
        public int getData(Ddeml.HDDEDATA hData, Pointer pDst, int cbMax, int cbOff) {
            return clientDelegate.getData(hData, pDst, cbMax, cbOff);
        }

        @Override
        public Pointer accessData(Ddeml.HDDEDATA hData, WinDef.DWORDByReference pcbDataSize) {
            return clientDelegate.accessData(hData, pcbDataSize);
        }

        @Override
        public void unaccessData(Ddeml.HDDEDATA hData) {
            clientDelegate.unaccessData(hData);
        }

        @Override
        public void postAdvise(Ddeml.HSZ hszTopic, Ddeml.HSZ hszItem) {
            clientDelegate.postAdvise(hszTopic, hszItem);
        }
        
        public void close() throws IOException {
            clientDelegate.uninitialize();
            messageLoop.exit();
        }

        @Override
        public boolean freeStringHandle(Ddeml.HSZ value) {
            return clientDelegate.freeStringHandle(value);
        }

        @Override
        public boolean keepStringHandle(Ddeml.HSZ value) {
            return clientDelegate.keepStringHandle(value);
        }

        @Override
        public void abandonTransactions() {
            clientDelegate.abandonTransactions();
        }

        @Override
        public IDdeConnectionList connectList(Ddeml.HSZ service, Ddeml.HSZ topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
            return clientDelegate.connectList(service, topic, existingList, ctx);
        }

        @Override
        public boolean enableCallback(int wCmd) {
            return clientDelegate.enableCallback(wCmd);
        }

        @Override
        public IDdeConnection wrap(HCONV conv) {
            return clientDelegate.wrap(conv);
        }

        @Override
        public IDdeConnection connect(String service, String topic, Ddeml.CONVCONTEXT convcontext) {
            return clientDelegate.connect(service, topic, convcontext);
        }

        @Override
        public boolean uninitialize() {
            return clientDelegate.uninitialize();
        }
        
        @Override
        public void postAdvise(String hszTopic, String hszItem) {
            clientDelegate.postAdvise(hszTopic, hszItem);
        }

        @Override
        public IDdeConnectionList connectList(String service, String topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
            return clientDelegate.connectList(service, topic, existingList, ctx);
        }

        @Override
        public void nameService(String name, int afCmd) throws DdemlException {
            clientDelegate.nameService(name, afCmd);
        }

        @Override
        public void registerAdvstartHandler(AdvstartHandler handler) {
            clientDelegate.registerAdvstartHandler(handler);
        }

        @Override
        public void unregisterAdvstartHandler(AdvstartHandler handler) {
            clientDelegate.unregisterAdvstartHandler(handler);
        }

        @Override
        public void registerAdvstopHandler(AdvstopHandler handler) {
            clientDelegate.registerAdvstopHandler(handler);
        }

        @Override
        public void unregisterAdvstopHandler(AdvstopHandler handler) {
            clientDelegate.unregisterAdvstopHandler(handler);
        }

        @Override
        public void registerConnectHandler(ConnectHandler handler) {
            clientDelegate.registerConnectHandler(handler);
        }

        @Override
        public void unregisterConnectHandler(ConnectHandler handler) {
            clientDelegate.unregisterConnectHandler(handler);
        }

        @Override
        public void registerAdvReqHandler(AdvreqHandler handler) {
            clientDelegate.registerAdvReqHandler(handler);
        }

        @Override
        public void unregisterAdvReqHandler(AdvreqHandler handler) {
            clientDelegate.unregisterAdvReqHandler(handler);
        }

        @Override
        public void registerRequestHandler(RequestHandler handler) {
            clientDelegate.registerRequestHandler(handler);
        }

        @Override
        public void unregisterRequestHandler(RequestHandler handler) {
            clientDelegate.unregisterRequestHandler(handler);
        }

        @Override
        public void registerWildconnectHandler(WildconnectHandler handler) {
            clientDelegate.registerWildconnectHandler(handler);
        }

        @Override
        public void unregisterWildconnectHandler(WildconnectHandler handler) {
            clientDelegate.unregisterWildconnectHandler(handler);
        }

        @Override
        public void registerAdvdataHandler(AdvdataHandler handler) {
            clientDelegate.registerAdvdataHandler(handler);
        }

        @Override
        public void unregisterAdvdataHandler(AdvdataHandler handler) {
            clientDelegate.unregisterAdvdataHandler(handler);
        }

        @Override
        public void registerExecuteHandler(ExecuteHandler handler) {
            clientDelegate.registerExecuteHandler(handler);
        }

        @Override
        public void unregisterExecuteHandler(ExecuteHandler handler) {
            clientDelegate.unregisterExecuteHandler(handler);
        }

        @Override
        public void registerPokeHandler(PokeHandler handler) {
            clientDelegate.registerPokeHandler(handler);
        }

        @Override
        public void unregisterPokeHandler(PokeHandler handler) {
            clientDelegate.unregisterPokeHandler(handler);
        }

        @Override
        public void registerConnectConfirmHandler(ConnectConfirmHandler handler) {
            clientDelegate.registerConnectConfirmHandler(handler);
        }

        @Override
        public void unregisterConnectConfirmHandler(ConnectConfirmHandler handler) {
            clientDelegate.unregisterConnectConfirmHandler(handler);
        }

        @Override
        public void registerDisconnectHandler(DisconnectHandler handler) {
            clientDelegate.registerDisconnectHandler(handler);
        }

        @Override
        public void unregisterDisconnectHandler(DisconnectHandler handler) {
            clientDelegate.unregisterDisconnectHandler(handler);
        }

        @Override
        public void registerErrorHandler(ErrorHandler handler) {
            clientDelegate.registerErrorHandler(handler);
        }

        @Override
        public void unregisterErrorHandler(ErrorHandler handler) {
            clientDelegate.unregisterErrorHandler(handler);
        }

        @Override
        public void registerRegisterHandler(RegisterHandler handler) {
            clientDelegate.registerRegisterHandler(handler);
        }

        @Override
        public void unregisterRegisterHandler(RegisterHandler handler) {
            clientDelegate.unregisterRegisterHandler(handler);
        }

        @Override
        public void registerXactCompleteHandler(XactCompleteHandler handler) {
            clientDelegate.registerXactCompleteHandler(handler);
        }

        @Override
        public void unregisterXactCompleteHandler(XactCompleteHandler handler) {
            clientDelegate.unregisterXactCompleteHandler(handler);
        }

        @Override
        public void registerUnregisterHandler(UnregisterHandler handler) {
            clientDelegate.registerUnregisterHandler(handler);
        }

        @Override
        public void unregisterUnregisterHandler(UnregisterHandler handler) {
            clientDelegate.unregisterUnregisterHandler(handler);
        }

        @Override
        public void registerMonitorHandler(MonitorHandler handler) {
            clientDelegate.registerMonitorHandler(handler);
        }

        @Override
        public void unregisterMonitorHandler(MonitorHandler handler) {
            clientDelegate.unregisterMonitorHandler(handler);
        }
    }
    
    private static class MessageLoopWrapper implements InvocationHandler {
        private final Object delegate;
        private final MessageLoopThread loopThread;

        public MessageLoopWrapper(MessageLoopThread thread, Object delegate) {
            this.loopThread = thread;
            this.delegate = delegate;
        }
        
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            try {
                Object result = method.invoke(delegate, args);
                Class<?> wrapClass = null;
                if ( result instanceof IDdeConnection ) {
                    wrapClass = IDdeConnection.class;
                } else if (result instanceof IDdeConnectionList) {
                    wrapClass = IDdeConnectionList.class;
                } else if (result instanceof IDdeClient) {
                    wrapClass = IDdeClient.class;
                }
                if(wrapClass != null && method.getReturnType().isAssignableFrom(wrapClass)) {
                    result = wrap(result, wrapClass);
                }
                return result;
            } catch (InvocationTargetException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof Exception) {
                    throw (Exception) cause;
                } else {
                    throw ex;
                }
            }
        }
        
        private <V> V wrap(V delegate, Class clazz) {
            V messageLoopHandler = (V) Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(),
                    new Class[]{clazz},
                    loopThread.new Handler(delegate));
            V clientDelegate = (V) Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(),
                    new Class[]{clazz},
                    new MessageLoopWrapper(loopThread, messageLoopHandler));
            return clientDelegate;
        }
    }
        
    public static class DdeConnection implements IDdeConnection {
        private HCONV conv;
        private final IDdeClient client;

        public DdeConnection(IDdeClient client, HCONV conv) {
            this.conv = conv;
            this.client = client;
        }

        public Ddeml.HCONV getConv() {
            return conv;
        }
       
        @Override
        public void abandonTransaction(int transactionId) {
            boolean result = Ddeml.INSTANCE.DdeAbandonTransaction(client.getInstanceIdentitifier(), conv, transactionId);
            if(! result) {
                throw DdemlException.create(client.getLastError());
            }
        }

        public void abandonTransactions() {
            boolean result = Ddeml.INSTANCE.DdeAbandonTransaction(client.getInstanceIdentitifier(), conv, 0);
            if(! result) {
                throw DdemlException.create(client.getLastError());
            }
        }
        
        @Override
        public Ddeml.HDDEDATA clientTransaction(Pointer data, int dataLength, Ddeml.HSZ item, int wFmt, int transaction, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle) {
            if(timeout == Ddeml.TIMEOUT_ASYNC && result == null) {
                result = new WinDef.DWORDByReference(); 
            }
            Ddeml.HDDEDATA returnData = Ddeml.INSTANCE.DdeClientTransaction(data, dataLength, conv, item, wFmt, transaction, timeout, result);
            if(returnData == null) {
                throw DdemlException.create(client.getLastError());
            }
            if (userHandle != null) {
                if (timeout != Ddeml.TIMEOUT_ASYNC) {
                    setUserHandle(Ddeml.QID_SYNC, userHandle);
                } else {
                    setUserHandle(result.getValue().intValue(), userHandle);
                }
            }
            return returnData;
        }

        public Ddeml.HDDEDATA clientTransaction(Pointer data, int dataLength, String item, int wFmt, int transaction, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle) {
            HSZ itemHSZ = null;
            try {
                itemHSZ = client.createStringHandle(item);
                return clientTransaction(data, dataLength, itemHSZ, wFmt, transaction, timeout, result, userHandle);
            } finally {
                client.freeStringHandle(itemHSZ);
            }
        }
        
        @Override
        public void poke(Pointer data, int dataLength, Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle) {
            clientTransaction(data, dataLength, item, wFmt, Ddeml.XTYP_POKE, timeout, result, userHandle);
        }
        
        public void poke(Pointer data, int dataLength, String item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle) {
            HSZ itemHSZ = null;
            try {
                itemHSZ = client.createStringHandle(item);
                poke(data, dataLength, itemHSZ, wFmt, timeout, result, userHandle);
            } finally {
                client.freeStringHandle(itemHSZ);
            }
        }
        
        @Override
        public Ddeml.HDDEDATA request(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle) {
            return clientTransaction(Pointer.NULL, 0, item, wFmt, Ddeml.XTYP_REQUEST, timeout, result, userHandle);
        }
        
        public Ddeml.HDDEDATA request(String item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle) {
            HSZ itemHSZ = null;
            try {
                itemHSZ = client.createStringHandle(item);
                return request(itemHSZ, wFmt, timeout, result, userHandle);
            } finally {
                client.freeStringHandle(itemHSZ);
            }
        }

        @Override
        public void execute(String executeString, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle) {
            Memory mem = new Memory(executeString.length() * 2 + 2);
            mem.setWideString(0, executeString);
            clientTransaction(mem, (int) mem.size(), (HSZ) null, 0, Ddeml.XTYP_EXECUTE, timeout, result, userHandle);
        }

        @Override
        public void advstart(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle) {
            clientTransaction(Pointer.NULL, 0, item, wFmt, Ddeml.XTYP_ADVSTART, timeout, result, userHandle);
        }

        public void advstart(String item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle) {
            HSZ itemHSZ = null;
            try {
                itemHSZ = client.createStringHandle(item);
                advstart(itemHSZ, wFmt, timeout, result, userHandle);
            } finally {
                client.freeStringHandle(itemHSZ);
            }
        }
        
        @Override
        public void advstop(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle) {
            clientTransaction(Pointer.NULL, 0, item, wFmt, Ddeml.XTYP_ADVSTOP, timeout, result, userHandle);
        }
        
        public void advstop(String item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle) {
            HSZ itemHSZ = null;
            try {
                itemHSZ = client.createStringHandle(item);
                advstop(itemHSZ, wFmt, timeout, result, userHandle);
            } finally {
                client.freeStringHandle(itemHSZ);
            }
        }

        public void impersonateClient() {
            boolean result = Ddeml.INSTANCE.DdeImpersonateClient(conv);
            if (!result) {
                throw DdemlException.create(client.getLastError());
            }
        }

        public void close() {
            boolean result = Ddeml.INSTANCE.DdeDisconnect(conv);
            if(! result) {
                throw DdemlException.create(client.getLastError());
            }
        }

        public void reconnect() {
            Ddeml.HCONV newConv = Ddeml.INSTANCE.DdeReconnect(conv);
            if(newConv != null) {
                conv = newConv;
            } else {
                throw DdemlException.create(client.getLastError());
            }
        }

        public boolean enableCallback(int wCmd) {
            boolean result = Ddeml.INSTANCE.DdeEnableCallback(client.getInstanceIdentitifier(), conv, wCmd);
            if ((!result) && wCmd == Ddeml.EC_QUERYWAITING) {
                throw DdemlException.create(client.getLastError());
            }
            return result;
        }

        public void setUserHandle(int id, DWORD_PTR hUser) throws DdemlException {
            boolean result = Ddeml.INSTANCE.DdeSetUserHandle(conv, id, hUser);
            if (!result) {
                throw DdemlException.create(client.getLastError());
            }
        }

        public CONVINFO queryConvInfo(int idTransaction) throws DdemlException {
            CONVINFO convInfo = new Ddeml.CONVINFO();
            convInfo.cb = convInfo.size();
            convInfo.ConvCtxt.cb = convInfo.ConvCtxt.size();
            convInfo.write();
            int result = Ddeml.INSTANCE.DdeQueryConvInfo(conv, idTransaction, convInfo);
            if (result == 0) {
                throw DdemlException.create(client.getLastError());
            }
            return convInfo;
        }
       
    }
    
    public static class DdeConnectionList implements IDdeConnectionList {
        private final IDdeClient client;
        private final HCONVLIST convList;
        
        public DdeConnectionList(IDdeClient client, Ddeml.HCONVLIST convList) {
            this.convList = convList;
            this.client = client;
        }

        @Override
        public Ddeml.HCONVLIST getHandle() {
            return this.convList;
        }

        @Override
        public IDdeConnection queryNextServer(IDdeConnection prevConnection) {
            Ddeml.HCONV conv = Ddeml.INSTANCE.DdeQueryNextServer(
                    convList,
                    prevConnection != null ? prevConnection.getConv() : null);
            if(conv != null) {
                return new DdeConnection(client, conv);
            } else {
                return null;
            }
        }

        @Override
        public void close() {
            boolean result = Ddeml.INSTANCE.DdeDisconnectList(convList);
            if(! result){
                throw DdemlException.create(client.getLastError());
            }
        }
    }
    
    public static class DdeClient implements IDdeClient {
        private Integer idInst;
        private final DdeAdapter ddeAdapter = new DdeAdapter();
        
        public Integer getInstanceIdentitifier() {
            return idInst;
        }
        
        public void initialize(int afCmd) throws DdemlException {
            WinDef.DWORDByReference pidInst = new WinDef.DWORDByReference();
            Integer result = Ddeml.INSTANCE.DdeInitialize(pidInst, ddeAdapter, afCmd, 0);
            if(result != Ddeml.DMLERR_NO_ERROR) {
                throw DdemlException.create(result);
            }
            idInst = pidInst.getValue().intValue();
            if(ddeAdapter instanceof DdeAdapter) {
                ddeAdapter.setInstanceIdentifier(idInst);
            }
        }
        
        public Ddeml.HSZ createStringHandle(String value) throws DdemlException {
            if(value == null) {
                return null;
            }
            int codePage;
            if(W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
                codePage = Ddeml.CP_WINUNICODE;
            } else {
                codePage = Ddeml.CP_WINANSI;
            }
            Ddeml.HSZ handle = Ddeml.INSTANCE.DdeCreateStringHandle(idInst, value, codePage);
            if(handle == null) {
                throw DdemlException.create(getLastError());
            }
            return handle;
        }
        
        public void nameService(Ddeml.HSZ name, int afCmd) throws DdemlException {
            Ddeml.HDDEDATA handle = Ddeml.INSTANCE.DdeNameService(idInst, name, new Ddeml.HSZ(), afCmd);
            if (handle == null) {
                throw DdemlException.create(getLastError());
            }
        }

        public void nameService(String name, int afCmd) throws DdemlException {
            HSZ nameHSZ = null;
            try {
                nameHSZ = createStringHandle(name);
                nameService(nameHSZ, afCmd);
            } finally {
                freeStringHandle(nameHSZ);
            }
        }

        public int getLastError() {
            return Ddeml.INSTANCE.DdeGetLastError(idInst);
        }
        
        public IDdeConnection connect(Ddeml.HSZ service, Ddeml.HSZ topic, Ddeml.CONVCONTEXT convcontext) {
            Ddeml.HCONV hconv = Ddeml.INSTANCE.DdeConnect(idInst, service, topic, convcontext);
            if(hconv == null) {
                throw DdemlException.create(getLastError());
            }
            return new DdeConnection(this, hconv);
        }
        
        public IDdeConnection connect(String service, String topic, Ddeml.CONVCONTEXT convcontext) {
            HSZ serviceHSZ = null;
            HSZ topicHSZ = null;
            try {
                serviceHSZ = createStringHandle(service);
                topicHSZ = createStringHandle(topic);
                return connect(serviceHSZ, topicHSZ, convcontext);
            } finally {
                freeStringHandle(topicHSZ);
                freeStringHandle(serviceHSZ);
            }
        }

        public String queryString(Ddeml.HSZ value) throws DdemlException {
            int codePage;
            int byteWidth;
            if(W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
                codePage = Ddeml.CP_WINUNICODE;
                byteWidth = 2;
            } else {
                codePage = Ddeml.CP_WINANSI;
                byteWidth = 1;
            }
            Memory buffer = new Memory((256 + 1) * byteWidth);
            try {
                int length = Ddeml.INSTANCE.DdeQueryString(idInst, value, buffer, 256, codePage);
                if (W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
                    return buffer.getWideString(0);
                } else {
                    return buffer.getString(0);
                }
            } finally {
                buffer.valid();
            }
        }
        
        
        public Ddeml.HDDEDATA createDataHandle(Pointer pSrc, int cb, int cbOff, Ddeml.HSZ hszItem, int wFmt, int afCmd) {
            Ddeml.HDDEDATA returnData = Ddeml.INSTANCE.DdeCreateDataHandle(idInst, pSrc, cb, cbOff, hszItem, wFmt, afCmd);
            if(returnData == null) {
                throw DdemlException.create(getLastError());
            }
            return returnData;
        }

        public void freeDataHandle(Ddeml.HDDEDATA hData) {
            boolean result = Ddeml.INSTANCE.DdeFreeDataHandle(hData);
            if(! result) {
                throw DdemlException.create(getLastError());
            }
        }

        public Ddeml.HDDEDATA addData(Ddeml.HDDEDATA hData, Pointer pSrc, int cb, int cbOff) {
            Ddeml.HDDEDATA newHandle = Ddeml.INSTANCE.DdeAddData(hData, pSrc, cb, cbOff);
            if(newHandle == null) {
                throw DdemlException.create(getLastError());
            }
            return newHandle;
        }

        public int getData(Ddeml.HDDEDATA hData, Pointer pDst, int cbMax, int cbOff) {
            int result = Ddeml.INSTANCE.DdeGetData(hData, pDst, cbMax, cbOff);
            int errorCode = getLastError();
            if(errorCode != Ddeml.DMLERR_NO_ERROR) {
                throw DdemlException.create(errorCode);
            }
            return result;
        }

        public Pointer accessData(Ddeml.HDDEDATA hData, WinDef.DWORDByReference pcbDataSize) {
            Pointer result = Ddeml.INSTANCE.DdeAccessData(hData, pcbDataSize);
            if(result == null) {
                throw DdemlException.create(getLastError());
            }
            return result;
        }

        public void unaccessData(Ddeml.HDDEDATA hData) {
            boolean result = Ddeml.INSTANCE.DdeUnaccessData(hData);
            if (!result) {
                throw DdemlException.create(getLastError());
            }
        }

        public void postAdvise(Ddeml.HSZ hszTopic, Ddeml.HSZ hszItem) {
            boolean result = Ddeml.INSTANCE.DdePostAdvise(idInst, hszTopic, hszItem);
            if (!result) {
                throw DdemlException.create(getLastError());
            }
        }
        
        public void postAdvise(String topic, String item) {
            HSZ itemHSZ = null;
            HSZ topicHSZ = null;
            try {
                topicHSZ = createStringHandle(topic);
                itemHSZ = createStringHandle(item);
                postAdvise(topicHSZ, itemHSZ);
            } finally {
                freeStringHandle(topicHSZ);
                freeStringHandle(itemHSZ);
            }
        }

        public boolean freeStringHandle(Ddeml.HSZ value) {
            if(value == null) {
                return true;
            }
            return Ddeml.INSTANCE.DdeFreeStringHandle(idInst, value);
        }

        public boolean keepStringHandle(Ddeml.HSZ value) {
            return Ddeml.INSTANCE.DdeKeepStringHandle(idInst, value);
        }

        public void abandonTransactions() {
            boolean result = Ddeml.INSTANCE.DdeAbandonTransaction(idInst, null, 0);
            if(! result) {
                throw DdemlException.create(getLastError());
            }
        }

        public IDdeConnectionList connectList(Ddeml.HSZ service, Ddeml.HSZ topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
            Ddeml.HCONVLIST convlist = Ddeml.INSTANCE.DdeConnectList(idInst, service, topic, existingList != null ? existingList.getHandle() : null, ctx);
            if(convlist == null) {
                throw DdemlException.create(getLastError());
            } else {
                return new DdeConnectionList(this, convlist);
            }
        }

        public IDdeConnectionList connectList(String service, String topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx) {
            HSZ serviceHSZ = null;
            HSZ topicHSZ = null;
            try {
                serviceHSZ = createStringHandle(service);
                topicHSZ = createStringHandle(topic);
                return connectList(serviceHSZ, topicHSZ, existingList, ctx);
            } finally {
                freeStringHandle(topicHSZ);
                freeStringHandle(serviceHSZ);
            }
        }
        
        public boolean enableCallback(int wCmd) {
            boolean result = Ddeml.INSTANCE.DdeEnableCallback(idInst, null, wCmd);
            if ((!result) && wCmd != Ddeml.EC_QUERYWAITING) {
                int errorCode = getLastError();
                if(errorCode != Ddeml.DMLERR_NO_ERROR) {
                    throw DdemlException.create(getLastError());
                }
            }
            return result;
        }
        
        public boolean uninitialize() {
            return Ddeml.INSTANCE.DdeUninitialize(idInst);
        }
        
        public void close() {
            uninitialize();
        }

        public IDdeConnection wrap(HCONV hconv) {
            return new DdeConnection(this, hconv);
        }
        

        public void unregisterDisconnectHandler(DisconnectHandler handler) {
            ddeAdapter.unregisterDisconnectHandler(handler);
        }
        
        public void registerAdvstartHandler(AdvstartHandler handler) {
            ddeAdapter.registerAdvstartHandler(handler);
        }

        public void unregisterAdvstartHandler(AdvstartHandler handler) {
            ddeAdapter.unregisterAdvstartHandler(handler);
        }

        public void registerAdvstopHandler(AdvstopHandler handler) {
            ddeAdapter.registerAdvstopHandler(handler);
        }

        public void unregisterAdvstopHandler(AdvstopHandler handler) {
            ddeAdapter.unregisterAdvstopHandler(handler);
        }

        public void registerConnectHandler(ConnectHandler handler) {
            ddeAdapter.registerConnectHandler(handler);
        }

        public void unregisterConnectHandler(ConnectHandler handler) {
            ddeAdapter.unregisterConnectHandler(handler);
        }

        public void registerAdvReqHandler(AdvreqHandler handler) {
            ddeAdapter.registerAdvReqHandler(handler);
        }

        public void unregisterAdvReqHandler(AdvreqHandler handler) {
            ddeAdapter.unregisterAdvReqHandler(handler);
        }

        public void registerRequestHandler(RequestHandler handler) {
            ddeAdapter.registerRequestHandler(handler);
        }

        public void unregisterRequestHandler(RequestHandler handler) {
            ddeAdapter.unregisterRequestHandler(handler);
        }

        public void registerWildconnectHandler(WildconnectHandler handler) {
            ddeAdapter.registerWildconnectHandler(handler);
        }

        public void unregisterWildconnectHandler(WildconnectHandler handler) {
            ddeAdapter.unregisterWildconnectHandler(handler);
        }

        public void registerAdvdataHandler(AdvdataHandler handler) {
            ddeAdapter.registerAdvdataHandler(handler);
        }

        public void unregisterAdvdataHandler(AdvdataHandler handler) {
            ddeAdapter.unregisterAdvdataHandler(handler);
        }

        public void registerExecuteHandler(ExecuteHandler handler) {
            ddeAdapter.registerExecuteHandler(handler);
        }

        public void unregisterExecuteHandler(ExecuteHandler handler) {
            ddeAdapter.unregisterExecuteHandler(handler);
        }

        public void registerPokeHandler(PokeHandler handler) {
            ddeAdapter.registerPokeHandler(handler);
        }

        public void unregisterPokeHandler(PokeHandler handler) {
            ddeAdapter.unregisterPokeHandler(handler);
        }

        public void registerConnectConfirmHandler(ConnectConfirmHandler handler) {
            ddeAdapter.registerConnectConfirmHandler(handler);
        }

        public void unregisterConnectConfirmHandler(ConnectConfirmHandler handler) {
            ddeAdapter.unregisterConnectConfirmHandler(handler);
        }

        public void registerDisconnectHandler(DisconnectHandler handler) {
            ddeAdapter.registerDisconnectHandler(handler);
        }

        public void registerErrorHandler(ErrorHandler handler) {
            ddeAdapter.registerErrorHandler(handler);
        }
        
        public void unregisterErrorHandler(ErrorHandler handler) {
            ddeAdapter.unregisterErrorHandler(handler);
        }

        public void registerRegisterHandler(RegisterHandler handler) {
            ddeAdapter.registerRegisterHandler(handler);
        }

        public void unregisterRegisterHandler(RegisterHandler handler) {
            ddeAdapter.unregisterRegisterHandler(handler);
        }

        public void registerXactCompleteHandler(XactCompleteHandler handler) {
            ddeAdapter.registerXactCompleteHandler(handler);
        }

        public void unregisterXactCompleteHandler(XactCompleteHandler handler) {
            ddeAdapter.xactCompleteXactCompleteHandler(handler);
        }

        public void registerUnregisterHandler(UnregisterHandler handler) {
            ddeAdapter.registerUnregisterHandler(handler);
        }

        public void unregisterUnregisterHandler(UnregisterHandler handler) {
            ddeAdapter.unregisterUnregisterHandler(handler);
        }

        public void registerMonitorHandler(MonitorHandler handler) {
            ddeAdapter.registerMonitorHandler(handler);
        }

        public void unregisterMonitorHandler(MonitorHandler handler) {
            ddeAdapter.unregisterMonitorHandler(handler);
        }
    }

    public interface AdvstartHandler {
        /**
         * A server callback function should return TRUE to allow an advise loop
         * on the specified topic name and item name pair, or FALSE to deny the
         * advise loop. If the callback function returns TRUE, any subsequent
         * calls to the DdePostAdvise function by the server on the same topic
         * name and item name pair causes the system to send XTYP_ADVREQ
         * transactions to the server.
         *
         * <p>
         * <strong>Remarks</strong></p>
         * <p>If a client requests an advise loop on a topic name, item name,
         * and data format for an advise loop that is already established, the
         * Dynamic Data Exchange Management Library (DDEML) does not create a
         * duplicate advise loop but instead alters the advise loop flags
         * (XTYPF_ACKREQ and XTYPF_NODATA) to match the latest request.</p>
         *
         * <p>
         * This transaction is filtered if the server application specified the
         * CBF_FAIL_ADVISES flag in the DdeInitialize function.</p>
         *
         * @param transactionType uType - The transaction type. 
         * @param dataFormat uFmt - The data format requested by the client. 
         * @param hconv A handle to the conversation. 
         * @param topic hsz1 - A handle to the topic name.
         * @param item hsz2 - A handle to the item name.
         * @return true if advise loop can be started
         */
        boolean onAdvstart(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item);
    }
    
    public interface AdvstopHandler {

        /**
         * A client uses the XTYP_ADVSTOP transaction to end an advise loop with
         * a server. A Dynamic Data Exchange (DDE) server callback function,
         * DdeCallback, receives this transaction when a client specifies
         * XTYP_ADVSTOP in the DdeClientTransaction function.
         *
         * @param transactionType uType - The transaction type.
         * @param dataFormat uFmt - The data format associated with the advise
         * loop being ended.
         * @param hconv A handle to the conversation.
         * @param topic hsz1 - A handle to the topic name.
         * @param item hsz2 - A handle to the item name.
         */
        void onAdvstop(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item);
    }
    
    public interface ConnectHandler {
        /**
         * A client uses the XTYP_CONNECT transaction to establish a
         * conversation. A Dynamic Data Exchange (DDE) server callback function,
         * DdeCallback, receives this transaction when a client specifies a
         * service name that the server supports (and a topic name that is not
         * NULL) in a call to the DdeConnect function.
         *
         * @param transactionType uType - The transaction type.
         * @param topic hsz1 - A handle to the topic name.
         * @param service hsz2 - A handle to the service name.
         * @param convcontext dwData1 - CONVCONTEXT structure that contains
         * context information for the conversation. If the client is not a
         * DDEML application, this parameter is NULL.
         * @param sameInstance dwData2 - Specifies whether the client is the
         * same application instance as the server.
         * @return true is connect can continue
         */
        boolean onConnect(int transactionType, HSZ topic, HSZ service, CONVCONTEXT convcontext, boolean sameInstance);
    }
    
    public interface AdvreqHandler {

        /**
         * The XTYP_ADVREQ transaction informs the server that an advise
         * transaction is outstanding on the specified topic name and item name
         * pair and that data corresponding to the topic name and item name pair
         * has changed. The system sends this transaction to the Dynamic Data
         * Exchange (DDE) callback function, DdeCallback, after the server calls
         * the DdePostAdvise function.
         *
         * @param transactionType uType - The transaction type.
         * @param dataFormat uFmt - The format in which the data should be
         * submitted to the client.
         * @param hconv A handle to the conversation.
         * @param topic hsz1 - A handle to the topic name.
         * @param item hsz2 - A handle to the item name that has changed.
         * @param count The count of XTYP_ADVREQ transactions that remain to be
         * processed on the same topic, item, and format name set within the
         * context of the current call to the DdePostAdvise function. The count
         * is zero if the current XTYP_ADVREQ transaction is the last one. A
         * server can use this count to determine whether to create an
         * HDATA_APPOWNED data handle to the advise data.
         *
         * <p>
         * This is set to CADV_LATEACK if the DDEML issued the XTYP_ADVREQ
         * transaction because of a late-arriving DDE_ACK message from a client
         * being outrun by the server. </p>
         *
         * @return Data for the changed item or NULL if transaction can't be
         * completed.
         */
        HDDEDATA onAdvreq(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item, int count);
    }
    
    public interface RequestHandler {
        /**
         * A client uses the XTYP_REQUEST transaction to request data from a
         * server. A Dynamic Data Exchange (DDE) server callback function,
         * DdeCallback, receives this transaction when a client specifies
         * XTYP_REQUEST in the DdeClientTransaction function.
         *
         * @param transactionType uType - The transaction type.
         * @param dataFormat uFmt - The format in which the data should be
         * submitted to the client.
         * @param hconv A handle to the conversation.
         * @param topic hsz1 - A handle to the topic name.
         * @param item hsz2 - A handle to the item name.
         *
         * @return Data for the changed item or NULL if transaction can't be
         * completed. If the server returns NULL, the client will receive a
         * DDE_FNOTPROCESSED flag.
         */
        HDDEDATA onRequest(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item);
    }
    
    public interface WildconnectHandler {

        /**
         * Enables a client to establish a conversation on each of the server's
         * service name and topic name pairs that match the specified service
         * name and topic name. A Dynamic Data Exchange (DDE) server callback
         * function, DdeCallback, receives this transaction when a client
         * specifies a NULL service name, a NULL topic name, or both in a call
         * to the DdeConnect or DdeConnectList function.
         *
         * @param transactionType uType - The transaction type.
         * @param topic A handle to the topic name. If this parameter is NULL,
         * the client is requesting a conversation on all topic names that the
         * server supports.
         * @param service A handle to the service name. If this parameter is
         * NULL, the client is requesting a conversation on all service names
         * that the server supports.
         * @param convcontext dwData1 - CONVCONTEXT structure that contains
         * context information for the conversation. If the client is not a
         * DDEML application, this parameter is NULL.
         * @param sameInstance dwData2 - Specifies whether the client is the
         * same application instance as the server.
         * @return the supported HSZPAIRs (do not include the terminating pair
         * needed be the DdeCallback!)
         */
        List<HSZPAIR> onWildconnect(int transactionType, HSZ topic, HSZ service, CONVCONTEXT convcontext, boolean sameInstance);
    }
    
    public interface AdvdataHandler {
        /**
         * Informs the client that the value of the data item has changed. The
         * Dynamic Data Exchange (DDE) client callback function, DdeCallback,
         * receives this transaction after establishing an advise loop with a
         * server.
         *
         * @param transactionType uType - The transaction type.
         * @param dataFormat uFmt - The format atom of the data sent from the
         * server.
         * @param hconv A handle to the conversation.
         * @param topic hsz1 - A handle to the topic name.
         * @param item hsz2 - A handle to the item name.
         * @param hdata A handle to the data associated with the topic name and
         * item name pair. This parameter is NULL if the client specified the
         * XTYPF_NODATA flag when it requested the advise loop.
         * @return A DDE callback function should return DDE_FACK if it
         * processes this transaction, DDE_FBUSY if it is too busy to process
         * this transaction, or DDE_FNOTPROCESSED if it rejects this
         * transaction.
         */
        int onAdvdata(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item, HDDEDATA hdata);
    }
    
    public interface ConnectConfirmHandler {

        /**
         * A Dynamic Data Exchange (DDE) server callback function, DdeCallback,
         * receives the XTYP_CONNECT_CONFIRM transaction to confirm that a
         * conversation has been established with a client and to provide the
         * server with the conversation handle. The system sends this
         * transaction as a result of a previous XTYP_CONNECT or
         * XTYP_WILDCONNECT transaction.
         *
         * @param transactionType uType - The transaction type.
         * @param hconv A handle to the new conversation.
         * @param topic hsz1 - A handle to the topic name.
         * @param service hsz2 - A handle to the service name on which the
         * conversation has been established.
         * @param sameInstance dwData2 - Specifies whether the client is the
         * same application instance as the server.
         */
        void onConnectConfirm(int transactionType, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ service, boolean sameInstance);
    }
        
    public interface DisconnectHandler {

        /**
         * An application's Dynamic Data Exchange (DDE) callback function,
         * DdeCallback, receives the XTYP_DISCONNECT transaction when the
         * application's partner in a conversation uses the DdeDisconnect
         * function to terminate the conversation.
         *
         * <p>
         * <strong>Remarks</strong></p>
         *
         * <p>
         * This transaction is filtered if the application specified the
         * CBF_SKIP_DISCONNECTS flag in the DdeInitialize function.</p>
         * <p>
         * The application can obtain the status of the terminated conversation
         * by calling the DdeQueryConvInfo function while processing this
         * transaction. The conversation handle becomes invalid after the
         * callback function returns.</p>
         * <p>
         * An application cannot block this transaction type; the CBR_BLOCK
         * return code is ignored.</p>
         *
         * @param transactionType uType - The transaction type.
         * @param hconv A handle to that the conversation was terminated.
         * @param sameInstance dwData2 - Specifies whether the client is the
         * same application instance as the server.
         */
        void onDisconnect(int transactionType, HCONV hconv, boolean sameInstance);
    }
        
    public interface ErrorHandler {

        /**
         * A Dynamic Data Exchange (DDE) callback function, DdeCallback,
         * receives the XTYP_ERROR transaction when a critical error occurs.
         *
         * <p>
         * <strong>Remarks</strong></p>
         *
         * <p>
         * An application cannot block this transaction type; the CBR_BLOCK
         * return code is ignored. The Dynamic Data Exchange Management Library
         * (DDEML) attempts to free memory by removing noncritical resources. An
         * application that has blocked conversations should unblock them. </p>
         *
         * @param transactionType uType - The transaction type.
         * @param hconv A handle to the conversation associated with the error.
         * This parameter is NULL if the error is not associated with a
         * conversation.
         * @param errorCode dwData1 - The error code in the low-order word.
         * Currently, only the following error code is supported.
         * <p>
         * Only Ddeml.DMLERR_LOW_MEMRORY is known: Memory is low; advise, poke,
         * or execute data may be lost, or the system may fail.</p>
         */
        void onError(int transactionType, HCONV hconv, int errorCode);
    }
        
    public interface RegisterHandler {
        /**
         * A Dynamic Data Exchange (DDE) callback function, DdeCallback, receives the XTYP_REGISTER transaction type whenever a Dynamic Data Exchange Management Library (DDEML) server application uses the DdeNameService function to register a service name, or whenever a non-DDEML application that supports the System topic is started. 
         * 
         * <p>
         * <strong>Remarks</strong></p>
         *<p>
         * This transaction is filtered if the application specified the
         * CBF_SKIP_REGISTRATIONS flag in the DdeInitialize function.</p>
         *<p>
         * A application cannot block this transaction type; the CBR_BLOCK
         * return code is ignored.</p>
         *<p>
         * An application should use the hsz1 parameter to add the service name
         * to the list of servers available to the user. An application should
         * use the hsz2 parameter to identify which application instance has
         * started. </p>
         *
         * @param transactionType uType - The transaction type.
         * @param baseServiceName hsz1 - A handle to the base service name being
         * registered.
         * @param instanceSpecificServiceName hsz2 - A handle to the
         * instance-specific service name being registered.
         */
        void onRegister(int transactionType, HSZ baseServiceName, HSZ instanceSpecificServiceName);
    }
    
    public interface XactCompleteHandler {
        /**
         * A Dynamic Data Exchange (DDE) client callback function, DdeCallback,
         * receives the XTYP_XACT_COMPLETE transaction when an asynchronous
         * transaction, initiated by a call to the DdeClientTransaction
         * function, has completed.
         *
         * <p>
         * <strong>Remarks</strong></p>
         * <p>
         * An application must not free the data handle obtained during this
         * transaction. An application must, however, copy the data associated
         * with the data handle if the application must process the data after
         * the callback function returns. An application can use the DdeGetData
         * function to copy the data.</p>
         *
         * @param transactionType uType - The transaction type.
         * @param dataFormat uFmt - The format of the data associated with the
         * completed transaction (if applicable) or NULL if no data was
         * exchanged during the transaction.
         * @param hConv - A handle to the conversation.
         * @param topic hsz1 - A handle to the topic name involved in the
         * completed transaction.
         * @param item hsz2 - A handle to the item name involved in the
         * completed transaction.
         * @param hdata A handle to the data involved in the completed
         * transaction, if applicable. If the transaction was successful but
         * involved no data, this parameter is TRUE. This parameter is NULL if
         * the transaction was unsuccessful.
         * @param transactionIdentifier dwData1 - The transaction identifier of
         * the completed transaction.
         * @param statusFlag dwData2 - Any applicable DDE_ status flags in the low word. This parameter provides support for applications dependent on DDE_APPSTATUS bits. It is recommended that applications no longer use these bits � they may not be supported in future versions of the DDEML.
         */
        void onXactComplete(int transactionType, int dataFormat, HCONV hConv, HSZ topic, HSZ item, HDDEDATA hdata, ULONG_PTR transactionIdentifier, ULONG_PTR statusFlag);
    }
        
    public interface UnregisterHandler {
        /**
         * A Dynamic Data Exchange (DDE) callback function, DdeCallback, receives the XTYP_REGISTER transaction type whenever a Dynamic Data Exchange Management Library (DDEML) server application uses the DdeNameService function to register a service name, or whenever a non-DDEML application that supports the System topic is started. 
         * 
         * <p>
         * <strong>Remarks</strong></p>
         * <p>
         * This transaction is filtered if the application specified the
         * CBF_SKIP_REGISTRATIONS flag in the DdeInitialize function.</p>
         *<p>
         * A application cannot block this transaction type; the CBR_BLOCK
         * return code is ignored.</p>
         *<p>
         * An application should use the hsz1 parameter to remove the service
         * name from the list of servers available to the user. An application
         * should use the hsz2 parameter to identify which application instance
         * has terminated. </p>
         *
         * @param transactionType uType - The transaction type.
         * @param baseServiceName hsz1 - A handle to the base service name being
         * registered.
         * @param instanceSpecificServiceName hsz2 - A handle to the
         * instance-specific service name being registered.
         */
        void onUnregister(int transactionType, HSZ baseServiceName, HSZ instanceSpecificServiceName);
    }
    
    public interface ExecuteHandler {
            /**
         * A client uses the XTYP_EXECUTE transaction to send a command string
         * to the server. A Dynamic Data Exchange (DDE) server callback
         * function, DdeCallback, receives this transaction when a client
         * specifies XTYP_EXECUTE in the DdeClientTransaction function.
         *
         * <p>
         * <strong>Remarks</strong></p>
         * <p>
         * This transaction is filtered if the server application specified the
         * CBF_FAIL_EXECUTES flag in the DdeInitialize function.</p>
         *<p>
         * Because most client applications expect a server application to
         * perform an XTYP_EXECUTE transaction synchronously, a server should
         * attempt to perform all processing of the XTYP_EXECUTE transaction
         * either from within the DDE callback function or by returning the
         * CBR_BLOCK return code. If the hdata parameter is a command that
         * instructs the server to terminate, the server should do so after
         * processing the XTYP_EXECUTE transaction. </p>
         * 
         * @param transactionType uType - The transaction type.
         * @param hconv A handle to the conversation.
         * @param topic hsz1 - A handle to the topic name.
         * @param commandString A handle to the command string.
         * @return A server callback function should return DDE_FACK if it
         * processes this transaction, DDE_FBUSY if it is too busy to process
         * this transaction, or DDE_FNOTPROCESSED if it rejects this
         * transaction.
         *
         */
        int onExecute(int transactionType, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HDDEDATA commandString);
    }
    
    public interface PokeHandler {
        /**
         * A client uses the XTYP_POKE transaction to send unsolicited data to
         * the server. A Dynamic Data Exchange (DDE) server callback function,
         * DdeCallback, receives this transaction when a client specifies
         * XTYP_POKE in the DdeClientTransaction function.
         *
         * @param transactionType uType - The transaction type.
         * @param dataFormat uFmt - The format of the data sent from the server.
         * @param hconv A handle to the conversation.
         * @param topic hsz1 - A handle to the topic name.
         * @param item hsz2 - A handle to the item name.
         * @param hdata - A handle to the data that the client is sending to the
         * server.
         * @return A server callback function should return the DDE_FACK flag if
         * it processes this transaction, the DDE_FBUSY flag if it is too busy
         * to process this transaction, or the DDE_FNOTPROCESSED flag if it
         * rejects this transaction.
         *
         */
        int onPoke(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item, HDDEDATA hdata);
    }
    
    public interface MonitorHandler {

        /**
         * A Dynamic Data Exchange (DDE) debugger's DDE callback function,
         * DdeCallback, receives the XTYP_MONITOR transaction whenever a DDE
         * event occurs in the system. To receive this transaction, an
         * application must specify the APPCLASS_MONITOR value when it calls the
         * DdeInitialize function.
         *
         * @param transactionType uType - The transaction type.
         * @param hdata A handle to a DDE object that contains information about
         * the DDE event. The application should use the DdeAccessData function
         * to obtain a pointer to the object.
         * @param eventCode dwData2 - The DDE event. This parameter can be one
         * of the following values:
         * 
         * <table>
         * <tr>
         * <th>Value</th><th>Meaning</th>
         * </tr>
         * <tr><td>
         * MF_CALLBACKS<br>
         * 0x08000000
         * </td>
         * <td>
         * The system sent a transaction to a DDE callback function. The DDE
         * object contains a MONCBSTRUCT structure that provides information
         * about the transaction.
         * </td>
         * </tr>
         * <tr><td>
         * MF_CONV<br>
         * 0x40000000
         * </td>
         * <td>
         * A DDE conversation was established or terminated. The DDE object
         * contains a MONCONVSTRUCT structure that provides information about
         * the conversation.
         * </td>
         * </tr>
         * <tr><td>
         * MF_ERRORS<br>
         * 0x10000000
         * </td>
         * <td>
         * A DDE error occurred. The DDE object contains a MONERRSTRUCT
         * structure that provides information about the error.
         * </td>
         * </tr>
         * <tr><td>
         * MF_HSZ_INFO<br>
         * 0x01000000
         * </td>
         * <td>
         * A DDE application created, freed, or incremented the usage count of a
         * string handle, or a string handle was freed as a result of a call to
         * the DdeUninitialize function. The DDE object contains a MONHSZSTRUCT
         * structure that provides information about the string handle.
         * </td>
         * </tr>
         * <tr><td>
         * MF_LINKS<br>
         * 0x20000000
         * </td>
         * <td>
         * A DDE application started or stopped an advise loop. The DDE object
         * contains a MONLINKSTRUCT structure that provides information about
         * the advise loop.
         * </td>
         * </tr>
         * <tr><td>
         * MF_POSTMSGS<br>
         * 0x04000000
         * </td>
         * <td>
         * The system or an application posted a DDE message. The DDE object
         * contains a MONMSGSTRUCT structure that provides information about the
         * message.
         * </td>
         * </tr>
         * <tr><td>
         * MF_SENDMSGS<br>
         * 0x02000000
         * </td>
         * <td>
         * The system or an application sent a DDE message. The DDE object
         * contains a MONMSGSTRUCT structure that provides information about the
         * message.
         * </td>
         * </tr>
         * </table>
         */
        void onMonitor(int transactionType, HDDEDATA hdata, int eventCode);
    }
    
    /**
     * DdeAdapter implements DdeCallback and allow dynamic registration for
     * mulitple handlers, that can be registered and unregistered at runtime.
     * 
     * <dl>
     * <dt>AdvstartHandler</dt>
     * <dd>All registered AdvstartHandler are evaluated. If at least one returns
     * true, the whole evaluation is considered true.</dd>
     * <dt>AdvstopHandler</dt>
     * <dd>All registered AdvstopHandler are invoked.</dd>
     * <dt>ConnectHandler</dt>
     * <dd>All registered ConnectHandler are evaluated. If at least one returns
     * true, the whole evaluation is considered true.</dd>
     * <dt>WildconnectHandler</dt>
     * <dd>All registered WildconnectHandler are evaluated. The result is the
     * union of all HSZPAIRs.</dd>
     * <dt>ConnectConfirmHandler</dt>
     * <dd>All registered ConnectConfirmHandler are evaluated.</dd>
     * <dt>DisconnectHandler</dt>
     * <dd>All registered DisconnectHandler are evaluated.</dd>
     * <dt>ErrorHandler</dt>
     * <dd>All registered ErrorHandler are evaluated.</dd>
     * <dt>RegisterHandler</dt>
     * <dd>All registered RegisterHandler are evaluated.</dd>
     * <dt>XactCompleteHandler</dt>
     * <dd>All registered XactCompleteHandler are evaluated.</dd>
     * <dt>UnregisterHandler</dt>
     * <dd>All registered UnregisterHandlers are evaluated.</dd>
     * <dt>MonitorHandler</dt>
     * <dd>All registered AdvstopHandler are invoked.</dd>
     * <dt>AdvdataHandler</dt>
     * <dd>The AdvdataHandlers are evaluated in registration order - evaluation
     * stops after the first handler not returning Ddeml.FNOTPROCESSED.</dd>
     * <dt>ExecuteHandler</dt>
     * <dd>The ExecuteHandler are evaluated in registration order - evaluation
     * stops after the first handler not returning Ddeml.FNOTPROCESSED.</dd>
     * <dt>PokeHandler</dt>
     * <dd>The PokeHandler are evaluated in registration order - evaluation
     * stops after the first handler not returning Ddeml.FNOTPROCESSED.</dd>
     * <dt>AdvreqHandler</dt>
     * <dd>The AdvreqHandlers are evaluated in registration order - evaluation
     * stops after the first handler returning a non null value.</dd>
     * <dt>RequestHandler</dt>
     * <dd>The RequestHandlers are evaluated in registration order - evaluation
     * stops after the first handler returning a non null value.</dd>
     * </dl>
     */
    public static class DdeAdapter implements Ddeml.DdeCallback {

        public static class BlockException extends RuntimeException{};
        
        private static final Logger LOG = Logger.getLogger(DdeAdapter.class.getName());

        private int idInst;
        
        public void setInstanceIdentifier(int idInst) {
            this.idInst = idInst;
        }
        
        public WinDef.PVOID ddeCallback(int wType, int wFmt, Ddeml.HCONV hConv, Ddeml.HSZ hsz1, Ddeml.HSZ hsz2, Ddeml.HDDEDATA hData, BaseTSD.ULONG_PTR lData1, BaseTSD.ULONG_PTR lData2) {
            boolean booleanResult;
            Ddeml.HDDEDATA data;
            Ddeml.CONVCONTEXT convcontext;
            int intResult;
            String transactionTypeName = null;
            try {
                switch (wType) {
                    case Ddeml.XTYP_ADVSTART:
                        booleanResult = onAdvstart(wType, wFmt, hConv, hsz1, hsz2);
                        return new WinDef.PVOID(Pointer.createConstant(new WinDef.BOOL(booleanResult).intValue()));
                    case Ddeml.XTYP_CONNECT:
                        convcontext = null;
                        if (lData1.toPointer() != null) {
                            convcontext = new Ddeml.CONVCONTEXT(new Pointer(lData1.longValue()));
                        }
                        booleanResult = onConnect(wType, hsz1, hsz2, convcontext, lData2 != null && lData2.intValue() != 0);
                        return new WinDef.PVOID(Pointer.createConstant(new WinDef.BOOL(booleanResult).intValue()));
                    case Ddeml.XTYP_ADVREQ:
                        int count = lData1.intValue() & 0xFFFF;
                        data = onAdvreq(wType, wFmt, hConv, hsz1, hsz2, count);
                        if (data == null) {
                            return new WinDef.PVOID();
                        } else {
                            return new WinDef.PVOID(data.getPointer());
                        }
                    case Ddeml.XTYP_REQUEST:
                        data = onRequest(wType, wFmt, hConv, hsz1, hsz2);
                        if (data == null) {
                            return new WinDef.PVOID();
                        } else {
                            return new WinDef.PVOID(data.getPointer());
                        }
                    case Ddeml.XTYP_WILDCONNECT:
                        convcontext = null;
                        if (lData1.toPointer() != null) {
                            convcontext = new Ddeml.CONVCONTEXT(new Pointer(lData1.longValue()));
                        }
                        Ddeml.HSZPAIR[] hszPairs = onWildconnect(wType, hsz1, hsz2, convcontext, lData2 != null && lData2.intValue() != 0);
                        if (hszPairs == null || hszPairs.length == 0) {
                            return new WinDef.PVOID();
                        }
                        int size = 0;
                        for (Ddeml.HSZPAIR hp : hszPairs) {
                            hp.write();
                            size += hp.size();
                        }
                        data = Ddeml.INSTANCE.DdeCreateDataHandle(idInst,
                                hszPairs[0].getPointer(),
                                size,
                                0,
                                null,
                                wFmt,
                                0);
                        return new WinDef.PVOID(data.getPointer());
                    case Ddeml.XTYP_ADVDATA:
                        intResult = onAdvdata(wType, wFmt, hConv, hsz1, hsz2, hData);
                        return new WinDef.PVOID(Pointer.createConstant(intResult));
                    case Ddeml.XTYP_EXECUTE:
                        intResult = onExecute(wType, hConv, hsz1, hData);
                        Ddeml.INSTANCE.DdeFreeDataHandle(hData);
                        return new WinDef.PVOID(Pointer.createConstant(intResult));
                    case Ddeml.XTYP_POKE:
                        intResult = onPoke(wType, wFmt, hConv, hsz1, hsz2, hData);
                        return new WinDef.PVOID(Pointer.createConstant(intResult));
                    case Ddeml.XTYP_ADVSTOP:
                        onAdvstop(wType, wFmt, hConv, hsz1, hsz2);
                        break;
                    case Ddeml.XTYP_CONNECT_CONFIRM:
                        onConnectConfirm(wType, hConv, hsz1, hsz2, lData2 != null && lData2.intValue() != 0);
                        break;
                    case Ddeml.XTYP_DISCONNECT:
                        onDisconnect(wType, hConv, lData2 != null && lData2.intValue() != 0);
                        break;
                    case Ddeml.XTYP_ERROR:
                        onError(wType, hConv, (int) (lData2.longValue() & 0xFFFF));
                        break;
                    case Ddeml.XTYP_REGISTER:
                        onRegister(wType, hsz1, hsz2);
                        break;
                    case Ddeml.XTYP_XACT_COMPLETE:
                        onXactComplete(wType, wFmt, hConv, hsz1, hsz2, hData, lData1, lData2);
                        break;
                    case Ddeml.XTYP_UNREGISTER:
                        onUnregister(wType, hsz1, hsz2);
                        break;
                    case Ddeml.XTYP_MONITOR:
                        onMonitor(wType, hData, lData2.intValue());
                        break;
                    default:
                        LOG.log(Level.FINE, String.format("Not implemented Operation - Transaction type: 0x%X (%s)", wType, transactionTypeName));
                }
            } catch (BlockException ex) {
                return new WinDef.PVOID(Pointer.createConstant(-1));
            } catch (Throwable ex) {
                LOG.log(Level.WARNING, "Exception in DDECallback", ex);
            }
            return new WinDef.PVOID();
        };
        
        private final List<AdvstartHandler> advstartHandler = new CopyOnWriteArrayList<AdvstartHandler>();
        
        public void registerAdvstartHandler(AdvstartHandler handler) {
            advstartHandler.add(handler);
        }
        
        public void unregisterAdvstartHandler(AdvstartHandler handler) {
            advstartHandler.remove(handler);
        }
        
        private boolean onAdvstart(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item) {
            boolean oneHandlerTrue = false;
            for (AdvstartHandler handler : advstartHandler) {
                if (handler.onAdvstart(transactionType, dataFormat, hconv, topic, item)) {
                    oneHandlerTrue = true;
                }
            }
            return oneHandlerTrue;
        }
        
        private final List<AdvstopHandler> advstopHandler = new CopyOnWriteArrayList<AdvstopHandler>();
        
        public void registerAdvstopHandler(AdvstopHandler handler) {
            advstopHandler.add(handler);
        }
        
        public void unregisterAdvstopHandler(AdvstopHandler handler) {
            advstopHandler.remove(handler);
        }
        
        private void onAdvstop(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item) {
            for (AdvstopHandler handler : advstopHandler) {
                handler.onAdvstop(transactionType, dataFormat, hconv, topic, item);
            }
        }
        
        private final List<ConnectHandler> connectHandler = new CopyOnWriteArrayList<ConnectHandler>();
        
        public void registerConnectHandler(ConnectHandler handler) {
            connectHandler.add(handler);
        }
        
        public void unregisterConnectHandler(ConnectHandler handler) {
            connectHandler.remove(handler);
        }
        
        private boolean onConnect(int transactionType, Ddeml.HSZ topic, Ddeml.HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
            boolean oneHandlerTrue = false;
            for (ConnectHandler handler : connectHandler) {
                if (handler.onConnect( transactionType, topic, service, convcontext, sameInstance)) {
                    oneHandlerTrue = true;
                }
            }
            return oneHandlerTrue;
        }
        
        private final List<AdvreqHandler> advReqHandler = new CopyOnWriteArrayList<AdvreqHandler>();
        
        public void registerAdvReqHandler(AdvreqHandler handler) {
            advReqHandler.add(handler);
        }
        
        public void unregisterAdvReqHandler(AdvreqHandler handler) {
            advReqHandler.remove(handler);
        }
        
        private Ddeml.HDDEDATA onAdvreq(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item, int count) {
            for (AdvreqHandler handler : advReqHandler) {
                HDDEDATA result = handler.onAdvreq(transactionType, dataFormat, hconv, topic, item, count);
                if(result != null) {
                    return result;
                }
            }
            return null;
        }
        
        private final List<RequestHandler> requestHandler = new CopyOnWriteArrayList<RequestHandler>();
        
        public void registerRequestHandler(RequestHandler handler) {
            requestHandler.add(handler);
        }
        
        public void unregisterRequestHandler(RequestHandler handler) {
            requestHandler.remove(handler);
        }
        
        private Ddeml.HDDEDATA onRequest(int transactionType, int dataFormat, Ddeml.HCONV hconv, Ddeml.HSZ topic, Ddeml.HSZ item) {
            for (RequestHandler handler : requestHandler) {
                HDDEDATA result = handler.onRequest(transactionType, dataFormat, hconv, topic, item);
                if(result != null) {
                    return result;
                }
            }
            return null;
        }
        
        private final List<WildconnectHandler> wildconnectHandler = new CopyOnWriteArrayList<WildconnectHandler>();
        
        public void registerWildconnectHandler(WildconnectHandler handler) {
            wildconnectHandler.add(handler);
        }
        
        public void unregisterWildconnectHandler(WildconnectHandler handler) {
            wildconnectHandler.remove(handler);
        }
        
        private Ddeml.HSZPAIR[] onWildconnect(int transactionType, HSZ topic, HSZ service, CONVCONTEXT convcontext, boolean sameInstance) {
            List<HSZPAIR> hszpairs = new ArrayList<HSZPAIR>(1);
            for(WildconnectHandler handler: wildconnectHandler) {
                hszpairs.addAll(handler.onWildconnect(transactionType, topic, service, convcontext, sameInstance));
            }
            return hszpairs.toArray(new HSZPAIR[hszpairs.size()]);
        }
        
        
        private final List<AdvdataHandler> advdataHandler = new CopyOnWriteArrayList<AdvdataHandler>();
        
        public void registerAdvdataHandler(AdvdataHandler handler) {
            advdataHandler.add(handler);
        }
        
        public void unregisterAdvdataHandler(AdvdataHandler handler) {
            advdataHandler.remove(handler);
        }
        
        private int onAdvdata(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item, HDDEDATA hdata) {
            for (AdvdataHandler handler : advdataHandler) {
                int result = handler.onAdvdata(transactionType, dataFormat, hconv, topic, item, hdata);
                if(result != Ddeml.DDE_FNOTPROCESSED) {
                    return result;
                }
            }
            return Ddeml.DDE_FNOTPROCESSED;
        }
        
        private final List<ExecuteHandler> executeHandler = new CopyOnWriteArrayList<ExecuteHandler>();
        
        public void registerExecuteHandler(ExecuteHandler handler) {
            executeHandler.add(handler);
        }
        
        public void unregisterExecuteHandler(ExecuteHandler handler) {
            executeHandler.remove(handler);
        }
        
        private int onExecute(int transactionType, HCONV hconv, HSZ topic, HDDEDATA commandString) {
            for (ExecuteHandler handler : executeHandler) {
                int result = handler.onExecute(transactionType, hconv, topic, commandString);
                if(result != Ddeml.DDE_FNOTPROCESSED) {
                    return result;
                }
            }
            return Ddeml.DDE_FNOTPROCESSED;
        }

        private final List<PokeHandler> pokeHandler = new CopyOnWriteArrayList<PokeHandler>();
        
        public void registerPokeHandler(PokeHandler handler) {
            pokeHandler.add(handler);
        }
        
        public void unregisterPokeHandler(PokeHandler handler) {
            pokeHandler.remove(handler);
        }
        
        private int onPoke(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item, HDDEDATA hdata) {
            for (PokeHandler handler : pokeHandler) {
                int result = handler.onPoke(transactionType, dataFormat, hconv, topic, item, hdata);
                if(result != Ddeml.DDE_FNOTPROCESSED) {
                    return result;
                }
            }
            return Ddeml.DDE_FNOTPROCESSED;
        }

        private final List<ConnectConfirmHandler> connectConfirmHandler = new CopyOnWriteArrayList<ConnectConfirmHandler>();
        
        public void registerConnectConfirmHandler(ConnectConfirmHandler handler) {
            connectConfirmHandler.add(handler);
        }
        
        public void unregisterConnectConfirmHandler(ConnectConfirmHandler handler) {
            connectConfirmHandler.remove(handler);
        }
        
        private void onConnectConfirm(int transactionType, HCONV hconv, HSZ topic, HSZ service, boolean sameInstance) {
            for(ConnectConfirmHandler handler: connectConfirmHandler) {
                handler.onConnectConfirm(transactionType, hconv, topic, service, sameInstance);
            }
        }
        
        private final List<DisconnectHandler> disconnectHandler = new CopyOnWriteArrayList<DisconnectHandler>();
        
        public void registerDisconnectHandler(DisconnectHandler handler) {
            disconnectHandler.add(handler);
        }
        
        public void unregisterDisconnectHandler(DisconnectHandler handler) {
            disconnectHandler.remove(handler);
        }
        
        private void onDisconnect(int transactionType, Ddeml.HCONV hconv, boolean sameInstance) {
            for(DisconnectHandler handler: disconnectHandler) {
                handler.onDisconnect(transactionType, hconv, sameInstance);
            }
        }

        private final List<ErrorHandler> errorHandler = new CopyOnWriteArrayList<ErrorHandler>();
        
        public void registerErrorHandler(ErrorHandler handler) {
            errorHandler.add(handler);
        }
        
        public void unregisterErrorHandler(ErrorHandler handler) {
            errorHandler.remove(handler);
        }
        
        private void onError(int transactionType, Ddeml.HCONV hconv, int errorCode) {
            for(ErrorHandler handler: errorHandler) {
                handler.onError(transactionType, hconv, errorCode);
            }
        }
        
        private final List<RegisterHandler> registerHandler = new CopyOnWriteArrayList<RegisterHandler>();
        
        public void registerRegisterHandler(RegisterHandler handler) {
            registerHandler.add(handler);
        }
        
        public void unregisterRegisterHandler(RegisterHandler handler) {
            registerHandler.remove(handler);
        }

        private void onRegister(int transactionType, Ddeml.HSZ baseServiceName, Ddeml.HSZ instanceSpecificServiceName) {
            for(RegisterHandler handler: registerHandler) {
                handler.onRegister(transactionType, baseServiceName, instanceSpecificServiceName);
            }
        }

        private final List<XactCompleteHandler> xactCompleteHandler = new CopyOnWriteArrayList<XactCompleteHandler>();
        
        public void registerXactCompleteHandler(XactCompleteHandler handler) {
            xactCompleteHandler.add(handler);
        }
        
        public void xactCompleteXactCompleteHandler(XactCompleteHandler handler) {
            xactCompleteHandler.remove(handler);
        }
        
        private void onXactComplete(int transactionType, int dataFormat, HCONV hConv, HSZ topic, HSZ item, HDDEDATA hdata, ULONG_PTR transactionIdentifier, ULONG_PTR statusFlag) {
            for(XactCompleteHandler handler: xactCompleteHandler) {
                handler.onXactComplete(transactionType, dataFormat, hConv, topic, item, hdata, transactionIdentifier, statusFlag);
            }
        }
        
        private final List<UnregisterHandler> unregisterHandler = new CopyOnWriteArrayList<UnregisterHandler>();
        
        public void registerUnregisterHandler(UnregisterHandler handler) {
            unregisterHandler.add(handler);
        }
        
        public void unregisterUnregisterHandler(UnregisterHandler handler) {
            unregisterHandler.remove(handler);
        }
        
        private void onUnregister(int transactionType, HSZ baseServiceName, HSZ instanceSpecificServiceName) {
            for(UnregisterHandler handler: unregisterHandler) {
                handler.onUnregister(transactionType, baseServiceName, instanceSpecificServiceName);
            }
        }
        
        private final List<MonitorHandler> monitorHandler = new CopyOnWriteArrayList<MonitorHandler>();
        
        public void registerMonitorHandler(MonitorHandler handler) {
            monitorHandler.add(handler);
        }
        
        public void unregisterMonitorHandler(MonitorHandler handler) {
            monitorHandler.remove(handler);
        }
        
        private void onMonitor(int transactionType, HDDEDATA hdata, int dwData2) {
            for(MonitorHandler handler: monitorHandler) {
                handler.onMonitor(transactionType, hdata, dwData2);
            }
        }
    }
    
    /**
     * DdemlException wraps error codes reported by the DDEML functions as an
     * exception.
     */
    public static class DdemlException extends RuntimeException {
        private static final Map<Integer,String> ERROR_CODE_MAP;
        
        static {
             Map<Integer,String> errorCodeMapBuilder = new HashMap<Integer,String>();
             for(Field f: Ddeml.class.getFields()) {
                 String name = f.getName();
                 if(name.startsWith("DMLERR_") && (! name.equals("DMLERR_FIRST")) && (! name.equals("DMLERR_LAST"))) {
                     try {
                         errorCodeMapBuilder.put(f.getInt(null), name);
                     } catch (IllegalArgumentException ex) {
                         throw new RuntimeException(ex);
                     } catch (IllegalAccessException ex) {
                         throw new RuntimeException(ex);
                     }
                 }
             }
             ERROR_CODE_MAP = Collections.unmodifiableMap(errorCodeMapBuilder);
        }
        
        private final int errorCode;
        
        public static DdemlException create(int errorCode) {
            String errorName = ERROR_CODE_MAP.get(errorCode);
            return new DdemlException(errorCode, String.format("%s (Code: 0x%X)", 
                    errorName != null ? errorName : "",
                    errorCode));
        }

        public DdemlException(int errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }
  
        public int getErrorCode() {
            return errorCode;
        }
    }
    
    /**
     * The IDdeConnection defines the functions, that work an a concrete
     * connection/conversation.
     */
    public interface IDdeConnection extends Closeable {
        public Ddeml.HCONV getConv();

        /**
         * Run an XTYP_EXECUTE client transaction.
         * 
         * @param executeString The string passed to the server for execution
         * @param timeout The maximum amount of time, in milliseconds, that
         * the client will wait for a response from the server application in a
         * synchronous transaction. This parameter should be TIMEOUT_ASYNC for
         * asynchronous transactions.
         * @param result A pointer to a variable that receives the result of
         * the transaction. An application that does not check the result can
         * use NULL for this value. For synchronous transactions, the low-order
         * word of this variable contains any applicable DDE_ flags resulting
         * from the transaction. This provides support for applications
         * dependent on DDE_APPSTATUS bits. It is, however, recommended that
         * applications no longer use these bits because they may not be
         * supported in future versions of the Dynamic Data Exchange Management
         * Library (DDEML). For asynchronous transactions, this variable is
         * filled with a unique transaction identifier for use with the
         * DdeAbandonTransaction function and the XTYP_XACT_COMPLETE
         * transaction.
         * @param userHandle data to associate with the transaction
         * 
         * <p>
         * If an error occurs, a DdemlException is raised with the appropriate
         * error code:</p>
         * 
         * <ul>
         * <li>DMLERR_BUSY</li>
         * <li>DMLERR_NOTPROCESSED</li>
         * </ul>
         */
        public void execute(String executeString, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle);

        /**
         * Run an XTYP_POKE client transaction
         * 
         * @param data The beginning of the data the client must pass to the
         * server.
         *
         * <p>
         * Optionally, an application can specify the data handle (HDDEDATA) to
         * pass to the server and in that case the cbData parameter should be
         * set to -1. This parameter is required only if the wType parameter is
         * XTYP_EXECUTE or XTYP_POKE. Otherwise, this parameter should be
         * NULL.</p>
         *
         * <p>
         * For the optional usage of this parameter, XTYP_POKE transactions
         * where pData is a data handle, the handle must have been created by a
         * previous call to the DdeCreateDataHandle function, employing the same
         * data format specified in the wFmt parameter.</p>
         *
         * @param dataLength The length, in bytes, of the data pointed to by the
         * pData parameter, including the terminating NULL, if the data is a
         * string. A value of -1 indicates that pData is a data handle that
         * identifies the data being sent.
         *
         * @param item A handle to the data item for which data is being
         * exchanged during the transaction. This handle must have been created
         * by a previous call to the DdeCreateStringHandle function. This
         * parameter is ignored (and should be set to 0L) if the wType parameter
         * is XTYP_EXECUTE.
         *
         * @param wFmt The standard clipboard format in which the data item is
         * being submitted or requested.
         *
         * <p>
         * If the transaction specified by the wType parameter does not pass
         * data or is XTYP_EXECUTE, this parameter should be zero.</p>
         *
         * <p>
         * If the transaction specified by the wType parameter references
         * non-execute DDE data ( XTYP_POKE, XTYP_ADVSTART, XTYP_ADVSTOP,
         * XTYP_REQUEST), the wFmt value must be either a valid predefined (CF_)
         * DDE format or a valid registered clipboard format.</p>
         *
         * @param timeout The maximum amount of time, in milliseconds, that
         * the client will wait for a response from the server application in a
         * synchronous transaction. This parameter should be TIMEOUT_ASYNC for
         * asynchronous transactions.
         * 
         * @param userHandle data to associate with the transaction
         *
         * @param result A pointer to a variable that receives the result of
         * the transaction. An application that does not check the result can
         * use NULL for this value. For synchronous transactions, the low-order
         * word of this variable contains any applicable DDE_ flags resulting
         * from the transaction. This provides support for applications
         * dependent on DDE_APPSTATUS bits. It is, however, recommended that
         * applications no longer use these bits because they may not be
         * supported in future versions of the Dynamic Data Exchange Management
         * Library (DDEML). For asynchronous transactions, this variable is
         * filled with a unique transaction identifier for use with the
         * DdeAbandonTransaction function and the XTYP_XACT_COMPLETE
         * transaction.
         * 
         * <p>
         * If an error occurs, a DdemlException is raised with the appropriate
         * error code:</p>
         * 
         * <ul>
         * <li>DMLERR_BUSY</li>
         * <li>DMLERR_NOTPROCESSED</li>
         * </ul>
         */
        public void poke(Pointer data, int dataLength, Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle);
        
        /**
         * Run an XTYP_POKE client transaction
         * 
         * @param data The beginning of the data the client must pass to the
         * server.
         *
         * <p>
         * Optionally, an application can specify the data handle (HDDEDATA) to
         * pass to the server and in that case the cbData parameter should be
         * set to -1. This parameter is required only if the wType parameter is
         * XTYP_EXECUTE or XTYP_POKE. Otherwise, this parameter should be
         * NULL.</p>
         *
         * <p>
         * For the optional usage of this parameter, XTYP_POKE transactions
         * where pData is a data handle, the handle must have been created by a
         * previous call to the DdeCreateDataHandle function, employing the same
         * data format specified in the wFmt parameter.</p>
         *
         * @param dataLength The length, in bytes, of the data pointed to by the
         * pData parameter, including the terminating NULL, if the data is a
         * string. A value of -1 indicates that pData is a data handle that
         * identifies the data being sent.
         *
         * @param item The data item for which data is being exchanged during
         * the transaction. This parameter is ignored (and should be set to NULL)
         * if the wType parameter is XTYP_EXECUTE.
         *
         * @param wFmt The standard clipboard format in which the data item is
         * being submitted or requested.
         *
         * <p>
         * If the transaction specified by the wType parameter does not pass
         * data or is XTYP_EXECUTE, this parameter should be zero.</p>
         *
         * <p>
         * If the transaction specified by the wType parameter references
         * non-execute DDE data ( XTYP_POKE, XTYP_ADVSTART, XTYP_ADVSTOP,
         * XTYP_REQUEST), the wFmt value must be either a valid predefined (CF_)
         * DDE format or a valid registered clipboard format.</p>
         *
         * @param timeout The maximum amount of time, in milliseconds, that
         * the client will wait for a response from the server application in a
         * synchronous transaction. This parameter should be TIMEOUT_ASYNC for
         * asynchronous transactions.
         * 
         * @param userHandle data to associate with the transaction
         *
         * @param result A pointer to a variable that receives the result of
         * the transaction. An application that does not check the result can
         * use NULL for this value. For synchronous transactions, the low-order
         * word of this variable contains any applicable DDE_ flags resulting
         * from the transaction. This provides support for applications
         * dependent on DDE_APPSTATUS bits. It is, however, recommended that
         * applications no longer use these bits because they may not be
         * supported in future versions of the Dynamic Data Exchange Management
         * Library (DDEML). For asynchronous transactions, this variable is
         * filled with a unique transaction identifier for use with the
         * DdeAbandonTransaction function and the XTYP_XACT_COMPLETE
         * transaction.
         * 
         * <p>
         * If an error occurs, a DdemlException is raised with the appropriate
         * error code:</p>
         * 
         * <ul>
         * <li>DMLERR_BUSY</li>
         * <li>DMLERR_NOTPROCESSED</li>
         * </ul>
         */
        public void poke(Pointer data, int dataLength, String item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle);
        
        /**
         * Begins a data transaction between a client and a server. Only a
         * Dynamic Data Exchange (DDE) client application can call this
         * function, and the application can use it only after establishing a
         * conversation with the server.
         *
         * @param item A handle to the data item for which data is being
         * exchanged during the transaction. This handle must have been created
         * by a previous call to the DdeCreateStringHandle function. This
         * parameter is ignored (and should be set to 0L) if the wType parameter
         * is XTYP_EXECUTE.
         *
         * @param wFmt The standard clipboard format in which the data item is
         * being submitted or requested.
         * 
         * <p>
         * If the transaction specified by the wType parameter references
         * non-execute DDE data ( XTYP_POKE, XTYP_ADVSTART, XTYP_ADVSTOP,
         * XTYP_REQUEST), the wFmt value must be either a valid predefined (CF_)
         * DDE format or a valid registered clipboard format.</p>
         *
         * @param timeout The maximum amount of time, in milliseconds, that
         * the client will wait for a response from the server application in a
         * synchronous transaction. This parameter should be TIMEOUT_ASYNC for
         * asynchronous transactions.
         *
         * @param result A pointer to a variable that receives the result of
         * the transaction. An application that does not check the result can
         * use NULL for this value. For synchronous transactions, the low-order
         * word of this variable contains any applicable DDE_ flags resulting
         * from the transaction. This provides support for applications
         * dependent on DDE_APPSTATUS bits. It is, however, recommended that
         * applications no longer use these bits because they may not be
         * supported in future versions of the Dynamic Data Exchange Management
         * Library (DDEML). For asynchronous transactions, this variable is
         * filled with a unique transaction identifier for use with the
         * DdeAbandonTransaction function and the XTYP_XACT_COMPLETE
         * transaction.
         * 
         * @param userHandle data to associate with the transaction
         *
         * @return If the function succeeds, the return value is a data handle
         * that identifies the data for successful synchronous transactions in
         * which the client expects data from the server. The return value is
         * nonzero for successful asynchronous transactions and for synchronous
         * transactions in which the client does not expect data. The return
         * value is zero for all unsuccessful transactions.
         *
         * <p>
         * If an error occurs, a DdemlException is raised with the appropriate
         * error code:</p>
         *
         * <ul>
         * <li>DMLERR_NOTPROCESSED</li>
         * </ul>
         */
        public Ddeml.HDDEDATA request(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle);
        
        /**
         * Begins a data transaction between a client and a server. Only a
         * Dynamic Data Exchange (DDE) client application can call this
         * function, and the application can use it only after establishing a
         * conversation with the server.
         *
         * @param item The data item for which data is being exchanged during
         * the transaction. This parameter is ignored (and should be set to NULL)
         * if the wType parameter is XTYP_EXECUTE.
         *
         * @param wFmt The standard clipboard format in which the data item is
         * being submitted or requested.
         * 
         * <p>
         * If the transaction specified by the wType parameter references
         * non-execute DDE data ( XTYP_POKE, XTYP_ADVSTART, XTYP_ADVSTOP,
         * XTYP_REQUEST), the wFmt value must be either a valid predefined (CF_)
         * DDE format or a valid registered clipboard format.</p>
         *
         * @param timeout The maximum amount of time, in milliseconds, that
         * the client will wait for a response from the server application in a
         * synchronous transaction. This parameter should be TIMEOUT_ASYNC for
         * asynchronous transactions.
         *
         * @param result A pointer to a variable that receives the result of
         * the transaction. An application that does not check the result can
         * use NULL for this value. For synchronous transactions, the low-order
         * word of this variable contains any applicable DDE_ flags resulting
         * from the transaction. This provides support for applications
         * dependent on DDE_APPSTATUS bits. It is, however, recommended that
         * applications no longer use these bits because they may not be
         * supported in future versions of the Dynamic Data Exchange Management
         * Library (DDEML). For asynchronous transactions, this variable is
         * filled with a unique transaction identifier for use with the
         * DdeAbandonTransaction function and the XTYP_XACT_COMPLETE
         * transaction.
         * 
         * @param userHandle data to associate with the transaction
         *
         * @return If the function succeeds, the return value is a data handle
         * that identifies the data for successful synchronous transactions in
         * which the client expects data from the server. The return value is
         * nonzero for successful asynchronous transactions and for synchronous
         * transactions in which the client does not expect data. The return
         * value is zero for all unsuccessful transactions.
         *
         * <p>
         * If an error occurs, a DdemlException is raised with the appropriate
         * error code:</p>
         *
         * <ul>
         * <li>DMLERR_NOTPROCESSED</li>
         * </ul>
         */
        public Ddeml.HDDEDATA request(String item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle);
        
        /**
         * Begins a data transaction between a client and a server. Only a
         * Dynamic Data Exchange (DDE) client application can call this
         * function, and the application can use it only after establishing a
         * conversation with the server.
         *
         * @param data The beginning of the data the client must pass to the
         * server.
         *
         * <p>
         * Optionally, an application can specify the data handle (HDDEDATA) to
         * pass to the server and in that case the cbData parameter should be
         * set to -1. This parameter is required only if the wType parameter is
         * XTYP_EXECUTE or XTYP_POKE. Otherwise, this parameter should be
         * NULL.</p>
         *
         * <p>
         * For the optional usage of this parameter, XTYP_POKE transactions
         * where pData is a data handle, the handle must have been created by a
         * previous call to the DdeCreateDataHandle function, employing the same
         * data format specified in the wFmt parameter.</p>
         *
         * @param dataLength The length, in bytes, of the data pointed to by the
         * pData parameter, including the terminating NULL, if the data is a
         * string. A value of -1 indicates that pData is a data handle that
         * identifies the data being sent.
         *
         * @param item A handle to the data item for which data is being
         * exchanged during the transaction. This handle must have been created
         * by a previous call to the DdeCreateStringHandle function. This
         * parameter is ignored (and should be set to 0L) if the wType parameter
         * is XTYP_EXECUTE.
         *
         * @param wFmt The standard clipboard format in which the data item is
         * being submitted or requested.
         *
         * <p>
         * If the transaction specified by the wType parameter does not pass
         * data or is XTYP_EXECUTE, this parameter should be zero.</p>
         *
         * <p>
         * If the transaction specified by the wType parameter references
         * non-execute DDE data ( XTYP_POKE, XTYP_ADVSTART, XTYP_ADVSTOP,
         * XTYP_REQUEST), the wFmt value must be either a valid predefined (CF_)
         * DDE format or a valid registered clipboard format.</p>
         *
         * @param transaction The transaction type. This parameter can be one of the
         * following values.
         *
         * <table>
         * <tr><th>Value</th><th>Meaning</th></tr>
         * <tr><td>XTYP_ADVSTART</td><td>Begins an advise loop. Any number of
         * distinct advise loops can exist within a conversation. An application
         * can alter the advise loop type by combining the XTYP_ADVSTART
         * transaction type with one or more of the following flags:
         * <dl>
         * <dt>XTYPF_NODATA.</dt><dd>Instructs the server to notify the client
         * of any data changes without actually sending the data. This flag
         * gives the client the option of ignoring the notification or
         * requesting the changed data from the server.</dd>
         * <dt>XTYPF_ACKREQ.</dt><dd>Instructs the server to wait until the
         * client acknowledges that it received the previous data item before
         * sending the next data item. This flag prevents a fast server from
         * sending data faster than the client can process it.</dd>
         * </dl>
         * </td></tr>
         * <tr><td>XTYP_ADVSTOP</td><td>Ends an advise loop.</td></tr>
         * <tr><td>XTYP_EXECUTE</td><td>Begins an execute transaction.</td></tr>
         * <tr><td>XTYP_POKE</td><td>Begins a poke transaction.</td></tr>
         * <tr><td>XTYP_REQUEST</td><td>Begins a request transaction.</td></tr>
         * </table>
         *
         * @param timeout The maximum amount of time, in milliseconds, that
         * the client will wait for a response from the server application in a
         * synchronous transaction. This parameter should be TIMEOUT_ASYNC for
         * asynchronous transactions.
         *
         * @param result A pointer to a variable that receives the result of
         * the transaction. An application that does not check the result can
         * use NULL for this value. For synchronous transactions, the low-order
         * word of this variable contains any applicable DDE_ flags resulting
         * from the transaction. This provides support for applications
         * dependent on DDE_APPSTATUS bits. It is, however, recommended that
         * applications no longer use these bits because they may not be
         * supported in future versions of the Dynamic Data Exchange Management
         * Library (DDEML). For asynchronous transactions, this variable is
         * filled with a unique transaction identifier for use with the
         * DdeAbandonTransaction function and the XTYP_XACT_COMPLETE
         * transaction.
         * 
         * @param userHandle data to associate with the transaction
         *
         * @return If the function succeeds, the return value is a data handle
         * that identifies the data for successful synchronous transactions in
         * which the client expects data from the server. The return value is
         * nonzero for successful asynchronous transactions and for synchronous
         * transactions in which the client does not expect data. The return
         * value is zero for all unsuccessful transactions.
         *
         * <p>
         * If an error occurs, a DdemlException is raised with the appropriate
         * error code:</p>
         *
         * <ul>
         * <li>DMLERR_ADVACKTIMEOUT</li>
         * <li>DMLERR_BUSY</li>
         * <li>DMLERR_DATAACKTIMEOUT</li>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_EXECACKTIMEOUT</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_MEMORY_ERROR</li>
         * <li>DMLERR_NO_CONV_ESTABLISHED</li>
         * <li>DMLERR_NO_ERROR</li>
         * <li>DMLERR_NOTPROCESSED</li>
         * <li>DMLERR_POKEACKTIMEOUT</li>
         * <li>DMLERR_POSTMSG_FAILED</li>
         * <li>DMLERR_REENTRANCY</li>
         * <li>DMLERR_SERVER_DIED</li>
         * <li>DMLERR_UNADVACKTIMEOUT</li>
         * </ul>
         */
        public Ddeml.HDDEDATA clientTransaction(Pointer data, int dataLength, Ddeml.HSZ item, int wFmt, int transaction, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle);
        
        /**
         * Begins a data transaction between a client and a server. Only a
         * Dynamic Data Exchange (DDE) client application can call this
         * function, and the application can use it only after establishing a
         * conversation with the server.
         *
         * @param data The beginning of the data the client must pass to the
         * server.
         *
         * <p>
         * Optionally, an application can specify the data handle (HDDEDATA) to
         * pass to the server and in that case the cbData parameter should be
         * set to -1. This parameter is required only if the wType parameter is
         * XTYP_EXECUTE or XTYP_POKE. Otherwise, this parameter should be
         * NULL.</p>
         *
         * <p>
         * For the optional usage of this parameter, XTYP_POKE transactions
         * where pData is a data handle, the handle must have been created by a
         * previous call to the DdeCreateDataHandle function, employing the same
         * data format specified in the wFmt parameter.</p>
         *
         * @param dataLength The length, in bytes, of the data pointed to by the
         * pData parameter, including the terminating NULL, if the data is a
         * string. A value of -1 indicates that pData is a data handle that
         * identifies the data being sent.
         *
         * @param item The data item for which data is being exchanged during
         * the transaction. This parameter is ignored (and should be set to NULL)
         * if the wType parameter is XTYP_EXECUTE.
         *
         * @param wFmt The standard clipboard format in which the data item is
         * being submitted or requested.
         *
         * <p>
         * If the transaction specified by the wType parameter does not pass
         * data or is XTYP_EXECUTE, this parameter should be zero.</p>
         *
         * <p>
         * If the transaction specified by the wType parameter references
         * non-execute DDE data ( XTYP_POKE, XTYP_ADVSTART, XTYP_ADVSTOP,
         * XTYP_REQUEST), the wFmt value must be either a valid predefined (CF_)
         * DDE format or a valid registered clipboard format.</p>
         *
         * @param transaction The transaction type. This parameter can be one of the
         * following values.
         *
         * <table>
         * <tr><th>Value</th><th>Meaning</th></tr>
         * <tr><td>XTYP_ADVSTART</td><td>Begins an advise loop. Any number of
         * distinct advise loops can exist within a conversation. An application
         * can alter the advise loop type by combining the XTYP_ADVSTART
         * transaction type with one or more of the following flags:
         * <dl>
         * <dt>XTYPF_NODATA.</dt><dd>Instructs the server to notify the client
         * of any data changes without actually sending the data. This flag
         * gives the client the option of ignoring the notification or
         * requesting the changed data from the server.</dd>
         * <dt>XTYPF_ACKREQ.</dt><dd>Instructs the server to wait until the
         * client acknowledges that it received the previous data item before
         * sending the next data item. This flag prevents a fast server from
         * sending data faster than the client can process it.</dd>
         * </dl>
         * </td></tr>
         * <tr><td>XTYP_ADVSTOP</td><td>Ends an advise loop.</td></tr>
         * <tr><td>XTYP_EXECUTE</td><td>Begins an execute transaction.</td></tr>
         * <tr><td>XTYP_POKE</td><td>Begins a poke transaction.</td></tr>
         * <tr><td>XTYP_REQUEST</td><td>Begins a request transaction.</td></tr>
         * </table>
         *
         * @param timeout The maximum amount of time, in milliseconds, that
         * the client will wait for a response from the server application in a
         * synchronous transaction. This parameter should be TIMEOUT_ASYNC for
         * asynchronous transactions.
         *
         * @param result A pointer to a variable that receives the result of
         * the transaction. An application that does not check the result can
         * use NULL for this value. For synchronous transactions, the low-order
         * word of this variable contains any applicable DDE_ flags resulting
         * from the transaction. This provides support for applications
         * dependent on DDE_APPSTATUS bits. It is, however, recommended that
         * applications no longer use these bits because they may not be
         * supported in future versions of the Dynamic Data Exchange Management
         * Library (DDEML). For asynchronous transactions, this variable is
         * filled with a unique transaction identifier for use with the
         * DdeAbandonTransaction function and the XTYP_XACT_COMPLETE
         * transaction.
         * 
         * @param userHandle data to associate with the transaction
         *
         * @return If the function succeeds, the return value is a data handle
         * that identifies the data for successful synchronous transactions in
         * which the client expects data from the server. The return value is
         * nonzero for successful asynchronous transactions and for synchronous
         * transactions in which the client does not expect data. The return
         * value is zero for all unsuccessful transactions.
         *
         * <p>
         * If an error occurs, a DdemlException is raised with the appropriate
         * error code:</p>
         *
         * <ul>
         * <li>DMLERR_ADVACKTIMEOUT</li>
         * <li>DMLERR_BUSY</li>
         * <li>DMLERR_DATAACKTIMEOUT</li>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_EXECACKTIMEOUT</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_MEMORY_ERROR</li>
         * <li>DMLERR_NO_CONV_ESTABLISHED</li>
         * <li>DMLERR_NO_ERROR</li>
         * <li>DMLERR_NOTPROCESSED</li>
         * <li>DMLERR_POKEACKTIMEOUT</li>
         * <li>DMLERR_POSTMSG_FAILED</li>
         * <li>DMLERR_REENTRANCY</li>
         * <li>DMLERR_SERVER_DIED</li>
         * <li>DMLERR_UNADVACKTIMEOUT</li>
         * </ul>
         */
        public Ddeml.HDDEDATA clientTransaction(Pointer data, int dataLength, String item, int wFmt, int transaction, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle);
        
        /**
         * Begins a data transaction between a client and a server. Only a
         * Dynamic Data Exchange (DDE) client application can call this
         * function, and the application can use it only after establishing a
         * conversation with the server.
         * 
         * @param item A handle to the data item for which data is being
         * exchanged during the transaction. This handle must have been created
         * by a previous call to the DdeCreateStringHandle function. This
         * parameter is ignored (and should be set to 0L) if the wType parameter
         * is XTYP_EXECUTE.
         *
         * @param wFmt The standard clipboard format in which the data item is
         * being submitted or requested.
         *
         * @param timeout The maximum amount of time, in milliseconds, that
         * the client will wait for a response from the server application in a
         * synchronous transaction. This parameter should be TIMEOUT_ASYNC for
         * asynchronous transactions.
         *
         * @param result A pointer to a variable that receives the result of
         * the transaction. An application that does not check the result can
         * use NULL for this value. For synchronous transactions, the low-order
         * word of this variable contains any applicable DDE_ flags resulting
         * from the transaction. This provides support for applications
         * dependent on DDE_APPSTATUS bits. It is, however, recommended that
         * applications no longer use these bits because they may not be
         * supported in future versions of the Dynamic Data Exchange Management
         * Library (DDEML). For asynchronous transactions, this variable is
         * filled with a unique transaction identifier for use with the
         * DdeAbandonTransaction function and the XTYP_XACT_COMPLETE
         * transaction.
         *
         * @param userHandle data to associate with the transaction
         * 
         * <p>
         * If an error occurs, a DdemlException is raised with the appropriate
         * error code:</p>
         *
         * <ul>
         * <li>DMLERR_NOTPROCESSED</li>
         * </ul>
         */
        public void advstart(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle);
        
        /**
         * Begins a data transaction between a client and a server. Only a
         * Dynamic Data Exchange (DDE) client application can call this
         * function, and the application can use it only after establishing a
         * conversation with the server.
         * 
         * @param item The data item for which data is being exchanged during
         * the transaction. This parameter is ignored (and should be set to NULL)
         * if the wType parameter is XTYP_EXECUTE.
         *
         * @param wFmt The standard clipboard format in which the data item is
         * being submitted or requested.
         *
         * @param timeout The maximum amount of time, in milliseconds, that
         * the client will wait for a response from the server application in a
         * synchronous transaction. This parameter should be TIMEOUT_ASYNC for
         * asynchronous transactions.
         *
         * @param result A pointer to a variable that receives the result of
         * the transaction. An application that does not check the result can
         * use NULL for this value. For synchronous transactions, the low-order
         * word of this variable contains any applicable DDE_ flags resulting
         * from the transaction. This provides support for applications
         * dependent on DDE_APPSTATUS bits. It is, however, recommended that
         * applications no longer use these bits because they may not be
         * supported in future versions of the Dynamic Data Exchange Management
         * Library (DDEML). For asynchronous transactions, this variable is
         * filled with a unique transaction identifier for use with the
         * DdeAbandonTransaction function and the XTYP_XACT_COMPLETE
         * transaction.
         *
         * @param userHandle data to associate with the transaction
         * 
         * <p>
         * If an error occurs, a DdemlException is raised with the appropriate
         * error code:</p>
         *
         * <ul>
         * <li>DMLERR_NOTPROCESSED</li>
         * </ul>
         */
        public void advstart(String item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle);
        
        /**
         * A client uses the XTYP_ADVSTOP transaction to end an advise loop with
         * a server. A Dynamic Data Exchange (DDE) server callback function,
         * DdeCallback, receives this transaction when a client specifies
         * XTYP_ADVSTOP in the DdeClientTransaction function.
         *
         * @param item A handle to the data item for which data is being
         * exchanged during the transaction. This handle must have been created
         * by a previous call to the DdeCreateStringHandle function. This
         * parameter is ignored (and should be set to 0L) if the wType parameter
         * is XTYP_EXECUTE.
         *
         * @param wFmt The standard clipboard format in which the data item is
         * being submitted or requested.
         *
         * @param timeout The maximum amount of time, in milliseconds, that
         * the client will wait for a response from the server application in a
         * synchronous transaction. This parameter should be TIMEOUT_ASYNC for
         * asynchronous transactions.
         *
         * @param result A pointer to a variable that receives the result of
         * the transaction. An application that does not check the result can
         * use NULL for this value. For synchronous transactions, the low-order
         * word of this variable contains any applicable DDE_ flags resulting
         * from the transaction. This provides support for applications
         * dependent on DDE_APPSTATUS bits. It is, however, recommended that
         * applications no longer use these bits because they may not be
         * supported in future versions of the Dynamic Data Exchange Management
         * Library (DDEML). For asynchronous transactions, this variable is
         * filled with a unique transaction identifier for use with the
         * DdeAbandonTransaction function and the XTYP_XACT_COMPLETE
         * transaction.
         *
         * @param userHandle data to associate with the transaction
         * 
         * <p>
         * If an error occurs, a DdemlException is raised with the appropriate
         * error code:</p>
         *
         * <ul>
         * <li>DMLERR_NOTPROCESSED</li>
         * </ul>
         */
        public void advstop(Ddeml.HSZ item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle);
        
        /**
         * A client uses the XTYP_ADVSTOP transaction to end an advise loop with
         * a server. A Dynamic Data Exchange (DDE) server callback function,
         * DdeCallback, receives this transaction when a client specifies
         * XTYP_ADVSTOP in the DdeClientTransaction function.
         *
         * @param item The data item for which data is being exchanged during
         * the transaction. This parameter is ignored (and should be set to NULL)
         * if the wType parameter is XTYP_EXECUTE.
         *
         * @param wFmt The standard clipboard format in which the data item is
         * being submitted or requested.
         *
         * @param timeout The maximum amount of time, in milliseconds, that
         * the client will wait for a response from the server application in a
         * synchronous transaction. This parameter should be TIMEOUT_ASYNC for
         * asynchronous transactions.
         *
         * @param result A pointer to a variable that receives the result of
         * the transaction. An application that does not check the result can
         * use NULL for this value. For synchronous transactions, the low-order
         * word of this variable contains any applicable DDE_ flags resulting
         * from the transaction. This provides support for applications
         * dependent on DDE_APPSTATUS bits. It is, however, recommended that
         * applications no longer use these bits because they may not be
         * supported in future versions of the Dynamic Data Exchange Management
         * Library (DDEML). For asynchronous transactions, this variable is
         * filled with a unique transaction identifier for use with the
         * DdeAbandonTransaction function and the XTYP_XACT_COMPLETE
         * transaction.
         *
         * @param userHandle data to associate with the transaction
         * 
         * <p>
         * If an error occurs, a DdemlException is raised with the appropriate
         * error code:</p>
         *
         * <ul>
         * <li>DMLERR_NOTPROCESSED</li>
         * </ul>
         */
        public void advstop(String item, int wFmt, int timeout, WinDef.DWORDByReference result, DWORD_PTR userHandle);
        
        /**
         * Abandons the specified asynchronous transaction and releases all
         * resources associated with the transaction.
         *
         * @param transactionId The identifier of the transaction to be
         * abandoned. If this parameter is 0L, all active transactions in the
         * specified conversation are abandoned.
         *
         * <p>
         * If the method fails a DdeException will be raised with the 
         * corresponding errorCode:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * <li>DMLERR_UNFOUND_QUEUE_ID</li>
         * </ul>
         */
        public void abandonTransaction(int transactionId);
        
        /**
         * Abandons all transactions of this conversation and releases all
         * resources associated with the transaction.
         *
         * <p>
         * If the method fails a DdeException will be raised with the 
         * corresponding errorCode:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * <li>DMLERR_UNFOUND_QUEUE_ID</li>
         * </ul>
         */
        public void abandonTransactions();
        
        /**
         * Impersonates a Dynamic Data Exchange (DDE) client application in a
         * DDE client conversation.
         */
        public void impersonateClient();
        
        /**
         * Terminates a conversation started by either the DdeConnect or
         * DdeConnectList function and invalidates the specified conversation
         * handle.
         * 
         * <p>Note: This wraps the DdeDisconnect function and aligns the name
         * with the Closable-wording.</p>
         * 
         * <p>
         * If the method fails a DdeException will be raised with the 
         * corresponding errorCode:</p>
         * 
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_NO_CONV_ESTABLISHED</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public void close();
        
        /**
         * Enables a client Dynamic Data Exchange Management Library (DDEML)
         * application to attempt to reestablish a conversation with a service
         * that has terminated a conversation with the client. When the
         * conversation is reestablished, the Dynamic Data Exchange Management
         * Library (DDEML) attempts to reestablish any preexisting advise loops.
         *
         * <p>
         * If the method fails a DdeException will be raised with the
         * corresponding errorCode:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_CONV_ESTABLISHED</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public void reconnect();

        /**
         * Enables or disables transactions for a specific conversation or for
         * all conversations currently established by the calling application.
         *
         * @param wCmd The function code. This parameter can be one of the
         * following values.
         * <table>
         * <tr><th>Value</th><th>Meaning</th></tr>
         * <tr><td>EC_ENABLEALL</td><td>Enables all transactions for the
         * specified conversation.</td></tr>
         * <tr><td>EC_ENABLEONE</td><td>Enables one transaction for the
         * specified conversation.</td></tr>
         * <tr><td>EC_DISABLE</td><td>Disables all blockable transactions for
         * the specified conversation.
         *
         * <p>
         * A server application can disable the following transactions:</p>
         * <ul>
         * <li>XTYP_ADVSTART</li>
         * <li>XTYP_ADVSTOP</li>
         * <li>XTYP_EXECUTE</li>
         * <li>XTYP_POKE</li>
         * <li>XTYP_REQUEST</li>
         * </ul>
         * <p>
         * A client application can disable the following transactions:</p>
         * <ul>
         * <li>XTYP_ADVDATA</li>
         * <li>XTYP_XACT_COMPLETE</li>
         * </ul>
         * </td></tr>
         * <tr><td>EC_QUERYWAITING</td><td>Determines whether any transactions
         * are in the queue for the specified conversation.</td></tr>
         * </table>
         *
         * @return If the function succeeds, the return value is nonzero.
         *
         * <p>
         * If the function fails, the return value is zero.</p>
         *
         * <p>
         * If the wCmd parameter is EC_QUERYWAITING, and the application
         * transaction queue contains one or more unprocessed transactions that
         * are not being processed, the return value is TRUE; otherwise, it is
         * FALSE.</p>
         *
         * <p>
         * If the method fails a DdeException will be raised with the
         * corresponding errorCode:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public boolean enableCallback(int wCmd);
        
        /**
         * Associates an application-defined value with a conversation handle or
         * a transaction identifier. This is useful for simplifying the
         * processing of asynchronous transactions. An application can use the
         * DdeQueryConvInfo function to retrieve this value.
         *
         * @param id The transaction identifier to associate with the value
         * specified by the hUser parameter. An application should set this
         * parameter to QID_SYNC to associate hUser with the conversation
         * identified by the hConv parameter.
         * @param hUser The value to be associated with the conversation handle.
         * 
         * <p>
         * If the method fails a DdeException will be raised with the
         * corresponding errorCode:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * <li>DMLERR_UNFOUND_QUEUE_ID</li>
         * </ul>
         */
        public void setUserHandle(int id, DWORD_PTR hUser) throws DdemlException;
        
        /**
         * Retrieves information about a Dynamic Data Exchange (DDE) transaction
         * and about the conversation in which the transaction takes place.
         *
         * @param idTransaction The transaction. For asynchronous transactions,
         * this parameter should be a transaction identifier returned by the
         * DdeClientTransaction function. For synchronous transactions, this
         * parameter should be QID_SYNC.
         * @return The CONVINFO structure
         *
         * <p>
         * If the method fails a DdeException will be raised with the
         * corresponding errorCode:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_NO_CONV_ESTABLISHED</li>
         * <li>DMLERR_NO_ERROR</li>
         * <li>DMLERR_UNFOUND_QUEUE_ID</li>
         * </ul>
         */
        public CONVINFO queryConvInfo(int idTransaction) throws DdemlException;
    }

    /** 
     * The IDdeClient defines functions that wrap a ddeml instance. and are
     * not tied to conversation.
     */
    public interface IDdeClient extends Closeable {
        public Integer getInstanceIdentitifier();
        /**
         * Registers an application with the Dynamic Data Exchange Management
         * Library (DDEML). An application must call this function before
         * calling any other Dynamic Data Exchange Management Library (DDEML)
         * function.
         * 
         * @param afCmd A set of APPCMD_, CBF_, and MF_ flags. The APPCMD_ flags
         * provide special instructions to DdeInitialize. The CBF_ flags specify
         * filters that prevent specific types of transactions from reaching the
         * callback function. The MF_ flags specify the types of DDE activity
         * that a DDE monitoring application monitors. Using these flags
         * enhances the performance of a DDE application by eliminating
         * unnecessary calls to the callback function.
         *
         * <p>
         * This parameter can be one or more of the following values.</p>
         *
         * <table>
         * <tr ><th>Value</th><th>Meaning</th></tr>
         * <tr><td><dl>
         * <dt><strong>APPCLASS_MONITOR</strong></dt>
         * <dt>0x00000001L</dt>
         * </dl>
         * </td><td>
         * <p>
         * Makes it possible for the application to monitor DDE activity in the
         * system. This flag is for use by DDE monitoring applications. The
         * application specifies the types of DDE activity to monitor by
         * combining one or more monitor flags with the APPCLASS_MONITOR flag.
         * For details, see the following Remarks section.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>APPCLASS_STANDARD</strong></dt>
         * <dt>0x00000000L</dt>
         * </dl>
         * </td><td>
         * <p>
         * Registers the application as a standard (nonmonitoring) DDEML
         * application.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>APPCMD_CLIENTONLY</strong></dt>
         * <dt>0x00000010L</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the application from becoming a server in a DDE
         * conversation. The application can only be a client. This flag reduces
         * consumption of resources by the DDEML. It includes the functionality
         * of the CBF_FAIL_ALLSVRXACTIONS flag.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>APPCMD_FILTERINITS</strong></dt>
         * <dt>0x00000020L</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the DDEML from sending XTYP_CONNECT and XTYP_WILDCONNECT
         * transactions to the application until the application has created its
         * string handles and registered its service names or has turned off
         * filtering by a subsequent call to the DdeNameService or DdeInitialize
         * function. This flag is always in effect when an application calls
         * DdeInitialize for the first time, regardless of whether the
         * application specifies the flag. On subsequent calls to DdeInitialize,
         * not specifying this flag turns off the application's service-name
         * filters, but specifying it turns on the application's service name
         * filters.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>CBF_FAIL_ALLSVRXACTIONS</strong></dt>
         * <dt>0x0003f000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the callback function from receiving server transactions.
         * The system returns DDE_FNOTPROCESSED to each client that sends a
         * transaction to this application. This flag is equivalent to combining
         * all CBF_FAIL_ flags.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>CBF_FAIL_ADVISES</strong></dt>
         * <dt>0x00004000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the callback function from receiving XTYP_ADVSTART and
         * XTYP_ADVSTOP transactions. The system returns DDE_FNOTPROCESSED to
         * each client that sends an XTYP_ADVSTART or XTYP_ADVSTOP transaction
         * to the server.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>CBF_FAIL_CONNECTIONS</strong></dt>
         * <dt>0x00002000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the callback function from receiving XTYP_CONNECT and
         * XTYP_WILDCONNECT transactions.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>CBF_FAIL_EXECUTES</strong></dt>
         * <dt>0x00008000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the callback function from receiving XTYP_EXECUTE
         * transactions. The system returns DDE_FNOTPROCESSED to a client that
         * sends an XTYP_EXECUTE transaction to the server.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>CBF_FAIL_POKES</strong></dt>
         * <dt>0x00010000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the callback function from receiving XTYP_POKE transactions.
         * The system returns DDE_FNOTPROCESSED to a client that sends an
         * XTYP_POKE transaction to the server.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>CBF_FAIL_REQUESTS</strong></dt>
         * <dt>0x00020000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the callback function from receiving XTYP_REQUEST
         * transactions. The system returns DDE_FNOTPROCESSED to a client that
         * sends an XTYP_REQUEST transaction to the server.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>CBF_FAIL_SELFCONNECTIONS</strong></dt>
         * <dt>0x00001000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the callback function from receiving XTYP_CONNECT
         * transactions from the application's own instance. This flag prevents
         * an application from establishing a DDE conversation with its own
         * instance. An application should use this flag if it needs to
         * communicate with other instances of itself but not with itself.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>CBF_SKIP_ALLNOTIFICATIONS</strong></dt>
         * <dt>0x003c0000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the callback function from receiving any notifications. This
         * flag is equivalent to combining all CBF_SKIP_ flags.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>CBF_SKIP_CONNECT_CONFIRMS</strong></dt>
         * <dt>0x00040000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the callback function from receiving XTYP_CONNECT_CONFIRM
         * notifications.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>CBF_SKIP_DISCONNECTS</strong></dt>
         * <dt>0x00200000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the callback function from receiving XTYP_DISCONNECT
         * notifications.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>CBF_SKIP_REGISTRATIONS</strong></dt>
         * <dt>0x00080000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the callback function from receiving XTYP_REGISTER
         * notifications.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>CBF_SKIP_UNREGISTRATIONS</strong></dt>
         * <dt>0x00100000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Prevents the callback function from receiving XTYP_UNREGISTER
         * notifications.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>MF_CALLBACKS</strong></dt>
         * <dt>0x08000000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Notifies the callback function whenever a transaction is sent to any
         * DDE callback function in the system.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>MF_CONV</strong></dt>
         * <dt>0x40000000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Notifies the callback function whenever a conversation is established
         * or terminated.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>MF_ERRORS</strong></dt>
         * <dt>0x10000000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Notifies the callback function whenever a DDE error occurs.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>MF_HSZ_INFO</strong></dt>
         * <dt>0x01000000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Notifies the callback function whenever a DDE application creates,
         * frees, or increments the usage count of a string handle or whenever a
         * string handle is freed as a result of a call to the DdeUninitialize
         * function.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>MF_LINKS</strong></dt>
         * <dt>0x20000000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Notifies the callback function whenever an advise loop is started or
         * ended.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>MF_POSTMSGS</strong></dt>
         * <dt>0x04000000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Notifies the callback function whenever the system or an application
         * posts a DDE message.</p>
         * </td></tr>
         * <tr><td><dl>
         * <dt><strong>MF_SENDMSGS</strong></dt>
         * <dt>0x02000000</dt>
         * </dl>
         * </td><td>
         * <p>
         * Notifies the callback function whenever the system or an application
         * sends a DDE message.</p>
         * </td></tr>
         * </table>
         *
         * <p>
         * If the function failsa DdemlException is raised with one of the
         * following errroCodes:</p>
         * <ul>
         * <li>DMLERR_DLL_USAGE</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_SYS_ERROR</li>
         * </ul>
         */
        public void initialize(int afCmd) throws DdemlException;
        /**
         * Creates a handle that identifies the specified string. A Dynamic Data
         * Exchange (DDE) client or server application can pass the string
         * handle as a parameter to other Dynamic Data Exchange Management
         * Library (DDEML) functions.
         *
         * @param value The string for which a handle is to be
         * created. This string can be up to 255 characters. The reason for this
         * limit is that DDEML string management functions are implemented using
         * atoms.
         * 
         * @return If the function succeeds, the return value is a string
         * handle. A parameter NULL will cause NULL to be returned.
         *
         * <p>If the function fails, a DdemlException is raised with the
         * corresponding errorCode:</p>
         * <ul>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * <li>DMLERR_SYS_ERROR</li>
         * </ul>
         */
        public Ddeml.HSZ createStringHandle(String value) throws DdemlException;

        /**
         * Copies text associated with a string handle into a buffer.
         *
         * @param value A handle to the string to copy. This handle must have been
         * created by a previous call to the DdeCreateStringHandle function.
         * @return String corresponding to the supplied handle
         */
        public String queryString(Ddeml.HSZ value) throws DdemlException;
       
        /**
         * Frees a string handle in the calling application.
         * 
         * @param value A handle to the string handle to be freed. This handle
         * must have been created by a previous call to the
         * createStringHandle function. A NULL value will be silently ignored.
         * 
         * @return true if the function succeeds.
         */
        public boolean freeStringHandle(Ddeml.HSZ value);

        /**
         * Increments the usage count associated with the specified handle. This
         * function enables an application to save a string handle passed to the
         * application's Dynamic Data Exchange (DDE) callback function.
         * Otherwise, a string handle passed to the callback function is deleted
         * when the callback function returns. This function should also be used
         * to keep a copy of a string handle referenced by the CONVINFO
         * structure returned by the DdeQueryConvInfo function.
         *
         * @param value A handle to the string handle to be saved.
         * @return true if the function succeeded
         */
        public boolean keepStringHandle(Ddeml.HSZ value);
        
        /**
         * Registers or unregisters the service names a Dynamic Data Exchange
         * (DDE) server supports. This function causes the system to send
         * XTYP_REGISTER or XTYP_UNREGISTER transactions to other running
         * Dynamic Data Exchange Management Library (DDEML) client applications.
         *
         * @param name A handle to the string that specifies the service name
         * the server is registering or unregistering. An application that is
         * unregistering all of its service names should set this parameter to
         * 0L.
         * @param afCmd The service name options. This parameter can be one of
         * the following values.
         *
         * <table>
         * <tr><th>Value</th><th>Meaning</th></tr>
         * <tr><td>DNS_REGISTER</td><td>Registers the error code service
         * name.</td></tr>
         * <tr><td>DNS_UNREGISTER</td><td>Unregisters the error code service
         * name. If the hsz1 parameter is 0L, all service names registered by
         * the server will be unregistered.</td></tr>
         * <tr><td>DNS_FILTERON</td><td>Turns on service name initiation
         * filtering. The filter prevents a server from receiving XTYP_CONNECT
         * transactions for service names it has not registered. This is the
         * default setting for this filter.
         * <br><br>
         * If a server application does not register any service names, the
         * application cannot receive XTYP_WILDCONNECT transactions.
         * </td></tr>
         * <tr><td>DNS_FILTEROFF</td><td>Turns off service name initiation
         * filtering. If this flag is specified, the server receives an
         * XTYP_CONNECT transaction whenever another DDE application calls the
         * DdeConnect function, regardless of the service name.</td></tr>
         * </table>
         * <p>
         * If the function fails, a DdemlException is raised with the
         * corresponding errorCode:</p>
         * 
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_DLL_USAGE</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public void nameService(Ddeml.HSZ name, int afCmd) throws DdemlException;
        
        /**
         * Registers or unregisters the service names a Dynamic Data Exchange
         * (DDE) server supports. This function causes the system to send
         * XTYP_REGISTER or XTYP_UNREGISTER transactions to other running
         * Dynamic Data Exchange Management Library (DDEML) client applications.
         *
         * @param name A string that specifies the service name the server is
         * registering or unregistering. An application that is unregistering
         * all of its service names should set this parameter to NULL.
         * @param afCmd The service name options. This parameter can be one of
         * the following values.
         *
         * <table>
         * <tr><th>Value</th><th>Meaning</th></tr>
         * <tr><td>DNS_REGISTER</td><td>Registers the error code service
         * name.</td></tr>
         * <tr><td>DNS_UNREGISTER</td><td>Unregisters the error code service
         * name. If the hsz1 parameter is 0L, all service names registered by
         * the server will be unregistered.</td></tr>
         * <tr><td>DNS_FILTERON</td><td>Turns on service name initiation
         * filtering. The filter prevents a server from receiving XTYP_CONNECT
         * transactions for service names it has not registered. This is the
         * default setting for this filter.
         * <br><br>
         * If a server application does not register any service names, the
         * application cannot receive XTYP_WILDCONNECT transactions.
         * </td></tr>
         * <tr><td>DNS_FILTEROFF</td><td>Turns off service name initiation
         * filtering. If this flag is specified, the server receives an
         * XTYP_CONNECT transaction whenever another DDE application calls the
         * DdeConnect function, regardless of the service name.</td></tr>
         * </table>
         * <p>
         * If the function fails, a DdemlException is raised with the
         * corresponding errorCode:</p>
         * 
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_DLL_USAGE</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public void nameService(String name, int afCmd) throws DdemlException;
        
        /**
         * @return See {@link Ddeml}.DMLERR_*
         */
        public int getLastError();
        
        /**
         * Establishes a conversation with a server application that supports
         * the specified service name and topic name pair. If more than one such
         * server exists, the system selects only one.
         *
         * @param service A handle to the string that specifies the service
         * name of the server application with which a conversation is to be
         * established. This handle must have been created by a previous call to
         * the DdeCreateStringHandle function. If this parameter is 0L, a
         * conversation is established with any available server.
         *
         * @param topic A handle to the string that specifies the name of the
         * topic on which a conversation is to be established. This handle must
         * have been created by a previous call to DdeCreateStringHandle. If
         * this parameter is 0L, a conversation on any topic supported by the
         * selected server is established.
         *
         * @param convcontext A pointer to the CONVCONTEXT structure that contains
         * conversation context information. If this parameter is NULL, the
         * server receives the default CONVCONTEXT structure during the
         * XTYP_CONNECT or XTYP_WILDCONNECT transaction.
         *
         * @return an established connection
         *
         * <p>If the function fails, a DdemlException is raised with the
         * corresponding errorCode:</p>
         *
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_CONV_ESTABLISHED</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public IDdeConnection connect(Ddeml.HSZ service, Ddeml.HSZ topic, Ddeml.CONVCONTEXT convcontext);
        
        /**
         * Establishes a conversation with a server application that supports
         * the specified service name and topic name pair. If more than one such
         * server exists, the system selects only one.
         *
         * @param service The service name of the server application with which
         * a conversation is to be established. If this parameter is NULL, a
         * conversation is established with any available server.
         *
         * @param topic The name of the topic on which a conversation is to be
         * established. If this parameter is NULL, a conversation on any topic
         * supported by the selected server is established.
         *
         * @param convcontext A pointer to the CONVCONTEXT structure that contains
         * conversation context information. If this parameter is NULL, the
         * server receives the default CONVCONTEXT structure during the
         * XTYP_CONNECT or XTYP_WILDCONNECT transaction.
         *
         * @return an established connection
         *
         * <p>If the function fails, a DdemlException is raised with the
         * corresponding errorCode:</p>
         *
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_CONV_ESTABLISHED</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public IDdeConnection connect(String service, String topic, Ddeml.CONVCONTEXT convcontext);
        
        /**
         * Creates a Dynamic Data Exchange (DDE) object and fills the object
         * with data from the specified buffer. A DDE application uses this
         * function during transactions that involve passing data to the partner
         * application.
         * 
         * @param pSrc The data to be copied to the DDE object. If this
         * parameter is NULL, no data is copied to the object.
         *
         * @param cb The amount of memory, in bytes, to copy from the buffer
         * pointed to by pSrc. (include the terminating NULL, if the data is a
         * string). If this parameter is zero, the pSrc parameter is ignored.
         *
         * @param cbOff An offset, in bytes, from the beginning of the buffer
         * pointed to by the pSrc parameter. The data beginning at this offset
         * is copied from the buffer to the DDE object.
         *
         * @param hszItem A handle to the string that specifies the data item
         * corresponding to the DDE object. This handle must have been created
         * by a previous call to the DdeCreateStringHandle function. If the data
         * handle is to be used in an XTYP_EXECUTE transaction, this parameter
         * must be 0L.
         *
         * @param wFmt The standard clipboard format of the data.
         *
         * @param afCmd The creation flags. This parameter can be
         * HDATA_APPOWNED, which specifies that the server application calling
         * the DdeCreateDataHandle function owns the data handle this function
         * creates. This flag enables the application to share the data handle
         * with other DDEML applications rather than creating a separate handle
         * to pass to each application. If this flag is specified, the
         * application must eventually free the shared memory object associated
         * with the handle by using the DdeFreeDataHandle function. If this flag
         * is not specified, the handle becomes invalid in the application that
         * created the handle after the data handle is returned by the
         * application's DDE callback function or is used as a parameter in
         * another DDEML function.
         *
         * @return If the function succeeds, the return value is a data handle.
         *
         * <p>
         * If the function fails a DdeException is raised with the following
         * errorCodes:</p>
         * 
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_MEMORY_ERROR</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public Ddeml.HDDEDATA createDataHandle(
                Pointer pSrc,
                int cb,
                int cbOff,
                Ddeml.HSZ hszItem,
                int wFmt,
                int afCmd);
        
        /**
         * Frees a Dynamic Data Exchange (DDE) object and deletes the data
         * handle associated with the object.
         *
         * @param hData A handle to the DDE object to be freed. This handle must
         * have been created by a previous call to the DdeCreateDataHandle
         * function or returned by the DdeClientTransaction function.
         * 
         * <p>
         * If the function fails a DdeException is raised with the following
         * errorCodes:</p>
         *
         * <ul>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public void freeDataHandle(Ddeml.HDDEDATA hData);

        /**
         * Adds data to the specified Dynamic Data Exchange (DDE) object. An
         * application can add data starting at any offset from the beginning of
         * the object. If new data overlaps data already in the object, the new
         * data overwrites the old data in the bytes where the overlap occurs.
         * The contents of locations in the object that have not been written to
         * are undefined.
         *
         * @param hData A handle to the DDE object that receives additional
         * data.
         *
         * @param pSrc The data to be added to the DDE object.
         *
         * @param cb The length, in bytes, of the data to be added to the DDE
         * object, including the terminating NULL, if the data is a string.
         *
         * @param cbOff An offset, in bytes, from the beginning of the DDE
         * object. The additional data is copied to the object beginning at this
         * offset.
         *
         * @return If the function succeeds, the return value is a new handle to
         * the DDE object. The new handle is used in all references to the
         * object.
         *
         * <p>
         * If the function fails a DdeException is raised with the following
         * errorCodes:</p>
         * 
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_MEMORY_ERROR</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public Ddeml.HDDEDATA addData(Ddeml.HDDEDATA hData, Pointer pSrc, int cb, int cbOff);

        /**
         * Copies data from the specified Dynamic Data Exchange (DDE) object to
         * the specified local buffer.
         *
         * @param hData A handle to the DDE object that contains the data to
         * copy.
         *
         * @param pDst A pointer to the buffer that receives the data. If this
         * parameter is NULL, the DdeGetData function returns the amount of
         * data, in bytes, that would be copied to the buffer.
         *
         * @param cbMax The maximum amount of data, in bytes, to copy to the
         * buffer pointed to by the pDst parameter. Typically, this parameter
         * specifies the length of the buffer pointed to by pDst.
         *
         * @param cbOff An offset within the DDE object. Data is copied from the
         * object beginning at this offset.
         *
         * @return If the pDst parameter points to a buffer, the return value is
         * the size, in bytes, of the memory object associated with the data
         * handle or the size specified in the cbMax parameter, whichever is
         * lower.
         *
         * <p>
         * If the function fails a DdeException is raised with the following
         * errorCodes:</p>
         * 
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public int getData(Ddeml.HDDEDATA hData, Pointer pDst, int cbMax, int cbOff);

        /**
         * An application must call the DdeUnaccessData function when it has
         * finished accessing the data in the object.
         *
         * @param hData A handle to the DDE object to be accessed.
         *
         * @param pcbDataSize A pointer to a variable that receives the size, in
         * bytes, of the DDE object identified by the hData parameter. If this
         * parameter is NULL, no size information is returned.
         *
         * @return If the function succeeds, the return value is a pointer to
         * the first byte of data in the DDE object.
         *
         * <p>
         * If the function fails a DdeException is raised with the following
         * errorCodes:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public Pointer accessData(Ddeml.HDDEDATA hData, WinDef.DWORDByReference pcbDataSize);

        /**
         * Unaccesses a Dynamic Data Exchange (DDE) object. An application must
         * call this function after it has finished accessing the object.
         *
         * @param hData A handle to the DDE object.
         * 
         * <p>
         * If the function fails a DdeException is raised with the following
         * errorCodes:</p>
         *
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public void unaccessData(Ddeml.HDDEDATA hData);
        
        /**
         * Causes the system to send an XTYP_ADVREQ transaction to the calling
         * (server) application's Dynamic Data Exchange (DDE) callback function
         * for each client with an active advise loop on the specified topic and
         * item. A server application should call this function whenever the
         * data associated with the topic name or item name pair changes.
         *
         * @param hszTopic A handle to a string that specifies the topic name.
         * To send notifications for all topics with active advise loops, an
         * application can set this parameter to 0L.
         *
         * @param hszItem A handle to a string that specifies the item name. To
         * send notifications for all items with active advise loops, an
         * application can set this parameter to 0L.
         *
         * <p>
         * If the function fails a DdeException is raised with the following
         * errorCodes:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_DLL_USAGE</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         *
         */
        public void postAdvise(Ddeml.HSZ hszTopic, Ddeml.HSZ hszItem);
        
        /**
         * Causes the system to send an XTYP_ADVREQ transaction to the calling
         * (server) application's Dynamic Data Exchange (DDE) callback function
         * for each client with an active advise loop on the specified topic and
         * item. A server application should call this function whenever the
         * data associated with the topic name or item name pair changes.
         *
         * @param hszTopic A string that specifies the topic name. To send
         * notifications for all topics with active advise loops, an application
         * can set this parameter to NULL.
         *
         * @param hszItem A string that specifies the item name. To send
         * notifications for all items with active advise loops, an application
         * can set this parameter to NULL.
         *
         * <p>
         * If the function fails a DdeException is raised with the following
         * errorCodes:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_DLL_USAGE</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         *
         */
        public void postAdvise(String hszTopic, String hszItem);
        
        /**
         * Abandons all asynchronous transaction and releases all
         * resources associated with the transaction.
         *
         * <p>
         * If the method fails a DdeException will be raised with the 
         * corresponding errorCode:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * <li>DMLERR_UNFOUND_QUEUE_ID</li>
         * </ul>
         */
        public void abandonTransactions();
        
        /**
         * Establishes a conversation with all server applications that support
         * the specified service name and topic name pair. An application can
         * also use this function to obtain a list of conversation handles by
         * passing the function an existing conversation handle. The Dynamic
         * Data Exchange Management Library removes the handles of any
         * terminated conversations from the conversation list. The resulting
         * conversation list contains the handles of all currently established
         * conversations that support the specified service name and topic name.
         * 
         * @param service A handle to the string that specifies the service
         * name of the server application with which a conversation is to be
         * established. If this parameter is 0L, the system attempts to
         * establish conversations with all available servers that support the
         * specified topic name.
         *
         * @param topic A handle to the string that specifies the name of the
         * topic on which a conversation is to be established. This handle must
         * have been created by a previous call to the DdeCreateStringHandle
         * function. If this parameter is 0L, the system will attempt to
         * establish conversations on all topics supported by the selected
         * server (or servers).
         * 
         * @param existingList An existinct conversation list to be enumerated.
         * This parameter should be NULL if a new conversation list is to be
         * established.
         *
         * @param ctx A pointer to the CONVCONTEXT structure that contains
         * conversation-context information. If this parameter is NULL, the
         * server receives the default CONVCONTEXT structure during the
         * XTYP_CONNECT or XTYP_WILDCONNECT transaction.
         *
         * @return The new ConnectionList
         *
         * <p>
         * If the function fails a DdeException is raised with the
         * appropriate errorCode:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_CONV_ESTABLISHED</li>
         * <li>DMLERR_NO_ERROR</li>
         * <li>DMLERR_SYS_ERROR</li>
         * </ul>
         */
        public IDdeConnectionList connectList(Ddeml.HSZ service, Ddeml.HSZ topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx);
        
        /**
         * Establishes a conversation with all server applications that support
         * the specified service name and topic name pair. An application can
         * also use this function to obtain a list of conversation handles by
         * passing the function an existing conversation handle. The Dynamic
         * Data Exchange Management Library removes the handles of any
         * terminated conversations from the conversation list. The resulting
         * conversation list contains the handles of all currently established
         * conversations that support the specified service name and topic name.
         * 
         * @param service A string that specifies the service name of the server
         * application with which a conversation is to be established. If this
         * parameter is NULL, the system attempts to establish conversations with
         * all available servers that support the specified topic name.
         *
         * @param topic A string that specifies the name of the topic on which a
         * conversation is to be established. If this parameter is NULL, the
         * system will attempt to establish conversations on all topics
         * supported by the selected server (or servers).
         * 
         * @param existingList An existinct conversation list to be enumerated.
         * This parameter should be NULL if a new conversation list is to be
         * established.
         *
         * @param ctx A pointer to the CONVCONTEXT structure that contains
         * conversation-context information. If this parameter is NULL, the
         * server receives the default CONVCONTEXT structure during the
         * XTYP_CONNECT or XTYP_WILDCONNECT transaction.
         *
         * @return The new ConnectionList
         *
         * <p>
         * If the function fails a DdeException is raised with the
         * appropriate errorCode:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_CONV_ESTABLISHED</li>
         * <li>DMLERR_NO_ERROR</li>
         * <li>DMLERR_SYS_ERROR</li>
         * </ul>
         */
        public IDdeConnectionList connectList(String service, String topic, IDdeConnectionList existingList, Ddeml.CONVCONTEXT ctx);
        
        /**
         * Enables or disables transactions for a specific conversation or for
         * all conversations currently established by the calling application.
         *
         * @param wCmd The function code. This parameter can be one of the
         * following values.
         * <table>
         * <tr><th>Value</th><th>Meaning</th></tr>
         * <tr><td>EC_ENABLEALL</td><td>Enables all transactions for the
         * specified conversation.</td></tr>
         * <tr><td>EC_ENABLEONE</td><td>Enables one transaction for the
         * specified conversation.</td></tr>
         * <tr><td>EC_DISABLE</td><td>Disables all blockable transactions for
         * the specified conversation.
         *
         * <p>
         * A server application can disable the following transactions:</p>
         * <ul>
         * <li>XTYP_ADVSTART</li>
         * <li>XTYP_ADVSTOP</li>
         * <li>XTYP_EXECUTE</li>
         * <li>XTYP_POKE</li>
         * <li>XTYP_REQUEST</li>
         * </ul>
         * <p>
         * A client application can disable the following transactions:</p>
         * <ul>
         * <li>XTYP_ADVDATA</li>
         * <li>XTYP_XACT_COMPLETE</li>
         * </ul>
         * </td></tr>
         * <tr><td>EC_QUERYWAITING</td><td>Determines whether any transactions
         * are in the queue for the specified conversation.</td></tr>
         * </table>
         *
         * @return If the function succeeds, the return value is nonzero.
         *
         * <p>
         * If the function fails, the return value is zero.</p>
         *
         * <p>
         * If the wCmd parameter is EC_QUERYWAITING, and the application
         * transaction queue contains one or more unprocessed transactions that
         * are not being processed, the return value is TRUE; otherwise, it is
         * FALSE.</p>
         *
         * <p>
         * If the function fails a DdeException is raised with the
         * appropriate errorCode:</p>
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         */
        public boolean enableCallback(int wCmd);
        
        /**
         * Frees all Dynamic Data Exchange Management Library (DDEML) resources
         * associated with the calling application. 
         * 
         * @return true if function succeeded
         */
        public boolean uninitialize();
        
        /**
         * Wrap a connection handle into a IDdeConnection helper class.
         * 
         * @param conv HCONV structure to wrap
         * @return wrapped IDdeConnection
         */
        public IDdeConnection wrap(HCONV conv);
        
        public void registerAdvstartHandler(AdvstartHandler handler);

        public void unregisterAdvstartHandler(AdvstartHandler handler);

        public void registerAdvstopHandler(AdvstopHandler handler);

        public void unregisterAdvstopHandler(AdvstopHandler handler);

        public void registerConnectHandler(ConnectHandler handler);

        public void unregisterConnectHandler(ConnectHandler handler);

        public void registerAdvReqHandler(AdvreqHandler handler);

        public void unregisterAdvReqHandler(AdvreqHandler handler);

        public void registerRequestHandler(RequestHandler handler);

        public void unregisterRequestHandler(RequestHandler handler);

        public void registerWildconnectHandler(WildconnectHandler handler);

        public void unregisterWildconnectHandler(WildconnectHandler handler);

        public void registerAdvdataHandler(AdvdataHandler handler);

        public void unregisterAdvdataHandler(AdvdataHandler handler);

        public void registerExecuteHandler(ExecuteHandler handler);

        public void unregisterExecuteHandler(ExecuteHandler handler);

        public void registerPokeHandler(PokeHandler handler);

        public void unregisterPokeHandler(PokeHandler handler);

        public void registerConnectConfirmHandler(ConnectConfirmHandler handler);

        public void unregisterConnectConfirmHandler(ConnectConfirmHandler handler);

        public void registerDisconnectHandler(DisconnectHandler handler);
        
        public void unregisterDisconnectHandler(DisconnectHandler handler);

        public void registerErrorHandler(ErrorHandler handler);
        
        public void unregisterErrorHandler(ErrorHandler handler);

        public void registerRegisterHandler(RegisterHandler handler);

        public void unregisterRegisterHandler(RegisterHandler handler);

        public void registerXactCompleteHandler(XactCompleteHandler handler);

        public void unregisterXactCompleteHandler(XactCompleteHandler handler);

        public void registerUnregisterHandler(UnregisterHandler handler);

        public void unregisterUnregisterHandler(UnregisterHandler handler);
        
        public void registerMonitorHandler(MonitorHandler handler);

        public void unregisterMonitorHandler(MonitorHandler handler);
    }
    
    /**
     * The IDdeConnectionList wraps a connectionlist.
     */
    public interface IDdeConnectionList extends Closeable {
        public Ddeml.HCONVLIST getHandle();

        /**
         * Retrieves the next conversation handle in the specified conversation
         * list.
         * 
         * @param prevConnection A handle to the conversation handle previously
         * returned by this function. If this parameter is NULL, the function
         * returns the first conversation handle in the list.
         *
         * @return If the list contains any more conversation handles, the
         * return value is the next conversation IDdeConnection in the list;
         * otherwise it is NULL.
         */
        public IDdeConnection queryNextServer(IDdeConnection prevConnection);
        
        /**
         * Destroys the specified conversation list and terminates all
         * conversations associated with the list.
         *
         * <p>
         * If the function fails a DdeException is raised with the
         * appropriate errorCode:</p>
         * 
         * <ul>
         * <li>DMLERR_DLL_NOT_INITIALIZED</li>
         * <li>DMLERR_INVALIDPARAMETER</li>
         * <li>DMLERR_NO_ERROR</li>
         * </ul>
         * 
         * <p>Note: This wraps DdeDisconnectList to align with Closeable wording.</p>
         */
        public void close();
    }    
}
