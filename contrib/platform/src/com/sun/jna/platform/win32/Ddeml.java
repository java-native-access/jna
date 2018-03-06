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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.DWORD_PTR;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.List;
import static com.sun.jna.Structure.createFieldsOrder;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINT_PTR;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.SECURITY_QUALITY_OF_SERVICE;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Ported from Ddeml.h. Microsoft Windows SDK 7.1.
 * 
 * <p>Bindings for the DDEML - Dynamic Data Exchange Management Library (DDEML)</p>
 * 
 * @see <a href="https://msdn.microsoft.com/de-de/library/windows/desktop/ms648713(v=vs.85).aspx">MSDN: About the DDEML</a>
 */
public interface Ddeml extends StdCallLibrary {

    Ddeml INSTANCE = Native.loadLibrary("user32", Ddeml.class, W32APIOptions.DEFAULT_OPTIONS);
    
    public class HCONVLIST extends PointerType {
    };

    public class HCONV extends PointerType {
    };

    public class HSZ extends PointerType {
    };

    public class HDDEDATA extends PVOID {
    };

    /**
     * The following structure is for use with {@link #XTYP_WILDCONNECT} processing.
     */
    public class HSZPAIR extends Structure {

        public static final List<String> FIELDS = createFieldsOrder("service", "topic");

        public HSZ service;
        public HSZ topic;

        public HSZPAIR() {
        }

