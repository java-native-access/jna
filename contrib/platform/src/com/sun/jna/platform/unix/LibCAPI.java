/* Copyright (c) 2015 Goldstein Lyor, 2021 Daniel Widdis, All Rights Reserved
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
package com.sun.jna.platform.unix;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.linux.ErrNo;

/**
 * Note: we are using this &quot;intermediate&quot; API in order to allow
 * Linux-like O/S-es to implement the same API, but maybe using a different
 * library name
 * @author Lyor Goldstein
 */
public interface LibCAPI extends Reboot, Resource {

    /**
     * This is an unsigned integer type used to represent the sizes of objects.
     */
    class size_t extends IntegerType {
        public static final size_t ZERO = new size_t();

        private static final long serialVersionUID = 1L;

        public static class ByReference extends com.sun.jna.ptr.ByReference {
            public ByReference() {
                this(0);
            }

            public ByReference(long value) {
                this(new size_t(value));
            }

            public ByReference(size_t value) {
                super(Native.SIZE_T_SIZE);
                setValue(value);
            }

            public void setValue(long value) {
                setValue(new size_t(value));
            }

            public void setValue(size_t value) {
                if (Native.SIZE_T_SIZE > 4) {
                    getPointer().setLong(0, value.longValue());
                } else {
                    getPointer().setInt(0, value.intValue());
                }
            }

            public long longValue() {
                return Native.SIZE_T_SIZE > 4 ? getPointer().getLong(0) : getPointer().getInt(0);
            }

            public size_t getValue() {
                return new size_t(longValue());
            }
        }

        public size_t() {
            this(0);
        }

        public size_t(long value) {
            super(Native.SIZE_T_SIZE, value, true);
        }
    }

    /**
     * This is a signed integer type used for a count of bytes or an error
     * indication.
     */
    class ssize_t extends IntegerType {
        public static final ssize_t ZERO = new ssize_t();

        private static final long serialVersionUID = 1L;

        public ssize_t() {
            this(0);
        }

        public ssize_t(long value) {
            super(Native.SIZE_T_SIZE, value, false);
        }
    }

    // see man(2) get/set uid/gid
    int getuid();
    int geteuid();
    int getgid();
    int getegid();

    int setuid(int uid);
    int seteuid(int uid);
    int setgid(int gid);
    int setegid(int gid);

    // see man(2) get/set hostname
    int HOST_NAME_MAX = 255; // not including the '\0'
    int gethostname(byte[] name, int len);
    int sethostname(String name, int len);

    // see man(2) get/set domainname
    int getdomainname(byte[] name, int len);
    int setdomainname(String name, int len);

    /**
     * @param name Environment variable name
     * @return Returns the value in the environment, or {@code null} if there
     * is no match for the name
     * @see <A HREF="https://www.freebsd.org/cgi/man.cgi?query=setenv&sektion=3">getenv(3)</A>
     */
    String getenv(String name);

    /**
     * Update or add a variable in the environment of the calling process.
     * @param name Environment variable name
     * @param value Required value
     * @param overwrite If the environment variable already exists and the
     * value of {@code overwrite} is non-zero, the function shall return
     * success and the environment shall be updated. If the environment
     * variable already exists and the value of {@code overwrite} is zero, the
     * function shall return success and the environment shall remain unchanged.
     * @return Upon successful completion, zero shall be returned. Otherwise,
     * -1 shall be returned, {@code errno} set to indicate the error, and the
     * environment shall be unchanged
     * @see <A HREF="https://www.freebsd.org/cgi/man.cgi?query=setenv&sektion=3">getenv(3)</A>
     */
    int setenv(String name, String value, int overwrite);

    /**
     * @param name Environment variable name - If the named variable does not
     * exist in the current environment, the environment shall be unchanged
     * and the function is considered to have completed successfully.
     * @return Upon successful completion, zero shall be returned. Otherwise,
     * -1 shall be returned, {@code errno} set to indicate the error, and the
     * environment shall be unchanged
     * @see <A HREF="https://www.freebsd.org/cgi/man.cgi?query=setenv&sektion=3">getenv(3)</A>
     */
    int unsetenv(String name);

    /**
     * The getloadavg() function returns the number of processes in the system
     * run queue averaged over various periods of time.  Up to nelem samples are
     * retrieved and assigned to successive elements of loadavg[].  The system
     * imposes a maximum of 3 samples, representing averages over the last 1, 5,
     * and 15 minutes, respectively.
     * @param loadavg An array of doubles which will be filled with the results
     * @param nelem Number of samples to return
     * @return If the load average was unobtainable, -1 is returned; otherwise,
     * the number of samples actually retrieved is returned.
     * @see <A HREF="https://www.freebsd.org/cgi/man.cgi?query=getloadavg&sektion=3">getloadavg(3)</A>
     */
    int getloadavg(double[] loadavg, int nelem);

