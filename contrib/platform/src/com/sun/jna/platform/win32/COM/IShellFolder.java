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
package com.sun.jna.platform.win32.COM;

/*
 * @author L W Ahonen, lwahonen@iki.fi
 */

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.ShTypes.STRRET;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface IShellFolder {


    /**
     * The interface IID for QueryInterface et al
     */
    public final static IID IID_ISHELLFOLDER = new IID(
            "{000214E6-0000-0000-C000-000000000046}");

    /**
     *
     * Retrieves pointers to the supported interfaces on an object.
     * This method calls IUnknown::AddRef on the pointer it returns.
     *
     * @param riid
     *            The identifier of the interface being requested.
     *
     * @param ppvObject
     *            The address of a pointer variable that receives the interface pointer requested in the riid parameter. Upon successful
     *            return, *ppvObject contains the requested interface pointer to the object. If the object does not support the
     *            interface, *ppvObject is set to NULL.
     *
     * @return
     *            This method returns S_OK if the interface is supported, and E_NOINTERFACE otherwise. If ppvObject is NULL, this method returns E_POINTER.
     *            For any one object, a specific query for the IUnknown interface on any of the object's interfaces must always return the same pointer value.
     *            This enables a client to determine whether two pointers point to the same component by calling QueryInterfacewith IID_IUnknown
     *            and comparing the results. It is specifically not the case that queries for interfaces other than IUnknown (even the same interface
     *            through the same pointer) must return the same pointer value.
     *
     *            There are four requirements for implementations of QueryInterface (In these cases, "must succeed" means "must succeed barring
     *            catastrophic failure."):
     *            The set of interfaces accessible on an object through QueryInterface must be static, not dynamic. This means that if a call
     *            toQueryInterface for a pointer to a specified interface succeeds the first time, it must succeed again, and if it fails
     *            the first time, it must fail on all subsequent queries. 
     *
     *            It must be reflexive: if a client holds a pointer to an interface on an object, and queries for that interface, the call must succeed. 
     *
     *            It must be symmetric: if a client holding a pointer to one interface queries successfully for another, a query through
     *            the obtained pointer for the first interface must succeed. 
     *
     *            It must be transitive: if a client holding a pointer to one interface queries successfully for a second, and through that
     *            pointer queries successfully for a third interface, a query for the first interface through the pointer for the
     *            third interface must succeed. 
     *            Notes to Implementers
     *            Implementations of QueryInterface must never check ACLs. The main reason for this rule is that COM requires that an object supporting a
     *            particular interface always return success when queried for that interface. Another reason is that checking ACLs on QueryInterface
     *            does not provide any real security because any client who has access to a particular interface can hand it directly to another
     *            client without any calls back to the server. Also, because COM caches interface pointers, it does not callQueryInterface on
     *            the server every time a client does a query.
     */
    HRESULT QueryInterface(
            REFIID riid,
            PointerByReference ppvObject);

    /**
     *
     * Increments the reference count for an interface on an object. This method should be called for every new copy of a pointer to an interface on an object.
     * @return
     *            The method returns the new reference count. This value is intended to be used only for test purposes.
     *
     *            Objects use a reference counting mechanism to ensure that the lifetime of the object includes the lifetime of references to it. You use AddRef
     *            to stabilize a copy of an interface pointer. It can also be called when the life of a cloned pointer must extend beyond the
     *            lifetime of the original pointer. The cloned pointer must be released by calling IUnknown::Release.
     *
     *            The internal reference counter that AddRef maintains should be a 32-bit unsigned integer.
     *            Notes to Callers
     *            Call this method for every new copy of an interface pointer that you make. For example, if you are passing a copy of a pointer
     *            back from a method, you must call AddRef on that pointer. You must also call AddRef on a pointer before passing it as an in-out
     *            parameter to a method; the method will call IUnknown::Release before copying the out-value on top of it.
     */
    int AddRef();

    /**
     * Decrements the reference count for an interface on an object.
     *
     * @return
     *            The method returns the new reference count. This value is intended to be used only for test purposes.
     *
     *            When the reference count on an object reaches zero, Release must cause the interface pointer to free itself. When the released
     *            pointer is the only existing reference to an object (whether the object supports single or multiple interfaces), the
     *            implementation must free the object.
     *
     *            Note that aggregation of objects restricts the ability to recover interface pointers.
     *            Notes to Callers
     *            Call this method when you no longer need to use an interface pointer. If you are writing a method that takes an in-out
     *            parameter, call Release on the pointer you are passing in before copying the out-value on top of it.
     */
    int Release();

    /**
     * Translates the display name of a file object or a folder into an item identifier list
     *
     * @param hwnd
     *            A window handle. The client should provide a window handle if it displays a dialog or message box. Otherwise set hwnd to NULL.
     *
     * @param pbc
     *            Optional. A pointer to a bind context used to pass parameters as inputs and outputs to the parsing function. These passed parameters
     *            are often specific to the data source and are documented by the data source owners. For example, the file system data source accepts
     *            the name being parsed (as a WIN32_FIND_DATA structure), using the STR_FILE_SYS_BIND_DATA bind context parameter.
     *            STR_PARSE_PREFER_FOLDER_BROWSING can be passed to indicate that URLs are parsed using the file system data source when possible.
     *            Construct a bind context object using CreateBindCtx and populate the values using IBindCtx::RegisterObjectParam. See Bind Context
     *            String Keys for a complete list of these.
     *
     *            If no data is being passed to or received from the parsing function, this value can be NULL.
     *
     * @param pszDisplayName
     *            A null-terminated Unicode string with the display name. Because each Shell folder defines its own parsing syntax, the
     *            form this string can take may vary. The desktop folder, for instance, accepts paths such as "C:\My Docs\My File.txt".
     *            It also will accept references to items in the namespace that have a GUID associated with them using the "::{GUID}" syntax.
     *            For example, to retrieve a fully qualified identifier list for the control panel from the desktop folder, you can use the following:
     *            "::{CLSID for Control Panel}\::{CLSID for printers folder}"
     *
     * @param pchEaten
     *            A pointer to a ULONG value that receives the number of characters of the display name that was parsed. If your application
     *            does not need this information, set pchEaten to NULL, and no value will be returned.
     *
     * @param ppidl
     *            When this method returns, contains a pointer to the PIDL for the object. The returned item identifier list specifies the item
     *            relative to the parsing folder. If the object associated with pszDisplayName is within the parsing folder, the returned item
     *            identifier list will contain only one SHITEMID structure. If the object is in a subfolder of the parsing folder, the returned
     *            item identifier list will contain multiple SHITEMID structures. If an error occurs, NULL is returned in this address.
     *
     *            When it is no longer needed, it is the responsibility of the caller to free this resource by calling CoTaskMemFree.
     *
     * @param pdwAttributes
     *            The value used to query for file attributes. If not used, it should be set to NULL. To query for one or more attributes, initialize
     *            this parameter with the SFGAO flags that represent the attributes of interest. On return, those attributes that are true and were requested will be set.
     * @return
     *            If this method succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     */
    HRESULT ParseDisplayName(
            WinDef.HWND hwnd,
            Pointer pbc,
            String pszDisplayName,
            IntByReference pchEaten,
            PointerByReference ppidl,
            IntByReference pdwAttributes);

    /**
     * Enables a client to determine the contents of a folder by creating an item identifier enumeration object and returning its IEnumIDList interface.
     * The methods supported by that interface can then be used to enumerate the folder's contents.
     *
     * @param hwnd
     *            If user input is required to perform the enumeration, this window handle should be used by the enumeration object as the parent window
     *            to take user input. An example would be a dialog box to ask for a password or prompt the user to insert a CD or floppy disk.
     *            If hwndOwner is set to NULL, the enumerator should not post any messages, and if user input is required, it should silently fail.
     *
     * @param grfFlags
     *            Flags indicating which items to include in the enumeration. For a list of possible values, see the SHCONTF enumerated type.
     *
     * @param ppenumIDList
     *            The address that receives a pointer to the IEnumIDList interface of the enumeration object created by this method.
     *            If an error occurs or no suitable subobjects are found, ppenumIDList is set to NULL.
     *
     * @return
     *            Returns S_OK if successful, or an error value otherwise. Some implementations may also return S_FALSE, indicating that there
     *            are no children matching the grfFlags that were passed in. If S_FALSE is returned, ppenumIDList is set to NULL.
     *
     */
    HRESULT EnumObjects(
            WinDef.HWND hwnd,
            int grfFlags,
            PointerByReference ppenumIDList);

    /**
     *
     *            Retrieves a handler, typically the Shell folder object that implements IShellFolder for a particular item. Optional
     *            parameters that control the construction of the handler are passed in the bind context.
     * @param pidl
     *
     *            The address of an ITEMIDLIST structure (PIDL) that identifies the subfolder. This value can refer to an item at any level below
     *            the parent folder in the namespace hierarchy. The structure contains one or more SHITEMID structures, followed by a terminating NULL.
     *
     * @param pbc
     *
     *            A pointer to an IBindCtx interface on a bind context object that can be used to pass parameters to the construction of the handler.
     *            If this parameter is not used, set it to NULL. Because support for this parameter is optional for folder object implementations,
     *            some folders may not support the use of bind contexts.
     *            Information that can be provided in the bind context includes a BIND_OPTS structure that includes a grfMode member that indicates
     *            the access mode when binding to a stream handler. Other parameters can be set and discovered using IBindCtx::RegisterObjectParam
     *            and IBindCtx::GetObjectParam.
     *
     * @param riid
     *            The identifier of the interface to return. This may be IID_IShellFolder, IID_IStream, or any other interface that identifies a particular handler.
     *
     * @param ppv
     *            When this method returns, contains the address of a pointer to the requested interface. If an error occurs, a NULL pointer is returned at this address.
     *
     * @return
     *            If this method succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     *
     */
    HRESULT BindToObject(
            Pointer pidl,
            Pointer pbc,
            REFIID riid,
            PointerByReference ppv);

    /**
     * Requests a pointer to an object's storage interface.
     * @param pidl
     *            The address of an ITEMIDLIST structure that identifies the subfolder relative to its parent folder. The structure must contain exactly one SHITEMID structure followed by a terminating zero.
     *
     * @param pbc
     *            The optional address of an IBindCtx interface on a bind context object to be used during this operation. If this parameter is
     *            not used, set it to NULL. Because support for pbc is optional for folder object implementations, some folders may not support the use of bind contexts.
     *
     * @param riid
     *            The IID of the requested storage interface. To retrieve an IStream, IStorage, or IPropertySetStorage interface pointer, set
     *            riid to IID_IStream, IID_IStorage, or IID_IPropertySetStorage, respectively.
     *
     * @param ppv
     *            The address that receives the interface pointer specified by riid. If an error occurs, a NULL pointer is returned in this address.
     *
     * @return
     *            If this method succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     *
     */
    HRESULT BindToStorage(
            Pointer pidl,
            Pointer pbc,
            REFIID riid,
            PointerByReference ppv);

    /**
     * Determines the relative order of two file objects or folders, given their item identifier lists.
     * @param lParam
     *            A value that specifies how the comparison should be performed.
     *            The lower sixteen bits of lParam define the sorting rule. Most applications set the sorting rule to the default value of zero, indicating that the
     *            two items should be compared by name. The system does not define any other sorting rules. Some folder objects might allow calling applications to
     *            use the lower sixteen bits of lParam to specify folder-specific sorting rules. The rules and their associated lParam values are defined by the folder.
     *
     *            When the system folder view object calls IShellFolder::CompareIDs, the lower sixteen bits of lParam are used to specify the column to be used for
     *            the comparison.
     *            The upper sixteen bits of lParam are used for flags that modify the sorting rule. The system currently defines these modifier flags.
     *
     *            SHCIDS_ALLFIELDS
     *            Version 5.0. Compare all the information contained in the ITEMIDLIST structure, not just the display names. This flag is valid only for folder objects that support
     *            the IShellFolder2 interface. For instance, if the two items are files, the folder should compare their names, sizes, file times, attributes, and any other information
     *            in the structures. If this flag is set, the lower sixteen bits of lParam must be zero.
     *
     *            SHCIDS_CANONICALONLY
     *            Version 5.0. When comparing by name, compare the system names but not the display names. When this flag is passed, the two items are compared by whatever criteria the
     *            Shell folder determines are most efficient, as long as it implements a consistent sort function. This flag is useful when comparing for equality or when the results of
     *            the sort are not displayed to the user. This flag cannot be combined with other flags.
     *
     * @param pidl1
     *            A pointer to the first item's ITEMIDLIST structure. It will be relative to the folder. This ITEMIDLIST structure can contain more than one
     *            element; therefore, the entire structure must be compared, not just the first element.
     *
     * @param pidl2
     *            A pointer to the second item's ITEMIDLIST structure. It will be relative to the folder. This ITEMIDLIST structure can contain more than one
     *            element; therefore, the entire structure must be compared, not just the first element.
     *
     * @return
     *            If this method is successful, the CODE field of the HRESULT contains one of the following values. For information regarding the extraction of
     *            the CODE field from the returned HRESULT, see Remarks. If this method is unsuccessful, it returns a COM error code.
     *            Negative
     *            A negative return value indicates that the first item should precede the second (pidl1 &lt; pidl2).
     *            Positive
     *            A positive return value indicates that the first item should follow the second (pidl1 &gt; pidl2).
     *            Zero
     *            A return value of zero indicates that the two items are the same (pidl1 = pidl2).
     *            Use the HRESULT_CODE macro to extract the CODE field from the HRESULT, then cast the result as a short.
     *            #define HRESULT_CODE(hr)    ((hr) &amp; 0xFFFF)
     *
     */
    HRESULT CompareIDs(
            WinDef.LPARAM lParam,
            Pointer pidl1,
            Pointer pidl2);


    /**
     * Requests an object that can be used to obtain information from or interact with a folder object.
     *
     * @param hwndOwner
     *            A handle to the owner window. If you have implemented a custom folder view object, your folder view window should be created as a child of hwndOwner.
     *
     * @param riid
     *            A reference to the IID of the interface to retrieve through ppv, typically IID_IShellView.
     *
     * @param ppv
     *            When this method returns successfully, contains the interface pointer requested in riid. This is typically IShellView. See the Remarks section for more details.
     *
     * @return
     *            If this method succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     *
     */
    HRESULT CreateViewObject(
            WinDef.HWND hwndOwner,
            REFIID riid,
            PointerByReference ppv);

    /**
     * Gets the attributes of one or more file or folder objects contained in the object represented by IShellFolder.
     *
     * @param cidl
     *            The number of items from which to retrieve attributes.
     *
     * @param apidl
     *            The address of an array of pointers to ITEMIDLIST structures, each of which uniquely identifies an item relative to the parent folder.
     *            Each ITEMIDLIST structure must contain exactly one SHITEMID structure followed by a terminating zero.
     *
     * @param rgfInOut
     *            Pointer to a single ULONG value that, on entry, contains the bitwise SFGAO attributes that the calling application is requesting. On
     *            exit, this value contains the requested attributes that are common to all of the specified items.
     *
     * @return
     *            If this method succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     *
     */
    HRESULT GetAttributesOf(
            int cidl,
            Pointer apidl,
            IntByReference rgfInOut);

    /**
     * If this method succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     *
     * @param hwndOwner
     *            A handle to the owner window that the client should specify if it displays a dialog box or message box.
     *
     * @param cidl
     *            The number of file objects or subfolders specified in the apidl parameter.
     *
     * @param apidl
     *            The address of an array of pointers to ITEMIDLIST structures, each of which uniquely identifies a file object or subfolder relative to
     *            the parent folder. Each item identifier list must contain exactly one SHITEMID structure followed by a terminating zero.
     *
     * @param riid
     *            A reference to the IID of the interface to retrieve through ppv. This can be any valid interface identifier that can be created for an
     *            item. The most common identifiers used by the Shell are listed in the comments at the end of this reference.
     *
     * @param rgfReserved
     *            Reserved.
     *
     * @param ppv
     *            When this method returns successfully, contains the interface pointer requested in riid.
     *
     * @return
     *            If this method succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     *
     */
    HRESULT GetUIObjectOf(
            WinDef.HWND hwndOwner,
            int cidl,
            Pointer apidl,
            REFIID riid,
            IntByReference rgfReserved,
            PointerByReference ppv);

    /**
     * Retrieves the display name for the specified file object or subfolder.
     *
     * @param pidl
     *            PIDL that uniquely identifies the file object or subfolder relative to the parent folder.
     *
     * @param flags
     *            Flags used to request the type of display name to return. For a list of possible values, see the SHGDNF enumerated type.
     *
     * @param pName
     *            When this method returns, contains a pointer to a STRRET structure in which to return the display name. The type of name returned in this structure can be the requested type, but the Shell folder might return a different type.
     *
     * @return
     *            If this method succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     *            It is the caller's responsibility to free resources allocated by this function.
     *
     */
    HRESULT GetDisplayNameOf(
            Pointer pidl,
            int flags,
            STRRET pName);

    /**
     * Sets the display name of a file object or subfolder, changing the item identifier in the process.
     *
     * @param hwnd
     *            A handle to the owner window of any dialog or message box that the client displays.
     *
     * @param pidl
     *            A pointer to an ITEMIDLIST structure that uniquely identifies the file object or subfolder relative to the parent folder.
     *            The structure must contain exactly one SHITEMID structure followed by a terminating zero.
     *
     * @param pszName
     *            A pointer to a null-terminated string that specifies the new display name.
     *
     * @param uFlags
     *            Flags that indicate the type of name specified by the pszName parameter. For a list of possible values and combinations of values, see SHGDNF.
     *
     * @param ppidlOut
     *            Optional. If specified, the address of a pointer to an ITEMIDLIST structure that receives the ITEMIDLIST of the renamed item. The
     *            caller requests this value by passing a non-null ppidlOut. Implementations of IShellFolder::SetNameOf must return a pointer to the
     *            new ITEMIDLIST in the ppidlOut parameter.
     *
     * @return
     *            If this method succeeds, it returns S_OK. Otherwise, it returns an HRESULT error code.
     *
     */
    HRESULT SetNameOf(
            WinDef.HWND hwnd,
            Pointer pidl,
            String pszName,
            int uFlags,
            PointerByReference ppidlOut);

    /*
    Use this like:
    PointerByReference pbr=new PointerByReference();
    HRESULT result=SomeCOMObject.QueryInterface(IID_ISHELLFOLDER, pbr);
    if(COMUtils.SUCCEEDED(result)) IShellFolder isf=IShellFolder.Converter.PointerToIShellFolder(pbr);
     */

    public static class Converter
    {
        public static IShellFolder PointerToIShellFolder(final PointerByReference ptr)
        {
            final Pointer interfacePointer = ptr.getValue();
            final Pointer vTablePointer = interfacePointer.getPointer(0);
            final Pointer[] vTable = new Pointer[13];
            vTablePointer.read(0, vTable, 0, 13);
            return new IShellFolder() {

                @Override
                public WinNT.HRESULT QueryInterface(REFIID byValue, PointerByReference pointerByReference) {
                    Function f = Function.getFunction(vTable[0], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, byValue, pointerByReference}));
                }

                @Override
                public int AddRef() {
                    Function f = Function.getFunction(vTable[1], Function.ALT_CONVENTION);
                    return f.invokeInt(new Object[]{interfacePointer});
                }

                public int Release() {
                    Function f = Function.getFunction(vTable[2], Function.ALT_CONVENTION);
                    return f.invokeInt(new Object[]{interfacePointer});
                }

                @Override
                public WinNT.HRESULT ParseDisplayName(WinDef.HWND hwnd, Pointer pbc, String pszDisplayName, IntByReference pchEaten, PointerByReference ppidl, IntByReference pdwAttributes) {
                    Function f = Function.getFunction(vTable[3], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, hwnd, pbc, pszDisplayName, pchEaten, ppidl, pdwAttributes}));
                }

                @Override
                public WinNT.HRESULT EnumObjects(WinDef.HWND hwnd, int grfFlags, PointerByReference ppenumIDList) {
                    Function f = Function.getFunction(vTable[4], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT( f.invokeInt(new Object[]{interfacePointer, hwnd, grfFlags, ppenumIDList}));
                }

                public WinNT.HRESULT BindToObject(Pointer pidl, Pointer pbc, REFIID riid, PointerByReference ppv) {
                    Function f = Function.getFunction(vTable[5], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT( f.invokeInt(new Object[]{interfacePointer, pidl, pbc, riid, ppv}));
                }

                @Override
                public HRESULT BindToStorage(Pointer pidl, Pointer pbc, REFIID riid, PointerByReference ppv) {
                    Function f = Function.getFunction(vTable[6], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT( f.invokeInt(new Object[]{interfacePointer, pidl, pbc, riid, ppv}));
                }

                @Override
                public HRESULT CompareIDs(WinDef.LPARAM lParam, Pointer pidl1, Pointer pidl2) {
                    Function f = Function.getFunction(vTable[7], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT( f.invokeInt(new Object[]{interfacePointer, lParam, pidl1, pidl2}));
                }

                @Override
                public HRESULT CreateViewObject(WinDef.HWND hwndOwner, REFIID riid, PointerByReference ppv) {
                    Function f = Function.getFunction(vTable[8], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT( f.invokeInt(new Object[]{interfacePointer, hwndOwner, riid, ppv}));
                }

                @Override
                public HRESULT GetAttributesOf(int cidl, Pointer apidl, IntByReference rgfInOut) {
                    Function f = Function.getFunction(vTable[9], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT( f.invokeInt(new Object[]{interfacePointer, cidl, apidl, rgfInOut}));
                }

                @Override
                public HRESULT GetUIObjectOf(WinDef.HWND hwndOwner, int cidl, Pointer apidl, REFIID riid, IntByReference rgfReserved, PointerByReference ppv) {
                    Function f = Function.getFunction(vTable[10], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT( f.invokeInt(new Object[]{interfacePointer, hwndOwner, cidl, apidl, riid, rgfReserved, ppv}));
                }

                public WinNT.HRESULT GetDisplayNameOf(Pointer pidl, int flags, STRRET pName){
                    Function f = Function.getFunction(vTable[11], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT( f.invokeInt(new Object[]{interfacePointer, pidl, flags, pName}));
                }

                @Override
                public HRESULT SetNameOf(WinDef.HWND hwnd, Pointer pidl, String pszName, int uFlags, PointerByReference ppidlOut) {
                    Function f = Function.getFunction(vTable[12], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT( f.invokeInt(new Object[]{interfacePointer, hwnd, pidl, pszName, uFlags, ppidlOut}));
                }
            };
        }
    }
}
