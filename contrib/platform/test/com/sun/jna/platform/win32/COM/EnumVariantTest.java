package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.util.IDispatch;
import com.sun.jna.platform.win32.COM.util.IRawDispatchHandle;
import com.sun.jna.platform.win32.COM.util.ObjectFactory;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant.VARIANT;
import static com.sun.jna.platform.win32.Variant.VARIANT.VARIANT_MISSING;
import com.sun.jna.ptr.PointerByReference;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

public class EnumVariantTest {

    private static ObjectFactory fact;
    private static ISWbemPropertySet propertySet;
    
    @BeforeClass
    public static void before() {
        /**
         * Test assumption:
         * 
         * - One of the Instances of Win32_CurrentTime is queried from WMI
         * - it is expected, that this instance does not change its shape
         * - stays available for the duration of this test
         * - will always be available
         * - the object has at least one property that is non-null
         */
        Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
        fact = new ObjectFactory();
        SWbemLocator objSWbemLocator = fact.createObject(SWbemLocator.class);
        ISWbemServices objSWbemServices = objSWbemLocator.ConnectServer(".", "root/cimv2", VARIANT_MISSING, VARIANT_MISSING, VARIANT_MISSING, VARIANT_MISSING, VARIANT_MISSING, VARIANT_MISSING);
        ISWbemObjectSet colSWbemObjectSet = objSWbemServices.InstancesOf("Win32_CurrentTime", VARIANT_MISSING, VARIANT_MISSING);
        ISWbemObject obj = colSWbemObjectSet.ItemIndex(0);
        propertySet = obj.getProperties_();
    }

    @AfterClass
    public static void afterClass() {
        fact.disposeAll();
        Ole32.INSTANCE.CoUninitialize();
    }
    
    @Test
    public void testSingleIteration() {
        // Test reading the Enumeration one-by-one
        int elementsRead = 0;
        EnumVariant ev = fetchEnumVariant(propertySet);
        try {
            for(VARIANT[] variants = ev.Next(1); variants.length > 0; variants = ev.Next(1)) {
                for (VARIANT v : variants) {
                    ISWbemProperty property = fact.createProxy(ISWbemProperty.class, (com.sun.jna.platform.win32.COM.IDispatch) v.getValue());
                    OleAuto.INSTANCE.VariantClear(v);
                    if (property.getName() != null && (!property.getName().isEmpty())
                            && property.getValue() != null) {
                        elementsRead++;
                    }
                }              
            }
        } finally {
            ev.Release();
        }
        assertTrue("No Property read", elementsRead > 0);
    }
    
    @Test
    public void testBlockIteration() {
        // Test reading the Enumeration in blocks of ten elements
        int elementsRead = 0;
        boolean readMoreThanOneItem = false;
        EnumVariant ev = fetchEnumVariant(propertySet);
        try {
            for(VARIANT[] variants = ev.Next(10); variants.length > 0; variants = ev.Next(10)) {
                if(! readMoreThanOneItem) {
                    readMoreThanOneItem = variants.length > 1;
                }
                for (VARIANT v : variants) {
                    ISWbemProperty property = fact.createProxy(ISWbemProperty.class, (com.sun.jna.platform.win32.COM.IDispatch) v.getValue());
                    OleAuto.INSTANCE.VariantClear(v);
                    if (property.getName() != null && (!property.getName().isEmpty())
                            && property.getValue() != null) {
                        elementsRead++;
                    }
                }              
            }
        } finally {
            ev.Release();
        }
        assertTrue("No Property read", elementsRead > 0);
        assertTrue("Batch fetching failed", readMoreThanOneItem);
    }
    
    @Test
    public void testReset() {
        // Test resetting the Enumeration. The enum is iterated twice with
        // a Reset inbetween. It is expected, that the property count is the
        // same before and after the reset
        int elementsRead = 0;
        int elementsRead2 = 0;
        EnumVariant ev = fetchEnumVariant(propertySet);
        try {
            for(VARIANT[] variants = ev.Next(10); variants.length > 0; variants = ev.Next(10)) {
                for (VARIANT v : variants) {
                    ISWbemProperty property = fact.createProxy(ISWbemProperty.class, (com.sun.jna.platform.win32.COM.IDispatch) v.getValue());
                    OleAuto.INSTANCE.VariantClear(v);
                    if (property.getName() != null && (!property.getName().isEmpty())
                            && property.getValue() != null) {
                        elementsRead++;
                    }
                }              
            }
            ev.Reset();
            for (VARIANT[] variants = ev.Next(10); variants.length > 0; variants = ev.Next(10)) {
                for (VARIANT v : variants) {
                    ISWbemProperty property = fact.createProxy(ISWbemProperty.class, (com.sun.jna.platform.win32.COM.IDispatch) v.getValue());
                    OleAuto.INSTANCE.VariantClear(v);
                    if (property.getName() != null && (!property.getName().isEmpty())
                            && property.getValue() != null) {
                        elementsRead2++;
                    }
                }
            }
        } finally {
            ev.Release();
        }
        assertTrue("Reset failed", elementsRead == elementsRead2);
    }
    