    /**
     * Closes a file descriptor, so that it no longer refers to any file and may be
     * reused. Any record locks held on the file it was associated with, and owned
     * by the process, are removed (regardless of the file descriptor that was used
     * to obtain the lock).
     * <p>
     * If {@code fd} is the last file descriptor referring to the underlying open
     * file description, the resources associated with the open file description are
     * freed; if the file descriptor was the last reference to a file which has been
     * removed using {@code unlink}, the file is deleted.
     * @param fd a file descriptor
     * @return returns zero on success. On error, -1 is returned, and {@code errno}
     * is set appropriately.
     * <p>
     * {@code close()} should not be retried after an error.
     */
    int close(int fd);

    /**
     * Flushes changes made to the in-core copy of a file that was mapped into
     * memory using {@link LibCUtil#mmap(Pointer, long, int, int, int, long)} back
     * to the filesystem. Without use of this call, there is no guarantee that
     * changes are written back before {@link #munmap(Pointer, size_t)} is called.
     * To be more precise, the part of the file that corresponds to the memory area
     * starting at {@code addr} and having length {@code length} is updated.
     * @param addr The start of the memory area to sync to the filesystem.
     * @param length The length of the memory area to sync to the filesystem.
     * @param flags The flags argument should specify exactly one of {@code MS_ASYNC}
     * and {@code MS_SYNC}, and may additionally include the
     * {@code MS_INVALIDATE} bit.
     * @return On success, zero is returned. On error, -1 is returned, and
     * {@code errno} is set appropriately.
     */
    int msync(Pointer addr, size_t length, int flags);

    /**
     * Deletes the mappings for the specified address range, and causes further
     * references to addresses within the range to generate invalid memory
     * references. The region is also automatically unmapped when the process is
     * terminated. On the other hand, closing the file descriptor does not unmap the
     * region.
     * <p>
     * It is not an error if the indicated range does not contain any mapped pages.
     * @param addr The base address from which to delete mappings. The address addr
     * must be a multiple of the page size (but length need not be).
     * @param length The length from the base address to delete mappings. All pages
     * containing a part of the indicated range are unmapped, and
     * subsequent references to these pages will generate
     * {@code SIGSEGV}.
     * @return On success, returns 0. On failure, it returns -1, and {@code errno}
     * is set to indicate the cause of the error (probably to
     * {@code EINVAL}).
     */
    int munmap(Pointer addr, size_t length);

    /**
     * Runs the provided command via "sh" and waits for its termination
     * (ignoring SIGINT and SIGQUIT, and blocking SIGCHLD).
     * <p>
     * This function is a cancellation point in multi-threaded programs. This is a problem if the thread allocates some resources (like memory, file descriptors, semaphores or whatever) at the time system is called.
     * If the thread gets canceled these resources stay allocated until the program ends. To avoid this calls to system should be protected using cancellation handlers.
     * <p>
     * Portability Note: Some C implementations may not have any notion of a command processor that can execute other programs.
     * You can determine whether a command processor exists by executing system (NULL); if the return value is nonzero, a command processor is available.
     * <p>
     * The popen and pclose functions (see Pipe to a Subprocess) are closely related to the system function.
     * They allow the parent process to communicate with the standard input and output channels of the command being executed.
     * @param command if null a return value of zero indicates that no command processor is available, otherwise
     * executes command as a shell command. In the GNU C Library, it always uses the default shell sh to run the command.
     * In particular, it searches the directories in PATH to find programs to execute.
     * @return -1 if it wasn't possible to create the shell process, and otherwise is the status of the shell process.
     * @see <a href="https://www.freebsd.org/cgi/man.cgi?query=system">system(1)</a>
     */
    int system(String command);

    /**
     * @return the process ID of the current process.
     */
    int getpid();

    /**
     * @return the process ID of the parent of the current process.
     */
    int getppid();

    /**
     * Creates a new (child) process.
     * The current address space is copied over to the new process,
     * which means that both see {@link #fork()} return, but with different values.
     *
     * @return On success, the PID of the child process is returned in the parent, and 0 is returned in the child.
     * On failure, -1 is returned in the parent, no child process is created, and errno ({@link ErrNo#EAGAIN}, {@link ErrNo#ENOMEM})
     * is set to indicate the error.
     * @see <a href="https://www.freebsd.org/cgi/man.cgi?query=fork">fork()</a>
     */
    int fork();

    /**
     * Executes the file named by filename as a new process image.
     * Use this function after {@link #fork()}.
     * The environment for the new process image is taken from the environment variable of the current process image.
     *
     * @param filename the name of the file to execute.
     * @param argv     array that is used to provide a value for the argv argument to the main function of the program to be executed.
     *                 The last element of this array must be a null pointer. By convention, the first element of this array is the file name of the program sans directory names.
     *                 Details <a href="https://www.gnu.org/software/libc/manual/html_mono/libc.html#Program-Arguments">here</a>.
     * @return normally doesn't return, since execution of a new program causes the currently executing program to go away completely.
     * A value of -1 is returned in the event of a failure or errno ({@link ErrNo#E2BIG}, {@link ErrNo#ENOEXEC}, {@link ErrNo#ENOMEM}),
     * in addition to the usual file name errors.
     * If execution of the new file succeeds, it updates the access time field of the file as if the file had been read.
     * @see <a href="https://www.freebsd.org/cgi/man.cgi?query=execv">execv(2)</a>
     */
    int execv(String filename, String[] argv);

