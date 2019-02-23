/* Copyright (c) 2009-2012 Timothy Wall, All Rights Reserved
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
package com.sun.jna;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;

/**
 * Run tests under web start
 * Works under OSX, windows, and linux.
 */
public class WebStartTest extends TestCase implements Paths {

    // Provide a policy file for unsigned jars
    // Unfortunately this does not allow native libraries
    private static final String POLICY =
        "grant { \n"
        + " permission java.security.AllPermission;\n"
        + "};";

    private static final String JNLP =
        "<?xml version='1.0' encoding='UTF-8'?>\n"
        + "<jnlp spec='1.0' codebase='{CODEBASE}' href='{JNLP_FILE}'>\n"
        + "  <information>\n"
        + "    <title>JNLP Web Start Test</title>\n"
        + "    <vendor>JNA</vendor>\n"
        + "    <homepage href='https://github.com/java-native-access/jna'/>\n"
        + "    <description>Local JNLP launch test.</description>\n"
        + "    <description kind='short'>Launch Test</description>\n"
        + "  </information>\n"
        // all-permissions is required for <nativelib>s
        // but will cause unsigned jars to fail (irrespective of policy)
        + "  <security><all-permissions/></security>\n"
        + "  <resources>\n"
        // Explicitly supply javawebstart.version, which is missing in NetX
        // Boo, java-vm-args doesn't work in NetX
        // and neither does javaws -J<arg>
        // java-vm-args also causes javaws to ask for the JNLP to be signed,
        // so don't bother
        //+ "    <j2se version='1.4+' java-vm-args='-Djavawebstart.version=0.0'/>\n"
        + "    <jar href='jna-test.jar'/>\n"
        + "    <jar href='jna.jar'/>\n"
        + "    <jar href='junit.jar'/>{CLOVER}\n"
        + "    <nativelib href='jnidispatch.jar'/>\n"
        + "  </resources>\n"
        + "  <offline-allowed/>\n"
        + "  <application-desc main-class='" + WebStartTest.class.getName() + "'>\n"
        + "    <argument>{CLASS}</argument>\n"
        + "    <argument>{METHOD}</argument>\n"
        + "    <argument>{PORT}</argument>\n"
        // NetX doesn't set javawebstart.version, so explicitly flag it
        + "    <argument>javawebstart</argument>\n"
        // Explicitly indicate the architecture we want to skip the test
        // if we somehow got the wrong architecture javaws
        + "    <argument>arch64=" + Platform.is64Bit() + "</argument"
        + "  </application-desc>\n"
        + "</jnlp>";

    public void testLaunchedUnderWebStart() throws Exception {
        assertNotNull("Test not launched under web start",
                      System.getProperty("javawebstart.version"));
    }

    private static final String FAILURE = "This test is supposed to fail";
    public void testDetectFailure() {
        fail(FAILURE);
    }

    private static final String ERROR = "This test is supposed to error";
    public void testDetectError() {
        throw new Error(ERROR);
    }

    public interface Dummy extends Library {
        void dummy();
    }
    public void testJNLPFindCustomLibrary() {
        assertNotNull("Custom library path not found by JNLP class loader",
                      Native.getWebStartLibraryPath("jnidispatch"));
        Native.load("jnidispatch", Dummy.class);
    }

    public void testJNLPFindProcessLibrary() {
        String libname = Platform.C_LIBRARY_NAME;
        assertNull("Process library path not expected to be found by JNLP class loader",
                   Native.getWebStartLibraryPath(libname));
        Native.load(libname, Dummy.class);
    }

    public void testJNLPFindLibraryFailure() {
        try {
            Native.load("xyzzy", Dummy.class);
            fail("Missing native libraries should throw UnsatisfiedLinkError");
        }
        catch(UnsatisfiedLinkError e) {
        }
    }

