/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

/**
 * Utility classes and methods for Sspi
 */
public class SspiUtil {
    /**
     * The SecBufferDesc structure describes an array of SecBuffer structures
     * to pass from a transport application to a security package.
     * 
     * <p>
     * ManagedSecBufferDesc is a convenience binding, that makes dealing with
     * {@link com.sun.jna.platform.win32.Sspi.SecBufferDesc SecBufferDesc}
     * easier by providing direct, bound access, to the contained
     * {@link com.sun.jna.platform.win32.Sspi.SecBuffer SecBuffer}s.
     * </p>
     * 
     * <p>
     * ManagedSecBufferDesc assumes, that the size (entry count) of the
     * SecBufferDesc is known at construction time. It is assumed, that this
     * covers all relevant use-cases.</p>
     */
    public static class ManagedSecBufferDesc extends Sspi.SecBufferDesc {
                
        private final Sspi.SecBuffer[] secBuffers;
        
        /**
         * Create a new SecBufferDesc with initial data.
         * @param type Token type.
         * @param token Initial token data.
         */
        public ManagedSecBufferDesc(int type, byte[] token) {
            secBuffers = new Sspi.SecBuffer[] { new Sspi.SecBuffer(type, token) };
            pBuffers = secBuffers[0].getPointer();
            cBuffers = secBuffers.length;
        }

        /**
         * Create a new SecBufferDesc with one SecBuffer of a given type and size.
         * @param type type
         * @param tokenSize token size
         */
        public ManagedSecBufferDesc(int type, int tokenSize) {
            secBuffers = new Sspi.SecBuffer[] { new Sspi.SecBuffer(type, tokenSize) };
            pBuffers = secBuffers[0].getPointer();
            cBuffers = secBuffers.length;
        }
        
        public ManagedSecBufferDesc(int bufferCount) {
            cBuffers = bufferCount;
            secBuffers = (Sspi.SecBuffer[]) new Sspi.SecBuffer().toArray(bufferCount);
            pBuffers = secBuffers[0].getPointer();
            cBuffers = secBuffers.length;
        }

        public Sspi.SecBuffer getBuffer(int idx) {
            return secBuffers[idx];
        }

        @Override
        public void write() {
            for(Sspi.SecBuffer sb: secBuffers)  {
                sb.write();
            }
            writeField("ulVersion");
            writeField("pBuffers");
            writeField("cBuffers");
        }

        @Override
        public void read() {
            for (Sspi.SecBuffer sb : secBuffers) {
                sb.read();
            }
        }

    }
}
