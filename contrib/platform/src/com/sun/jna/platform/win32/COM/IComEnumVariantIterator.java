/* Copyright (c) 2017 Matthias Bläsing, All Rights Reserved
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

package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.util.Iterator;

/**
 * Wrapper for an EnumVariant Iteration. The usecase is a for-loop in the style:
 * 
 * <pre>{@code
 * // Aquire an IDispatch, that has a new NewEnum Property (DISPID_NEWENUM)
 * for(VARIANT v: IComEnumVariantIterator.wrap(dispatch)) {
 *      // Work with the acquired Variant
 *      // ...
 *      // Finally free it
 *      OleAuto.INSTANCE.VariantClear(v);
 * }
 * }</pre>
 * 
 * <p>The {@code IComEnumVariantIterator} iterator closes the enumeration it
 * wraps after the enumeration is exhausted or when the iterator is GCed, 
 * whatever happens earlier.</p>
 */
public class IComEnumVariantIterator implements Iterable<Variant.VARIANT>, Iterator<Variant.VARIANT>, Closeable {

    /**
     * Helper to get new enumeration from an {@link com.sun.jna.platform.win32.COM.util.IDispatch}.
     * 
     * <p>This expects, that the supplied IDispatch has a property identified by
     * a {@link com.sun.jna.platform.win32.OaIdl.DISPID} of {@link com.sun.jna.platform.win32.OaIdl#DISPID_NEWENUM}</p>
     * 
     * @param dispatch IDispatch to be analysed
     * @return IComEnumVariantIterator wrapping the enumeration queried from the supplied object
     */
    public static IComEnumVariantIterator wrap(com.sun.jna.platform.win32.COM.util.IDispatch dispatch) {
        PointerByReference pbr = new PointerByReference();
        IUnknown unknwn = dispatch.getProperty(IUnknown.class, OaIdl.DISPID_NEWENUM);
        unknwn.QueryInterface(EnumVariant.REFIID, pbr);
        // QueryInterace AddRefs the interface and we are done with the Unknown instance
        unknwn.Release();
        EnumVariant variant = new EnumVariant(pbr.getValue());
        return new IComEnumVariantIterator(variant);
    }
    
    private Variant.VARIANT nextValue;
    private EnumVariant backingIteration;

    /**
     * IComEnumVariantIterator wraps the supplied EnumVariant and exposes that
     * as an {@code Iterable<Variant.VARIANT>}/{@code Iterator<Variant.VARIANT>}.
     * 
     * The class takes possion of the supplied EnumVariant. So the EnumVariant
     * is Released when the enumeration is exhausted or the Iterator is GCed.
     * 
     * @param backingIteration 
     */
    public IComEnumVariantIterator(EnumVariant backingIteration) {
        this.backingIteration = backingIteration;
        retrieveNext();
    }

    @Override
    public boolean hasNext() {
        return nextValue != null;
    }

    @Override
    public Variant.VARIANT next() {
        Variant.VARIANT current = nextValue;
        retrieveNext();
        return current;
    }

    private void retrieveNext() {
        if(backingIteration == null) {
            return;
        }
        Variant.VARIANT[] variants = backingIteration.Next(1);
        if (variants.length == 0) {
            close();
        } else {
            nextValue = variants[0];
        }
    }

    @Override
    public void close() {
        if (backingIteration != null) {
            nextValue = null;
            backingIteration.Release();
            backingIteration = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
    
    @Override
    public Iterator<Variant.VARIANT> iterator() {
        return this;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