    private static final int SOCKET_TIMEOUT = 30000;
    private void runTestUnderWebStart(String testClass, String testMethod) throws Exception {
        String dir = System.getProperty("jna.builddir", BUILDDIR);
        String codebase = new File(dir, "jws").toURI().toURL().toString();

        ServerSocket s = new ServerSocket(0);
        try {
            s.setSoTimeout(SOCKET_TIMEOUT);
            int port = s.getLocalPort();

            File jnlp = File.createTempFile(getName(), ".jnlp");
            String contents = JNLP.replace("{CLASS}", testClass);
            contents = contents.replace("{METHOD}", testMethod);
            contents = contents.replace("{CODEBASE}", codebase);
            contents = contents.replace("{JNLP_FILE}", jnlp.toURI().toURL().toString());
            contents = contents.replace("{PORT}", String.valueOf(port));
            contents = contents.replace("{CLOVER}", USING_CLOVER ? "<jar href='clover.jar'/>" : "");

            try {
                OutputStream os = new FileOutputStream(jnlp);
                os.write(contents.getBytes());
                os.close();
                String path = findJWS();
                String[] cmd = {
                    path,
                    Platform.isWindows() ? "-J-Ddummy" : (Platform.is64Bit() ? "-J-d64" : "-J-d32"),
                    "-Xnosplash",
                    "-wait",
                    jnlp.toURI().toURL().toString(),
                };
                final Process p = Runtime.getRuntime().exec(cmd);
                final StringBuffer output = new StringBuffer();
                class SocketHandler extends Thread {
                    private InputStream is;
                    private StringBuffer sb;
                    public SocketHandler(Socket s, StringBuffer b) throws IOException {
                        this.is = s.getInputStream();
                        this.sb = b;
                    }
                    @Override
                    public void run() {
                        byte[] buf = new byte[256];
                        while (true) {
                            try {
                                int count = is.read(buf, 0, buf.length);
                                if (count == -1) break;
                                if (count == 0) {
                                    try { sleep(1); } catch(InterruptedException e) { }
                                }
                                else {
                                    sb.append(new String(buf, 0, count));
                                }
                            }
                            catch(IOException e) {
                                showMessage("read error: " + e.toString());
                            }
                        }
                        try { is.close(); } catch(IOException e) { }
                    }
                }

                Thread out = null;
                try {
                    out = new SocketHandler(s.accept(), output);
                    out.start();
                }
                catch(SocketTimeoutException e) {
                    try {
                        p.exitValue();
                    }
                    catch(IllegalThreadStateException e2) {
                        p.destroy();
                        throw new Error("JWS Timed out");
                    }
                }
                p.waitFor();
                if (out != null) {
                    out.join();
                }

                int code = p.exitValue();
                String error = output.toString();
                if (code != 0 || !"".equals(error)) {
                    if (code == 1
                        || error.indexOf("AssertionFailedError") != -1) {
                        fail("JWS FAIL: " + error);
                    }
                    throw new Error("JWS ERROR: " + error);
                }
            }
            finally {
                jnlp.delete();
            }
        } finally {
            s.close();
        }
    }

    private static boolean runningWebStart() {
        return System.getProperty("javawebstart.version") != null;
    }

    private void runTestUnderWebStart() throws Exception {
        if (getClass().equals(WebStartTest.class)) {
            if (getName().equals("testDetectFailure")) {
                try {
                    runTestUnderWebStart(getClass().getName(), getName());
                }
                catch(AssertionFailedError e) {
                    if (e.getMessage().indexOf(FAILURE) != -1)
                        return;
                }
                fail("Failed to detect test failure");
            }
            else if (getName().equals("testDetectError")) {
                try {
                    runTestUnderWebStart(getClass().getName(), getName());
                }
                catch(AssertionFailedError e) {
                    fail("Test produced a failure instead of an error: " + e);
                }
                catch(Error e) {
                    if (e.getMessage().indexOf(ERROR) != -1)
                        return;
                    throw e;
                }
                fail("Failed to detect test error");
            }
            else {
                runTestUnderWebStart(getClass().getName(), getName());
            }
        }
        else {
            runTestUnderWebStart(getClass().getName(), getName());
        }
    }