    @Test
    public void testClone() {
        // Test cloning the Enumeration. The enum is cloned and both are iterated
        // It is expected, that the property count is the same for both
        int elementsRead = 0;
        int elementsRead2 = 0;
        EnumVariant ev = fetchEnumVariant(propertySet);
        EnumVariant ev2 = ev.Clone();
        try {
            for(VARIANT[] variants = ev.Next(10); variants.length > 0; variants = ev.Next(10)) {
                for (VARIANT v : variants) {
                    OleAuto.INSTANCE.VariantClear(v);
                    elementsRead++;
                }              
            }
            for (VARIANT[] variants = ev2.Next(10); variants.length > 0; variants = ev2.Next(10)) {
                for (VARIANT v : variants) {
                    OleAuto.INSTANCE.VariantClear(v);
                    elementsRead2++;
                }
            }
        } finally {
            ev2.Release();
            ev.Release();
        }
        assertEquals("Reset failed", elementsRead, elementsRead2);
    }
    
    @Test
    public void testSkip() {
        // Test skipping in the Enumeration. The enum is cloned and the first
        // iteration is skipped 5 elements. Both are iterated
        // It is expected, that the property count for the second enum is 
        // 5 elements larger than the first
        int elementsRead = 5;
        int elementsRead2 = 0;
        EnumVariant ev = fetchEnumVariant(propertySet);
        EnumVariant ev2 = ev.Clone();
        try {
            ev.Skip(5);
            for(VARIANT[] variants = ev.Next(10); variants.length > 0; variants = ev.Next(10)) {
                for (VARIANT v : variants) {
                    OleAuto.INSTANCE.VariantClear(v);
                    elementsRead++;
                }              
            }
            for (VARIANT[] variants = ev2.Next(10); variants.length > 0; variants = ev2.Next(10)) {
                for (VARIANT v : variants) {
                    OleAuto.INSTANCE.VariantClear(v);
                    elementsRead2++;
                }
            }
        } finally {
            ev2.Release();
            ev.Release();
        }
        assertEquals("Skip failed", elementsRead, elementsRead2);
    }
    
    @Test
    public void testForLoopIteration() {
        // Test iteration with an enhanced for loop
        int elementsRead = 0;
        for (VARIANT v : IComEnumVariantIterator.wrap(propertySet)) {
            ISWbemProperty property = fact.createProxy(ISWbemProperty.class, (com.sun.jna.platform.win32.COM.IDispatch) v.getValue());
            OleAuto.INSTANCE.VariantClear(v);
            if(property.getName() != null && (! property.getName().isEmpty()) &&
                    property.getValue() != null ) {
                elementsRead++;
            }
        }
        assertTrue("No Property read", elementsRead > 0);
    }

    private static EnumVariant fetchEnumVariant(IDispatch object) {
        PointerByReference pbr = new PointerByReference();
        IUnknown unknwn = object.getProperty(IUnknown.class, OaIdl.DISPID_NEWENUM);
        unknwn.QueryInterface(EnumVariant.REFIID, pbr);
        // QueryInterace AddRefs the interface and we are done with the Unknown instance
        unknwn.Release();
        return new EnumVariant(pbr.getValue());
    }

    //
    //
    // ------------------- generated interfaces --------------
    //
    // The following codes were generated from the type library
    //
    
    /**
     * Used to obtain Namespace connections
     *
     * <p>
     * uuid({76A64158-CB41-11D1-8B02-00600806D9B6})</p>
     * <p>
     * interface(ISWbemLocator)</p>
     */
    @ComObject(clsId = "{76A64158-CB41-11D1-8B02-00600806D9B6}")
    public interface SWbemLocator extends IUnknown,
            ISWbemLocator {

    }

    /**
     * Used to obtain Namespace connections
     *
     * <p>
     * uuid({76A6415B-CB41-11D1-8B02-00600806D9B6})</p>
     */
    @ComInterface(iid = "{76A6415B-CB41-11D1-8B02-00600806D9B6}")
    public interface ISWbemLocator extends IUnknown, IRawDispatchHandle, IDispatch {