    /**
     * Similar to {@link #execv(String, String[])}, but permits you to specify the environment for the new program explicitly as the env argument.
     * This should be an array of strings in the same format as for the "environ" variable.
     *
     * @param filename the name of the file to execute.
     * @param argv     array that is used to provide a value for the argv argument to the main function of the program to be executed.
     *                 The last element of this array must be a null pointer. By convention, the first element of this array is the file name of the program sans directory names.
     * @param env      the enviornment variables.
     * @return normally doesn't return, since execution of a new program causes the currently executing program to go away completely.
     * A value of -1 is returned in the event of a failure or errno ({@link ErrNo#E2BIG}, {@link ErrNo#ENOEXEC}, {@link ErrNo#ENOMEM}),
     * in addition to the usual file name errors.
     * If execution of the new file succeeds, it updates the access time field of the file as if the file had been read.
     * @see <a href="https://www.freebsd.org/cgi/man.cgi?query=execve">execve(3)</a>
     */
    int execve(String filename, String[] argv, String[] env);

    /**
     * Similar to {@link #execv(String, String[])}, but instead of identifying the program executable by its pathname,
     * the file descriptor fd is used.
     * <p>
     * On Linux, fexecve can fail with an error of ENOSYS if /proc has not been mounted and
     * the kernel lacks support for the underlying execveat system call.
     *
     * @param fd   file descriptor. Must have been opened with the O_RDONLY flag or (on Linux) the O_PATH flag.
     * @param argv array that is used to provide a value for the argv argument to the main function of the program to be executed.
     *             The last element of this array must be a null pointer. By convention, the first element of this array is the file name of the program sans directory names.
     * @param env  the enviornment variables.
     * @return normally doesn't return, since execution of a new program causes the currently executing program to go away completely.
     * A value of -1 is returned in the event of a failure or errno ({@link ErrNo#E2BIG}, {@link ErrNo#ENOEXEC}, {@link ErrNo#ENOMEM}),
     *      * in addition to the usual file name errors.
     * If execution of the new file succeeds, it updates the access time field of the file as if the file had been read.
     * @see <a href="https://www.freebsd.org/cgi/man.cgi?query=fexecve">fexecve(3)</a>
     */
    int fexecve(int fd, String[] argv, String[] env);

    int WAIT_ANY = -1;
    int WAIT_MYPGRP = 0;

    /**
     * The waitpid function is used to request status information from a child process.
     * Normally, the calling process is suspended until the child process makes status information available by terminating.
     * <p>
     * If status information for a child process is available immediately, this function returns immediately without waiting.
     * If more than one eligible child process has status information available, one of them is chosen randomly, and its status is returned immediately.
     * To get the status from the other eligible child processes, you need to call waitpid again.
     * <p>
     * This function is a cancellation point in multi-threaded programs.
     * This is a problem if the thread allocates some resources (like memory, file descriptors, semaphores or whatever) at the time waitpid is called.
     * If the thread gets canceled these resources stay allocated until the program ends.
     * To avoid this calls to waitpid should be protected using cancellation handlers.
     * @param pid       the process ID of the child process to request information from.
     *                  Can also be {@link #WAIT_ANY}, which requests status information for any child process.
     *                  Can also be {@link #WAIT_MYPGRP}, which requests information for any child process in the same process group as the calling process,
     *                  and any other negative value - pgid requests information for any child process whose process group ID is pgid.
     * @param status    the status information from the child process is stored in this object, unless it's a null pointer.
     * @param options   a bit mask. Its value should be the bitwise OR (that is, the ‘|’ operator) of zero or more of the WNOHANG and WUNTRACED flags.
     *                  You can use the WNOHANG flag to indicate that the parent process shouldn't wait; and the WUNTRACED flag to request status information
     *                  from stopped processes as well as processes that have terminated.
     * @return the process ID of the child process whose status is reported otherwise -1 if it failed
     * or errno ({@link ErrNo#EINTR}, {@link ErrNo#ECHILD}, {@link ErrNo#EINVAL}).
     * If there are child processes but none of them is waiting to be noticed, waitpid will block until one is.
     * However, if the WNOHANG option was specified, waitpid will return zero instead of blocking.
     * If a specific PID to wait for was given, it will ignore all other children (if any). Therefore, if there are children waiting
     * to be noticed but the child whose PID was specified is not one of them, waitpid will block or return zero as described above.
     * @see <a href="https://www.freebsd.org/cgi/man.cgi?query=waitpid">waitpid(3)</a>
     */
    int waitpid(int pid, int status, int options);

    /**
     * Simplified version of {@link #waitpid(int, int, int)}. Exactly the same as: waitpid(-1, status, 0);
     * Suspends execution of its calling thread until status information is available
     * for a child process or a signal is received.
     *
     * @param status the status information from the child process is stored in this object, unless it's a null pointer.
     * @return the PID of the process that reported a status change. The provided status object is also populated with
     * the received status information.
     * @see <a href="https://www.freebsd.org/cgi/man.cgi?query=waitpid">wait(1)</a>
     */
    int wait(int status);

}