    public interface FolderInfo extends com.sun.jna.win32.StdCallLibrary {
        int MAX_PATH = 260;
        int SHGFP_TYPE_CURRENT = 0;
        int SHGFP_TYPE_DEFAULT = 1;
        int CSIDL_APPDATA = 26;
        int CSIDL_WINDOWS = 36;
        int SHGetFolderPathW(Pointer owner, int folder, Pointer token,
                             int flags, char[] path);
    }

    private String findJWS() throws IOException {
        String JAVA_HOME = System.getProperty("java.home");
        String BIN = new File(JAVA_HOME, "/bin").getAbsolutePath();
        File javaws = new File(BIN, "javaws" + (Platform.isWindows()?".exe":""));
        List<File> tried = new ArrayList<File>();
        tried.add(javaws);
        if (!javaws.exists()) {
            // NOTE: OSX puts javaws somewhere else entirely
            if (Platform.isMac()) {
                javaws = new File(JAVA_HOME, "../Commands/javaws");
                tried.add(javaws);
                if (!javaws.exists()) {
                    // Hack, look for the "old" location
                    javaws = new File("/System/Library/Frameworks/JavaVM.framework/Versions/Current/Commands/javaws");
                }
            }
            // NOTE: win64 only includes javaws in the system path
            if (Platform.isWindows()) {
                FolderInfo info = Native.load("shell32", FolderInfo.class);
                char[] buf = new char[FolderInfo.MAX_PATH];
                //int result =
                info.SHGetFolderPathW(null, FolderInfo.CSIDL_WINDOWS, null, 0, buf);
                String path = Native.toString(buf);
                if (Platform.is64Bit()) {
                    javaws = new File(path, "SysWOW64/javaws.exe");
                }
                else {
                    javaws = new File(path, "system32/javaws.exe");
                }
            }
            tried.add(javaws);
            if (!javaws.exists()) {
                throw new IOException("javaws executable not found, tried " + tried);
            }
        }
        return javaws.getAbsolutePath();
    }

    // TODO: find some way of querying the current VM for the deployment
    // properties path
    private File findDeploymentProperties() {
        String path = System.getProperty("user.home");
        File deployment;
        String vendor = System.getProperty("java.vm.vendor");
        if (vendor.indexOf(" ") != -1) {
            vendor = vendor.substring(0, vendor.indexOf(" "));
        }
        if (Platform.isWindows()) {
            FolderInfo info = Native.load("shell32", FolderInfo.class);
            char[] buf = new char[FolderInfo.MAX_PATH];
            info.SHGetFolderPathW(null, FolderInfo.CSIDL_APPDATA,
                                  null, 0, buf);
            path = Native.toString(buf);

            // NOTE: works for Sun(Oracle) and IBM, may not work for others
            if ("Oracle".equals(vendor)) {
                vendor = "Sun";
            }
            deployment = new File(path + "/" + vendor + "/Java/Deployment");
            if (!deployment.exists()) {
                // Vista and later puts WebStart into LocalLow instead of Local,
                // and sometimes returns Roaming instead of Local
                // TODO: Use SHGetKnownFolderPath to look it up
                deployment = new File(deployment.getAbsolutePath().replace("Local", "LocalLow").replace("Roaming", "LocalLow"));
            }
        }
        else if (Platform.isMac()) {
            if ("Oracle".equals(vendor)) {
                deployment = new File(path + "/Library/Application Support/Oracle/Java/Deployment");
            }
            else {
                // Older Apple path
                deployment = new File(path + "/Library/Caches/Java");
            }
        }
        else {
            deployment = new File(path + "/.java/deployment");
        }
        if (!deployment.exists()) {
            throw new Error("The user deployment directory " + deployment + " does not exist; save Java Control Panel or Web Start settings to initialize it");
        }
        return new File(deployment, "deployment.properties");
    }

