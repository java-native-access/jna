/* Copyright (c) 2018 Daniel Widdis, All Rights Reserved
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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.Wbemcli.IEnumWbemClassObject;
import com.sun.jna.platform.win32.Wbemcli.IWbemClassObject;
import com.sun.jna.platform.win32.Wbemcli.IWbemLocator;
import com.sun.jna.platform.win32.Wbemcli.IWbemServices;
import com.sun.jna.platform.win32.Wbemcli.WbemcliException;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Utility class providing access to Windows Management Interface (WMI) via COM.
 */
public class WbemcliUtil {
    /**
     * Instance to generate the WmiQuery class.
     */
    public static final WbemcliUtil INSTANCE = new WbemcliUtil();

    /**
     * The default namespace for most WMI queries.
     */
    public static final String DEFAULT_NAMESPACE = "ROOT\\CIMV2";

    // Constant for WMI used often.
    private static final BSTR WQL = OleAuto.INSTANCE.SysAllocString("WQL");

    // Track initialization of COM and Security
    private static boolean comInitialized = false;
    private static boolean securityInitialized = false;

    /**
     * Enum containing the property used for WMI Namespace query.
     */
    private enum NamespaceProperty {
        NAME;
    }

    /**
     * Helper class wrapping information required for a WMI query.
     */
    public class WmiQuery<T extends Enum<T>> {
        private String nameSpace;
        private String wmiClassName;
        private Class<T> propertyEnum;

        /**
         * Instantiate a WmiQuery.
         * 
         * @param nameSpace
         *            The WMI namespace to use.
         * @param wmiClassName
         *            The WMI class to use. Optionally include a WQL WHERE
         *            clause with filters results to properties matching the
         *            input.
         * @param propertyEnum
         *            An enum for type mapping.
         */
        public WmiQuery(String nameSpace, String wmiClassName, Class<T> propertyEnum) {
            super();
            this.nameSpace = nameSpace;
            this.wmiClassName = wmiClassName;
            this.propertyEnum = propertyEnum;
        }

        /**
         * @return The enum containing the properties
         */
        public Class<T> getPropertyEnum() {
            return propertyEnum;
        }

        /**
         * @return The namespace
         */
        public String getNameSpace() {
            return nameSpace;
        }

        /**
         * @param nameSpace
         *            The namespace to set
         */
        public void setNameSpace(String nameSpace) {
            this.nameSpace = nameSpace;
        }

        /**
         * @return The class name
         */
        public String getWmiClassName() {
            return wmiClassName;
        }

        /**
         * @param wmiClassName
         *            The classname to set
         */
        public void setWmiClassName(String wmiClassName) {
            this.wmiClassName = wmiClassName;
        }
    }

    /**
     * Helper class wrapping an EnumMap containing the results of a query.
     */
    public class WmiResult<T extends Enum<T>> {
        private Map<T, List<Object>> propertyMap;
        private Map<T, Integer> vtTypeMap;
        private int resultCount = 0;

        /**
         * @param propertyEnum
         *            The enum associated with this map
         */
        public WmiResult(Class<T> propertyEnum) {
            propertyMap = new EnumMap<T, List<Object>>(propertyEnum);
            vtTypeMap = new EnumMap<T, Integer>(propertyEnum);
            for (T type : propertyEnum.getEnumConstants()) {
                propertyMap.put(type, new ArrayList<Object>());
                vtTypeMap.put(type, Variant.VT_NULL);
            }
        }

        /**
         * Gets a String value from the WmiResult. This is the return type when
         * the WMI result is mapped to a BSTR, including results of UINT64 and
         * DATETIME type which must be further parsed by the user.
         * 
         * @param property
         *            The property (column) to fetch
         * @param index
         *            The index (row) to fetch
         * @return The String containing the specified value, or empty String if
         *         null
         */
        public String getString(T property, int index) {
            Object o = this.propertyMap.get(property).get(index);
            if (o == null) {
                return "";
            } else if (vtTypeMap.get(property).equals(Variant.VT_BSTR)) {
                return (String) o;
            }
            throw new Wbemcli.WbemcliException(property.name() + " is not a String type.", vtTypeMap.get(property));
        }

        /**
         * Gets an Integer value from the WmiResult. This is the return type
         * when the WMI result is mapped to a VT_I4 (4-byte integer) value,
         * including results of UINT32 and UINT16. If an unsigned result is
         * desired, it may require further processing by the user.
         * 
         * @param property
         *            The property (column) to fetch
         * @param index
         *            The index (row) to fetch
         * @return The Integer containing the specified value, or 0 if null
         */
        public Integer getInteger(T property, int index) {
            Object o = this.propertyMap.get(property).get(index);
            if (o == null) {
                return 0;
            } else if (vtTypeMap.get(property).equals(Variant.VT_I4)) {
                return (Integer) o;
            }
            throw new Wbemcli.WbemcliException(property.name() + " is not an Integer type.", vtTypeMap.get(property));
        }

