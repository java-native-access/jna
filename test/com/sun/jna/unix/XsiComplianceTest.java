package com.sun.jna.unix;

import com.sun.jna.LastErrorException;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import junit.framework.TestCase;

public final class XsiComplianceTest extends TestCase {

   public static void main(String[] args) {
      junit.textui.TestRunner.run(XsiComplianceTest.class);
   }

   /**
    * Regression test for https://github.com/java-native-access/jna/issues/392
    * 
    * Depending on the toolchain on GCC/GLIBC the wrong definition of
    * strerror_r was included.
    * 
    * This leads to broken messages.
    */
   public void testErrorReporting() {
       boolean exceptionWasCaught = false;
       try {
           INSTANCE.read(Integer.MAX_VALUE, new Memory(1024), 1024);
       } catch (LastErrorException ex) {
           exceptionWasCaught = true;
           assertEquals("[9] Bad file descriptor", ex.getMessage());
           assertEquals(9, ex.getErrorCode());
       }
       assertEquals(true, exceptionWasCaught);
   }
   
   static POSIX INSTANCE = (POSIX) Native.loadLibrary(POSIX.class);

   interface POSIX extends Library {

      long read(int fildes, Pointer buf, long nbyte) throws LastErrorException;
   }
}