    private static final String POLICY_KEY =
        "deployment.user.security.policy";
    private static final String CERTS_KEY =
        "deployment.user.security.trusted.certs";
    @Override
    public void runBare() throws Throwable {
        if (runningWebStart()) {
            super.runBare();
        }
        else if (!GraphicsEnvironment.isHeadless()) {
            File policy = File.createTempFile(getName(), ".policy");
            OutputStream os = new FileOutputStream(policy);
            os.write(POLICY.getBytes());
            os.close();
            File dpfile = findDeploymentProperties();
            Properties saved = new Properties();
            saved.load(new FileInputStream(dpfile));
            Properties props = new Properties();
            props.putAll(saved);
            props.setProperty(CERTS_KEY, new File("jna.keystore").getAbsolutePath());
            props.setProperty(POLICY_KEY, policy.getAbsolutePath());
            os = new FileOutputStream(dpfile);
            props.store(os, "deployment.properties (for testing)");
            os.close();
            try {
                runTestUnderWebStart();
            }
            finally {
                policy.delete();
                os = new FileOutputStream(dpfile);
                saved.store(os, "deployment.properties");
                os.close();
            }
        }
    }

    private static void sendResults(Throwable t, int port) throws IOException {
        Socket s = new Socket(InetAddress.getLocalHost(), port);
        OutputStream os = s.getOutputStream();
        if (t != null) {
            t.printStackTrace(new PrintStream(os));
        }
        s.close();
    }

    private static Throwable runTestCaseTest(String testClass, String method, int port) throws Exception {
        TestCase test = (TestCase)Class.forName(testClass).getConstructor().newInstance();
        test.setName(method);
        TestResult result = new TestResult();
        test.run(result);
        if (result.failureCount() != 0) {
            Enumeration<TestFailure> e = result.failures();
            return e.nextElement().thrownException();
        } else if (result.errorCount() != 0) {
            Enumeration<TestFailure> e = result.errors();
            return e.nextElement().thrownException();
        }
        return null;
    }

    private static void showMessage(String msg) {
        showMessage(msg, 60000);
    }

    private static void showMessage(String msg, int timeout) {
        JFrame f = new JFrame("Web Start Test Failure");
        f.getContentPane().add(new JScrollPane(new JLabel(msg)));
        f.pack();
        f.setLocation(100, 100);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (timeout != 0) {
            try { Thread.sleep(timeout); } catch(Exception e) { }
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length >= 4
                && "javawebstart".equals(args[3])
                && !runningWebStart()) {
                System.setProperty("javawebstart.version", "fake");
            }
            if (runningWebStart()) {
                String testClass = args.length > 0
                    ? args[0] : WebStartTest.class.getName();
                String testMethod = args.length > 1
                    ? args[1] : "testLaunchedUnderWebStart";
                int port = args.length > 2
                    ? Integer.parseInt(args[2]) : 8080;

                try {
                    if (args.length >=5
                        && "arch64=true".equals(args[4])
                        && !Platform.is64Bit()) {
                        throw new Error("Cannot run 64-bit test on 32-bit javaws");
                    }
                    else {
                        Throwable t = runTestCaseTest(testClass, testMethod, port);
                        sendResults(t, port);
                    }
                }
                catch(Throwable t) {
                    try {
                        sendResults(t, port);
                    }
                    catch(Throwable e) {
                        // Can't communicate back to launching process
                        showMessage("ERROR: " + e.getMessage());
                    }
                }
            }
            else {
                junit.textui.TestRunner.run(WebStartTest.class);
            }
        }
        catch(Throwable t) {
            showMessage("ERROR: " + t.getMessage());
        }
        // NOTE: System.exit with non-zero status causes an error dialog
        // on w32 sun "1.6.0_14" (build 1.6.0_14-b08)
        System.exit(0);
    }
}