        /**
         * Connect to a Namespace
         *
         * <p>
         * id(0x1)</p>
         * <p>
         * vtableId(7)</p>
         *
         * @param strServer [in, optional] {@code String}
         * @param strNamespace [in, optional] {@code String}
         * @param strUser [in, optional] {@code String}
         * @param strPassword [in, optional] {@code String}
         * @param strLocale [in, optional] {@code String}
         * @param strAuthority [in, optional] {@code String}
         * @param iSecurityFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "ConnectServer", dispId = 0x1)
        ISWbemServices ConnectServer(Object strServer,
                Object strNamespace,
                Object strUser,
                Object strPassword,
                Object strLocale,
                Object strAuthority,
                Object iSecurityFlags,
                Object objWbemNamedValueSet);

    }

    /**
     * A collection of Classes or Instances
     *
     * <p>
     * uuid({76A6415F-CB41-11D1-8B02-00600806D9B6})</p>
     */
    @ComInterface(iid = "{76A6415F-CB41-11D1-8B02-00600806D9B6}")
    public interface ISWbemObjectSet extends IUnknown, IRawDispatchHandle, IDispatch {

        /**
         * Get an Object with a specific path from this collection
         *
         * <p>
         * id(0x0)</p>
         * <p>
         * vtableId(8)</p>
         *
         * @param strObjectPath [in] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         */
        @ComMethod(name = "Item", dispId = 0x0)
        ISWbemObject Item(String strObjectPath,
                Object iFlags);

        /**
         * The number of items in this collection
         *
         * <p>
         * id(0x1)</p>
         * <p>
         * vtableId(9)</p>
         */
        @ComProperty(name = "Count", dispId = 0x1)
        Integer getCount();

        /**
         * Get an Object with a specific index from this collection
         *
         * <p>
         * id(0x5)</p>
         * <p>
         * vtableId(11)</p>
         *
         * @param lIndex [in] {@code Integer}
         */
        @ComMethod(name = "ItemIndex", dispId = 0x5)
        ISWbemObject ItemIndex(Integer lIndex);

    }

    /**
     * A Class or Instance
     *
     * <p>
     * uuid({76A6415A-CB41-11D1-8B02-00600806D9B6})</p>
     */
    @ComInterface(iid = "{76A6415A-CB41-11D1-8B02-00600806D9B6}")
    public interface ISWbemObject extends IUnknown, IRawDispatchHandle, IDispatch {

