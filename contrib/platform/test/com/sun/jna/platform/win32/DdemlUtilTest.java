
package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.Ddeml.CONVINFO;
import com.sun.jna.platform.win32.Ddeml.HCONV;
import com.sun.jna.platform.win32.Ddeml.HDDEDATA;
import com.sun.jna.platform.win32.Ddeml.HSZ;
import com.sun.jna.platform.win32.DdemlUtil.DdeAdapter;
import com.sun.jna.platform.win32.DdemlUtil.IDdeConnection;
import com.sun.jna.platform.win32.DdemlUtil.StandaloneDdeClient;
import java.io.Closeable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.hamcrest.core.Is.is;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import com.sun.jna.platform.win32.DdemlUtil.AdvstartHandler;
import com.sun.jna.platform.win32.DdemlUtil.ConnectHandler;
import com.sun.jna.platform.win32.DdemlUtil.AdvreqHandler;
import com.sun.jna.platform.win32.DdemlUtil.RequestHandler;
import com.sun.jna.platform.win32.DdemlUtil.WildconnectHandler;
import com.sun.jna.platform.win32.DdemlUtil.AdvdataHandler;
import com.sun.jna.platform.win32.DdemlUtil.ConnectConfirmHandler;
import com.sun.jna.platform.win32.DdemlUtil.DisconnectHandler;
import com.sun.jna.platform.win32.DdemlUtil.RegisterHandler;
import com.sun.jna.platform.win32.DdemlUtil.XactCompleteHandler;
import com.sun.jna.platform.win32.DdemlUtil.ExecuteHandler;
import com.sun.jna.platform.win32.DdemlUtil.PokeHandler;


