/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinDef.LPVOID;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Ole32.dll Interface.
 * 
 * @author dblock[at]dblock.org
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public interface Ole32 extends StdCallLibrary {

    /** The instance. */
    Ole32 INSTANCE = (Ole32) Native.loadLibrary("Ole32", Ole32.class,
                                                W32APIOptions.UNICODE_OPTIONS);

    /**
     * Identifies the version number of the installed OLE framework.
     *
     * @return A DWORD uniquely identifying the installed OLE framework build.
     */
    int OleBuildVersion();

    /**
     * Initializes the COM library on the current apartment, identifies the
     * concurrency model as single-thread apartment (STA), and enables
     * additional functionality described in the Remarks section below.
     * Applications must initialize the COM library before they can call COM
     * library functions other than CoGetMalloc and memory allocation functions.
     * @param pvReserved Reserved; must be null.
     * @return {@link WinError#S_OK S_OK} if the COM library and additional functionality were
     *              initialized successfully on this apartment.<p>
     *         {@link WinError#S_FALSE S_FALSE} if the COM library is already initialized on this apartment.<p>
     *         {@link WinError#OLE_E_WRONGCOMPOBJ OLE_E_WRONGCOMPOBJ} if the versions of COMPOBJ.DLL and OLE2.DLL on
     *                            your machine are incompatible with each other.<p>
     *         {@link WinError#RPC_E_CHANGED_MODE RPC_E_CHANGED_MODE} if a previous call to CoInitializeEx specified
     *                            the concurrency model for this apartment as
     *                            multithread apartment (MTA). If running
     *                            Windows 2000, this could also mean that a
     *                            change from neutral threaded apartment to
     *                            single threaded apartment occurred.
     */
    HRESULT OleInitialize(Pointer pvReserved);
    /**
     * Closes the COM library on the apartment, releases any class factories,
     * other COM objects, or servers held by the apartment, disables RPC on the
     * apartment, and frees any resources the apartment maintains.
     *
     * Remarks:
     * Call OleUninitialize on application shutdown, as the last COM library
     * call, if the apartment was initialized with a call to
     * {@link #OleInitialize}. OleUninitialize calls the CoUninitialize function
     * internally to shut down the OLE Component Object(COM) Library.
     *
     * If the COM library was initialized on the apartment with a call to
     * CoInitialize or CoInitializeEx, it must be closed with a call to
     * CoUninitialize.
     *
     * The {@link #OleInitialize} and OleUninitialize calls must be balanced — 
     * if there are multiple calls to the {@link #OleInitialize} function, there
     * must be the same number of calls to OleUninitialize: Only the
     * OleUninitialize call corresponding to the {@link #OleInitialize} call
     * that actually initialized the library can close it.
     */
    void OleUninitialize();

    /**
     * Carries out the clipboard shutdown sequence. It also releases the
     * IDataObject pointer that was placed on the clipboard by the
     * OleSetClipboard function.
     * @return {@link WinError#S_OK S_OK} on success.<p>
     *         {@link WinError#CLIPBRD_E_CANT_OPEN CLIPBRD_E_CANT_OPEN} The Windows OpenClipboard function used
     *                             within OleFlushClipboard failed.<p>
     *         {@link WinError#CLIPBRD_E_CANT_CLOSE CLIPBRD_E_CANT_CLOSE} The Windows CloseClipboard function used
     *                              within OleFlushClipboard failed.<p>
     * <b>Remarks</b><p>
     * OleFlushClipboard renders the data from a data object onto the clipboard
     * and releases the IDataObject pointer to the data object. While the
     * application that put the data object on the clipboard is running, the
     * clipboard holds only a pointer to the data object, thus saving memory.
     * If you are writing an application that acts as the source of a clipboard
     * operation, you can call the OleFlushClipboard function when your
     * application is closed, such as when the user exits from your application.
     * Calling OleFlushClipboard enables pasting and paste-linking of OLE
     * objects after application shutdown.
     * Before calling OleFlushClipboard, you can easily determine if your data
     * is still on the clipboard with a call to the OleIsCurrentClipboard
     * function.
     *
     * OleFlushClipboard leaves all formats offered by the data transfer object,
     * including the OLE 1 compatibility formats, on the clipboard so they are
     * available after application shutdown. In addition to OLE 1 compatibility
     * formats, these include all formats offered on a global handle medium (all
     * except for TYMED_FILE) and formatted with a null target device. For
     * example, if a data-source application offers a particular clipboard
     * format (say cfFOO) on an IStorage object, and calls the OleFlushClipboard
     * function, the storage object is copied into memory and the hglobal memory
     * handle is put on the clipboard.
     *
     * To retrieve the information on the clipboard, you can call the
     * OleGetClipboard function from another application, which creates a
     * default data object, and the hglobal from the clipboard again becomes a
     * storage object. Furthermore, the FORMATETC enumerator and the
     * IDataObject::QueryGetData method would all correctly indicate that the
     * original clipboard format (cfFOO) is again available on a TYMED_ISTORAGE.
     *
     * To empty the clipboard, call the OleSetClipboard function specifying a
     * null value for its parameter. The application should call this when it
     * closes if there is no need to leave data on the clipboard after shutdown,
     * or if data will be placed on the clipboard using the standard Windows
     * clipboard functions.
     */
    HRESULT OleFlushClipboard();

    /**
     * Puts an OLE compound document object into the running state.
     * @param pUnknown [in] Pointer to the {@link IUnknown IUnknown} interface
     *                 on the object, with which it will query for a pointer to
     *                 the IRunnableObject interface, and then call its Run method.
     * @return This function returns  on success.
     *         Other possible values include the following.<p>
     *         {@link WinError#OLE_E_CLASSDIFF OLE_E_CLASSDIFF} The source of an
     *         OLE link has been converted to a different class.<p>
     * <B>Remarks</B><p>
     * The OleRun function puts an object in the running state. The
     * implementation of OleRun was changed in OLE 2.01 to coincide with the
     * publication of the IRunnableObject interface. You can use OleRun and
     * IRunnableObject::Run interchangeably. OleRun queries the object for a
     * pointer to IRunnableObject. If successful, the function returns the
     * results of calling the IRunnableObject::Run method.<p><p>
     * For more information on using this function, see IRunnableObject::Run.
     */
    HRESULT OleRun(Pointer pUnknown);

    /**
     * Creates a GUID, a unique 128-bit integer used for CLSIDs and interface
     * identifiers.
     * 
     * @param pguid
     *            A pointer to the requested GUID.
     * @return S_OK if the GUID was successfully created.
     */
    HRESULT CoCreateGuid(GUID pguid);

    /** @deprecated use {@link #CoCreateGuid(Guid.GUID)}. */
    HRESULT CoCreateGuid(GUID.ByReference pguid);

    /**
     * Converts a globally unique identifier (GUID) into a string of printable
     * characters.
     * 
     * @param rguid
     *            The GUID to be converted.
     * @param lpsz
     *            A pointer to a caller-allocated string variable to receive the
     *            resulting string.
     * @param cchMax
     *            The number of characters available in the lpsz buffer.
     * @return If the function succeeds, the return value is the number of
     *         characters in the returned string, including the null terminator.
     *         If the buffer is too small to contain the string, the return
     *         value is 0.
     */
    int StringFromGUID2(GUID rguid, char[] lpsz, int cchMax);

    /**
     * Converts a string generated by the StringFromIID function back into the
     * original interface identifier (IID).
     * 
     * @param lpsz
     *            A pointer to the string representation of the IID.
     * @param lpiid
     *            A pointer to the requested IID on return.
     * @return This function can return the standard return values E_INVALIDARG,
     *         E_OUTOFMEMORY, and S_OK.
     */
    HRESULT IIDFromString(String lpsz, GUID lpiid);

    /**
     * Initializes the COM library on the current thread and identifies the
     * concurrency model as single-thread apartment (STA).
     * 
     * New applications should call CoInitializeEx instead of CoInitialize.
     * 
     * pvReserved [in, optional] This parameter is reserved and must be NULL.
     * 
     * @param pvReserved
     *            the pv reserved
     * @return S_OK The COM library was initialized successfully on this thread.
     * 
     *         S_FALSE The COM library is already initialized on this thread.
     * 
     *         RPC_E_CHANGED_MODE A previous call to CoInitializeEx specified
     *         the concurrency model for this thread as multithread apartment
     *         (MTA). This could also indicate that a change from
     *         neutral-threaded apartment to single-threaded apartment has
     *         occurred.
     */
    HRESULT CoInitialize(LPVOID pvReserved);

    int COINIT_APARTMENTTHREADED  = 0x2;
    int COINIT_MULTITHREADED      = 0x0;
    int COINIT_DISABLE_OLE1DDE    = 0x4;
    int COINIT_SPEED_OVER_MEMORY  = 0x8;

    /**
     * Initializes the COM library for use by the calling thread, sets the
     * thread's concurrency model, and creates a new apartment for the thread if
     * one is required.
     * 
     * @param reserved
     *            This parameter is reserved and must be NULL.
     * @param dwCoInit
     *            The concurrency model and initialization options for the
     *            thread. Values for this parameter are taken from the COINIT
     *            enumeration. Any combination of values from COINIT can be
     *            used, except that the {@link #COINIT_APARTMENTTHREADED} and
     *            {@link #COINIT_MULTITHREADED} flags cannot both be set. The
     *            default (and only sane choice) is
     *            {@link #COINIT_MULTITHREADED}. 
     * @return This function can return the standard return values E_INVALIDARG,
     *         E_OUTOFMEMORY, and E_UNEXPECTED, as well as the following values.
     *         S_OK, S_FALSE, RPC_E_CHANGED_MODE
     */
    HRESULT CoInitializeEx(Pointer reserved, int dwCoInit);

    /**
     * Closes the COM library on the current thread, unloads all DLLs loaded by
     * the thread, frees any other resources that the thread maintains, and
     * forces all RPC connections on the thread to close.
     * 
     */
    void CoUninitialize();

    /**
     * Creates a single uninitialized object of the class associated with a
     * specified CLSID.
     * 
     * @param rclsid
     *            The CLSID associated with the data and code that will be used
     *            to create the object.
     * @param pUnkOuter
     *            If NULL, indicates that the object is not being created as
     *            part of an aggregate. If non-NULL, pointer to the aggregate
     *            object's IUnknown interface (the controlling IUnknown).
     * @param dwClsContext
     *            Context in which the code that manages the newly created
     *            object will run. The values are taken from the enumeration
     *            CLSCTX defined in WTypes.
     * @param riid
     *            A reference to the identifier of the interface to be used to
     *            communicate with the object.
     * @param ppv
     *            Address of pointer variable that receives the interface
     *            pointer requested in riid. Upon successful return, *ppv
     *            contains the requested interface pointer. Upon failure, *ppv
     *            contains NULL.
     * @return an HRESULT
     */
    HRESULT CoCreateInstance(GUID rclsid, Pointer pUnkOuter, int dwClsContext,
                             GUID riid, PointerByReference ppv);

    /**
     * Looks up a CLSID in the registry, given a ProgID.
     * 
     * @param lpszProgID
     *            [in] A pointer to the ProgID whose CLSID is requested.
     * @param lpclsid
     *            [out] Receives a pointer to the retrieved CLSID on return.
     * 
     * @return S_OK The CLSID was retrieved successfully.
     * 
     *         CO_E_CLASSSTRING The registered CLSID for the ProgID is invalid.
     * 
     *         REGDB_E_WRITEREGDB An error occurred writing the CLSID to the
     *         registry. See Remarks below.
     * 
     *         Remarks Given a ProgID, CLSIDFromProgID looks up its associated
     *         CLSID in the registry. If the ProgID cannot be found in the
     *         registry, CLSIDFromProgID creates an OLE 1 CLSID for the ProgID
     *         and a CLSID entry in the registry. Because of the restrictions
     *         placed on OLE 1 CLSID values, CLSIDFromProgID and CLSIDFromString
     *         are the only two functions that can be used to generate a CLSID
     *         for an OLE 1 object.
     */
    HRESULT CLSIDFromProgID(String lpszProgID, CLSID.ByReference lpclsid);

    /**
     * Converts a string generated by the StringFromCLSID function back into the
     * original CLSID.
     * 
     * @param lpsz
     *            [in] The string representation of the CLSID.
     * 
     * @param pclsid
     *            [out] A pointer to the CLSID.
     * 
     * @return Return value
     * 
     *         This function can return the standard return value E_INVALIDARG,
     *         as well as the following values.
     * 
     *         NOERROR The CLSID was obtained successfully.
     * 
     *         CO_E_CLASSSTRING The class string was improperly formatted.
     * 
     *         REGDB_E_CLASSNOTREG The CLSID corresponding to the class string
     *         was not found in the registry.
     * 
     *         REGDB_E_READREGDB The registry could not be opened for reading.
     */
    HRESULT CLSIDFromString(WString lpsz, CLSID.ByReference pclsid);

}