        /**
         * Save this Object asynchronously
         *
         * <p>
         * id(0x2)</p>
         * <p>
         * vtableId(8)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "PutAsync_", dispId = 0x2)
        void PutAsync_(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Delete this Object
         *
         * <p>
         * id(0x3)</p>
         * <p>
         * vtableId(9)</p>
         *
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "Delete_", dispId = 0x3)
        void Delete_(Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Delete this Object asynchronously
         *
         * <p>
         * id(0x4)</p>
         * <p>
         * vtableId(10)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "DeleteAsync_", dispId = 0x4)
        void DeleteAsync_(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Return all instances of this Class
         *
         * <p>
         * id(0x5)</p>
         * <p>
         * vtableId(11)</p>
         *
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "Instances_", dispId = 0x5)
        ISWbemObjectSet Instances_(Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Return all instances of this Class asynchronously
         *
         * <p>
         * id(0x6)</p>
         * <p>
         * vtableId(12)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "InstancesAsync_", dispId = 0x6)
        void InstancesAsync_(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Enumerate subclasses of this Class
         *
         * <p>
         * id(0x7)</p>
         * <p>
         * vtableId(13)</p>
         *
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "Subclasses_", dispId = 0x7)
        ISWbemObjectSet Subclasses_(Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Enumerate subclasses of this Class asynchronously
         *
         * <p>
         * id(0x8)</p>
         * <p>
         * vtableId(14)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "SubclassesAsync_", dispId = 0x8)
        void SubclassesAsync_(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Get the Associators of this Object
         *
         * <p>
         * id(0x9)</p>
         * <p>
         * vtableId(15)</p>
         *
         * @param strAssocClass [in, optional] {@code String}
         * @param strResultClass [in, optional] {@code String}
         * @param strResultRole [in, optional] {@code String}
         * @param strRole [in, optional] {@code String}
         * @param bClassesOnly [in, optional] {@code Boolean}
         * @param bSchemaOnly [in, optional] {@code Boolean}
         * @param strRequiredAssocQualifier [in, optional] {@code String}
         * @param strRequiredQualifier [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "Associators_", dispId = 0x9)
        ISWbemObjectSet Associators_(Object strAssocClass,
                Object strResultClass,
                Object strResultRole,
                Object strRole,
                Object bClassesOnly,
                Object bSchemaOnly,
                Object strRequiredAssocQualifier,
                Object strRequiredQualifier,
                Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Get the Associators of this Object asynchronously
         *
         * <p>
         * id(0xa)</p>
         * <p>
         * vtableId(16)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param strAssocClass [in, optional] {@code String}
         * @param strResultClass [in, optional] {@code String}
         * @param strResultRole [in, optional] {@code String}
         * @param strRole [in, optional] {@code String}
         * @param bClassesOnly [in, optional] {@code Boolean}
         * @param bSchemaOnly [in, optional] {@code Boolean}
         * @param strRequiredAssocQualifier [in, optional] {@code String}
         * @param strRequiredQualifier [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "AssociatorsAsync_", dispId = 0xa)
        void AssociatorsAsync_(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                Object strAssocClass,
                Object strResultClass,
                Object strResultRole,
                Object strRole,
                Object bClassesOnly,
                Object bSchemaOnly,
                Object strRequiredAssocQualifier,
                Object strRequiredQualifier,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Get the References to this Object
         *
         * <p>
         * id(0xb)</p>
         * <p>
         * vtableId(17)</p>
         *
         * @param strResultClass [in, optional] {@code String}
         * @param strRole [in, optional] {@code String}
         * @param bClassesOnly [in, optional] {@code Boolean}
         * @param bSchemaOnly [in, optional] {@code Boolean}
         * @param strRequiredQualifier [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "References_", dispId = 0xb)
        ISWbemObjectSet References_(Object strResultClass,
                Object strRole,
                Object bClassesOnly,
                Object bSchemaOnly,
                Object strRequiredQualifier,
                Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Get the References to this Object asynchronously
         *
         * <p>
         * id(0xc)</p>
         * <p>
         * vtableId(18)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param strResultClass [in, optional] {@code String}
         * @param strRole [in, optional] {@code String}
         * @param bClassesOnly [in, optional] {@code Boolean}
         * @param bSchemaOnly [in, optional] {@code Boolean}
         * @param strRequiredQualifier [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "ReferencesAsync_", dispId = 0xc)
        void ReferencesAsync_(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                Object strResultClass,
                Object strRole,
                Object bClassesOnly,
                Object bSchemaOnly,
                Object strRequiredQualifier,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Execute a Method of this Object
         *
         * <p>
         * id(0xd)</p>
         * <p>
         * vtableId(19)</p>
         *
         * @param strMethodName [in] {@code String}
         * @param objWbemInParameters [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "ExecMethod_", dispId = 0xd)
        ISWbemObject ExecMethod_(String strMethodName,
                Object objWbemInParameters,
                Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Execute a Method of this Object asynchronously
         *
         * <p>
         * id(0xe)</p>
         * <p>
         * vtableId(20)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param strMethodName [in] {@code String}
         * @param objWbemInParameters [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "ExecMethodAsync_", dispId = 0xe)
        void ExecMethodAsync_(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                String strMethodName,
                Object objWbemInParameters,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Clone this Object
         *
         * <p>
         * id(0xf)</p>
         * <p>
         * vtableId(21)</p>
         */
        @ComMethod(name = "Clone_", dispId = 0xf)
        ISWbemObject Clone_();

        /**
         * Get the MOF text of this Object
         *
         * <p>
         * id(0x10)</p>
         * <p>
         * vtableId(22)</p>
         *
         * @param iFlags [in, optional] {@code Integer}
         */
        @ComMethod(name = "GetObjectText_", dispId = 0x10)
        String GetObjectText_(Object iFlags);

        /**
         * Create a subclass of this Object
         *
         * <p>
         * id(0x11)</p>
         * <p>
         * vtableId(23)</p>
         *
         * @param iFlags [in, optional] {@code Integer}
         */
        @ComMethod(name = "SpawnDerivedClass_", dispId = 0x11)
        ISWbemObject SpawnDerivedClass_(Object iFlags);

        /**
         * Create an Instance of this Object
         *
         * <p>
         * id(0x12)</p>
         * <p>
         * vtableId(24)</p>
         *
         * @param iFlags [in, optional] {@code Integer}
         */
        @ComMethod(name = "SpawnInstance_", dispId = 0x12)
        ISWbemObject SpawnInstance_(Object iFlags);

        /**
         * Compare this Object with another
         *
         * <p>
         * id(0x13)</p>
         * <p>
         * vtableId(25)</p>
         *
         * @param objWbemObject [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param iFlags [in, optional] {@code Integer}
         */
        @ComMethod(name = "CompareTo_", dispId = 0x13)
        Boolean CompareTo_(com.sun.jna.platform.win32.COM.util.IDispatch objWbemObject,
                Object iFlags);