        public HSZPAIR(HSZ service, HSZ topic) {
            this.service = service;
            this.topic = topic;
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The following structure is used by {@link #DdeConnect} and {@link #DdeConnectList} and
     * by {@link #XTYP_CONNECT} and {@link #XTYP_WILDCONNECT} callbacks.
     */
    public class CONVCONTEXT extends Structure {

        public static final List<String> FIELDS = createFieldsOrder(
                "cb", "wFlags", "wCountryID", "iCodePage", "dwLangID",
                "dwSecurity", "qos");
        /**
         * set to sizeof(CONVCONTEXT)
         */
        public int cb;
        /**
         * none currently defined.
         */
        public int wFlags;
        /**
         * country/region code for topic/item strings used.
         */
        public int wCountryID;
        /**
         * codepage used for topic/item strings.
         */
        public int iCodePage;
        /**
         * language ID for topic/item strings.
         */
        public int dwLangID;
        /**
         * Private security code.
         */
        public int dwSecurity;
        /**
         * The quality of service a DDE client wants from the system during a
         * given conversation. The quality of service level specified lasts for
         * the duration of the conversation. It cannot be changed once the
         * conversation is started.
         */
        public SECURITY_QUALITY_OF_SERVICE qos;

        public CONVCONTEXT() {
        }

        public CONVCONTEXT(Pointer p) {
            super(p);
        }

        @Override
        public void write() {
            this.cb = size();
            super.write();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Contains information about a Dynamic Data Exchange (DDE) conversation.
     *
     * @see #DdeQueryConvInfo(HCONV hConv, int idTransaction, CONVINFO pConvInfo)
     */
    public class CONVINFO extends Structure {

        public static final List<String> FIELDS = createFieldsOrder(
                "cb", "hUser", "hConvPartner", "hszSvcPartner", "hszServiceReq",
                "hszTopic", "hszItem", "wFmt", "wType", "wStatus", "wConvst",
                "wLastError", "hConvList", "ConvCtxt", "hwnd", "hwndPartner");
        /** The structure's size, in bytes. */
        public int cb;
        /** User specified field  */
        public DWORD_PTR hUser;
        /** hConv on other end or 0 if non-ddemgr partner  */
        public HCONV hConvPartner;
        /** App name of partner if obtainable  */
        public HSZ hszSvcPartner;
        /** AppName requested for connection  */
        public HSZ hszServiceReq;
        /** Topic name for conversation  */
        public HSZ hszTopic;
        /** Transaction item name or NULL if quiescent  */
        public HSZ hszItem;
        /** Transaction format or NULL if quiescent  */
        public int wFmt;
        /** XTYP_ for current transaction  */
        public int wType;
        /** ST_ constant for current conversation  */
        public int wStatus;
        /** XST_ constant for current transaction  */
        public int wConvst;
        /** Last transaction error.  */
        public int wLastError;
        /** Parent hConvList if this conversation is in a list */
        public HCONVLIST hConvList;
        /** Conversation context */
        public CONVCONTEXT ConvCtxt;
        /** Window handle for this conversation */
        public HWND hwnd;
        /** Partner window handle for this conversation */
        public HWND hwndPartner;

        @Override
        public void write() {
            this.cb = size();
            super.write();
        }
        
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    /**
     * Contains information about the current Dynamic Data Exchange (DDE)
     * transaction. A DDE debugging application can use this structure when
     * monitoring transactions that the system passes to the DDE callback
     * functions of other applications.
     */
    public class MONCBSTRUCT extends Structure {

        public static final List<String> FIELDS = createFieldsOrder(
                "cb", "dwTime", "hTask", "dwRet", "wType", "wFmt", "hConv",
                "hsz1", "hsz2", "hData", "dwData1", "dwData2", "cc", "cbData",
                "Data"
        );

        /**
         * The structure's size, in bytes.
         */
        public int cb;
        /**
         * The Windows time at which the transaction occurred. Windows time is
         * the number of milliseconds that have elapsed since the system was
         * booted.
         */
        public int dwTime;
        /**
         * A handle to the task (application instance) containing the DDE
         * callback function that received the transaction.
         */
        public HANDLE hTask;
        /**
         * The value returned by the DDE callback function that processed the
         * transaction.
         */
        public DWORD dwRet;
        /**
         * The transaction type.
         */
        public int wType;
        /**
         * The format of the data exchanged (if any) during the transaction.
         */
        public int wFmt;
        /**
         * A handle to the conversation in which the transaction took place.
         */
        public HCONV hConv;
        /**
         * A handle to a string.
         */
        public HSZ hsz1;
        /**
         * A handle to a string.
         */
        public HSZ hsz2;
        /**
         * A handle to the data exchanged (if any) during the transaction.
         */
        public HDDEDATA hData;
        /**
         * Additional data.
         */
        public ULONG_PTR dwData1;
        /**
         * Additional data.
         */
        public ULONG_PTR dwData2;
        /**
         * The language information used to share data in different languages.
         */
        public CONVCONTEXT cc;
        /**
         * The amount, in bytes, of data being passed with the transaction. This
         * value can be more than 32 bytes.
         */
        public int cbData;
        /**
         * Contains the first 32 bytes of data being passed with the transaction
         * (8 * sizeof(DWORD)).
         */
        public byte[] Data = new byte[32];
        
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    /**
     * <p>
     * Contains information about a Dynamic Data Exchange (DDE) conversation. A
     * DDE monitoring application can use this structure to obtain information
     * about a conversation that has been established or has terminated.</p>
     *
     * <p>
     * <strong>Remarks</strong></p>
     *
     * <p>
     * Because string handles are local to the process, the hszSvc and hszTopic
     * members are global atoms. Similarly, conversation handles are local to
     * the instance; therefore, the hConvClient and hConvServer members are
     * window handles.</p>
     *
     * <p>
     * The hConvClient and hConvServer members of the MONCONVSTRUCT structure do
     * not hold the same value as would be seen by the applications engaged in
     * the conversation. Instead, they hold a globally unique pair of values
     * that identify the conversation.</p>
     */
    public class MONCONVSTRUCT extends Structure {

        public static final List<String> FIELDS = createFieldsOrder(
                "cb", "fConnect", "dwTime", "hTask", "hszSvc", "hszTopic",
                "hConvClient", "hConvServer"
        );

        /**
         * The structure's size, in bytes.
         */
        public UINT cb;
        /**
         * Indicates whether the conversation is currently established. A value
         * of TRUE indicates the conversation is established; FALSE indicates it
         * is not.
         */
        public BOOL fConnect;
        /**
         * The Windows time at which the conversation was established or
         * terminated. Windows time is the number of milliseconds that have
         * elapsed since the system was booted.
         */
        public DWORD dwTime;
        /**
         * A handle to a task (application instance) that is a partner in the
         * conversation.
         */
        public HANDLE hTask;
        /**
         * A handle to the service name on which the conversation is
         * established.
         */
        public HSZ hszSvc;
        /**
         * A handle to the topic name on which the conversation is established.
         */
        public HSZ hszTopic;
        /**
         * A handle to the client conversation.
         */
        public HCONV hConvClient;
        /**
         * A handle to the server conversation.
         */
        public HCONV hConvServer;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    /**
     * Contains information about the current Dynamic Data Exchange (DDE) error.
     * A DDE monitoring application can use this structure to monitor errors
     * returned by DDE Management Library functions.
     */
    public class MONERRSTRUCT extends Structure {

        public static final List<String> FIELDS = createFieldsOrder(
                "cb", "wLastError", "dwTime", "hTask"
        );

        /**
         * The structure's size, in bytes.
         */
        public int cb;
        /**
         * The current error.
         */
        public int wLastError;
        /**
         * The Windows time at which the error occurred. Windows time is the
         * number of milliseconds that have elapsed since the system was booted.
         */
        public int dwTime;
        /**
         * A handle to the task (application instance) that called the DDE
         * function that caused the error.
         */
        public HANDLE hTask;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    /**
     * Contains information about a Dynamic Data Exchange (DDE) string handle. A
     * DDE monitoring application can use this structure when monitoring the
     * activity of the string manager component of the DDE Management Library.
     */
    public class MONHSZSTRUCT extends Structure {

        public static final List<String> FIELDS = createFieldsOrder(
                "cb", "fsAction", "dwTime", "hsz", "hTask", "str"
        );

        /**
         * The structure's size, in bytes.
         */
        public int cb;
        /**
         * The action being performed on the string identified by the hsz
         * member.
         * <table>
         * <tr>
         * <th>Value</th><th>Meaning</th>
         * </tr>
         * <tr>
         * <td>MH_CLEANUP (4)</td>
         * <td>An application is freeing its DDE resources, causing the system
         * to delete string handles the application had created. (The
         * application called the {@link #DdeUninitialize} function.)</td>
         * </tr>
         * <tr>
         * <td>MH_CREATE (1)</td>
         * <td>An application is creating a string handle. (The application
         * called the {@link #DdeCreateStringHandle} function.)</td>
         * </tr>
         * <tr>
         * <td>MH_DELETE (3)</td>
         * <td>An application is deleting a string handle. (The application
         * called the {@link #DdeFreeStringHandle} function.)</td>
         * </tr>
         * <tr>
         * <td>MH_KEEP (2)</td>
         * <td>An application is increasing the usage count of a string handle.
         * (The application called the {@link #DdeKeepStringHandle} function.)</td>
         * </tr>
         * </table>
         */
        public int fsAction;
        /**
         * The Windows time at which the action specified by the fsAction member
         * takes place. Windows time is the number of milliseconds that have
         * elapsed since the system was booted.
         */
        public int dwTime;
        /**
         * A handle to the string. Because string handles are local to the
         * process, this member is a global atom.
         */
        public HSZ hsz;
        /**
         * A handle to the task (application instance) performing the action on
         * the string handle.
         */
        public HANDLE hTask;
        /**
         * String identified by the hsz member.
         */
        public byte[] str = new byte[1];

        @Override
        public void write() {
            cb = this.calculateSize(true);
            super.write();
        }

        @Override
        public void read() {
            readField("cb");
            allocateMemory(cb);
            super.read();
        }
        
        public String getStr() {
            int offset = fieldOffset("str");
            if(W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
                return getPointer().getWideString(offset);
            } else {
                return getPointer().getString(offset);
            }
        }
        
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    /**
     * Contains information about a Dynamic Data Exchange (DDE) advise loop. A
     * DDE monitoring application can use this structure to obtain information
     * about an advise loop that has started or ended.
     *
     * <p>
     * <strong>Remarks</strong></p>
     * 
     * <p>
     * Because string handles are local to the process, the hszSvc, hszTopic,
     * and hszItem members are global atoms.</p>
     *
     * <p>
     * The hConvClient and hConvServer members of the MONLINKSTRUCT structure do
     * not hold the same value as would be seen by the applications engaged in
     * the conversation. Instead, they hold a globally unique pair of values
     * that identify the conversation.</p>
     */
    public class MONLINKSTRUCT extends Structure {

        public static final List<String> FIELDS = createFieldsOrder(
                "cb", "dwTime", "hTask", "fEstablished", "fNoData", "hszSvc",
                "hszTopic", "hszItem", "wFmt", "fServer", "hConvServer",
                "hConvClient"
        );

        /**
         * The structure's size, in bytes.
         */
        public int cb;
        /**
         * The Windows time at which the advise loop was started or ended.
         * Windows time is the number of milliseconds that have elapsed since
         * the system was booted.
         */
        public int dwTime;
        /**
         * A handle to a task (application instance) that is a partner in the
         * advise loop.
         */
        public HANDLE hTask;
        /**
         * Indicates whether an advise loop was successfully established. A
         * value of TRUE indicates an advise loop was established; FALSE
         * indicates it was not.
         */
        public BOOL fEstablished;
        /**
         * Indicates whether the {@link #XTYPF_NODATA} flag is set for the advise loop. A
         * value of TRUE indicates the flag is set; FALSE indicates it is not.
         */
        public BOOL fNoData;
        /**
         * A handle to the service name of the server in the advise loop.
         */
        public HSZ hszSvc;
        /**
         * A handle to the topic name on which the advise loop is established.
         */
        public HSZ hszTopic;
        /**
         * A handle to the item name that is the subject of the advise loop.
         */
        public HSZ hszItem;
        /**
         * The format of the data exchanged (if any) during the advise loop.
         */
        public int wFmt;
        /**
         * Indicates whether the link notification came from the server. A value
         * of TRUE indicates the notification came from the server; FALSE
         * indicates otherwise.
         */
        public BOOL fServer;
        /**
         * A handle to the server conversation.
         */
        public HCONV hConvServer;
        /**
         * A handle to the client conversation.
         */
        public HCONV hConvClient;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    public class MONMSGSTRUCT extends Structure {

        public static final List<String> FIELDS = createFieldsOrder(
                "cb", "hwndTo", "dwTime", "hTask", "wMsg", "wParam", "lParam",
                "dmhd"
        );

        /**
         * The structure's size, in bytes.
         */
        public int cb;
        /**
         * A handle to the window that receives the DDE message.
         */
        public HWND hwndTo;
        /**
         * The Windows time at which the message was sent or posted. Windows
         * time is the number of milliseconds that have elapsed since the system
         * was booted.
         */
        public int dwTime;
        /**
         * A handle to the task (application instance) containing the window
         * that receives the DDE message.
         */
        public HANDLE hTask;
        /**
         * The identifier of the DDE message.
         */
        public int wMsg;
        /**
         * The wParam parameter of the DDE message.
         */
        public WPARAM wParam;
        /**
         * The lParam parameter of the DDE message.
         */
        public LPARAM lParam;
        /**
         * Additional information about the DDE message.
         */
        public DDEML_MSG_HOOK_DATA dmhd;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    public class DDEML_MSG_HOOK_DATA extends Structure {

        public static final List<String> FIELDS = createFieldsOrder(
                "uiLo", "uiHi", "cbData", "Data"
        );

        /**
         * The unpacked low-order word of the lParam parameter associated with
         * the DDE message.
         */
        public UINT_PTR uiLo;
        /**
         * The unpacked high-order word of the lParam parameter associated with
         * the DDE message.
         */
        public UINT_PTR uiHi;
        /**
         * The amount of data being passed with the message, in bytes. This
         * value can be greater than 32.
         */
        public int cbData;
        /**
         * The first 32 bytes of data being passed with the message (8 *
         * sizeof(DWORD)).
         */
        public byte[] Data = new byte[32];

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
    
    /* conversation states (usState) */
    /* quiescent states */
    public int XST_NULL = 0;
    public int XST_INCOMPLETE = 1;
    public int XST_CONNECTED = 2;
    /* mid-initiation states */
    public int XST_INIT1 = 3;
    public int XST_INIT2 = 4;
    /* active conversation states */
    public int XST_REQSENT = 5;
    public int XST_DATARCVD = 6;
    public int XST_POKESENT = 7;
    public int XST_POKEACKRCVD = 8;
    public int XST_EXECSENT = 9;
    public int XST_EXECACKRCVD = 10;
    public int XST_ADVSENT = 11;
    public int XST_UNADVSENT = 12;
    public int XST_ADVACKRCVD = 13;
    public int XST_UNADVACKRCVD = 14;
    public int XST_ADVDATASENT = 15;
    public int XST_ADVDATAACKRCVD = 16;

    /* used in LOWORD(dwData1) of XTYP_ADVREQ callbacks... */
    public int CADV_LATEACK = 0xFFFF;

    /* conversation status bits (fsStatus) */
    public int ST_CONNECTED = 0x0001;
    public int ST_ADVISE = 0x0002;
    public int ST_ISLOCAL = 0x0004;
    public int ST_BLOCKED = 0x0008;
    public int ST_CLIENT = 0x0010;
    public int ST_TERMINATED = 0x0020;
    public int ST_INLIST = 0x0040;
    public int ST_BLOCKNEXT = 0x0080;
    public int ST_ISSELF = 0x0100;

    /* DDE constants for wStatus field */
    public int DDE_FACK = 0x8000;
    public int DDE_FBUSY = 0x4000;
    public int DDE_FDEFERUPD = 0x4000;
    public int DDE_FACKREQ = 0x8000;
    public int DDE_FRELEASE = 0x2000;
    public int DDE_FREQUESTED = 0x1000;
    public int DDE_FAPPSTATUS = 0x00ff;
    public int DDE_FNOTPROCESSED = 0x0000;

    public int DDE_FACKRESERVED = ~(DDE_FACK | DDE_FBUSY | DDE_FAPPSTATUS);
    public int DDE_FADVRESERVED = ~(DDE_FACKREQ | DDE_FDEFERUPD);
    public int DDE_FDATRESERVED = ~(DDE_FACKREQ | DDE_FRELEASE | DDE_FREQUESTED);
    public int DDE_FPOKRESERVED = ~(DDE_FRELEASE);

    /* message filter hook types */
    public int MSGF_DDEMGR = 0x8001;

    /* codepage constants */
    /** default codepage for windows &amp; old DDE convs. */
    public int CP_WINANSI = 1004;
    /** default codepage for usage from java */
    public int CP_WINUNICODE = 1200;
    public int CP_WINNEUTRAL = CP_WINUNICODE;

    /* transaction types */
    /* CBR_BLOCK will not work */
    public int XTYPF_NOBLOCK = 0x0002;
    /* DDE_FDEFERUPD */
    public int XTYPF_NODATA = 0x0004;
    /* DDE_FACKREQ */
    public int XTYPF_ACKREQ = 0x0008;

    public int XCLASS_MASK = 0xFC00;
    public int XCLASS_BOOL = 0x1000;
    public int XCLASS_DATA = 0x2000;
    public int XCLASS_FLAGS = 0x4000;
    public int XCLASS_NOTIFICATION = 0x8000;

    /**
     * A Dynamic Data Exchange (DDE) callback function, DdeCallback, receives
     * the XTYP_ERROR transaction when a critical error occurs.
     *
     * <p>
     * <strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>hconv</dt><dd>A handle to the conversation associated with the error.
     * This parameter is NULL if the error is not associated with a
     * conversation. </dd>
     * <dt>dwData1</dt><dd>The error code in the low-order word. Currently, only
     * the following error code is supported.
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>DMLERR_LOW_MEMORY</td><td>Memory is low; advise, poke, or execute
     * data may be lost, or the system may fail.</td></tr>
     * </table>
     * </dd>
     * </dl>
     *
     * <p>
     * <strong>Remarks</strong></p>
     *
     * <p>
     * An application cannot block this transaction type; the CBR_BLOCK return
     * code is ignored. The Dynamic Data Exchange Management Library (DDEML)
     * attempts to free memory by removing noncritical resources. An application
     * that has blocked conversations should unblock them.
     * </p>
     */
    public int XTYP_ERROR = 0x0000 | XCLASS_NOTIFICATION | XTYPF_NOBLOCK;
    /**
     * Informs the client that the value of the data item has changed. The
     * Dynamic Data Exchange (DDE) client callback function, DdeCallback,
     * receives this transaction after establishing an advise loop with a
     * server.
     *
     * <p><strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>uFmt</dt><dd>The format atom of the data sent from the server.</dd>
     * <dt>hconv</dt><dd>A handle to the conversation.</dd>
     * <dt>hsz1</dt><dd>A handle to the topic name.</dd>
     * <dt>hsz2</dt><dd>A handle to the item name.</dd>
     * <dt>hdata</dt><dd>A handle to the data associated with the topic name and
     * item name pair. This parameter is NULL if the client specified the
     * {@link #XTYPF_NODATA} flag when it requested the advise loop.</dd>
     * </dl>
     * 
     * <p><strong>Return value</strong></p>
     *
     * <p>
     * A DDE callback function should return {@link #DDE_FACK} if it processes this
     * transaction, {@link #DDE_FBUSY} if it is too busy to process this transaction, or
     * {@link #DDE_FNOTPROCESSED} if it rejects this transaction.</p>
     * <p>
     * <strong>Remarks</strong></p>
     *
     * <p>An application must not free the data handle obtained during this
     * transaction. An application must, however, copy the data associated with
     * the data handle if the application must process the data after the
     * callback function returns. An application can use the {@link #DdeGetData} function
     * to copy the data.</p>
     */
    public int XTYP_ADVDATA = 0x0010 | XCLASS_FLAGS;
    /**
     * The XTYP_ADVREQ transaction informs the server that an advise transaction
     * is outstanding on the specified topic name and item name pair and that
     * data corresponding to the topic name and item name pair has changed. The
     * system sends this transaction to the Dynamic Data Exchange (DDE) callback
     * function, DdeCallback, after the server calls the {@link #DdePostAdvise} function.
     * 
     * <p><strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>uFmt</dt><dd>The format in which the data should be submitted to the client.</dd>
     * <dt>hconv</dt><dd>A handle to the conversation.</dd>
     * <dt>hsz1</dt><dd>A handle to the topic name.</dd>
     * <dt>hsz2</dt><dd>A handle to the item name that has changed.</dd>
     * <dt>dwData1</dt><dd>The count, in the low-order word, of XTYP_ADVREQ
     * transactions that remain to be processed on the same topic, item, and
     * format name set within the context of the current call to the
     * {@link #DdePostAdvise} function. The count is zero if the current XTYP_ADVREQ
     * transaction is the last one. A server can use this count to determine
     * whether to create an HDATA_APPOWNED data handle to the advise data.
     * <br><br>
     * The low-order word is set to {@link #CADV_LATEACK} if the DDEML issued the
     * XTYP_ADVREQ transaction because of a late-arriving DDE_ACK message from a
     * client being outrun by the server.
     * <br><br>
     * The high-order word is not used.</dd>
     * </dl>
     * 
     * <p><strong>Return value</strong></p>
     *<p>
     * The server should first call the {@link #DdeCreateDataHandle} function to create a
     * data handle that identifies the changed data and then return the handle.
     * The server should return NULL if it is unable to complete the
     * transaction.</p>
     * <p>
     * <strong>Remarks</strong></p>
     *
     * <p>
     * A server cannot block this transaction type; the CBR_BLOCK return code is
     * ignored.</p>
     */
    public int XTYP_ADVREQ = 0x0020 | XCLASS_DATA | XTYPF_NOBLOCK;
    /**
     * A client uses the XTYP_ADVSTART transaction to establish an advise loop
     * with a server. A Dynamic Data Exchange (DDE) server callback function,
     * DdeCallback, receives this transaction when a client specifies
     * XTYP_ADVSTART as the wType parameter of the {@link #DdeClientTransaction}
     * function.
     *
     * <p><strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>uFmt</dt><dd>The data format requested by the client.</dd>
     * <dt>hconv</dt><dd>A handle to the conversation.</dd>
     * <dt>hsz1</dt><dd>A handle to the topic name.</dd>
     * <dt>hsz2</dt><dd>A handle to the item name.</dd>
     * </dl>
     * 
     * <p><strong>Return value</strong></p>
     * <p>
     * A server callback function should return TRUE to allow an advise loop on
     * the specified topic name and item name pair, or FALSE to deny the advise
     * loop. If the callback function returns TRUE, any subsequent calls to the
     * {@link #DdePostAdvise} function by the server on the same topic name and item name
     * pair causes the system to send {@link #XTYP_ADVREQ} transactions to the server.
     * </p>
     * <p><strong>Remarks</strong></p>
     * <p>
     * If a client requests an advise loop on a topic name, item name, and data
     * format for an advise loop that is already established, the Dynamic Data
     * Exchange Management Library (DDEML) does not create a duplicate advise
     * loop but instead alters the advise loop flags ({@link #XTYPF_ACKREQ} and
     * {@link #XTYPF_NODATA}) to match the latest request.</p>
     * <p>This transaction is filtered if the server application specified the
     * {@link #CBF_FAIL_ADVISES} flag in the {@link #DdeInitialize} function. </p>
     */
    public int XTYP_ADVSTART = 0x0030 | XCLASS_BOOL;
    /**
     * A client uses the XTYP_ADVSTOP transaction to end an advise loop with a
     * server. A Dynamic Data Exchange (DDE) server callback function,
     * DdeCallback, receives this transaction when a client specifies
     * XTYP_ADVSTOP in the {@link #DdeClientTransaction} function.
     *
     * <p><strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>uFmt</dt><dd>The data format requested by the client.</dd>
     * <dt>hconv</dt><dd>A handle to the conversation.</dd>
     * <dt>hsz1</dt><dd>A handle to the topic name.</dd>
     * <dt>hsz2</dt><dd>A handle to the item name.</dd>
     * </dl>
     * <p><strong>Remarks</strong></p>
     * <p>
     * This transaction is filtered if the server application specified the
     * {@link #CBF_FAIL_ADVISES} flag in the {@link #DdeInitialize} function.</p>
     */
    public int XTYP_ADVSTOP = 0x0040 | XCLASS_NOTIFICATION;
    /**
     * A client uses the XTYP_EXECUTE transaction to send a command string to
     * the server. A Dynamic Data Exchange (DDE) server callback function,
     * DdeCallback, receives this transaction when a client specifies
     * XTYP_EXECUTE in the {@link #DdeClientTransaction} function.
     *
     * <p><strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>hconv</dt><dd>A handle to the conversation.</dd>
     * <dt>hsz1</dt><dd>A handle to the topic name.</dd>
     * <dt>hdata</dt><dd>A handle to the command string.</dd>
     * </dl>
     * 
     * <p><strong>Return value</strong></p>
     *
     * <p>
     * A server callback function should return {@link #DDE_FACK} if it processes this
     * transaction, {@link #DDE_FBUSY} if it is too busy to process this transaction, or
     * {@link #DDE_FNOTPROCESSED} if it rejects this transaction.</p>
     * 
     * <p><strong>Remarks</strong></p>
     *
     * <p>
     * This transaction is filtered if the server application specified the
     * {@link #CBF_FAIL_EXECUTES} flag in the {@link #DdeInitialize} function.</p>
     * 
     * <p>
     * An application must free the data handle obtained during this
     * transaction. An application must, however, copy the command string
     * associated with the data handle if the application must process the
     * string after the callback function returns. An application can use the
     * {@link #DdeGetData} function to copy the data.</p>
     * 
     * <p>
     * Because most client applications expect a server application to perform
     * an XTYP_EXECUTE transaction synchronously, a server should attempt to
     * perform all processing of the XTYP_EXECUTE transaction either from within
     * the DDE callback function or by returning the CBR_BLOCK return code. If
     * the hdata parameter is a command that instructs the server to terminate,
     * the server should do so after processing the XTYP_EXECUTE
     * transaction.</p>
     */
    public int XTYP_EXECUTE = 0x0050 | XCLASS_FLAGS;
    /**
     * A client uses the XTYP_CONNECT transaction to establish a conversation. A
     * Dynamic Data Exchange (DDE) server callback function, DdeCallback,
     * receives this transaction when a client specifies a service name that the
     * server supports (and a topic name that is not NULL) in a call to the
     * {@link #DdeConnect} function.
     * 
     * <p><strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>hsz1</dt><dd>A handle to the topic name.</dd>
     * <dt>hsz2</dt><dd>A handle to the service name.</dd>
     * <dt>dwData1</dt><dd>A pointer to a CONVCONTEXT structure that contains
     * context information for the conversation. If the client is not a DDEML
     * application, this parameter is 0.</dd>
     * <dt>dwData2</dt><dd>Specifies whether the client is the same application
     * instance as the server. If the parameter is 1, the client is the same
     * instance. If the parameter is 0, the client is a different instance.
     * </dd>
     * </dl>
     * 
     * <p><strong>Return value</strong></p>
     *
     * <p>
     * A server callback function should return TRUE to allow the client to
     * establish a conversation on the specified service name and topic name
     * pair, or the function should return FALSE to deny the conversation. If
     * the callback function returns TRUE and a conversation is successfully
     * established, the system passes the conversation handle to the server by
     * issuing an {@link #XTYP_CONNECT_CONFIRM} transaction to the server's callback
     * function (unless the server specified the {@link #CBF_SKIP_CONNECT_CONFIRMS} flag
     * in the {@link #DdeInitialize function}).</p>
     * 
     * <p><strong>Remarks</strong></p>
     *
     * <p>
     * This transaction is filtered if the server application specified the
     * {@link #CBF_FAIL_CONNECTIONS} flag in the {@link #DdeInitialize} function.</p>
     * <p>
     * A server cannot block this transaction type; the CBR_BLOCK return code is
     * ignored. </p>
     */
    public int XTYP_CONNECT = 0x0060 | XCLASS_BOOL | XTYPF_NOBLOCK;
    /**
     * A Dynamic Data Exchange (DDE) server callback function, DdeCallback,
     * receives the XTYP_CONNECT_CONFIRM transaction to confirm that a
     * conversation has been established with a client and to provide the server
     * with the conversation handle. The system sends this transaction as a
     * result of a previous {@link #XTYP_CONNECT} or {@link #XTYP_WILDCONNECT} transaction.
     *
     * <p><strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>hconv</dt><dd>A handle to the new conversation.</dd>
     * <dt>hsz1</dt><dd>A handle to the topic name on which the conversation has been established.</dd>
     * <dt>hsz2</dt><dd>A handle to the service name on which the conversation has been established.</dd>
     * <dt>dwData2</dt><dd>Specifies whether the client is the same application
     * instance as the server. If the parameter is 1, the client is the same
     * instance. If the parameter is 0, the client is a different instance.
     * </dd>
     * </dl>
     * 
     * <p><strong>Remarks</strong></p>
     *
     * <p>
     * This transaction is filtered if the server application specified the {@link #CBF_SKIP_CONNECT_CONFIRMS} flag in the {@link #DdeInitialize} function.
     * </p>
     * <p>
     * A server cannot block this transaction type; the CBR_BLOCK return code is ignored.
     * </p>
     */
    public int XTYP_CONNECT_CONFIRM = 0x0070 | XCLASS_NOTIFICATION | XTYPF_NOBLOCK;
    /**
     * A Dynamic Data Exchange (DDE) client callback function, DdeCallback,
     * receives the XTYP_XACT_COMPLETE transaction when an asynchronous
     * transaction, initiated by a call to the {@link #DdeClientTransaction} function,
     * has completed.
     * 
     * <p><strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>uFmt</dt><dd>The format of the data associated with the completed transaction (if applicable) or NULL if no data was exchanged during the transaction.</dd>
     * <dt>hConv</dt><dd>A handle to the conversation.</dd>
     * <dt>hsz1</dt><dd>A handle to the topic name involved in the completed transaction.</dd>
     * <dt>hsz2</dt><dd>A handle to the item name involved in the completed transaction.</dd>
     * <dt>hdata</dt><dd>A handle to the data involved in the completed transaction, if applicable. If the transaction was successful but involved no data, this parameter is TRUE. This parameter is NULL if the transaction was unsuccessful.</dd>
     * <dt>dwData1</dt><dd>The transaction identifier of the completed transaction.</dd>
     * <dt>dwData2</dt><dd>Any applicable DDE_ status flags in the low word. This parameter provides support for applications dependent on DDE_APPSTATUS bits. It is recommended that applications no longer use these bits — they may not be supported in future versions of the DDEML.</dd>
     * </dl>
     * 
     * <p><strong>Remarks</strong></p>
     *
     * <p>
     * An application must not free the data handle obtained during this
     * transaction. An application must, however, copy the data associated with
     * the data handle if the application must process the data after the
     * callback function returns. An application can use the {@link #DdeGetData} function
     * to copy the data.
     * </p>
     */
    public int XTYP_XACT_COMPLETE = 0x0080 | XCLASS_NOTIFICATION;
    /**
     * A client uses the XTYP_POKE transaction to send unsolicited data to the
     * server. A Dynamic Data Exchange (DDE) server callback function,
     * DdeCallback, receives this transaction when a client specifies XTYP_POKE
     * in the {@link #DdeClientTransaction} function.
     * 
     * <p><strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>uFmt</dt><dd>The format of the data sent from the server.</dd>
     * <dt>hConv</dt><dd>A handle to the conversation.</dd>
     * <dt>hsz1</dt><dd>A handle to the topic name.</dd>
     * <dt>hsz2</dt><dd>A handle to the service name.</dd>
     * <dt>hdata</dt><dd>A handle to the data that the client is sending to the server.</dd>
     * </dl>
     * 
     * <p><strong>Return value</strong></p>
     *
     * <p>
     * A server callback function should return the {@link #DDE_FACK} flag if it
     * processes this transaction, the {@link #DDE_FBUSY} flag if it is too busy to
     * process this transaction, or the {@link #DDE_FNOTPROCESSED} flag if it rejects
     * this transaction. </p>
     * 
     * <p><strong>Remarks</strong></p>
     *
     * <p>
     * This transaction is filtered if the server application specified the {@link #CBF_FAIL_POKES} flag in the {@link #DdeInitialize} function.
     * </p>
     */
    public int XTYP_POKE = 0x0090 | XCLASS_FLAGS;
    /**
     * A Dynamic Data Exchange (DDE) callback function, DdeCallback, receives
     * the XTYP_REGISTER transaction type whenever a Dynamic Data Exchange
     * Management Library (DDEML) server application uses the {@link #DdeNameService}
     * function to register a service name, or whenever a non-DDEML application
     * that supports the System topic is started.
     * 
     * <p>Used Parameters</p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>hsz1</dt><dd>A handle to the base service name being registered.
     * </dd>
     * <dt>hsz2</dt><dd>A handle to the instance-specific service name being
     * registered.</dd>
     * </dl>
     *
     * <strong>Remarks</strong>
     * <p>
     * This transaction is filtered if the application specified the
     * {@link #CBF_SKIP_REGISTRATIONS} flag in the {@link #DdeInitialize} function.</p>
     *
     * <p>
     * A application cannot block this transaction type; the CBR_BLOCK return
     * code is ignored.</p>
     *
     * <p>
     * An application should use the hsz1 parameter to add the service name to
     * the list of servers available to the user. An application should use the
     * hsz2 parameter to identify which application instance has started.</p>
     */
    public int XTYP_REGISTER = 0x00A0 | XCLASS_NOTIFICATION | XTYPF_NOBLOCK;
    /**
     * A client uses the XTYP_REQUEST transaction to request data from a server.
     * A Dynamic Data Exchange (DDE) server callback function, DdeCallback,
     * receives this transaction when a client specifies XTYP_REQUEST in the
     * {@link #DdeClientTransaction} function.
     * 
     * <p><strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>uFmt</dt><dd>The format in which the server should submit data to the client.</dd>
     * <dt>hConv</dt><dd>A handle to the conversation.</dd>
     * <dt>hsz1</dt><dd>A handle to the topic name.</dd>
     * <dt>hsz2</dt><dd>A handle to the service name.</dd>
     * </dl>
     * 
     * <p><strong>Return value</strong></p>
     *
     * <p>
     * The server should call the {@link #DdeCreateDataHandle} function to create a data
     * handle that identifies the data and then return the handle. The server
     * should return NULL if it is unable to complete the transaction. If the
     * server returns NULL, the client will receive a {@link #DDE_FNOTPROCESSED}
     * flag.</p>
     * 
     * <p><strong>Remarks</strong></p>
     *
     * <p>
     * This transaction is filtered if the server application specified the
     * {@link #CBF_FAIL_REQUESTS} flag in the {@link #DdeInitialize} function.</p>
     *<p>
     * If responding to this transaction requires lengthy processing, the server
     * can return the CBR_BLOCK return code to suspend future transactions on
     * the current conversation and then process the transaction asynchronously.
     * When the server has finished and the data is ready to pass to the client,
     * the server can call the {@link #DdeEnableCallback} function to resume the
     * conversation.</p>
     */
    public int XTYP_REQUEST = 0x00B0 | XCLASS_DATA;
    /**
     * An application's Dynamic Data Exchange (DDE) callback function,
     * DdeCallback, receives the XTYP_DISCONNECT transaction when the
     * application's partner in a conversation uses the {@link #DdeDisconnect} function
     * to terminate the conversation.
     *
     * <p><strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>hconv</dt><dd>A handle to that the conversation was terminated. </dd>
     * <dt>dwData2</dt><dd>Specifies whether the client is the same application
     * instance as the server. If the parameter is 1, the client is the same
     * instance. If the parameter is 0, the client is a different instance.
     * </dd>
     * </dl>

     * <p><strong>Remarks</strong></p>
     *
     * <p>
     * This transaction is filtered if the application specified the
     * {@link #CBF_SKIP_DISCONNECTS} flag in the {@link #DdeInitialize} function.</p>
     *<p>
     * The application can obtain the status of the terminated conversation by
     * calling the {@link #DdeQueryConvInfo} function while processing this transaction.
     * The conversation handle becomes invalid after the callback function
     * returns.</p>
     *<p>
     * An application cannot block this transaction type; the CBR_BLOCK return
     * code is ignored. </p>
     */
    public int XTYP_DISCONNECT = 0x00C0 | XCLASS_NOTIFICATION | XTYPF_NOBLOCK;
    /**
     * A Dynamic Data Exchange (DDE) callback function, DdeCallback, receives
     * the XTYP_UNREGISTER transaction whenever a Dynamic Data Exchange
     * Management Library (DDEML) server application uses the {@link #DdeNameService}
     * function to unregister a service name, or whenever a non-DDEML
     * application that supports the System topic is terminated.
     *
     * <p>
     * <strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>hsz1</dt><dd>A handle to the base service name being unregistered.</dd>
     * <dt>hsz2</dt><dd>A handle to the instance-specific service name being unregistered.</dd>
     * </dl>
     *
     * <p>
     * <strong>Remarks</strong></p>
     *
     * <p>
     * This transaction is filtered if the application specified the
     * {@link #CBF_SKIP_REGISTRATIONS} flag in the {@link #DdeInitialize} function.</p>
     *<p>
     * A application cannot block this transaction type; the CBR_BLOCK return
     * code is ignored.</p>
     *<p>
     * An application should use the hsz1 parameter to remove the service name
     * from the list of servers available to the user. An application should use
     * the hsz2 parameter to identify which application instance has
     * terminated.</p>
     */
    public int XTYP_UNREGISTER = 0x00D0 | XCLASS_NOTIFICATION | XTYPF_NOBLOCK;
    /**
     * Enables a client to establish a conversation on each of the server's
     * service name and topic name pairs that match the specified service name
     * and topic name. A Dynamic Data Exchange (DDE) server callback function,
     * DdeCallback, receives this transaction when a client specifies a NULL
     * service name, a NULL topic name, or both in a call to the {@link #DdeConnect} or
     * {@link #DdeConnectList} function.
     *
     * <p>
     * <strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>hsz1</dt><dd>A handle to the topic name. If this parameter is NULL,
     * the client is requesting a conversation on all topic names that the
     * server supports.</dd>
     * <dt>hsz2</dt><dd>A handle to the service name. If this parameter is NULL,
     * the client is requesting a conversation on all service names that the
     * server supports.</dd>
     * <dt>dwData1</dt><dd>A pointer to a CONVCONTEXT structure that contains
     * context information for the conversation. If the client is not a DDEML
     * application, this parameter is set to 0.</dd>
     * <dt>dwData2</dt><dd>Specifies whether the client is the same application
     * instance as the server. If the parameter is 1, the client is same
     * instance. If the parameter is 0, the client is a different instance.</dd>
     * </dl>
     *
     * <p>
     * <strong>Return value</strong></p>
     *
     * <p>
     * The server should return a data handle that identifies an array of
     * HSZPAIR structures. The array should contain one structure for each
     * service-name and topic-name pair that matches the service-name and
     * topic-name pair requested by the client. The array must be terminated by
     * a NULL string handle. The system sends the {@link #XTYP_CONNECT_CONFIRM}
     * transaction to the server to confirm each conversation and to pass the
     * conversation handles to the server. The server will not receive these
     * confirmations if it specified the {@link #CBF_SKIP_CONNECT_CONFIRMS} flag in the
     * {@link #DdeInitialize} function.</p>
     * <p>
     * The server should return NULL to refuse the XTYP_WILDCONNECT transaction.
     * </p>
     *
     * <p>
     * <strong>Remarks</strong></p>
     *
     * <p>
     * This transaction is filtered if the server application specified the
     * {@link #CBF_FAIL_CONNECTIONS} flag in the {@link #DdeInitialize} function.</p>
     * <p>
     * A server cannot block this transaction type; the CBR_BLOCK return code is
     * ignored.</p>
     */
    public int XTYP_WILDCONNECT = 0x00E0 | XCLASS_DATA | XTYPF_NOBLOCK;
    /**
     * A Dynamic Data Exchange (DDE) debugger's DDE callback function,
     * DdeCallback, receives the XTYP_MONITOR transaction whenever a DDE event
     * occurs in the system. To receive this transaction, an application must
     * specify the {@link #APPCLASS_MONITOR} value when it calls the {@link #DdeInitialize}
     * function.
     *
     * <p>
     * <strong>Used Parameters</strong></p>
     * <dl>
     * <dt>uType</dt><dd>The transaction type.</dd>
     * <dt>hdata</dt><dd>A handle to a DDE object that contains information
     * about the DDE event. The application should use the {@link #DdeAccessData}
     * function to obtain a pointer to the object.</dd>
     * <dt>dwData2</dt><dd>The DDE event. This parameter can be one of the
     * following values.
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>{@link #MF_CALLBACKS}</td><td>The system sent a transaction to a DDE
     * callback function. The DDE object contains a MONCBSTRUCT structure that
     * provides information about the transaction.</td></tr>
     * <tr><td>{@link #MF_CONV}</td><td>A DDE conversation was established or terminated.
     * The DDE object contains a MONCONVSTRUCT structure that provides
     * information about the conversation.</td></tr>
     * <tr><td>{@link #MF_ERRORS}</td><td>A DDE error occurred. The DDE object contains a
     * MONERRSTRUCT structure that provides information about the
     * error.</td></tr>
     * <tr><td>{@link #MF_HSZ_INFO}</td><td>A DDE application created, freed, or
     * incremented the usage count of a string handle, or a string handle was
     * freed as a result of a call to the {@link #DdeUninitialize} function. The DDE
     * object contains a MONHSZSTRUCT structure that provides information about
     * the string handle.</td></tr>
     * <tr><td>{@link #MF_LINKS}</td><td>A DDE application started or stopped an advise
     * loop. The DDE object contains a MONLINKSTRUCT structure that provides
     * information about the advise loop.</td></tr>
     * <tr><td>{@link #MF_POSTMSGS}</td><td>The system or an application posted a DDE
     * message. The DDE object contains a MONMSGSTRUCT structure that provides
     * information about the message.</td></tr>
     * <tr><td>{@link #MF_SENDMSGS}</td><td>The system or an application sent a DDE
     * message. The DDE object contains a MONMSGSTRUCT structure that provides
     * information about the message.</td></tr>
     * </table>
     * </dd>
     * </dl>
     *
     * <p>
     * <strong>Return value</strong></p>
     *
     * <p>
     * If the callback function processes this transaction, it should return 0.
     * </p>
     */
    public int XTYP_MONITOR = 0x00F0 | XCLASS_NOTIFICATION | XTYPF_NOBLOCK;

    public int XTYP_MASK = 0x00F0;
    /* shift to turn XTYP_ into an index */
    public int XTYP_SHIFT = 4;

    /** Timeout constants for asynchronous requests */
    public int TIMEOUT_ASYNC = 0xFFFFFFFF;

    /** Pseudo Transaction ID constant for the synchronous transaction */
    public int QID_SYNC = 0xFFFFFFFF;

    /* public strings used in DDE */
    public String SZDDESYS_TOPIC = "System";
    public String SZDDESYS_ITEM_TOPICS = "Topics";
    public String SZDDESYS_ITEM_SYSITEMS = "SysItems";
    public String SZDDESYS_ITEM_RTNMSG = "ReturnMessage";
    public String SZDDESYS_ITEM_STATUS = "Status";
    public String SZDDESYS_ITEM_FORMATS = "Formats";
    public String SZDDESYS_ITEM_HELP = "Help";
    public String SZDDE_ITEM_ITEMLIST = "TopicItemList";

    public int DMLERR_NO_ERROR = 0; /* must be 0 */

    public int DMLERR_FIRST = 0x4000;

    /**
     * A request for a synchronous advise transaction has timed out.
     */
    public int DMLERR_ADVACKTIMEOUT = 0x4000;
    /**
     * The response to the transaction caused the {@link #DDE_FBUSY} flag to be set.
     */
    public int DMLERR_BUSY = 0x4001;
    /**
     * A request for a synchronous data transaction has timed out.
     */
    public int DMLERR_DATAACKTIMEOUT = 0x4002;
    /**
     * A DDEML function was called without first calling the {@link #DdeInitialize}
     * function, or an invalid instance identifier was passed to a DDEML
     * function.
     */
    public int DMLERR_DLL_NOT_INITIALIZED = 0x4003;
    /**
     * An application initialized as {@link #APPCLASS_MONITOR} has attempted to perform a
     * DDE transaction, or an application initialized as {@link #APPCMD_CLIENTONLY} has
     * attempted to perform server transactions.
     */
    public int DMLERR_DLL_USAGE = 0x4004;
    /**
     * A request for a synchronous execute transaction has timed out.
     */
    public int DMLERR_EXECACKTIMEOUT = 0x4005;
    /**
     * A parameter failed to be validated by the DDEML. Some of the possible
     * causes follow:
     *
     * <ul>
     * <li>The application used a data handle initialized with a different item
     * name handle than was required by the transaction.</li>
     * <li>The application used a data handle that was initialized with a
     * different clipboard data format than was required by the
     * transaction.</li>
     * <li>The application used a client-side conversation handle with a
     * server-side function or vice versa.</li>
     * <li>The application used a freed data handle or string handle.</li>
     * <li>More than one instance of the application used the same object.</li>
     * </ul>
     */
    public int DMLERR_INVALIDPARAMETER = 0x4006;
    /**
     * A DDEML application has created a prolonged race condition (in which the
     * server application outruns the client), causing large amounts of memory
     * to be consumed.
     */
    public int DMLERR_LOW_MEMORY = 0x4007;
    /**
     * A memory allocation has failed.
     */
    public int DMLERR_MEMORY_ERROR = 0x4008;
    /**
     * A transaction has failed.
     */
    public int DMLERR_NOTPROCESSED = 0x4009;
    /**
     * A client's attempt to establish a conversation has failed.
     */
    public int DMLERR_NO_CONV_ESTABLISHED = 0x400a;
    /**
     * A request for a synchronous poke transaction has timed out.
     */
    public int DMLERR_POKEACKTIMEOUT = 0x400b;
    /**
     * An internal call to the PostMessage function has failed.
     */
    public int DMLERR_POSTMSG_FAILED = 0x400c;
    /**
     * An application instance with a synchronous transaction already in
     * progress attempted to initiate another synchronous transaction, or the
     * {@link #DdeEnableCallback} function was called from within a DDEML callback
     * function.
     */
    public int DMLERR_REENTRANCY = 0x400d;
    /**
     * A server-side transaction was attempted on a conversation terminated by
     * the client, or the server terminated before completing a transaction.
     */
    public int DMLERR_SERVER_DIED = 0x400e;
    /**
     * An internal error has occurred in the DDEML.
     */
    public int DMLERR_SYS_ERROR = 0x400f;
    /**
     * A request to end an advise transaction has timed out.
     */
    public int DMLERR_UNADVACKTIMEOUT = 0x4010;
    /**
     * An invalid transaction identifier was passed to a DDEML function. Once
     * the application has returned from an {@link #XTYP_XACT_COMPLETE} callback, the
     * transaction identifier for that callback function is no longer valid.
     */
    public int DMLERR_UNFOUND_QUEUE_ID = 0x4011;

    public int DMLERR_LAST = 0x4011;

    public int HDATA_APPOWNED = 0x0001;

    public interface DdeCallback extends StdCallLibrary.StdCallCallback {
        PVOID ddeCallback(int wType, int wFmt, HCONV hConv, HSZ hsz1, HSZ hsz2, HDDEDATA hData, ULONG_PTR lData1, ULONG_PTR lData2);
    }

    /**
     * Prevents the callback function from receiving {@link #XTYP_CONNECT} transactions
     * from the application's own instance. This flag prevents an application
     * from establishing a DDE conversation with its own instance. An
     * application should use this flag if it needs to communicate with other
     * instances of itself but not with itself.
     */
    public int CBF_FAIL_SELFCONNECTIONS = 0x00001000;
    /**
     * Prevents the callback function from receiving {@link #XTYP_CONNECT} and
     * {@link #XTYP_WILDCONNECT} transactions.
     */
    public int CBF_FAIL_CONNECTIONS = 0x00002000;
    /**
     * Prevents the callback function from receiving {@link #XTYP_ADVSTART} and
     * {@link #XTYP_ADVSTOP} transactions. The system returns {@link #DDE_FNOTPROCESSED} to each
     * client that sends an {@link #XTYP_ADVSTART} or {@link #XTYP_ADVSTOP} transaction to the
     * server.
     */
    public int CBF_FAIL_ADVISES = 0x00004000;
    /**
     * Prevents the callback function from receiving {@link #XTYP_EXECUTE} transactions.
     * The system returns {@link #DDE_FNOTPROCESSED} to a client that sends an
     * {@link #XTYP_EXECUTE} transaction to the server.
     */
    public int CBF_FAIL_EXECUTES = 0x00008000;
    /**
     * Prevents the callback function from receiving {@link #XTYP_POKE} transactions. The
     * system returns {@link #DDE_FNOTPROCESSED} to a client that sends an {@link #XTYP_POKE}
     * transaction to the server.
     */
    public int CBF_FAIL_POKES = 0x00010000;
    /**
     * Prevents the callback function from receiving {@link #XTYP_REQUEST} transactions.
     * The system returns {@link #DDE_FNOTPROCESSED} to a client that sends an
     * {@link #XTYP_REQUEST} transaction to the server.
     */
    public int CBF_FAIL_REQUESTS = 0x00020000;
    /**
     * Prevents the callback function from receiving server transactions. The
     * system returns {@link #DDE_FNOTPROCESSED} to each client that sends a transaction
     * to this application. This flag is equivalent to combining all CBF_FAIL_
     * flags.
     */
    public int CBF_FAIL_ALLSVRXACTIONS = 0x0003f000;

    /**
     * Prevents the callback function from receiving {@link #XTYP_CONNECT_CONFIRM}
     * notifications.
     */
    public int CBF_SKIP_CONNECT_CONFIRMS = 0x00040000;
    /**
     * Prevents the callback function from receiving {@link #XTYP_REGISTER}
     * notifications.
     */
    public int CBF_SKIP_REGISTRATIONS = 0x00080000;
    /**
     *Prevents the callback function from receiving {@link #XTYP_UNREGISTER}
     * notifications.
     */
    public int CBF_SKIP_UNREGISTRATIONS = 0x00100000;
    /**
     * Prevents the callback function from receiving {@link #XTYP_DISCONNECT}
     * notifications.
     */    
    public int CBF_SKIP_DISCONNECTS = 0x00200000;
    /**
     * Prevents the callback function from receiving any notifications. This
     * flag is equivalent to combining all CBF_SKIP_ flags.
     */
    public int CBF_SKIP_ALLNOTIFICATIONS = 0x003c0000;

    /**
     * Prevents the application from becoming a server in a DDE conversation.
     * The application can only be a client. This flag reduces consumption of
     * resources by the DDEML. It includes the functionality of the
     * {@link #CBF_FAIL_ALLSVRXACTIONS} 
     */
    public int APPCMD_CLIENTONLY = 0x00000010;
    /**
     * Prevents the DDEML from sending {@link #XTYP_CONNECT} and {@link #XTYP_WILDCONNECT}
     * transactions to the application until the application has created its
     * string handles and registered its service names or has turned off
     * filtering by a subsequent call to the {@link #DdeNameService} or {@link #DdeInitialize}
     * function. This flag is always in effect when an application calls
     * {@link #DdeInitialize} for the first time, regardless of whether the application
     * specifies the flag. On subsequent calls to {@link #DdeInitialize}, not specifying
     * this flag turns off the application's service-name filters, but
     * specifying it turns on the application's service name filters.
     */
    public int APPCMD_FILTERINITS = 0x00000020;
    public int APPCMD_MASK = 0x00000FF0;

    /**
     * Registers the application as a standard (nonmonitoring) DDEML
     * application.
     */
    public int APPCLASS_STANDARD = 0x00000000;
    /**
     * Makes it possible for the application to monitor DDE activity in the
     * system. This flag is for use by DDE monitoring applications. The
     * application specifies the types of DDE activity to monitor by combining
     * one or more monitor flags with the APPCLASS_MONITOR flag.
     */
    public int APPCLASS_MONITOR = 0x00000001;
    public int APPCLASS_MASK = 0x0000000F;

    /**
     * Notifies the callback function whenever a DDE application creates, frees,
     * or increments the usage count of a string handle or whenever a string
     * handle is freed as a result of a call to the {@link #DdeUninitialize}
     * function.
     */
    public int MF_HSZ_INFO = 0x01000000;
    /**
     * Notifies the callback function whenever the system or an application
     * sends a DDE message.
     */
    public int MF_SENDMSGS = 0x02000000;
    /**
     * Notifies the callback function whenever the system or an application
     * posts a DDE message.
     */
    public int MF_POSTMSGS = 0x04000000;
    /**
     * Notifies the callback function whenever a transaction is sent to any DDE
     * callback function in the system.
     */
    public int MF_CALLBACKS = 0x08000000;
    /**
     * Notifies the callback function whenever a DDE error occurs.
     */
    public int MF_ERRORS = 0x10000000;
    /**
     * Notifies the callback function whenever an advise loop is started or
     * ended.
     */
    public int MF_LINKS = 0x20000000;
    /**
     * Notifies the callback function whenever a conversation is established or
     * terminated.
     */
    public int MF_CONV = 0x40000000;

    public int MF_MASK = 0xFF000000;

    public int EC_ENABLEALL = 0;
    public int EC_ENABLEONE = ST_BLOCKNEXT;
    public int EC_DISABLE = ST_BLOCKED;
    public int EC_QUERYWAITING = 2;

    public int DNS_REGISTER = 0x0001;
    public int DNS_UNREGISTER = 0x0002;
    public int DNS_FILTERON = 0x0004;
    public int DNS_FILTEROFF = 0x0008;
    
    /**
     * Registers an application with the Dynamic Data Exchange Management 
     * Library (DDEML). An application must call this function before calling 
     * any other Dynamic Data Exchange Management Library (DDEML) function. 
     * 
     * @param pidInst The application instance identifier. At initialization,
     * this parameter should point to 0. If the function succeeds, this
     * parameter points to the instance identifier for the application. This
     * value should be passed as the idInst parameter in all other DDEML
     * functions that require it. If an application uses multiple instances of
     * the DDEML dynamic-link library (DLL), the application should provide a
     * different callback function for each instance.
     *
     * <p>If pidInst points to a nonzero value, reinitialization of the DDEML is
     * implied. In this case, pidInst must point to a valid application-instance
     * identifier.</p>
     * 
     * @param fnCallback A pointer to the application-defined DDE callback function.
     * This function processes DDE transactions sent by the system.
     * For more information, see the DdeCallback callback function. 
     * 
     * @param afCmd A set of APPCMD_, CBF_, and MF_ flags. The APPCMD_ flags 
     * provide special instructions to DdeInitialize. The CBF_ flags specify
     * filters that prevent specific types of transactions from reaching the
     * callback function. The MF_ flags specify the types of DDE activity that a
     * DDE monitoring application monitors. Using these flags enhances the
     * performance of a DDE application by eliminating unnecessary calls to the
     * callback function.
     *
     * <p>This parameter can be one or more of the following values.</p>
     * 
     * <table>
     * <tr ><th>Value</th><th>Meaning</th></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #APPCLASS_MONITOR}</strong></dt>
     * <dt>0x00000001L</dt>
     * </dl>
     * </td><td>
     * <p>
     * Makes it possible for the application to monitor DDE activity in the
     * system. This flag is for use by DDE monitoring applications. The
     * application specifies the types of DDE activity to monitor by combining
     * one or more monitor flags with the {@link #APPCLASS_MONITOR} flag. For details,
     * see the following Remarks section.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #APPCLASS_STANDARD}</strong></dt>
     * <dt>0x00000000L</dt>
     * </dl>
     * </td><td>
     * <p>
     * Registers the application as a standard (nonmonitoring) DDEML
     * application.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #APPCMD_CLIENTONLY}</strong></dt>
     * <dt>0x00000010L</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the application from becoming a server in a DDE conversation.
     * The application can only be a client. This flag reduces consumption of
     * resources by the DDEML. It includes the functionality of the
     * {@link #CBF_FAIL_ALLSVRXACTIONS} flag.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #APPCMD_FILTERINITS}</strong></dt>
     * <dt>0x00000020L</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the DDEML from sending {@link #XTYP_CONNECT} and {@link #XTYP_WILDCONNECT}
     * transactions to the application until the application has created its
     * string handles and registered its service names or has turned off
     * filtering by a subsequent call to the {@link #DdeNameService} or DdeInitialize
     * function. This flag is always in effect when an application calls
     * DdeInitialize for the first time, regardless of whether the application
     * specifies the flag. On subsequent calls to DdeInitialize, not specifying
     * this flag turns off the application's service-name filters, but
     * specifying it turns on the application's service name filters.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #CBF_FAIL_ALLSVRXACTIONS}</strong></dt>
     * <dt>0x0003f000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the callback function from receiving server transactions. The
     * system returns {@link #DDE_FNOTPROCESSED} to each client that sends a transaction
     * to this application. This flag is equivalent to combining all CBF_FAIL_
     * flags.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #CBF_FAIL_ADVISES}</strong></dt>
     * <dt>0x00004000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the callback function from receiving {@link #XTYP_ADVSTART} and
     * {@link #XTYP_ADVSTOP} transactions. The system returns {@link #DDE_FNOTPROCESSED} to each
     * client that sends an {@link #XTYP_ADVSTART} or {@link #XTYP_ADVSTOP} transaction to the
     * server.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #CBF_FAIL_CONNECTIONS}</strong></dt>
     * <dt>0x00002000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the callback function from receiving {@link #XTYP_CONNECT} and
     * {@link #XTYP_WILDCONNECT} transactions.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #CBF_FAIL_EXECUTES}</strong></dt>
     * <dt>0x00008000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the callback function from receiving {@link #XTYP_EXECUTE} transactions.
     * The system returns {@link #DDE_FNOTPROCESSED} to a client that sends an
     * {@link #XTYP_EXECUTE} transaction to the server.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #CBF_FAIL_POKES}</strong></dt>
     * <dt>0x00010000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the callback function from receiving {@link #XTYP_POKE} transactions. The
     * system returns {@link #DDE_FNOTPROCESSED} to a client that sends an {@link #XTYP_POKE}
     * transaction to the server.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #CBF_FAIL_REQUESTS}</strong></dt>
     * <dt>0x00020000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the callback function from receiving {@link #XTYP_REQUEST} transactions.
     * The system returns {@link #DDE_FNOTPROCESSED} to a client that sends an
     * {@link #XTYP_REQUEST} transaction to the server.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #CBF_FAIL_SELFCONNECTIONS}</strong></dt>
     * <dt>0x00001000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the callback function from receiving {@link #XTYP_CONNECT} transactions
     * from the application's own instance. This flag prevents an application
     * from establishing a DDE conversation with its own instance. An
     * application should use this flag if it needs to communicate with other
     * instances of itself but not with itself.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #CBF_SKIP_ALLNOTIFICATIONS}</strong></dt>
     * <dt>0x003c0000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the callback function from receiving any notifications. This
     * flag is equivalent to combining all CBF_SKIP_ flags.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #CBF_SKIP_CONNECT_CONFIRMS}</strong></dt>
     * <dt>0x00040000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the callback function from receiving {@link #XTYP_CONNECT_CONFIRM}
     * notifications.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #CBF_SKIP_DISCONNECTS}</strong></dt>
     * <dt>0x00200000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the callback function from receiving {@link #XTYP_DISCONNECT}
     * notifications.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #CBF_SKIP_REGISTRATIONS}</strong></dt>
     * <dt>0x00080000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the callback function from receiving {@link #XTYP_REGISTER}
     * notifications.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #CBF_SKIP_UNREGISTRATIONS}</strong></dt>
     * <dt>0x00100000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Prevents the callback function from receiving {@link #XTYP_UNREGISTER}
     * notifications.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #MF_CALLBACKS}</strong></dt>
     * <dt>0x08000000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Notifies the callback function whenever a transaction is sent to any DDE
     * callback function in the system.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #MF_CONV}</strong></dt>
     * <dt>0x40000000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Notifies the callback function whenever a conversation is established or
     * terminated.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #MF_ERRORS}</strong></dt>
     * <dt>0x10000000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Notifies the callback function whenever a DDE error occurs.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #MF_HSZ_INFO}</strong></dt>
     * <dt>0x01000000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Notifies the callback function whenever a DDE application creates, frees,
     * or increments the usage count of a string handle or whenever a string
     * handle is freed as a result of a call to the {@link #DdeUninitialize}
     * function.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #MF_LINKS}</strong></dt>
     * <dt>0x20000000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Notifies the callback function whenever an advise loop is started or
     * ended.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #MF_POSTMSGS}</strong></dt>
     * <dt>0x04000000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Notifies the callback function whenever the system or an application
     * posts a DDE message.</p>
     * </td></tr>
     * <tr><td><dl>
     * <dt><strong>{@link #MF_SENDMSGS}</strong></dt>
     * <dt>0x02000000</dt>
     * </dl>
     * </td><td>
     * <p>
     * Notifies the callback function whenever the system or an application
     * sends a DDE message.</p>
     * </td></tr>
     * </table>
     *
     * @param ulRes Reserved; must be set to zero.
     * @return If the function succeeds, the return value is {@link #DMLERR_NO_ERROR}.
     * 
     * <p>If the function fails, the return value is one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_USAGE}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_SYS_ERROR}</li>
     * </ul>
     */
    public int DdeInitialize(DWORDByReference pidInst, DdeCallback fnCallback, int afCmd, int ulRes);

    /**
     * Frees all Dynamic Data Exchange Management Library (DDEML) resources
     * associated with the calling application. 
     * 
     * @param idInst The application instance identifier obtained by a previous 
     * call to the {@link #DdeInitialize} function.
     * 
     * @return true if function succeeded
     */
    public boolean DdeUninitialize(int idInst);

    /**
     * Establishes a conversation with all server applications that support the 
     * specified service name and topic name pair. An application can also use 
     * this function to obtain a list of conversation handles by passing the 
     * function an existing conversation handle. The Dynamic Data Exchange 
     * Management Library removes the handles of any terminated conversations 
     * from the conversation list. The resulting conversation list contains the 
     * handles of all currently established conversations that support the 
     * specified service name and topic name. 
     * 
     * @param idInst The application instance identifier obtained by a previous 
     * call to the {@link #DdeInitialize} function.
     * 
     * @param hszService A handle to the string that specifies the service name 
     * of the server application with which a conversation is to be established.
     * If this parameter is 0L, the system attempts to establish conversations 
     * with all available servers that support the specified topic name.
     * 
     * @param hszTopic A handle to the string that specifies the name of the
     * topic on which a conversation is to be established. This handle must
     * have been created by a previous call to the {@link #DdeCreateStringHandle} 
     * function. If this parameter is 0L, the system will attempt to establish
     * conversations on all topics supported by the selected server (or servers).
     * 
     * @param hConvList A handle to the conversation list to be enumerated.
     * This parameter should be 0L if a new conversation list is to be established.
     * 
     * @param pCC A pointer to the CONVCONTEXT structure that contains 
     * conversation-context information. If this parameter is NULL, the server 
     * receives the default CONVCONTEXT structure during the {@link #XTYP_CONNECT} or 
     * {@link #XTYP_WILDCONNECT} transaction.
     * 
     * @return If the function succeeds, the return value is the handle to a 
     * new conversation list.
     * 
     * <p>If the function fails, the return value is 0L. The handle to the old
     * conversation list is no longer valid.</p>
     * 
     * <p>The {@link #DdeGetLastError} function can be used to get the error code, which
     * can be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_CONV_ESTABLISHED}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     *   <li>{@link #DMLERR_SYS_ERROR}</li>
     * </ul>
     */
    public HCONVLIST DdeConnectList(int idInst, HSZ hszService, HSZ hszTopic,
            HCONVLIST hConvList, CONVCONTEXT pCC);

    /**
     * Retrieves the next conversation handle in the specified conversation list. 
     * 
     * @param hConvList A handle to the conversation list. This handle must have
     * been created by a previous call to the {@link #DdeConnectList} function. 
     * 
     * @param hConvPrev A handle to the conversation handle previously returned
     * by this function. If this parameter is 0L, the function returns the first
     * conversation handle in the list. 
     * 
     * @return If the list contains any more conversation handles, the return 
     * value is the next conversation handle in the list; otherwise, it is 0L. 
     */
    public HCONV DdeQueryNextServer( HCONVLIST hConvList, HCONV hConvPrev);

    /**
     * Destroys the specified conversation list and terminates all 
     * conversations associated with the list. 
     * 
     * @param hConvList A handle to the conversation list. This handle must have
     * been created by a previous call to the {@link #DdeConnectList} function. 
     * 
     * @return true if the function succeeds, the return value is nonzero.
     * 
     * <p>The {@link #DdeGetLastError} function can be used to get the error code, 
     * which can be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     */
    public boolean DdeDisconnectList(HCONVLIST hConvList);

    /**
     * Establishes a conversation with a server application that supports the
     * specified service name and topic name pair. If more than one such server
     * exists, the system selects only one.
     *
     * @param idInst The application instance identifier obtained by a previous
     * call to the {@link #DdeInitialize} function.
     *
     * @param hszService A handle to the string that specifies the service name
     * of the server application with which a conversation is to be established.
     * This handle must have been created by a previous call to the
     * {@link #DdeCreateStringHandle} function. If this parameter is 0L, a conversation
     * is established with any available server.
     *
     * @param hszTopic A handle to the string that specifies the name of the
     * topic on which a conversation is to be established. This handle must have
     * been created by a previous call to {@link #DdeCreateStringHandle}. If this
     * parameter is 0L, a conversation on any topic supported by the selected
     * server is established.
     *
     * @param pCC A pointer to the CONVCONTEXT structure that contains
     * conversation context information. If this parameter is NULL, the server
     * receives the default CONVCONTEXT structure during the {@link #XTYP_CONNECT} or
     * {@link #XTYP_WILDCONNECT} transaction.
     *
     * @return If the function succeeds, the return value is the handle to the
     * established conversation.
     *
     * <p>If the function fails, the return value is 0L.</p>
     *
     * <p>The {@link #DdeGetLastError} function can be used to get the error code, which can
     * be one of the following values:</p>
     *
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_CONV_ESTABLISHED}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     */
    public HCONV DdeConnect( int idInst, HSZ hszService, HSZ hszTopic, CONVCONTEXT pCC);

    /**
     * Terminates a conversation started by either the {@link #DdeConnect} or
     * {@link #DdeConnectList} function and invalidates the specified conversation
     * handle.
     *
     * @param hConv A handle to the active conversation to be terminated.
     *
     * @return true if the function succeeds
     *
     * <p>
     * The {@link #DdeGetLastError} function can be used to get the error code, which can
     * be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_NO_CONV_ESTABLISHED}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     */
    public boolean DdeDisconnect( HCONV hConv);

    /**
     * Enables a client Dynamic Data Exchange Management Library (DDEML)
     * application to attempt to reestablish a conversation with a service that
     * has terminated a conversation with the client. When the conversation is
     * reestablished, the Dynamic Data Exchange Management Library (DDEML)
     * attempts to reestablish any preexisting advise loops.
     *
     * @param hConv A handle to the conversation to be reestablished. A client
     * must have obtained the conversation handle by a previous call to the
     * {@link #DdeConnect} function or from an {@link #XTYP_DISCONNECT} transaction.
     * @return If the function succeeds, the return value is the handle to the
     * reestablished conversation.
     *
     * <p>
     * If the function fails, the return value is 0L.</p>
     *
     * <p>
     * The {@link #DdeGetLastError} function can be used to get the error code, which can
     * be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_CONV_ESTABLISHED}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     */
    public HCONV DdeReconnect( HCONV hConv);

    /**
     * Retrieves information about a Dynamic Data Exchange (DDE) transaction and about the conversation in which the transaction takes place.
     * 
     * @param hConv A handle to the conversation.
     * @param idTransaction The transaction. For asynchronous transactions, this
     * parameter should be a transaction identifier returned by the
     * {@link #DdeClientTransaction} function. For synchronous transactions, this
     * parameter should be {@link #QID_SYNC}.
     * @param pConvInfo A pointer to the CONVINFO structure that receives
     * information about the transaction and conversation. The cb member of the
     * CONVINFO structure must specify the length of the buffer allocated for
     * the structure.
     * @return If the function succeeds, the return value is the number of bytes
     * copied into the CONVINFO structure.
     *
     * <p>
     * If the function fails, the return value is FALSE.</p>
     *
     * <p>
     * The {@link #DdeGetLastError} function can be used to get the error code, which can
     * be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_NO_CONV_ESTABLISHED}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     *   <li>{@link #DMLERR_UNFOUND_QUEUE_ID}</li>
     * </ul>
     */
    public int DdeQueryConvInfo( HCONV hConv, int idTransaction, CONVINFO pConvInfo);

    /**
     * Associates an application-defined value with a conversation handle or a
     * transaction identifier. This is useful for simplifying the processing of
     * asynchronous transactions. An application can use the {@link #DdeQueryConvInfo}
     * function to retrieve this value.
     *
     * @param hConv A handle to the conversation.
     * @param id The transaction identifier to associate with the value
     * specified by the hUser parameter. An application should set this
     * parameter to {@link #QID_SYNC} to associate hUser with the conversation identified
     * by the hConv parameter.
     * @param hUser The value to be associated with the conversation handle.
     * @return true If the function succeeds.
     *
     * <p>
     * The {@link #DdeGetLastError} function can be used to get the error code, which can
     * be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     *   <li>{@link #DMLERR_UNFOUND_QUEUE_ID}</li>
     * </ul>
     */
    public boolean DdeSetUserHandle( HCONV hConv, int id, DWORD_PTR hUser);

    /**
     * Abandons the specified asynchronous transaction and releases all
     * resources associated with the transaction.
     *
     * @param idInst The application instance identifier obtained by a previous
     * call to the {@link #DdeInitialize} function.
     * @param hConv A handle to the conversation in which the transaction was
     * initiated. If this parameter is 0L, all transactions are abandoned (that
     * is, the idTransaction parameter is ignored).
     * @param idTransaction The identifier of the transaction to be abandoned.
     * If this parameter is 0L, all active transactions in the specified
     * conversation are abandoned.
     * @return true if the function succeeds
     *
     * <p>
     * The {@link #DdeGetLastError} function can be used to get the error code, which can
     * be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     *   <li>{@link #DMLERR_UNFOUND_QUEUE_ID}</li>
     * </ul>
     */
    public boolean DdeAbandonTransaction(int idInst, HCONV hConv, int idTransaction);

    /**
     * Causes the system to send an {@link #XTYP_ADVREQ} transaction to the calling
     * (server) application's Dynamic Data Exchange (DDE) callback function for
     * each client with an active advise loop on the specified topic and item. A
     * server application should call this function whenever the data associated
     * with the topic name or item name pair changes.
     *
     *
     * @param idInst The application instance identifier obtained by a previous
     * call to the {@link #DdeInitialize} function.
     *
     * @param hszTopic A handle to a string that specifies the topic name. To
     * send notifications for all topics with active advise loops, an
     * application can set this parameter to 0L.
     *
     * @param hszItem A handle to a string that specifies the item name. To send
     * notifications for all items with active advise loops, an application can
     * set this parameter to 0L.
     *
     * @return true if the function succeeds
     * 
     * <p>
     * The {@link #DdeGetLastError} function can be used to get the error code, which can
     * be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_DLL_USAGE}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     *
     */
    public boolean DdePostAdvise(int idInst, HSZ hszTopic, HSZ hszItem);

    /**
     * Enables or disables transactions for a specific conversation or for all
     * conversations currently established by the calling application.
     *
     * @param idInst The application-instance identifier obtained by a previous
     * call to the {@link #DdeInitialize} function.
     * @param hConv A handle to the conversation to enable or disable. If this
     * parameter is NULL, the function affects all conversations.
     * @param wCmd The function code. This parameter can be one of the following
     * values.
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>{@link #EC_ENABLEALL}</td><td>Enables all transactions for the specified
     * conversation.</td></tr>
     * <tr><td>{@link #EC_ENABLEONE}</td><td>Enables one transaction for the specified
     * conversation.</td></tr>
     * <tr><td>{@link #EC_DISABLE}</td><td>Disables all blockable transactions for the
     * specified conversation.
     *
     * <p>
     * A server application can disable the following transactions:</p>
     * <ul>
     *   <li>{@link #XTYP_ADVSTART}</li>
     *   <li>{@link #XTYP_ADVSTOP}</li>
     *   <li>{@link #XTYP_EXECUTE}</li>
     *   <li>{@link #XTYP_POKE}</li>
     *   <li>{@link #XTYP_REQUEST}</li>
     * </ul>
     * <p>
     * A client application can disable the following transactions:</p>
     * <ul>
     *   <li>{@link #XTYP_ADVDATA}</li>
     *   <li>{@link #XTYP_XACT_COMPLETE}</li>
     * </ul>
     * </td></tr>
     * <tr><td>{@link #EC_QUERYWAITING}</td><td>Determines whether any transactions are
     * in the queue for the specified conversation.</td></tr>
     * </table>
     *
     * @return If the function succeeds, the return value is nonzero.
     *
     * <p>
     * If the function fails, the return value is zero.</p>
     *
     * <p>
     * If the wCmd parameter is {@link #EC_QUERYWAITING}, and the application transaction
     * queue contains one or more unprocessed transactions that are not being
     * processed, the return value is TRUE; otherwise, it is FALSE.</p>
     *
     * <p>
     * The {@link #DdeGetLastError} function can be used to get the error code, which can
     * be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     */
    public boolean DdeEnableCallback(int idInst, HCONV hConv, int wCmd);

    /**
     * Impersonates a Dynamic Data Exchange (DDE) client application in a DDE
     * client conversation.
     *
     * @param hConv A handle to the DDE client conversation to be impersonated.
     * @return true if the function succeeds
     *
     * <p>To get extended error information call GetLastError.</p>
     */
    public boolean DdeImpersonateClient(HCONV hConv);

    /**
     * Registers or unregisters the service names a Dynamic Data Exchange (DDE)
     * server supports. This function causes the system to send {@link #XTYP_REGISTER} or
     * {@link #XTYP_UNREGISTER} transactions to other running Dynamic Data Exchange
     * Management Library (DDEML) client applications.
     *
     * @param idInst The application instance identifier obtained by a previous
     * call to the {@link #DdeInitialize} function.
     * @param hsz1 A handle to the string that specifies the service name the
     * server is registering or unregistering. An application that is
     * unregistering all of its service names should set this parameter to 0L.
     * @param hsz2 Reserved; should be set to 0L.
     * @param afCmd The service name options. This parameter can be one of the
     * following values.
     *
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>{@link #DNS_REGISTER}</td><td>Registers the error code service
     * name.</td></tr>
     * <tr><td>{@link #DNS_UNREGISTER}</td><td>Unregisters the error code service name.
     * If the hsz1 parameter is 0L, all service names registered by the server
     * will be unregistered.</td></tr>
     * <tr><td>{@link #DNS_FILTERON}</td><td>Turns on service name initiation filtering.
     * The filter prevents a server from receiving {@link #XTYP_CONNECT} transactions for
     * service names it has not registered. This is the default setting for this
     * filter.
     * <br><br>
     * If a server application does not register any service names, the
     * application cannot receive {@link #XTYP_WILDCONNECT} transactions.
     * </td></tr>
     * <tr><td>{@link #DNS_FILTEROFF}</td><td>Turns off service name initiation
     * filtering. If this flag is specified, the server receives an {@link #XTYP_CONNECT}
     * transaction whenever another DDE application calls the {@link #DdeConnect}
     * function, regardless of the service name.</td></tr>
     * </table>
     * @return If the function succeeds, it returns a nonzero value. That value
     * is not a true HDDEDATA value, merely a Boolean indicator of success. The
     * function is typed HDDEDATA to allow for possible future expansion of the
     * function and a more sophisticated return value.
     *
     * <p>
     * If the function fails, the return value is 0L.</p>
     *
     * <p>
     * The {@link #DdeGetLastError} function can be used to get the error code, which can
     * be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_DLL_USAGE}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     */
    public HDDEDATA DdeNameService(int idInst,HSZ hsz1, HSZ hsz2, int afCmd);
    
    /**
     * Begins a data transaction between a client and a server. Only a Dynamic 
     * Data Exchange (DDE) client application can call this function, and the 
     * application can use it only after establishing a conversation with the 
     * server. 
     * 
     * @param pData The beginning of the data the client must pass to the server.
     * 
     * <p>Optionally, an application can specify the data handle (HDDEDATA) to
     * pass to the server and in that case the cbData parameter should be set
     * to -1. This parameter is required only if the wType parameter is 
     * {@link #XTYP_EXECUTE} or {@link #XTYP_POKE}. Otherwise, this parameter should be NULL.</p>
     * 
     * <p>For the optional usage of this parameter, {@link #XTYP_POKE} transactions where
     * pData is a data handle, the handle must have been created by a previous
     * call to the {@link #DdeCreateDataHandle} function, employing the same data format
     * specified in the wFmt parameter.</p>
     * 
     * @param cbData The length, in bytes, of the data pointed to by the pData 
     * parameter, including the terminating NULL, if the data is a string. 
     * A value of -1 indicates that pData is a data handle that identifies the 
     * data being sent.
     * 
     * @param hConv A handle to the conversation in which the transaction is to 
     * take place.
     * 
     * @param hszItem A handle to the data item for which data is being 
     * exchanged during the transaction. This handle must have been created by 
     * a previous call to the {@link #DdeCreateStringHandle} function. This parameter is
     * ignored (and should be set to 0L) if the wType parameter is {@link #XTYP_EXECUTE}.
     * 
     * @param wFmt The standard clipboard format in which the data item is 
     * being submitted or requested.
     * 
     * <p>If the transaction specified by the wType parameter does not pass
     * data or is {@link #XTYP_EXECUTE}, this parameter should be zero.</p>
     * 
     * <p>If the transaction specified by the wType parameter references 
     * non-execute DDE data ( {@link #XTYP_POKE}, {@link #XTYP_ADVSTART}, {@link #XTYP_ADVSTOP}, {@link #XTYP_REQUEST}), 
     * the wFmt value must be either a valid predefined (CF_) DDE format or a
     * valid registered clipboard format.</p>
     * 
     * @param wType     The transaction type. This parameter can be one of the
     *                  following values.
     *
     * <table>
     * <tr><th>Value</th><th>Meaning</th></tr>
     * <tr><td>{@link #XTYP_ADVSTART}</td><td>Begins an advise loop. Any number of
     * distinct advise loops can exist within a conversation. An application can
     * alter the advise loop type by combining the {@link #XTYP_ADVSTART} transaction
     * type with one or more of the following flags:
     * <dl>
     * <dt>{@link #XTYPF_NODATA}.</dt><dd>Instructs the server to notify the client of
     * any data changes without actually sending the data. This flag gives the
     * client the option of ignoring the notification or requesting the changed
     * data from the server.</dd>
     * <dt>{@link #XTYPF_ACKREQ}.</dt><dd>Instructs the server to wait until the client
     * acknowledges that it received the previous data item before sending the
     * next data item. This flag prevents a fast server from sending data faster
     * than the client can process it.</dd>
     * </dl>
     * </td></tr>
     * <tr><td>{@link #XTYP_ADVSTOP}</td><td>Ends an advise loop.</td></tr>
     * <tr><td>{@link #XTYP_EXECUTE}</td><td>Begins an execute transaction.</td></tr>
     * <tr><td>{@link #XTYP_POKE}</td><td>Begins a poke transaction.</td></tr>
     * <tr><td>{@link #XTYP_REQUEST}</td><td>Begins a request transaction.</td></tr>
     * </table>
     *
     * @param dwTimeout The maximum amount of time, in milliseconds, that the
     * client will wait for a response from the server application in a 
     * synchronous transaction. This parameter should be {@link #TIMEOUT_ASYNC} for 
     * asynchronous transactions.
     * 
     * @param pdwResult A pointer to a variable that receives the result of the
     * transaction. An application that does not check the result can use NULL
     * for this value. For synchronous transactions, the low-order word of this 
     * variable contains any applicable DDE_ flags resulting from the 
     * transaction. This provides support for applications dependent on 
     * DDE_APPSTATUS bits. It is, however, recommended that applications no 
     * longer use these bits because they may not be supported in future 
     * versions of the Dynamic Data Exchange Management Library (DDEML).
     * For asynchronous transactions, this variable is filled with a unique 
     * transaction identifier for use with the {@link #DdeAbandonTransaction} function 
     * and the {@link #XTYP_XACT_COMPLETE} transaction.
     * 
     * @return If the function succeeds, the return value is a data handle that
     * identifies the data for successful synchronous transactions in which the 
     * client expects data from the server. The return value is nonzero for
     * successful asynchronous transactions and for synchronous transactions
     * in which the client does not expect data. The return value is zero
     * for all unsuccessful transactions.
     * 
     * <p>The {@link #DdeGetLastError} function can be used to get the error code,
     * which can be one of the following values:</p>
     * 
     * <ul>
     *   <li>{@link #DMLERR_ADVACKTIMEOUT}</li>
     *   <li>{@link #DMLERR_BUSY}</li>
     *   <li>{@link #DMLERR_DATAACKTIMEOUT}</li>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_EXECACKTIMEOUT}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_MEMORY_ERROR}</li>
     *   <li>{@link #DMLERR_NO_CONV_ESTABLISHED}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     *   <li>{@link #DMLERR_NOTPROCESSED}</li>
     *   <li>{@link #DMLERR_POKEACKTIMEOUT}</li>
     *   <li>{@link #DMLERR_POSTMSG_FAILED}</li>
     *   <li>{@link #DMLERR_REENTRANCY}</li>
     *   <li>{@link #DMLERR_SERVER_DIED}</li>
     *   <li>{@link #DMLERR_UNADVACKTIMEOUT}</li>
     * </ul>
     */
    public HDDEDATA DdeClientTransaction(
            Pointer pData,
            int cbData,
            HCONV hConv,
            HSZ hszItem,
            int wFmt,
            int wType,
            int dwTimeout,
            WinDef.DWORDByReference pdwResult);

    /**
     * Creates a Dynamic Data Exchange (DDE) object and fills the object with
     * data from the specified buffer. A DDE application uses this function
     * during transactions that involve passing data to the partner application.
     * 
     * @param idInst The application instance identifier obtained by a previous 
     * call to the {@link #DdeInitialize} function.
     * 
     * @param pSrc The data to be copied to the DDE object. If this parameter
     * is NULL, no data is copied to the object.
     * 
     * @param cb The amount of memory, in bytes, to copy from the buffer pointed
     * to by pSrc. (include the terminating NULL, if the data is a string).
     * If this parameter is zero, the pSrc parameter is ignored.
     * 
     * @param cbOff An offset, in bytes, from the beginning of the buffer 
     * pointed to by the pSrc parameter. The data beginning at this offset is 
     * copied from the buffer to the DDE object.
     * 
     * @param hszItem A handle to the string that specifies the data item 
     * corresponding to the DDE object. This handle must have been created by a 
     * previous call to the {@link #DdeCreateStringHandle} function. If the data handle 
     * is to be used in an {@link #XTYP_EXECUTE} transaction, this parameter must be 0L.
     * 
     * @param wFmt The standard clipboard format of the data.
     * 
     * @param afCmd The creation flags. This parameter can be HDATA_APPOWNED, 
     * which specifies that the server application calling the 
     * DdeCreateDataHandle function owns the data handle this function creates.
     * This flag enables the application to share the data handle with other 
     * DDEML applications rather than creating a separate handle to pass to each
     * application. If this flag is specified, the application must eventually 
     * free the shared memory object associated with the handle by using the
     * {@link #DdeFreeDataHandle} function. If this flag is not specified, the handle 
     * becomes invalid in the application that created the handle after the 
     * data handle is returned by the application's DDE callback function or 
     * is used as a parameter in another DDEML function.
     * 
     * @return If the function succeeds, the return value is a data handle.
     * 
     * <p>If the function fails, the return value is 0L.</p>
     * 
     * <p>The {@link #DdeGetLastError} function can be used to get the error code, 
     * which can be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_MEMORY_ERROR}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     */
    public HDDEDATA DdeCreateDataHandle(
            int idInst,
            Pointer pSrc,
            int cb,
            int cbOff,
            HSZ hszItem,
            int wFmt,
            int afCmd);

    /**
     * Adds data to the specified Dynamic Data Exchange (DDE) object. An
     * application can add data starting at any offset from the beginning of 
     * the object. If new data overlaps data already in the object, 
     * the new data overwrites the old data in the bytes where the overlap 
     * occurs. The contents of locations in the object that have not been 
     * written to are undefined. 
     * 
     * @param hData A handle to the DDE object that receives additional data.
     * 
     * @param pSrc The data to be added to the DDE object.
     * 
     * @param cb The length, in bytes, of the data to be added to the DDE 
     * object, including the terminating NULL, if the data is a string.
     * 
     * @param cbOff An offset, in bytes, from the beginning of the DDE object.
     * The additional data is copied to the object beginning at this offset.
     * 
     * @return If the function succeeds, the return value is a new handle to 
     * the DDE object. The new handle is used in all references to the object.
     * 
     * <p>If the function fails, the return value is zero.</p>
     * 
     * <p>The {@link #DdeGetLastError} function can be used to get the error code, which
     * can be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_MEMORY_ERROR}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     */
    public HDDEDATA DdeAddData(HDDEDATA hData, Pointer pSrc, int cb, int cbOff);

    /**
     * Copies data from the specified Dynamic Data Exchange (DDE) object to
     * the specified local buffer. 
     * 
     * @param hData A handle to the DDE object that contains the data to copy.
     * 
     * @param pDst A pointer to the buffer that receives the data. If this
     * parameter is NULL, the DdeGetData function returns the amount of data, 
     * in bytes, that would be copied to the buffer.
     * 
     * @param cbMax The maximum amount of data, in bytes, to copy to the buffer 
     * pointed to by the pDst parameter. Typically, this parameter specifies
     * the length of the buffer pointed to by pDst.
     * 
     * @param cbOff An offset within the DDE object. Data is copied from the 
     * object beginning at this offset.
     * 
     * @return If the pDst parameter points to a buffer, the return value is
     * the size, in bytes, of the memory object associated with the data handle 
     * or the size specified in the cbMax parameter, whichever is lower.
     * 
     * <p>If the pDst parameter is NULL, the return value is the size, in bytes,
     * of the memory object associated with the data handle.</p>
     * 
     * <p>The {@link #DdeGetLastError} function can be used to get the error code, which 
     * can be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     */
    public int DdeGetData(HDDEDATA hData, Pointer pDst, int cbMax, int cbOff);

    /**
     * Provides access to the data in the specified Dynamic Data Exchange (DDE) object.
     * An application must call the {@link #DdeUnaccessData} function when it has
     * finished accessing the data in the object.
     *
     * @param hData A handle to the DDE object to be accessed.
     *
     * @param pcbDataSize A pointer to a variable that receives the size, in
     * bytes, of the DDE object identified by the hData parameter. If this
     * parameter is NULL, no size information is returned.
     *
     * @return If the function succeeds, the return value is a pointer to the
     * first byte of data in the DDE object.
     *
     * <p>If the function fails, the return value is NULL.</p>
     *
     * <p>The {@link #DdeGetLastError} function can be used to get the error code, which can
     * be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     */
    public Pointer DdeAccessData(HDDEDATA hData, WinDef.DWORDByReference pcbDataSize);

    /**
     * Unaccesses a Dynamic Data Exchange (DDE) object. An application must call
     * this function after it has finished accessing the object. 
     * 
     * @param hData A handle to the DDE object.
     * 
     * @return true if the function succeeds
     * 
     * <p>The {@link #DdeGetLastError} function can be used to get the error code, which
     * can be one of the following values:</p>
     * 
     * <ul>
     *   <li>{@link #DMLERR_DLL_NOT_INITIALIZED}</li>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     */
    public boolean DdeUnaccessData(HDDEDATA hData);

    /**
     * Frees a Dynamic Data Exchange (DDE) object and deletes the data handle
     * associated with the object.
     * 
     * @param hData A handle to the DDE object to be freed. This handle must 
     * have been created by a previous call to the {@link #DdeCreateDataHandle} function
     * or returned by the {@link #DdeClientTransaction} function.
     * 
     * @return true if freeing succeeded
     * 
     * <p>The {@link #DdeGetLastError} function can be used to get the error code,
     * which can be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     * </ul>
     */
    public boolean DdeFreeDataHandle(HDDEDATA hData);

    /**
     * 
     * @param idInst The application instance identifier obtained by a previous
     * call to the {@link #DdeInitialize} function. 
     * 
     * @return See {@link Ddeml}.DMLERR_*
     */
    public int DdeGetLastError(int idInst);

    /**
     * Creates a handle that identifies the specified string. A Dynamic 
     * Data Exchange (DDE) client or server application can pass the string 
     * handle as a parameter to other Dynamic Data Exchange Management 
     * Library (DDEML) functions. 
     * 
     * @param idInst The application instance identifier obtained by a previous
     * call to the {@link #DdeInitialize} function.
     * 
     * @param psz The null-terminated string for which a handle is to be created. 
     * This string can be up to 255 characters. The reason for this limit is that
     * DDEML string management functions are implemented using atoms.
     * 
     * @param iCodePage The code page to be used to render the string. This 
     * value should be either {@link #CP_WINANSI} (the default code page) or 
     * {@link #CP_WINUNICODE}, depending on whether the ANSI or Unicode version of
     * {@link #DdeInitialize} was called by the client application.
     * 
     * @return If the function succeeds, the return value is a string handle.
     * 
     * <p>If the function fails, the return value is 0L.</p>
     * 
     * <p>The {@link #DdeGetLastError} function can be used to get the error code, which
     * can be one of the following values:</p>
     * <ul>
     *   <li>{@link #DMLERR_INVALIDPARAMETER}</li>
     *   <li>{@link #DMLERR_NO_ERROR}</li>
     *   <li>{@link #DMLERR_SYS_ERROR}</li>
     * </ul>
     */
    public HSZ DdeCreateStringHandle(int idInst, String psz, int iCodePage);

    /**
     * Copies text associated with a string handle into a buffer.    
     * 
     * @param idInst The application instance identifier obtained by a previous
     * call to the {@link #DdeInitialize} function.
     * 
     * @param hsz A handle to the string to copy. This handle must have been
     * created by a previous call to the {@link #DdeCreateStringHandle} function.
     * 
     * @param psz A pointer to a buffer that receives the string. To obtain the
     * length of the string, this parameter should be set to NULL.
     * 
     * @param cchMax The length, in characters, of the buffer pointed to by the 
     * psz parameter. For the ANSI version of the function, this is the number 
     * of bytes; for the Unicode version, this is the number of characters. 
     * If the string is longer than ( cchMax� 1), it will be truncated. If the 
     * psz parameter is set to NULL, this parameter is ignored.
     * 
     * @param iCodePage The code page used to render the string. This value 
     * should be either {@link #CP_WINANSI} or {@link #CP_WINUNICODE}.
     * 
     * @return If the psz parameter specified a valid pointer, the return value 
     * is the length, in characters, of the returned text 
     * (not including the terminating null character). If the psz parameter 
     * specified a NULL pointer, the return value is the length of the text
     * associated with the hsz parameter (not including the terminating null 
     * character).
     * 
     * <p>If an error occurs, the return value is 0L</p>
     */
    public int DdeQueryString(int idInst, HSZ hsz, Pointer psz, int cchMax, int iCodePage);
    
    /**
     * Frees a string handle in the calling application.
     *
     * @param idInst The application instance identifier obtained by a previous
     * call to the {@link #DdeInitialize} function.
     *
     * @param hsz A handle to the string handle to be freed. This handle must
     * have been created by a previous call to the {@link #DdeCreateStringHandle}
     * function.
     *
     * @return true if the function succeeds.
     *
     */
    public boolean DdeFreeStringHandle(int idInst, HSZ hsz);

    /**
     * Increments the usage count associated with the specified handle. This
     * function enables an application to save a string handle passed to the
     * application's Dynamic Data Exchange (DDE) callback function. Otherwise, a
     * string handle passed to the callback function is deleted when the
     * callback function returns. This function should also be used to keep a
     * copy of a string handle referenced by the CONVINFO structure returned by
     * the {@link #DdeQueryConvInfo} function.
     *
     * @param idInst The application instance identifier obtained by a previous
     * call to the {@link #DdeInitialize} function.
     * @param hsz A handle to the string handle to be saved.
     * @return true if the function succeeded
     */
    public boolean DdeKeepStringHandle(int idInst, HSZ hsz);
}
