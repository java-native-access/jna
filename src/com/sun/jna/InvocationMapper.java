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
package com.sun.jna;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/** Provide a method for overriding how a given function is invoked.
 * An instance of this interface may be provided to
 * {@link Native#load(String, Class, java.util.Map)} as an entry in
 * the options map with key {@link Library#OPTION_INVOCATION_MAPPER}.<p>
 * This is useful for implementing inlined functions, or functions which
 * are actually C preprocessor macros.  Given a native library and JNA
 * interface method, the mapper may provide a handler which implements the
 * desired functionality (which may or may not actually make use of a
 * native method).
 * <p>
 * For example, the GNU C library remaps the <code>stat</code> function
 * into a call to <code>_xstat</code> with a slight rearrangement of arguments.
 * A mapper for the GNU C library might look like the following:<br>
 * <blockquote>
 * <pre><code>
 * new InvocationMapper() {
 *     public InvocationHandler getInvocationHandler(NativeLibrary lib, Method m) {
 *         if (m.getName().equals("stat")) {
 *             final Function f = lib.getFunction("_xstat");
 *             return new InvocationHandler() {
 *                 public Object invoke(Object proxy, Method method, Object[] args) {
 *                     Object[] newArgs = new Object[args.length+1];
 *                     System.arraycopy(args, 0, newArgs, 1, args.length);
 *                     newArgs[0] = Integer.valueOf(3); // _xstat version
 *                     return f.invoke(newArgs);
 *                 }
 *             };
 *         }
 *         return null;
 *     }
 * }
 * </code></pre>
 * </blockquote>
 * Another situation is where a header provides a function-like macro or
 * inline function definition.
 * <blockquote>
 * <pre><code>
 * // Original C code (macro and inline variations)
 * #define allocblock(x) malloc(x * 1024)
 * static inline void* allocblock(size_t x) { return malloc(x * 1024); }
 *
 * // Invocation mapping
 * new InvocationMapper() {
 *     public InvocationHandler getInvocationHandler(NativeLibrary lib, Method m) {
 *         if (m.getName().equals("allocblock")) {
 *             final Function f = lib.getFunction("malloc");
 *             return new InvocationHandler() {
 *                 public Object invoke(Object proxy, Method method, Object[] args) {
 *                     args[0] = ((Integer)args[0]).intValue() * 1024;
 *                     return f.invoke(newArgs);
 *                 }
 *             };
 *         }
 *         return null;
 *     }
 * }
 * </code></pre>
 * </blockquote>
 * @author twall
 */
public interface InvocationMapper {
    /** Return an {@link InvocationHandler} responsible for handling the
     * invocation of the given method, or <code>null</code> if the default
     * handling should be used.  Note that the result of a call to this method
     * with a given library and method may be cached.
     * @param lib Target library
     * @param m Original JNA interface method that was invoked.
     */
    InvocationHandler getInvocationHandler(NativeLibrary lib, Method m);
}
