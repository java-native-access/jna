/* Copyright (c) 2009 Timothy Wall, All Rights Reserved
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
package com.sun.jna;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;

public class WebStartTest extends TestCase {
    
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
        + "    <homepage href='http://jna.dev.java.net'/>\n"
        + "    <description>Local JNLP launch test.</description>\n"
        + "    <description kind='short'>Launch Test</description>\n"
        + "  </information>\n"
        // all-permissions is required for <nativelib>s
        // but will cause unsigned jars to fail (irrespective of policy)
        + "  <security><all-permissions/></security>\n"
        + "  <resources>\n"
        + "    <j2se version='1.3+' href='http://java.sun.com/products/autodl/j2se'/>\n"
        + "    <jar href='jna.jar'/>\n"
        + "    <jar href='jna-test.jar'/>\n"
        + "    <jar href='junit.jar'/>\n"
        + "    <nativelib href='jnidispatch.jar'/>\n"
        + "  </resources>\n"
        + "  <offline-allowed/>\n"
        + "  <application-desc main-class='" + WebStartTest.class.getName() + "'>\n"
        + "    <argument>{CLASS}</argument>\n"
        + "    <argument>{METHOD}</argument>\n"
        + "    <argument>{PORT}</argument>\n"
        + "  </application-desc>\n"
        + "</jnlp>";

    public void testLaunchedUnderWebStart() throws Exception {
        assertNotNull("Test not launched under web start",
                      System.getProperty("javawebstart.version"));
    }

    public void testDetectFailure() {
        fail("This test is supposed to fail");
    }

    public void testDetectError() {
        throw new Error("This test is supposed to error");
    }

    public void testJNLPFindLibrary() {
        String path = Native.getWebStartLibraryPath("jnidispatch");
        assertNotNull("Web Start library path not found", path);
    }

    private void runTestUnderWebStart(String testClass, String testMethod) throws Exception {
        String BUILDDIR = System.getProperty("jna.builddir",
                                             "build"
                                             + (Platform.is64Bit()
                                                ? "-d64" : ""));
        String codebase = new File(BUILDDIR, "jws").toURI().toURL().toString();

        ServerSocket s = new ServerSocket(0);
        s.setSoTimeout(10000);
        int port = s.getLocalPort();

        //File jnlp = new File(getName() + ".jnlp");
        File jnlp = File.createTempFile(getName(), ".jnlp");
        jnlp.deleteOnExit();
        String contents = JNLP.replace("{CLASS}", testClass);
        contents = contents.replace("{METHOD}", testMethod);
        contents = contents.replace("{CODEBASE}", codebase);
        contents = contents.replace("{JNLP_FILE}", jnlp.toURI().toURL().toString());
        contents = contents.replace("{PORT}", String.valueOf(port));

        OutputStream os = new FileOutputStream(jnlp);
        os.write(contents.getBytes());
        os.close();
        File keystore = new File("jna.keystore");
        String JAVA_HOME = System.getProperty("java.home");
        String LIB = new File(JAVA_HOME, "/lib").getAbsolutePath();
        if (!new File(LIB, "javaws.jar").exists()) {
            LIB = new File("/System/Library/Frameworks/JavaVM.framework/Resources/Deploy.bundle/Contents/Home/lib").getAbsolutePath();
            if (!new File(LIB, "javaws.jar").exists()) {
                throw new IOException("javaws.jar not found");
            }
        }
        String PS = System.getProperty("path.separator");
        String[] cmd = {
            "javaws",
            "-Xnosplash",
            "-wait", 
            jnlp.toURI().toURL().toString(),
        };
        Process p = Runtime.getRuntime().exec(cmd);
        StringBuffer output = new StringBuffer();
        class SocketHandler extends Thread {
            private InputStream is;
            private StringBuffer sb;
            public SocketHandler(Socket s, StringBuffer b) throws IOException {
                this.is = s.getInputStream();
                this.sb = b;
            }
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
                    }
                }
                try { is.close(); } catch(IOException e) { }
            }
        }

        try {
            Thread out = new SocketHandler(s.accept(), output);
            out.start();
        }
        catch(SocketTimeoutException e) {
            //p.destroy();
            //throw new Error("JWS Timed out");
        }
        p.waitFor();
        
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
                    return;
                }
                fail("Failed to detect test failure");
            }
            else if (getName().equals("testDetectError")) {
                try {
                    runTestUnderWebStart(getClass().getName(), getName());
                }
                catch(AssertionFailedError e) {
                    fail("Wrong error type: " + e);
                }
                catch(Error e) {
                    return;
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
    
    private File findDeploymentProperties() {
        String path = System.getProperty("user.home");
        File deployment = new File(path + "/Application Data/Sun/Java/Deployment");
        if (!deployment.exists()) {
            deployment = new File(path + "/Library/Caches/Java");
        }
        if (!deployment.exists()) {
            deployment = new File(path + "/.java/deployment");
        }
        if (!deployment.exists()) {
            throw new Error("Deployment directory does not exist; save Java Control Panel settings to initialize it");
        }
        return new File(deployment, "deployment.properties");
    }

    private static final String POLICY_KEY = 
        "deployment.user.security.policy";
    private static final String CERTS_KEY =
        "deployment.user.security.trusted.certs";
    public void runBare() throws Throwable {
        if (runningWebStart()) {
            super.runBare();
        }
        else if (!GraphicsEnvironment.isHeadless()) {
            //File policy = new File(getName() + ".policy");
            File policy = File.createTempFile(getName(), ".policy");
            policy.deleteOnExit();
            OutputStream os = new FileOutputStream(policy);
            os.write(POLICY.getBytes());
            os.close();
            File dpfile = findDeploymentProperties();
            Properties saved = new Properties();
            saved.load(new FileInputStream(dpfile));
            Properties props = new Properties();
            props.putAll(saved);
            props.setProperty(CERTS_KEY, "jna.keystore");
            props.setProperty(POLICY_KEY, policy.getAbsolutePath());
            props.store(new FileOutputStream(dpfile), "deployment.properties (for testing)");
            try {
                runTestUnderWebStart();
            }
            finally {
                saved.store(new FileOutputStream(dpfile), "deployment.properties");
            }
        }
    }

    private static void runTestCaseTest(String testClass, String method, int port) {
        try {
            TestCase test = (TestCase)Class.forName(testClass).newInstance();
            test.setName(method);
            TestResult result = new TestResult();
            test.run(result);
            Socket s = new Socket(InetAddress.getLocalHost(), port);
            OutputStream os = s.getOutputStream();
            if (result.failureCount() != 0) {
                Enumeration e = result.failures();
                Throwable t = ((TestFailure)e.nextElement()).thrownException();
                t.printStackTrace(new PrintStream(os));
                s.close();
                System.exit(1);
            }
            else if (result.errorCount() != 0) {
                Enumeration e = result.errors();
                Throwable t = ((TestFailure)e.nextElement()).thrownException();
                t.printStackTrace(new PrintStream(os));
                s.close();
                System.exit(2);
            }
            s.close();
            Thread.sleep(5000);
            System.exit(0);
        }
        catch(Throwable e) {
            // Can't communicate back to launching process
            showMessage("ERROR: " + e.getMessage());
            System.exit(-1);
        }
    }

    private static void showMessage(String msg) {
        JFrame f = new JFrame("Web Start Test Failure");
        f.getContentPane().add(new JLabel(msg));
        f.pack();
        f.setLocation(100, 100);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try { Thread.sleep(60000); } catch(Exception e) { }
    }

    public static void main(String[] args) {
        if (runningWebStart()) {
            String testClass = args.length > 0
                ? args[0] : WebStartTest.class.getName();
            String testMethod = args.length > 1
                ? args[1] : "testLaunchedUnderWebStart";
            int port = args.length > 2
                ? Integer.parseInt(args[2]) : 8080;
            runTestCaseTest(testClass, testMethod, port);
        }
        else {
            junit.textui.TestRunner.run(WebStartTest.class);
        }
    }
}