        /**
         * The collection of Properties of this Object
         *
         * <p>
         * id(0x15)</p>
         * <p>
         * vtableId(27)</p>
         */
        @ComProperty(name = "Properties_", dispId = 0x15)
        ISWbemPropertySet getProperties_();

        /**
         * An array of strings describing the class derivation heirarchy, in
         * most-derived-from order (the first element in the array defines the
         * superclass and the last element defines the dynasty class).
         *
         * <p>
         * id(0x17)</p>
         * <p>
         * vtableId(29)</p>
         */
        @ComProperty(name = "Derivation_", dispId = 0x17)
        Object getDerivation_();

        
        /**
         * The path of this Object
         *
         * <p>
         * id(0x18)</p>
         * <p>
         * vtableId(30)</p>
         */
        @ComProperty(name = "Path_", dispId = 0x18)
        ISWbemObjectPath getPath_();
    }

    /**
     * A collection of Properties
     *
     * <p>
     * uuid({DEA0A7B2-D4BA-11D1-8B09-00600806D9B6})</p>
     */
    @ComInterface(iid = "{DEA0A7B2-D4BA-11D1-8B09-00600806D9B6}")
    public interface ISWbemPropertySet extends IUnknown, IRawDispatchHandle, IDispatch {

        /**
         * Get a named Property from this collection
         *
         * <p>
         * id(0x0)</p>
         * <p>
         * vtableId(8)</p>
         *
         * @param strName [in] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         */
        @ComMethod(name = "Item", dispId = 0x0)
        ISWbemProperty Item(String strName,
                Object iFlags);

        /**
         * The number of items in this collection
         *
         * <p>
         * id(0x1)</p>
         * <p>
         * vtableId(9)</p>
         */
        @ComProperty(name = "Count", dispId = 0x1)
        Integer getCount();

        /**
         * Remove a Property from this collection
         *
         * <p>
         * id(0x3)</p>
         * <p>
         * vtableId(11)</p>
         *
         * @param strName [in] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         */
        @ComMethod(name = "Remove", dispId = 0x3)
        void Remove(String strName,
                Object iFlags);

    }

    /**
     * A Property
     *
     * <p>
     * uuid({1A388F98-D4BA-11D1-8B09-00600806D9B6})</p>
     */
    @ComInterface(iid = "{1A388F98-D4BA-11D1-8B09-00600806D9B6}")
    public interface ISWbemProperty extends IUnknown, IRawDispatchHandle, IDispatch {

        /**
         * The value of this Property
         *
         * <p>
         * id(0x0)</p>
         * <p>
         * vtableId(7)</p>
         */
        @ComProperty(name = "Value", dispId = 0x0)
        Object getValue();

        /**
         * The value of this Property
         *
         * <p>
         * id(0x0)</p>
         * <p>
         * vtableId(8)</p>
         *
         * @param param0 [in] {@code Object}
         */
        @ComProperty(name = "Value", dispId = 0x0)
        void setValue(Object param0);

        /**
         * The name of this Property
         *
         * <p>
         * id(0x1)</p>
         * <p>
         * vtableId(9)</p>
         */
        @ComProperty(name = "Name", dispId = 0x1)
        String getName();

        /**
         * Indicates whether this Property is local or propagated
         *
         * <p>
         * id(0x2)</p>
         * <p>
         * vtableId(10)</p>
         */
        @ComProperty(name = "IsLocal", dispId = 0x2)
        Boolean getIsLocal();

        /**
         * The originating class of this Property
         *
         * <p>
         * id(0x3)</p>
         * <p>
         * vtableId(11)</p>
         */
        @ComProperty(name = "Origin", dispId = 0x3)
        String getOrigin();

        /**
         * Indicates whether this Property is an array type
         *
         * <p>
         * id(0x6)</p>
         * <p>
         * vtableId(14)</p>
         */
        @ComProperty(name = "IsArray", dispId = 0x6)
        Boolean getIsArray();

    }

    /**
     * A connection to a Namespace
     *
     * <p>
     * uuid({76A6415C-CB41-11D1-8B02-00600806D9B6})</p>
     */
    @ComInterface(iid = "{76A6415C-CB41-11D1-8B02-00600806D9B6}")
    public interface ISWbemServices extends IUnknown, IRawDispatchHandle, IDispatch {

