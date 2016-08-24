/* Copyright (c) 2016 Minoru Sakamoto, All Rights Reserved
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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.win32.W32APITypeMapper;

import java.util.Arrays;
import java.util.List;

/**
 * @author Minoru Sakamoto
 */
public interface EvtRpcLogin {

    /**
     * Contains the information used to connect to a remote computer.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385566(v=vs.85).aspx
     */
    public class EVT_RPC_LOGIN extends Structure {

        /** The name of the remote computer to connect to. */
        public WString Server;

        /** The user name to use to connect to the remote computer. */
        public WString User;

        /** The domain to which the user account belongs. Optional. */
        public WString Domain;

        /** The password for the user account. */
        public WString Password;

        /**
         * The authentication method to use to authenticate the user when connecting to the remote computer.
         * For possible authentication methods, see the {@link Winevt.EVT_RPC_LOGIN_FLAGS} enumeration.
         */
        public int Flags;

        public EVT_RPC_LOGIN() {
            super(W32APITypeMapper.DEFAULT);
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList("Server", "User", "Domain", "Password", "Flags");
        }

        public EVT_RPC_LOGIN(WString Server, WString User, WString Domain, WString Password, int Flags) {
            super();
            this.Server = Server;
            this.User = User;
            this.Domain = Domain;
            this.Password = Password;
            this.Flags = Flags;
        }

        public EVT_RPC_LOGIN(Pointer peer) {
            super(peer, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
        }

        protected ByReference newByReference() {
            return new ByReference();
        }

        protected ByValue newByValue() {
            return new ByValue();
        }

        protected EVT_RPC_LOGIN newInstance() {
            return new EVT_RPC_LOGIN();
        }


        public static class ByReference extends EVT_RPC_LOGIN implements Structure.ByReference {

        }

        public static class ByValue extends EVT_RPC_LOGIN implements Structure.ByValue {

        }
    }
}
