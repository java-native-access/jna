/* Copyright (c) 2018 Matthias Bläsing, All Rights Reserved
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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinCrypt.CERT_CONTEXT;
import com.sun.jna.platform.win32.WinCrypt.CRL_CONTEXT;
import com.sun.jna.platform.win32.WinCrypt.CRYPT_ATTRIBUTE;
import com.sun.jna.platform.win32.WinCrypt.CRYPT_SIGN_MESSAGE_PARA;


public abstract class WinCryptUtil {

    public static class MANAGED_CRYPT_SIGN_MESSAGE_PARA extends CRYPT_SIGN_MESSAGE_PARA {

        private CERT_CONTEXT[] rgpMsgCerts;
        private CRL_CONTEXT[] rgpMsgCrls;
        private CRYPT_ATTRIBUTE[] rgAuthAttrs;
        private CRYPT_ATTRIBUTE[] rgUnauthAttrs;

        public void setRgpMsgCert(CERT_CONTEXT[] rgpMsgCerts) {
            this.rgpMsgCerts = rgpMsgCerts;
            if (rgpMsgCerts == null || rgpMsgCerts.length == 0) {
                rgpMsgCert = null;
                cMsgCert = 0;
            } else {
                cMsgCert = rgpMsgCerts.length;
                Memory mem = new Memory(Native.POINTER_SIZE * rgpMsgCerts.length);
                for (int i = 0; i < rgpMsgCerts.length; i++) {
                    mem.setPointer(i * Native.POINTER_SIZE, rgpMsgCerts[i].getPointer());
                }
                rgpMsgCert = mem;
            }
        }

        @Override
        public CERT_CONTEXT[] getRgpMsgCert() {
            return rgpMsgCerts;
        }

        public void setRgpMsgCrl(CRL_CONTEXT[] rgpMsgCrls) {
            this.rgpMsgCrls = rgpMsgCrls;
            if (rgpMsgCrls == null || rgpMsgCrls.length == 0) {
                rgpMsgCert = null;
                cMsgCert = 0;
            } else {
                cMsgCert = rgpMsgCrls.length;
                Memory mem = new Memory(Native.POINTER_SIZE * rgpMsgCrls.length);
                for (int i = 0; i < rgpMsgCrls.length; i++) {
                    mem.setPointer(i * Native.POINTER_SIZE, rgpMsgCrls[i].getPointer());
                }
                rgpMsgCert = mem;
            }
        }

        @Override
        public CRL_CONTEXT[] getRgpMsgCrl() {
            return rgpMsgCrls;
        }

        /**
         * @param rgAuthAttrs array of CRYPT_ATTRIBUTE - it must be created from
         * a continous memory region (manually allocated memory or
         * CRYPT_ATTRIBUTE#toArray)
         */
        public void setRgAuthAttr(CRYPT_ATTRIBUTE[] rgAuthAttrs) {
            this.rgAuthAttrs = rgAuthAttrs;
            if (rgAuthAttrs == null || rgAuthAttrs.length == 0) {
                rgAuthAttr = null;
                cMsgCert = 0;
            } else {
                cMsgCert = rgpMsgCerts.length;
                rgAuthAttr = rgAuthAttrs[0].getPointer();
            }
        }

        @Override
        public CRYPT_ATTRIBUTE[] getRgAuthAttr() {
            return rgAuthAttrs;
        }

        /**
         * @param rgUnauthAttrs array of CRYPT_ATTRIBUTE - it must be created
         * from a continous memory region (manually allocated memory or
         * CRYPT_ATTRIBUTE#toArray)
         */
        public void setRgUnauthAttr(CRYPT_ATTRIBUTE[] rgUnauthAttrs) {
            this.rgUnauthAttrs = rgUnauthAttrs;
            if (rgUnauthAttrs == null || rgUnauthAttrs.length == 0) {
                rgUnauthAttr = null;
                cMsgCert = 0;
            } else {
                cMsgCert = rgpMsgCerts.length;
                rgUnauthAttr = rgUnauthAttrs[0].getPointer();
            }
        }

        @Override
        public CRYPT_ATTRIBUTE[] getRgUnauthAttr() {
            return rgUnauthAttrs;
        }

        @Override
        public void write() {
            if (rgpMsgCerts != null) {
                for (CERT_CONTEXT cc : rgpMsgCerts) {
                    cc.write();
                }
            }
            if (rgpMsgCrls != null) {
                for (CRL_CONTEXT cc : rgpMsgCrls) {
                    cc.write();
                }
            }
            if (rgAuthAttrs != null) {
                for (CRYPT_ATTRIBUTE cc : rgAuthAttrs) {
                    cc.write();
                }
            }
            if (rgUnauthAttrs != null) {
                for (CRYPT_ATTRIBUTE cc : rgUnauthAttrs) {
                    cc.write();
                }
            }
            cbSize = size();
            super.write();
        }

        @Override
        public void read() {
            if (rgpMsgCerts != null) {
                for (CERT_CONTEXT cc : rgpMsgCerts) {
                    cc.read();
                }
            }
            if (rgpMsgCrls != null) {
                for (CRL_CONTEXT cc : rgpMsgCrls) {
                    cc.read();
                }
            }
            if (rgAuthAttrs != null) {
                for (CRYPT_ATTRIBUTE cc : rgAuthAttrs) {
                    cc.read();
                }
            }
            if (rgUnauthAttrs != null) {
                for (CRYPT_ATTRIBUTE cc : rgUnauthAttrs) {
                    cc.read();
                }
            }
            super.read();
        }
    }
}