        /**
         * Get a single Class or Instance
         *
         * <p>
         * id(0x1)</p>
         * <p>
         * vtableId(7)</p>
         *
         * @param strObjectPath [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "Get", dispId = 0x1)
        ISWbemObject Get(Object strObjectPath,
                Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Get a single Class or Instance asynchronously
         *
         * <p>
         * id(0x2)</p>
         * <p>
         * vtableId(8)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param strObjectPath [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "GetAsync", dispId = 0x2)
        void GetAsync(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                Object strObjectPath,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Delete a Class or Instance
         *
         * <p>
         * id(0x3)</p>
         * <p>
         * vtableId(9)</p>
         *
         * @param strObjectPath [in] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "Delete", dispId = 0x3)
        void Delete(String strObjectPath,
                Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Delete a Class or Instance asynchronously
         *
         * <p>
         * id(0x4)</p>
         * <p>
         * vtableId(10)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param strObjectPath [in] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "DeleteAsync", dispId = 0x4)
        void DeleteAsync(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                String strObjectPath,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Enumerate the Instances of a Class
         *
         * <p>
         * id(0x5)</p>
         * <p>
         * vtableId(11)</p>
         *
         * @param strClass [in] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "InstancesOf", dispId = 0x5)
        ISWbemObjectSet InstancesOf(String strClass,
                Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Enumerate the Instances of a Class asynchronously
         *
         * <p>
         * id(0x6)</p>
         * <p>
         * vtableId(12)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param strClass [in] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "InstancesOfAsync", dispId = 0x6)
        void InstancesOfAsync(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                String strClass,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Enumerate the subclasses of a Class
         *
         * <p>
         * id(0x7)</p>
         * <p>
         * vtableId(13)</p>
         *
         * @param strSuperclass [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "SubclassesOf", dispId = 0x7)
        ISWbemObjectSet SubclassesOf(Object strSuperclass,
                Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Enumerate the subclasses of a Class asynchronously
         *
         * <p>
         * id(0x8)</p>
         * <p>
         * vtableId(14)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param strSuperclass [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "SubclassesOfAsync", dispId = 0x8)
        void SubclassesOfAsync(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                Object strSuperclass,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Execute a Query
         *
         * <p>
         * id(0x9)</p>
         * <p>
         * vtableId(15)</p>
         *
         * @param strQuery [in] {@code String}
         * @param strQueryLanguage [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "ExecQuery", dispId = 0x9)
        ISWbemObjectSet ExecQuery(String strQuery,
                Object strQueryLanguage,
                Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Execute an asynchronous Query
         *
         * <p>
         * id(0xa)</p>
         * <p>
         * vtableId(16)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param strQuery [in] {@code String}
         * @param strQueryLanguage [in, optional] {@code String}
         * @param lFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "ExecQueryAsync", dispId = 0xa)
        void ExecQueryAsync(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                String strQuery,
                Object strQueryLanguage,
                Object lFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Get the Associators of a class or instance
         *
         * <p>
         * id(0xb)</p>
         * <p>
         * vtableId(17)</p>
         *
         * @param strObjectPath [in] {@code String}
         * @param strAssocClass [in, optional] {@code String}
         * @param strResultClass [in, optional] {@code String}
         * @param strResultRole [in, optional] {@code String}
         * @param strRole [in, optional] {@code String}
         * @param bClassesOnly [in, optional] {@code Boolean}
         * @param bSchemaOnly [in, optional] {@code Boolean}
         * @param strRequiredAssocQualifier [in, optional] {@code String}
         * @param strRequiredQualifier [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "AssociatorsOf", dispId = 0xb)
        ISWbemObjectSet AssociatorsOf(String strObjectPath,
                Object strAssocClass,
                Object strResultClass,
                Object strResultRole,
                Object strRole,
                Object bClassesOnly,
                Object bSchemaOnly,
                Object strRequiredAssocQualifier,
                Object strRequiredQualifier,
                Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Get the Associators of a class or instance asynchronously
         *
         * <p>
         * id(0xc)</p>
         * <p>
         * vtableId(18)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param strObjectPath [in] {@code String}
         * @param strAssocClass [in, optional] {@code String}
         * @param strResultClass [in, optional] {@code String}
         * @param strResultRole [in, optional] {@code String}
         * @param strRole [in, optional] {@code String}
         * @param bClassesOnly [in, optional] {@code Boolean}
         * @param bSchemaOnly [in, optional] {@code Boolean}
         * @param strRequiredAssocQualifier [in, optional] {@code String}
         * @param strRequiredQualifier [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "AssociatorsOfAsync", dispId = 0xc)
        void AssociatorsOfAsync(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                String strObjectPath,
                Object strAssocClass,
                Object strResultClass,
                Object strResultRole,
                Object strRole,
                Object bClassesOnly,
                Object bSchemaOnly,
                Object strRequiredAssocQualifier,
                Object strRequiredQualifier,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Get the References to a class or instance
         *
         * <p>
         * id(0xd)</p>
         * <p>
         * vtableId(19)</p>
         *
         * @param strObjectPath [in] {@code String}
         * @param strResultClass [in, optional] {@code String}
         * @param strRole [in, optional] {@code String}
         * @param bClassesOnly [in, optional] {@code Boolean}
         * @param bSchemaOnly [in, optional] {@code Boolean}
         * @param strRequiredQualifier [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "ReferencesTo", dispId = 0xd)
        ISWbemObjectSet ReferencesTo(String strObjectPath,
                Object strResultClass,
                Object strRole,
                Object bClassesOnly,
                Object bSchemaOnly,
                Object strRequiredQualifier,
                Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Get the References to a class or instance asynchronously
         *
         * <p>
         * id(0xe)</p>
         * <p>
         * vtableId(20)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param strObjectPath [in] {@code String}
         * @param strResultClass [in, optional] {@code String}
         * @param strRole [in, optional] {@code String}
         * @param bClassesOnly [in, optional] {@code Boolean}
         * @param bSchemaOnly [in, optional] {@code Boolean}
         * @param strRequiredQualifier [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "ReferencesToAsync", dispId = 0xe)
        void ReferencesToAsync(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                String strObjectPath,
                Object strResultClass,
                Object strRole,
                Object bClassesOnly,
                Object bSchemaOnly,
                Object strRequiredQualifier,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Execute an asynchronous Query to receive Notifications
         *
         * <p>
         * id(0x10)</p>
         * <p>
         * vtableId(22)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param strQuery [in] {@code String}
         * @param strQueryLanguage [in, optional] {@code String}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "ExecNotificationQueryAsync", dispId = 0x10)
        void ExecNotificationQueryAsync(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                String strQuery,
                Object strQueryLanguage,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

        /**
         * Execute a Method
         *
         * <p>
         * id(0x11)</p>
         * <p>
         * vtableId(23)</p>
         *
         * @param strObjectPath [in] {@code String}
         * @param strMethodName [in] {@code String}
         * @param objWbemInParameters [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "ExecMethod", dispId = 0x11)
        ISWbemObject ExecMethod(String strObjectPath,
                String strMethodName,
                Object objWbemInParameters,
                Object iFlags,
                Object objWbemNamedValueSet);

        /**
         * Execute a Method asynchronously
         *
         * <p>
         * id(0x12)</p>
         * <p>
         * vtableId(24)</p>
         *
         * @param objWbemSink [in]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param strObjectPath [in] {@code String}
         * @param strMethodName [in] {@code String}
         * @param objWbemInParameters [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param iFlags [in, optional] {@code Integer}
         * @param objWbemNamedValueSet [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         * @param objWbemAsyncContext [in, optional]
         * {@code com.sun.jna.platform.win32.COM.util.IDispatch}
         */
        @ComMethod(name = "ExecMethodAsync", dispId = 0x12)
        void ExecMethodAsync(com.sun.jna.platform.win32.COM.util.IDispatch objWbemSink,
                String strObjectPath,
                String strMethodName,
                Object objWbemInParameters,
                Object iFlags,
                Object objWbemNamedValueSet,
                Object objWbemAsyncContext);

    }
  