        /**
         * Gets a Short value from the WmiResult. This is the return type when
         * the WMI result is mapped to a VT_I2 (2-byte integer) value. If an
         * unsigned result is desired, it may require further processing by the
         * user.
         * 
         * @param property
         *            The property (column) to fetch
         * @param index
         *            The index (row) to fetch
         * @return The Short containing the specified value, or 0 if null
         */
        public Short getShort(T property, int index) {
            Object o = this.propertyMap.get(property).get(index);
            if (o == null) {
                return 0;
            } else if (vtTypeMap.get(property).equals(Variant.VT_I2)) {
                return (Short) o;
            }
            throw new Wbemcli.WbemcliException(property.name() + " is not a Short type.", vtTypeMap.get(property));
        }

        /**
         * Gets a Byte value from the WmiResult. This is the return type when
         * the WMI result is mapped to a VT_UI1 (1-byte integer) value. If an
         * unsigned result is desired, it may require further processing by the
         * user.
         * 
         * @param property
         *            The property (column) to fetch
         * @param index
         *            The index (row) to fetch
         * @return The Byte containing the specified value, or 0 if null
         */
        public Byte getByte(T property, int index) {
            Object o = this.propertyMap.get(property).get(index);
            if (o == null) {
                return 0;
            } else if (vtTypeMap.get(property).equals(Variant.VT_UI1)) {
                return (Byte) o;
            }
            throw new Wbemcli.WbemcliException(property.name() + " is not a Byte type.", vtTypeMap.get(property));
        }

        /**
         * Gets a Boolean value from the WmiResult. This is the return type when
         * the WMI result is mapped to a VT_BOOL (boolean) value.
         * 
         * @param property
         *            The property (column) to fetch
         * @param index
         *            The index (row) to fetch
         * @return The Boolean containing the specified value, or false if null
         */
        public Boolean getBoolean(T property, int index) {
            Object o = this.propertyMap.get(property).get(index);
            if (o == null) {
                return Boolean.FALSE;
            } else if (vtTypeMap.get(property).equals(Variant.VT_BOOL)) {
                return (Boolean) o;
            }
            throw new Wbemcli.WbemcliException(property.name() + " is not a Boolean type.", vtTypeMap.get(property));
        }

        /**
         * Gets a Float value from the WmiResult. This is the return type when
         * the WMI result is mapped to a VT_R4 (4-byte real) value.
         * 
         * @param property
         *            The property (column) to fetch
         * @param index
         *            The index (row) to fetch
         * @return The Float containing the specified value, or 0.0 if null
         */
        public Float getFloat(T property, int index) {
            Object o = this.propertyMap.get(property).get(index);
            if (o == null) {
                return 0f;
            } else if (vtTypeMap.get(property).equals(Variant.VT_R4)) {
                return (Float) o;
            }
            throw new Wbemcli.WbemcliException(property.name() + " is not a Float type.", vtTypeMap.get(property));
        }

        /**
         * Gets a Double value from the WmiResult. This is the return type when
         * the WMI result is mapped to a VT_R8 (8-byte real) value.
         * 
         * @param property
         *            The property (column) to fetch
         * @param index
         *            The index (row) to fetch
         * @return The Double containing the specified value, or 0.0 if null
         */
        public Double getDouble(T property, int index) {
            Object o = this.propertyMap.get(property).get(index);
            if (o == null) {
                return 0d;
            } else if (vtTypeMap.get(property).equals(Variant.VT_R8)) {
                return (Double) o;
            }
            throw new Wbemcli.WbemcliException(property.name() + " is not a Double type.", vtTypeMap.get(property));
        }

        /**
         * Gets a value from the WmiResult, which may be null. Works with any
         * return type. User must check for null and cast the result.
         * 
         * @param property
         *            The property (column) to fetch
         * @param index
         *            The index (row) to fetch
         * @return The Object containing the specified value, which may be null
         */
        public Object getValue(T property, int index) {
            return this.propertyMap.get(property).get(index);
        }

        /**
         * Adds a value to the WmiResult at the next index for that property
         * 
         * @param vtType
         *            The Variant type of this object
         * @param property
         *            The property (column) to store
         * @param o
         *            The object to store
         */
        private void add(int vtType, T property, Object o) {
            this.propertyMap.get(property).add(o);
            if (vtType != Variant.VT_NULL && this.vtTypeMap.get(property).equals(Variant.VT_NULL)) {
                this.vtTypeMap.put(property, vtType);
            }
        }