public class DdemlUtilTest {
    @BeforeClass
    public static void init() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.getHandlers()[0].setLevel(Level.ALL);
        Logger.getLogger(DdeAdapter.class.getName()).setLevel(Level.FINE);
    }
    
    @Test
    public void testNameService() throws InterruptedException {
        final String serviceName = "TestService";
        final CountDownLatch latch = new CountDownLatch(1);
        StandaloneDdeClient client = null;
        StandaloneDdeClient server = null;
        
        try {
            client = new StandaloneDdeClient() {
                private final RegisterHandler registerHandler = new RegisterHandler() {
                    public void onRegister(int transactionType, HSZ baseServiceName, HSZ instanceSpecificServiceName) {
                        if (serviceName.equals(queryString(baseServiceName))) {
                            latch.countDown();
                        }
                    }
                };
                
                {
                    registerRegisterHandler(registerHandler);
                    this.initialize(Ddeml.APPCMD_CLIENTONLY);
                }
            };
            
            server = new StandaloneDdeClient() {
                {
                    this.initialize(Ddeml.APPCMD_FILTERINITS
                            | Ddeml.CBF_SKIP_ALLNOTIFICATIONS
                            | Ddeml.CBF_FAIL_ALLSVRXACTIONS);
                }
            };
            
            server.nameService(serviceName, Ddeml.DNS_REGISTER);
    
            Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
        } finally {
            closeQuitely(server);
            closeQuitely(client);
        }
    }
    
    @Test
    public void testConnectDisconnect() throws InterruptedException {
        final String serviceName = "TestService";
        final String topicName = "TestTopic";
        final CountDownLatch connectLatch = new CountDownLatch(1);
        final CountDownLatch disconnectLatch = new CountDownLatch(1);

        StandaloneDdeClient client = null;
        StandaloneDdeClient server = null;

        try {
            client = new StandaloneDdeClient() {
                {
                    this.initialize(Ddeml.APPCMD_CLIENTONLY
                            | Ddeml.CBF_SKIP_REGISTRATIONS
                            | Ddeml.CBF_SKIP_UNREGISTRATIONS);
                }
            };
            server = new StandaloneDdeClient() {
                private final ConnectHandler connectHandler = new ConnectHandler() {
                    public boolean onConnect(int transactionType, HSZ topic, HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
                        return topicName.equals(queryString(topic));
                    }
                };

                private final ConnectConfirmHandler connectConfirmHandler = new ConnectConfirmHandler() {
                    public void onConnectConfirm(int transactionType, HCONV hconv, HSZ topic, HSZ service, boolean sameInstance) {
                        if (topicName.equals(queryString(topic))) {
                            connectLatch.countDown();
                        }
                    }
                };
                        
                private final DisconnectHandler disconnectHandler = new DisconnectHandler() {
                    public void onDisconnect(int transactionType, HCONV hconv, boolean sameInstance) {
                        disconnectLatch.countDown();
                    }
                };

                {
                    registerConnectHandler(connectHandler);
                    registerConnectConfirmHandler(connectConfirmHandler);
                    registerDisconnectHandler(disconnectHandler);
                    this.initialize(Ddeml.APPCMD_FILTERINITS
                            | Ddeml.CBF_SKIP_REGISTRATIONS
                            | Ddeml.CBF_SKIP_UNREGISTRATIONS
                    );
                }
            };

            server.nameService(serviceName, Ddeml.DNS_REGISTER);


            IDdeConnection connection = client.connect(serviceName, topicName, null);
            assertTrue("Failed to connect", connectLatch.await(5, TimeUnit.SECONDS));
            connection.close();
            assertTrue("Failed to disconnect", disconnectLatch.await(5, TimeUnit.SECONDS));

        } finally {
            closeQuitely(client);
            closeQuitely(server);
        }
    }

    @Test
    public void testConnectListDisconnectListQueryNextServer() throws InterruptedException {
        final String serviceName = "TestService";
        final String topicName = "TestTopic";
        final CountDownLatch connectLatch1 = new CountDownLatch(1);
        final CountDownLatch disconnectLatch1 = new CountDownLatch(1);
        final CountDownLatch connectLatch2 = new CountDownLatch(1);
        final CountDownLatch disconnectLatch2 = new CountDownLatch(1);

        StandaloneDdeClient client = null;
        StandaloneDdeClient server1 = null;
        StandaloneDdeClient server2 = null;

        try {
            client = new StandaloneDdeClient() {
                {
                    this.initialize(Ddeml.APPCMD_CLIENTONLY
                            | Ddeml.CBF_SKIP_REGISTRATIONS
                            | Ddeml.CBF_SKIP_UNREGISTRATIONS);
                }
            };

            class Server extends StandaloneDdeClient {
                private final CountDownLatch connectLatch;
                private final CountDownLatch disconnectLatch;
                
                private final ConnectHandler connectHandler = new ConnectHandler() {
                    public boolean onConnect(int transactionType, HSZ topic, HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
                        return topicName.equals(queryString(topic));
                    }
                };
                
                private final ConnectConfirmHandler connectConfirmHandler = new  ConnectConfirmHandler() {
                    public void onConnectConfirm(int transactionType, Ddeml.HCONV hconv, HSZ topic, HSZ service, boolean sameInstance) {
                        if (topicName.equals(queryString(topic))) {
                            connectLatch.countDown();
                        }
                    }
                };

                private final DisconnectHandler disconnectHandler = new DisconnectHandler() {
                    public void onDisconnect(int transactionType, HCONV hconv, boolean sameInstance) {
                        disconnectLatch.countDown();
                    }
                };
                    
                private final WildconnectHandler wildconnectHandler = new WildconnectHandler() {
                    public List<Ddeml.HSZPAIR> onWildconnect(int transactionType, HSZ topic, HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
                        return Collections.singletonList(new Ddeml.HSZPAIR(service, topic));
                    }
                };
                
                public Server(CountDownLatch connectLatch, CountDownLatch disconnectLatch) {
                    registerConnectHandler(connectHandler);
                    registerConnectConfirmHandler(connectConfirmHandler);
                    registerDisconnectHandler(disconnectHandler);
                    registerWildconnectHandler(wildconnectHandler);
                    this.initialize(Ddeml.APPCMD_FILTERINITS
                            | Ddeml.CBF_SKIP_REGISTRATIONS
                            | Ddeml.CBF_SKIP_UNREGISTRATIONS
                    );
                    this.connectLatch = connectLatch;
                    this.disconnectLatch = disconnectLatch;
                }
            };

            server1 = new Server(connectLatch1, disconnectLatch1);
            server2 = new Server(connectLatch2, disconnectLatch2);

            server1.nameService(serviceName, Ddeml.DNS_REGISTER);
            server2.nameService(serviceName, Ddeml.DNS_REGISTER);

            DdemlUtil.IDdeConnectionList connectionList = client.connectList(serviceName, topicName, null, null);

            IDdeConnection con1 = connectionList.queryNextServer(null);
            assertNotNull(con1);
            IDdeConnection con2 = connectionList.queryNextServer(con1);
            assertNotNull(con2);
            IDdeConnection con3 = connectionList.queryNextServer(con2);
            assertNull(con3);
            
            connectionList.close();
            
            assertTrue(connectLatch1.await(5, TimeUnit.SECONDS));
            assertTrue(connectLatch2.await(5, TimeUnit.SECONDS));
            assertTrue(disconnectLatch1.await(5, TimeUnit.SECONDS));
            assertTrue(disconnectLatch2.await(5, TimeUnit.SECONDS));
        } finally {
            closeQuitely(client);
            closeQuitely(server1);
            closeQuitely(server2);
        }
    }
    
    @Test
    public void testExecute() throws InterruptedException {
        final String serviceName = "TestService";
        final String topicName = "TestTopic";
        final String testExecute = "Execute�������";
        final CountDownLatch executeReceived = new CountDownLatch(1);

        StandaloneDdeClient client = null;
        StandaloneDdeClient server = null;

        try {
            client = new StandaloneDdeClient() {
                {
                    this.initialize(Ddeml.APPCMD_CLIENTONLY
                            | Ddeml.CBF_SKIP_REGISTRATIONS
                            | Ddeml.CBF_SKIP_UNREGISTRATIONS);
                }
            };

            server = new StandaloneDdeClient() {
                
                private final ConnectHandler connectHandler = new ConnectHandler() {
                    public boolean onConnect(int transactionType, HSZ topic, HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
                        return topicName.equals(queryString(topic));
                    }
                };

                private final ExecuteHandler executeHandler = new ExecuteHandler() {
                    public int onExecute(int transactionType, HCONV hconv, HSZ topic, Ddeml.HDDEDATA commandStringData) {
                        Pointer[] pointer = new Pointer[] { accessData(commandStringData, null) };
                        try {
                            String commandString = pointer[0].getWideString(0);
                            if(testExecute.equals(commandString) && queryString(topic).equals(topicName)) {
                                executeReceived.countDown();
                                return Ddeml.DDE_FACK;
                            }
                        } finally {
                            synchronized(pointer) {
                                unaccessData(commandStringData);
                            }
                        }
                        return Ddeml.DDE_FNOTPROCESSED;
                    }
                };
                
                {
                    registerConnectHandler(connectHandler);
                    registerExecuteHandler(executeHandler);
                    this.initialize(Ddeml.APPCMD_FILTERINITS
                            | Ddeml.CBF_SKIP_ALLNOTIFICATIONS
                    );
                }
            };

            server.nameService(serviceName, Ddeml.DNS_REGISTER);
            
            IDdeConnection con = client.connect(serviceName, topicName, null);
            con.execute(testExecute, 5 * 1000, null, null);
            
            assertTrue(executeReceived.await(5, TimeUnit.SECONDS));
        } finally {
            closeQuitely(client);
            closeQuitely(server);
        }
    }
    
    @Test
    public void testPoke() throws InterruptedException {
        final String serviceName = "TestService";
        final String topicName = "TestTopic";
        final String itemName = "TestItem";
        final String testValue = "Execute�������";
        final CountDownLatch pokeReceived = new CountDownLatch(1);

        StandaloneDdeClient client = null;
        StandaloneDdeClient server = null;

        try {
            client = new StandaloneDdeClient() {
                {
                    this.initialize(Ddeml.APPCMD_CLIENTONLY
                            | Ddeml.CBF_SKIP_REGISTRATIONS
                            | Ddeml.CBF_SKIP_UNREGISTRATIONS);
                }
            };

            server = new StandaloneDdeClient() {
                private final ConnectHandler connectHandler = new ConnectHandler() {
                     public boolean onConnect(int transactionType, HSZ topic, HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
                        return topicName.equals(queryString(topic));
                     }
                };
                 
                private final PokeHandler pokeHandler = new PokeHandler() {
                    @Override
                    public int onPoke(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item, Ddeml.HDDEDATA hdata) {
                        Pointer[] pointer = new Pointer[]{accessData(hdata, null)};
                        try {
                            String commandString = pointer[0].getWideString(0);
                            if (testValue.equals(commandString) && queryString(topic).equals(topicName) && queryString(item).equals(itemName)) {
                                pokeReceived.countDown();
                                return Ddeml.DDE_FACK;
                            }
                        } finally {
                            synchronized (pointer) {
                            }
                        }
                        return Ddeml.DDE_FNOTPROCESSED;
                    }
                };
                
                {
                    registerConnectHandler(connectHandler);
                    registerPokeHandler(pokeHandler);
                    this.initialize(Ddeml.APPCMD_FILTERINITS
                            | Ddeml.CBF_SKIP_ALLNOTIFICATIONS
                    );
                }
            };

            server.nameService(serviceName, Ddeml.DNS_REGISTER);
            
            IDdeConnection con = client.connect(serviceName, topicName, null);
            Memory mem = new Memory( (testValue.length() + 1 ) * 2);
            mem.setWideString(0, testValue);
            con.poke(mem, (int) mem.size(), itemName, WinUser.CF_UNICODETEXT, 5 * 1000, null, null);
            
            assertTrue(pokeReceived.await(5, TimeUnit.SECONDS));
        } finally {
            closeQuitely(client);
            closeQuitely(server);
        }
    }
    
    @Test
    public void testRequest() throws InterruptedException {
        final String serviceName = "TestService";
        final String topicName = "TestTopic";
        final String itemName = "TestItem";
        final String testValue = "Execute�������";
        final CountDownLatch pokeReceived = new CountDownLatch(1);

        StandaloneDdeClient client = null;
        StandaloneDdeClient server = null;

        try {
            client = new StandaloneDdeClient() {
                {
                    this.initialize(Ddeml.APPCMD_CLIENTONLY
                            | Ddeml.CBF_SKIP_REGISTRATIONS
                            | Ddeml.CBF_SKIP_UNREGISTRATIONS);
                }
            };

            server = new StandaloneDdeClient() {
                private final ConnectHandler connectHandler = new ConnectHandler() {
                    public boolean onConnect(int transactionType, HSZ topic, HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
                        return topicName.equals(queryString(topic));
                    }
                };
                
                private final RequestHandler requestHandler = new RequestHandler() {
                    public HDDEDATA onRequest(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item) {
                        if (dataFormat == WinUser.CF_UNICODETEXT && queryString(topic).equals(topicName) && queryString(item).equals(itemName)) {
                            Memory mem = new Memory((testValue.length() + 1) * 2);
                            mem.setWideString(0, testValue);
                            HDDEDATA result = createDataHandle(mem, (int) mem.size(), 0, item, dataFormat, 0);
                            pokeReceived.countDown();
                            return result;
                        } else {
                            return null;
                        }
                    }
                };

                {
                    registerConnectHandler(connectHandler);
                    registerRequestHandler(requestHandler);
                    this.initialize(Ddeml.APPCMD_FILTERINITS
                            | Ddeml.CBF_SKIP_ALLNOTIFICATIONS
                    );
                }
            };

            server.nameService(serviceName, Ddeml.DNS_REGISTER);
            
            IDdeConnection con = client.connect(serviceName, topicName, null);
            HDDEDATA data = con.request(itemName, WinUser.CF_UNICODETEXT, 5 * 1000, null, null);
            try {
                try {
                    Pointer pointer = server.accessData(data, null);
                    assertThat(pointer.getWideString(0), is(testValue));
                } finally {
                    server.unaccessData(data);
                }
            } finally {
                server.freeDataHandle(data);
            }
            
            assertTrue(pokeReceived.await(5, TimeUnit.SECONDS));
        } finally {
            closeQuitely(client);
            closeQuitely(server);
        }
    }

    @Test
    public void testAbandonTransaction() throws InterruptedException {
        final String serviceName = "TestService";
        final String topicName = "TestTopic";
        final String testExecute = "Execute�������";
        final CountDownLatch allTransactionsInvoked = new CountDownLatch(1);
        final CountDownLatch executesProcessed = new CountDownLatch(3);

        StandaloneDdeClient client = null;
        StandaloneDdeClient server = null;

        try {
            client = new StandaloneDdeClient() {
                private final XactCompleteHandler xactCompleteHandler = new XactCompleteHandler() {
                    public void onXactComplete(int transactionType, int dataFormat, HCONV hConv, HSZ topic, HSZ item, HDDEDATA hdata, ULONG_PTR transactionIdentifier, ULONG_PTR statusFlag) {
                        executesProcessed.countDown();
                    }
                };

                {
                    registerXactCompleteHandler(xactCompleteHandler);
                    this.initialize(Ddeml.APPCMD_CLIENTONLY
                            | Ddeml.CBF_SKIP_REGISTRATIONS
                            | Ddeml.CBF_SKIP_UNREGISTRATIONS);
                }
            };

            server = new StandaloneDdeClient() {
                private final ConnectHandler connectHandler = new ConnectHandler() {
                    public boolean onConnect(int transactionType, HSZ topic, HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
                        return topicName.equals(queryString(topic));
                    }
                };

                private final ExecuteHandler executeHandler = new ExecuteHandler() {
                    public int onExecute(int transactionType, HCONV hconv, HSZ topic, Ddeml.HDDEDATA commandStringData) {
                        try {
                            if(! allTransactionsInvoked.await(5, TimeUnit.SECONDS)) {
                                return Ddeml.DDE_FNOTPROCESSED;
                            }
                            Pointer[] pointer = new Pointer[] { accessData(commandStringData, null) };
                            try {
                                String commandString = pointer[0].getWideString(0);
                                if(testExecute.equals(commandString) && queryString(topic).equals(topicName)) {
                                    return Ddeml.DDE_FACK;
                                }
                            } finally {
                                synchronized(pointer) {
                                    unaccessData(commandStringData);
                                }
                            }
                            return Ddeml.DDE_FNOTPROCESSED;
                        }   catch (InterruptedException ex) {
                            Logger.getLogger(DdemlUtilTest.class.getName()).log(Level.SEVERE, null, ex);
                            return Ddeml.DDE_FNOTPROCESSED;
                        }
                    }
                };
                
                {
                    registerConnectHandler(connectHandler);
                    registerExecuteHandler(executeHandler);
                    this.initialize(Ddeml.APPCMD_FILTERINITS
                            | Ddeml.CBF_SKIP_ALLNOTIFICATIONS
                    );
                }
            };

            server.nameService(serviceName, Ddeml.DNS_REGISTER);
            
            IDdeConnection con = client.connect(serviceName, topicName, null);
            
            WinDef.DWORDByReference result = new WinDef.DWORDByReference();
            con.execute(testExecute, Ddeml.TIMEOUT_ASYNC, result, null);
            con.execute(testExecute, Ddeml.TIMEOUT_ASYNC, result, null);
            int transactionId2 = result.getValue().intValue();
            con.execute(testExecute, Ddeml.TIMEOUT_ASYNC, result, null);
            
            con.abandonTransaction(transactionId2);
            
            allTransactionsInvoked.countDown();
            
            assertFalse(executesProcessed.await(2, TimeUnit.SECONDS));
            assertThat(executesProcessed.getCount(), is(1L));
        } finally {
            closeQuitely(client);
            closeQuitely(server);
        }
    }
    
    @Test
    public void testEnableCallback() throws InterruptedException {
        final String serviceName = "TestService";
        final String topicName = "TestTopic";
        final String testExecute = "Execute�������";
        final CountDownLatch executesProcessed = new CountDownLatch(3);

        StandaloneDdeClient client = null;
        StandaloneDdeClient server = null;

        try {
            client = new StandaloneDdeClient() {
                private final XactCompleteHandler xactCompleteHandler = new XactCompleteHandler() {
                    public void onXactComplete(int transactionType, int dataFormat, HCONV hConv, HSZ topic, HSZ item, HDDEDATA hdata, ULONG_PTR transactionIdentifier, ULONG_PTR statusFlag) {
                        executesProcessed.countDown();
                    }
                    
                };

                {
                    registerXactCompleteHandler(xactCompleteHandler);
                    this.initialize(Ddeml.APPCMD_CLIENTONLY
                            | Ddeml.CBF_SKIP_REGISTRATIONS
                            | Ddeml.CBF_SKIP_UNREGISTRATIONS);
                }
            };

            server = new StandaloneDdeClient() {
                private final ConnectHandler connectHandler = new ConnectHandler() {
                    public boolean onConnect(int transactionType, HSZ topic, HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
                        return topicName.equals(queryString(topic));
                    }
                };


                private final ExecuteHandler executeHandler = new ExecuteHandler() {
                    public int onExecute(int transactionType, HCONV hconv, HSZ topic, Ddeml.HDDEDATA commandStringData) {
                        Pointer[] pointer = new Pointer[] { accessData(commandStringData, null) };
                        try {
                            String commandString = pointer[0].getWideString(0);
                            if(testExecute.equals(commandString) && queryString(topic).equals(topicName)) {
                                return Ddeml.DDE_FACK;
                            }
                        } finally {
                            synchronized(pointer) {
                                unaccessData(commandStringData);
                            }
                        }
                        return Ddeml.DDE_FNOTPROCESSED;
                    }
                };
                
                {
                    registerConnectHandler(connectHandler);
                    registerExecuteHandler(executeHandler);
                    this.initialize(Ddeml.APPCMD_FILTERINITS
                            | Ddeml.CBF_SKIP_ALLNOTIFICATIONS
                    );
                }
            };

            server.nameService(serviceName, Ddeml.DNS_REGISTER);
            server.enableCallback(Ddeml.EC_DISABLE);
            
            assertThat(server.enableCallback(Ddeml.EC_QUERYWAITING), is(false));
            
            IDdeConnection con = client.connect(serviceName, topicName, null);
            
            WinDef.DWORDByReference result = new WinDef.DWORDByReference();
            con.execute(testExecute, Ddeml.TIMEOUT_ASYNC, result, null);
            con.execute(testExecute, Ddeml.TIMEOUT_ASYNC, result, null);
            con.execute(testExecute, Ddeml.TIMEOUT_ASYNC, result, null);
            
            assertThat(server.enableCallback(Ddeml.EC_QUERYWAITING), is(true));
            
            server.enableCallback(Ddeml.EC_ENABLEALL);
            
            assertTrue(executesProcessed.await(3, TimeUnit.SECONDS));
        } finally {
            closeQuitely(client);
            closeQuitely(server);
        }
    }

    @Test
    public void testQueryConvInfoSetUserHandle() throws InterruptedException {
        final String serviceName = "TestService";
        final String topicName = "TestTopic";
        final String testExecute = "Execute�������";
        final CountDownLatch executesProcessed = new CountDownLatch(1);

        StandaloneDdeClient client = null;
        StandaloneDdeClient server = null;

        try {
            client = new StandaloneDdeClient() {
                private final XactCompleteHandler xactCompleteHandler = new XactCompleteHandler() {
                    public void onXactComplete(int transactionType, int dataFormat, HCONV hConv, HSZ topic, HSZ item, HDDEDATA hdata, ULONG_PTR transactionIdentifier, ULONG_PTR statusFlag) {
                        CONVINFO convInfo = wrap(hConv).queryConvInfo(transactionIdentifier.intValue());
                        if(convInfo.hUser.intValue() == 42) {
                            executesProcessed.countDown();
                        }
                    }
                    
                };

                {
                    registerXactCompleteHandler(xactCompleteHandler);
                    this.initialize(Ddeml.APPCMD_CLIENTONLY
                            | Ddeml.CBF_SKIP_REGISTRATIONS
                            | Ddeml.CBF_SKIP_UNREGISTRATIONS);
                }
            };

            server = new StandaloneDdeClient() {
                private final ConnectHandler connectHandler = new ConnectHandler() {
                    public boolean onConnect(int transactionType, HSZ topic, HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
                        return topicName.equals(queryString(topic));
                    }
                };

                private final ExecuteHandler executeHandler = new ExecuteHandler() {
                    public int onExecute(int transactionType, HCONV hconv, HSZ topic, Ddeml.HDDEDATA commandStringData) {
                        Pointer[] pointer = new Pointer[] { accessData(commandStringData, null) };
                        try {
                            String commandString = pointer[0].getWideString(0);
                            if(testExecute.equals(commandString) && queryString(topic).equals(topicName)) {
                                return Ddeml.DDE_FACK;
                            }
                        } finally {
                            synchronized(pointer) {
                                unaccessData(commandStringData);
                            }
                        }
                        return Ddeml.DDE_FNOTPROCESSED;
                    }
                };
                
                {
                    registerConnectHandler(connectHandler);
                    registerExecuteHandler(executeHandler);
                    this.initialize(Ddeml.APPCMD_FILTERINITS
                            | Ddeml.CBF_SKIP_ALLNOTIFICATIONS
                    );
                }
            };

            server.nameService(serviceName, Ddeml.DNS_REGISTER);

            IDdeConnection con = client.connect(serviceName, topicName, null);

            con.execute(testExecute, Ddeml.TIMEOUT_ASYNC, null, new BaseTSD.DWORD_PTR(42L));

            assertTrue(executesProcessed.await(3, TimeUnit.SECONDS));
        } finally {
            closeQuitely(client);
            closeQuitely(server);
        }
    }
    
    @Test
    public void testAdvise() throws InterruptedException {
        final String serviceName = "TestService";
        final String topicName = "TestTopic";
        final String itemName = "TestItem";
        final String testValue = "Execute�������";
        final CountDownLatch adviseStartReceived = new CountDownLatch(1);
        final CountDownLatch adviseDataRequestReceived = new CountDownLatch(1);
        final CountDownLatch adviseDataReceived = new CountDownLatch(1);

        StandaloneDdeClient client = null;
        StandaloneDdeClient server = null;

        try {
            client = new StandaloneDdeClient() {
                private final AdvdataHandler advdataHandler = new DdemlUtil.AdvdataHandler() {
                    public int onAdvdata(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item, HDDEDATA hdata) {
                        if (dataFormat == WinUser.CF_UNICODETEXT
                                && topicName.equals(queryString(topic))
                                && itemName.equals(queryString(item))) {
                            Pointer pointer = accessData(hdata, null);
                            try {
                                if (testValue.equals(pointer.getWideString(0))) {
                                    adviseDataReceived.countDown();
                                }
                            } finally {
                                unaccessData(hdata);
                            }
                        }
                        return Ddeml.DDE_FACK;
                    }
                };

                {
                    registerAdvdataHandler(advdataHandler);
                    this.initialize(Ddeml.APPCMD_CLIENTONLY
                            | Ddeml.CBF_SKIP_REGISTRATIONS
                            | Ddeml.CBF_SKIP_UNREGISTRATIONS);
                }
            };

            server = new StandaloneDdeClient() {
                private final ConnectHandler connectHandler = new ConnectHandler() {
                    public boolean onConnect(int transactionType, HSZ topic, HSZ service, Ddeml.CONVCONTEXT convcontext, boolean sameInstance) {
                        return topicName.equals(queryString(topic));
                    }
                };

                private final AdvreqHandler advreqHandler = new AdvreqHandler() {
                    public HDDEDATA onAdvreq(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item, int count) {
                        adviseDataRequestReceived.countDown();
                        Memory mem = new Memory( (testValue.length() + 1) * 2);
                        mem.setWideString(0, testValue);
                        return createDataHandle(mem, (int) mem.size(), 0, item, dataFormat, 0);
                    }
                };

                private final AdvstartHandler advstartHandler = new AdvstartHandler() {
                    public boolean onAdvstart(int transactionType, int dataFormat, HCONV hconv, HSZ topic, HSZ item) {
                        adviseStartReceived.countDown();
                        return dataFormat == WinUser.CF_UNICODETEXT
                                && topicName.equals(queryString(topic))
                                && itemName.equals(queryString(item));
                    }
                };
                
                {
                    registerConnectHandler(connectHandler);
                    registerAdvReqHandler(advreqHandler);
                    registerAdvstartHandler(advstartHandler);
                    this.initialize(Ddeml.APPCMD_FILTERINITS
                            | Ddeml.CBF_SKIP_ALLNOTIFICATIONS
                    );
                }
            };

            server.nameService(serviceName, Ddeml.DNS_REGISTER);
            
            IDdeConnection con = client.connect(serviceName, topicName, null);
            con.advstart(itemName, WinUser.CF_UNICODETEXT, 5 * 1000, null, null);
            
            assertTrue(adviseStartReceived.await(5, TimeUnit.SECONDS));
            
            server.postAdvise(topicName, itemName);
            
            assertTrue(adviseDataRequestReceived.await(5, TimeUnit.SECONDS));
            assertTrue(adviseDataReceived.await(5, TimeUnit.SECONDS));
        } finally {
            closeQuitely(client);
            closeQuitely(server);
        }
    }
    
    private static void closeQuitely(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ex) {
            }
        }
    }
}