    /**
     * An Object path
     *
     * <p>
     * uuid({5791BC27-CE9C-11D1-97BF-0000F81E849C})</p>
     */
    @ComInterface(iid = "{5791BC27-CE9C-11D1-97BF-0000F81E849C}")
    public interface ISWbemObjectPath extends IUnknown, IRawDispatchHandle, IDispatch {

        /**
         * The full path
         *
         * <p>
         * id(0x0)</p>
         * <p>
         * vtableId(7)</p>
         */
        @ComProperty(name = "Path", dispId = 0x0)
        String getPath();

        /**
         * The full path
         *
         * <p>
         * id(0x0)</p>
         * <p>
         * vtableId(8)</p>
         *
         * @param param0 [in] {@code String}
         */
        @ComProperty(name = "Path", dispId = 0x0)
        void setPath(String param0);

        /**
         * The relative path
         *
         * <p>
         * id(0x1)</p>
         * <p>
         * vtableId(9)</p>
         */
        @ComProperty(name = "RelPath", dispId = 0x1)
        String getRelPath();

        /**
         * The relative path
         *
         * <p>
         * id(0x1)</p>
         * <p>
         * vtableId(10)</p>
         *
         * @param param0 [in] {@code String}
         */
        @ComProperty(name = "RelPath", dispId = 0x1)
        void setRelPath(String param0);