        /**
         * @return The number of results in each mapped list
         */
        public int getResultCount() {
            return this.resultCount;
        }

        /**
         * Increment the result count by one.
         */
        public void incrementResultCount() {
            this.resultCount++;
        }
    }
    
    /**
     * Private construtor for cleanup hook
     */
    private WbemcliUtil() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                OleAuto.INSTANCE.SysFreeString(WQL);
            }
        });
    }


    /**
     * Create a WMI Query
     * 
     * @param <T>
     *            an enum
     * @param nameSpace
     *            The WMI Namespace to use
     * @param wmiClassName
     *            The WMI Class to use. May include a WHERE clause with
     *            filtering conditions.
     * @param propertyEnum
     *            An Enum that contains the properties to query
     * @return A WmiQuery object wrapping the parameters
     */
    public static <T extends Enum<T>> WmiQuery<T> createQuery(String nameSpace, String wmiClassName,
            Class<T> propertyEnum) {
        return INSTANCE.new WmiQuery<T>(nameSpace, wmiClassName, propertyEnum);
    }

    /**
     * Create a WMI Query in the default namespace
     * 
     * @param <T>
     *            an enum
     * @param wmiClassName
     *            The WMI Class to use. May include a WHERE clause with
     *            filtering conditions.
     * @param propertyEnum
     *            An Enum that contains the properties to query
     * @return A WmiQuery object wrapping the parameters
     */
    public static <T extends Enum<T>> WmiQuery<T> createQuery(String wmiClassName, Class<T> propertyEnum) {
        return createQuery(DEFAULT_NAMESPACE, wmiClassName, propertyEnum);
    }

    /**
     * Determine if WMI has the requested namespace. Some namespaces only exist
     * on newer versions of Windows.
     *
     * @param namespace
     *            The namespace to test
     * @return true if the namespace exists, false otherwise
     */
    public static boolean hasNamespace(String namespace) {
        // Strip off leading ROOT\ for valid match
        String ns = namespace;
        if (namespace.toUpperCase().startsWith("ROOT\\")) {
            ns = namespace.substring(5);
        }
        // Test
        WmiQuery<NamespaceProperty> namespaceQuery = createQuery("ROOT", "__NAMESPACE", NamespaceProperty.class);
        WmiResult<NamespaceProperty> namespaces = queryWMI(namespaceQuery);
        for (int i = 0; i < namespaces.getResultCount(); i++) {
            if (ns.equalsIgnoreCase(namespaces.getString(NamespaceProperty.NAME, i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Query WMI for values, with no timeout.
     * 
     * @param <T>
     *            an enum
     * @param query
     *            A WmiQuery object encapsulating the namespace, class, and
     *            properties
     * @return a WmiResult object containing the query results, wrapping an
     *         EnumMap
     */
    public static <T extends Enum<T>> WmiResult<T> queryWMI(WmiQuery<T> query) {
        try {
            return queryWMI(query, Wbemcli.WBEM_INFINITE);
        } catch (TimeoutException e) {
            throw new WbemcliException("Got a WMI timeout when infinite wait was specified. This should never happen.",
                    Wbemcli.WBEM_INFINITE);
        }
    }

    /**
     * Query WMI for values, with a specified timeout.
     * 
     * @param <T>
     *            an enum
     * @param query
     *            A WmiQuery object encapsulating the namespace, class, and
     *            properties
     * @param timeout
     *            Number of milliseconds to wait for results before timing out.
     *            If {@link IEnumWbemClassObject#WBEM_INFINITE} (-1), will
     *            always wait for results. If a timeout occurs, throws a
     *            {@link TimeoutException}.
     * @return a WmiResult object containing the query results, wrapping an
     *         EnumMap
     * @throws TimeoutException
     *             if the query times out before completion
     */
    public static <T extends Enum<T>> WmiResult<T> queryWMI(WmiQuery<T> query, int timeout) throws TimeoutException {
        // Idiot check
        if (query.getPropertyEnum().getEnumConstants().length < 1) {
            throw new WbemcliException("The query's property enum has no values.",
                    query.getPropertyEnum().getEnumConstants().length);
        }

        // Connect to the server
        IWbemServices svc = connectServer(query.getNameSpace());

        // Send query
        IEnumWbemClassObject enumerator = selectProperties(svc, query);

        try {
            return enumerateProperties(enumerator, query.getPropertyEnum(), timeout);
        } catch (TimeoutException e) {
            throw new TimeoutException(e.getMessage());
        } finally {
            // Cleanup
            enumerator.Release();
            svc.Release();
        }
    }

    /*
     * Below methods ported from: Getting WMI Data from Local Computer
     * https://docs.microsoft.com/en-us/windows/desktop/WmiSdk/example--getting-
     * wmi-data-from-the-local-computer
     *
     * Steps in the comments correspond to the above link. Steps 1 - 2 are the
     * responsibility of the user. Steps 3 - 5 contain all the steps required to
     * set up and connect to WMI, and steps 6 and 7 are where data is queried
     * and received.
     */

    /**
     * Obtains a locator to the WMI server and connects to the specified
     * namespace
     *
     * @param namespace
     *            The namespace to connect to
     * @return A service representing the connected namespace, which can be
     *         queried. This service may be re-used for multiple queries and
     *         should be released by the user
     */
    public static IWbemServices connectServer(String namespace) {
        PointerByReference pSvc = new PointerByReference();
        // Step 3: ---------------------------------------------------
        // Obtain the initial locator to WMI -------------------------
        IWbemLocator loc = IWbemLocator.create();

        // Step 4: -----------------------------------------------------
        // Connect to WMI through the IWbemLocator::ConnectServer method
        // Connect to the namespace with the current user and obtain pointer
        // pSvc to make IWbemServices calls.
        BSTR namespaceStr = OleAuto.INSTANCE.SysAllocString(namespace);
        HRESULT hres = loc.ConnectServer(namespaceStr, null, null, null, 0, null, null, pSvc);
        OleAuto.INSTANCE.SysFreeString(namespaceStr);
        // Release the locator. If successful, pSvc contains connection
        // information
        loc.Release();
        if (COMUtils.FAILED(hres)) {
            throw new WbemcliException(String.format("Could not connect to namespace %s.", namespace), hres.intValue());
        }

        // Step 5: --------------------------------------------------
        // Set security levels on the proxy -------------------------
        hres = Ole32.INSTANCE.CoSetProxyBlanket(pSvc.getValue(), Ole32.RPC_C_AUTHN_WINNT, Ole32.RPC_C_AUTHZ_NONE, null,
                Ole32.RPC_C_AUTHN_LEVEL_CALL, Ole32.RPC_C_IMP_LEVEL_IMPERSONATE, null, Ole32.EOAC_NONE);
        if (COMUtils.FAILED(hres)) {
            new IWbemServices(pSvc.getValue()).Release();
            throw new WbemcliException("Could not set proxy blanket.", hres.intValue());
        }
        return new IWbemServices(pSvc.getValue());
    }

    /**
     * Selects properties from WMI. Returns immediately (asynchronously), even
     * while results are being retrieved; results may begun to be enumerated in
     * the forward direction only.
     *
     * @param svc
     *            A WbemServices object to make the calls
     * @param query
     *            A WmiQuery object encapsulating the details of the query
     * @return An enumerator to receive the results of the query
     */
    public static <T extends Enum<T>> IEnumWbemClassObject selectProperties(IWbemServices svc, WmiQuery<T> query) {
        PointerByReference pEnumerator = new PointerByReference();
        // Step 6: --------------------------------------------------
        // Use the IWbemServices pointer to make requests of WMI ----
        T[] props = query.getPropertyEnum().getEnumConstants();
        StringBuilder sb = new StringBuilder("SELECT ");
        // We earlier checked for at least one enum constant
        sb.append(props[0].name());
        for (int i = 1; i < props.length; i++) {
            sb.append(',').append(props[i].name());
        }
        sb.append(" FROM ").append(query.getWmiClassName());
        // Send the query. The flags allow us to return immediately and begin
        // enumerating in the forward direction as results come in.
        BSTR queryStr = OleAuto.INSTANCE.SysAllocString(sb.toString().replaceAll("\\\\", "\\\\\\\\"));
        HRESULT hres = svc.ExecQuery(WQL, queryStr,
                Wbemcli.WBEM_FLAG_FORWARD_ONLY | Wbemcli.WBEM_FLAG_RETURN_IMMEDIATELY, null, pEnumerator);
        OleAuto.INSTANCE.SysFreeString(queryStr);
        if (COMUtils.FAILED(hres)) {
            svc.Release();
            throw new WbemcliException(String.format("Query '%s' failed.", sb.toString()), hres.intValue());
        }
        return new IEnumWbemClassObject(pEnumerator.getValue());
    }

    /*-
     * The following table maps WMI return types (CIM type) to the VT type of
     * the returned VARIANT. 
     * 
     * CIM type  |  VT type
     * ----------|----------
     * BOOLEAN   |  VT_BOOL 
     * ----------|----------
     * UINT8     |  VT_UI1 
     * ----------|----------
     * SINT8     |  VT_I2 
     * SINT16    |  VT_I2 
     * CHAR16    |  VT_I2
     * ----------|----------
     * UINT16    |  VT_I4
     * SINT32    |  VT_I4
     * UINT32    |  VT_I4
     * ----------|----------
     * SINT64    |  VT_BSTR
     * UINT64    |  VT_BSTR
     * DATETIME  |  VT_BSTR
     * REFERENCE |  VT_BSTR
     * STRING    |  VT_BSTR
     * ----------|----------
     * REAL32    |  VT_R4
     * ----------|----------
     * REAL64    |  VT_R8
     * ----------|----------
     * OBJECT    |  VT_UNKNOWN (not implemented)
     */

    /**
     * Enumerate the results of a WMI query. This method is called while results
     * are still being retrieved and may iterate in the forward direction only.
     * 
     * @param enumerator
     *            The enumerator with the results
     * @param propertyEnum
     *            The enum containing the properties to enumerate, which are the
     *            keys to the WmiResult map
     * @param timeout
     *            Number of milliseconds to wait for results before timing out.
     *            If {@link IEnumWbemClassObject#WBEM_INFINITE} (-1), will
     *            always wait for results.
     * @return A WmiResult object encapsulating an EnumMap which will hold the
     *         results.
     * @throws TimeoutException
     *             if the query times out before completion
     */
    public static <T extends Enum<T>> WmiResult<T> enumerateProperties(IEnumWbemClassObject enumerator,
            Class<T> propertyEnum, int timeout) throws TimeoutException {
        WmiResult<T> values = INSTANCE.new WmiResult<T>(propertyEnum);
        // Step 7: -------------------------------------------------
        // Get the data from the query in step 6 -------------------
        PointerByReference pclsObj = new PointerByReference();
        IntByReference uReturn = new IntByReference(0);
        Map<T, WString> wstrMap = new HashMap<T, WString>();
        HRESULT hres = null;
        for (T property : propertyEnum.getEnumConstants()) {
            wstrMap.put(property, new WString(property.name()));
        }
        while (enumerator.getPointer() != Pointer.NULL) {
            // Enumerator will be released by calling method so no need to
            // release it here.
            hres = enumerator.Next(timeout, 1, pclsObj, uReturn);
            // Enumeration complete or no more data; we're done, exit the loop
            if (hres.intValue() == Wbemcli.WBEM_S_FALSE || hres.intValue() == Wbemcli.WBEM_S_NO_MORE_DATA) {
                break;
            }
            // Throw exception to notify user of timeout
            if (hres.intValue() == Wbemcli.WBEM_S_TIMEDOUT) {
                throw new TimeoutException("No results after " + timeout + " ms.");
            }
            // Other exceptions here.
            if (COMUtils.FAILED(hres)) {
                throw new WbemcliException("Failed to enumerate results.", hres.intValue());
            }

            VARIANT.ByReference pVal = new VARIANT.ByReference();

            // Get the value of the properties
            IWbemClassObject clsObj = new IWbemClassObject(pclsObj.getValue());
            for (T property : propertyEnum.getEnumConstants()) {
                clsObj.Get(wstrMap.get(property), 0, pVal, null, null);
                int type = (pVal.getValue() == null ? Variant.VT_NULL : pVal.getVarType()).intValue();
                switch (type) {
                case Variant.VT_BSTR:
                    values.add(type, property, pVal.stringValue());
                    break;
                case Variant.VT_I4:
                    values.add(type, property, pVal.intValue());
                    break;
                case Variant.VT_UI1:
                    values.add(type, property, pVal.byteValue());
                    break;
                case Variant.VT_I2:
                    values.add(type, property, pVal.shortValue());
                    break;
                case Variant.VT_BOOL:
                    values.add(type, property, pVal.booleanValue());
                    break;
                case Variant.VT_R4:
                    values.add(type, property, pVal.floatValue());
                    break;
                case Variant.VT_R8:
                    values.add(type, property, pVal.doubleValue());
                    break;
                case Variant.VT_NULL:
                    values.add(type, property, null);
                    break;
                // Unimplemented type. User must cast
                default:
                    values.add(type, property, pVal.getValue());
                }
                OleAuto.INSTANCE.VariantClear(pVal);
            }
            clsObj.Release();

            values.incrementResultCount();
        }
        return values;
    }
}