        /**
         * The name of the Server
         *
         * <p>
         * id(0x2)</p>
         * <p>
         * vtableId(11)</p>
         */
        @ComProperty(name = "Server", dispId = 0x2)
        String getServer();

        /**
         * The name of the Server
         *
         * <p>
         * id(0x2)</p>
         * <p>
         * vtableId(12)</p>
         *
         * @param param0 [in] {@code String}
         */
        @ComProperty(name = "Server", dispId = 0x2)
        void setServer(String param0);

        /**
         * The Namespace path
         *
         * <p>
         * id(0x3)</p>
         * <p>
         * vtableId(13)</p>
         */
        @ComProperty(name = "Namespace", dispId = 0x3)
        String getNamespace();

        /**
         * The Namespace path
         *
         * <p>
         * id(0x3)</p>
         * <p>
         * vtableId(14)</p>
         *
         * @param param0 [in] {@code String}
         */
        @ComProperty(name = "Namespace", dispId = 0x3)
        void setNamespace(String param0);

        /**
         * The parent Namespace path
         *
         * <p>
         * id(0x4)</p>
         * <p>
         * vtableId(15)</p>
         */
        @ComProperty(name = "ParentNamespace", dispId = 0x4)
        String getParentNamespace();

        /**
         * The Display Name for this path
         *
         * <p>
         * id(0x5)</p>
         * <p>
         * vtableId(16)</p>
         */
        @ComProperty(name = "DisplayName", dispId = 0x5)
        String getDisplayName();

        /**
         * The Display Name for this path
         *
         * <p>
         * id(0x5)</p>
         * <p>
         * vtableId(17)</p>
         *
         * @param param0 [in] {@code String}
         */
        @ComProperty(name = "DisplayName", dispId = 0x5)
        void setDisplayName(String param0);

        /**
         * The Class name
         *
         * <p>
         * id(0x6)</p>
         * <p>
         * vtableId(18)</p>
         */
        @ComProperty(name = "Class", dispId = 0x6)
        String get_Class();

        /**
         * The Class name
         *
         * <p>
         * id(0x6)</p>
         * <p>
         * vtableId(19)</p>
         *
         * @param param0 [in] {@code String}
         */
        @ComProperty(name = "Class", dispId = 0x6)
        void set_Class(String param0);

        /**
         * Indicates whether this path addresses a Class
         *
         * <p>
         * id(0x7)</p>
         * <p>
         * vtableId(20)</p>
         */
        @ComProperty(name = "IsClass", dispId = 0x7)
        Boolean getIsClass();

        /**
         * Coerce this path to address a Class
         *
         * <p>
         * id(0x8)</p>
         * <p>
         * vtableId(21)</p>
         */
        @ComMethod(name = "SetAsClass", dispId = 0x8)
        void SetAsClass();

        /**
         * Indicates whether this path addresses a Singleton Instance
         *
         * <p>
         * id(0x9)</p>
         * <p>
         * vtableId(22)</p>
         */
        @ComProperty(name = "IsSingleton", dispId = 0x9)
        Boolean getIsSingleton();

        /**
         * Coerce this path to address a Singleton Instance
         *
         * <p>
         * id(0xa)</p>
         * <p>
         * vtableId(23)</p>
         */
        @ComMethod(name = "SetAsSingleton", dispId = 0xa)
        void SetAsSingleton();

        /**
         * Defines locale component of this path
         *
         * <p>
         * id(0xd)</p>
         * <p>
         * vtableId(26)</p>
         */
        @ComProperty(name = "Locale", dispId = 0xd)
        String getLocale();

        /**
         * Defines locale component of this path
         *
         * <p>
         * id(0xd)</p>
         * <p>
         * vtableId(27)</p>
         *
         * @param param0 [in] {@code String}
         */
        @ComProperty(name = "Locale", dispId = 0xd)
        void setLocale(String param0);

        /**
         * Defines authentication authority component of this path
         *
         * <p>
         * id(0xe)</p>
         * <p>
         * vtableId(28)</p>
         */
        @ComProperty(name = "Authority", dispId = 0xe)
        String getAuthority();

        /**
         * Defines authentication authority component of this path
         *
         * <p>
         * id(0xe)</p>
         * <p>
         * vtableId(29)</p>
         *
         * @param param0 [in] {@code String}
         */
        @ComProperty(name = "Authority", dispId = 0xe)
        void setAuthority(String param0);

    }
}
