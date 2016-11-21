/* Copyright (c) 2015 Goldstein Lyor, All Rights Reserved
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

import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;

/**
 * Various performance counters structures and definitions
 * @author Lyor Goldstein
 * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa373093(v=vs.85).aspx">Performance Counters Structures</A>
 */
public interface WinPerf {
    /**
     * Describes the performance data block that you queried
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa373157(v=vs.85).aspx">PERF_DATA_BLOCK</A>
     */
    public class PERF_DATA_BLOCK extends Structure {
        public static final List<String> FIELDS = createFieldsOrder(
                "Signature", "LittleEndian", "Version",
                "Revision", "TotalByteLength", "HeaderLength",
                "NumObjectTypes", "DefaultObject", "SystemTime",
                "PerfTime", "PerfFreq", "PerfTime100nSec",
                "SystemNameLength", "SystemNameOffset");

        public char[]        Signature = new char[4];
        public int           LittleEndian;
        public int           Version;
        public int           Revision;
        public int           TotalByteLength;
        public int           HeaderLength;
        public int           NumObjectTypes;
        public int           DefaultObject;
        public SYSTEMTIME    SystemTime = new SYSTEMTIME();
        public LARGE_INTEGER PerfTime = new LARGE_INTEGER();
        public LARGE_INTEGER PerfFreq = new LARGE_INTEGER();
        public LARGE_INTEGER PerfTime100nSec = new LARGE_INTEGER();
        public int           SystemNameLength;
        public int           SystemNameOffset;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    };

    /**
     * Describes an instance of a performance object
     * @see <A HREF="https://msdn.microsoft.com/en-us/library/windows/desktop/aa373159(v=vs.85).aspx">PERF_INSTANCE_DEFINITION</A>
     */
    public class PERF_INSTANCE_DEFINITION extends Structure {
        public static final List<String> FIELDS = createFieldsOrder(
                "ByteLength", "ParentObjectTitleIndex", "ParentObjectInstance",
                "UniqueID", "NameOffset", "NameLength");

        public int ByteLength;
        public int ParentObjectTitleIndex;
        public int ParentObjectInstance;
        public int UniqueID;
        public int NameOffset;
        public int NameLength;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    };

    int PERF_NO_INSTANCES = -1;  // no instances (see NumInstances above)

    //
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //
    //  PERF_COUNTER_DEFINITION.CounterType field values
    //
    //
    //            Counter ID Field Definition:
    //
    //   3      2        2    2    2        1        1    1
    //   1      8        4    2    0        6        2    0    8                0
    //  +--------+--------+----+----+--------+--------+----+----+----------------+
    //  |Display |Calculation  |Time|Counter |        |Ctr |Size|                |
    //  |Flags   |Modifiers    |Base|SubType |Reserved|Type|Fld |   Reserved     |
    //  +--------+--------+----+----+--------+--------+----+----+----------------+
    //
    //
    //  The counter type is the "or" of the following values as described below
    //
    //  select one of the following to indicate the counter's data size
    //
    int PERF_SIZE_DWORD        = 0x00000000;  // 32 bit field
    int PERF_SIZE_LARGE        = 0x00000100;  // 64 bit field
    int PERF_SIZE_ZERO         = 0x00000200;  // for Zero Length fields
    int PERF_SIZE_VARIABLE_LEN = 0x00000300;  // length is in CounterLength field
                                                //  of Counter Definition struct
    //
    //  select one of the following values to indicate the counter field usage
    //
    int PERF_TYPE_NUMBER  = 0x00000000;  // a number (not a counter)
    int PERF_TYPE_COUNTER = 0x00000400;  // an increasing numeric value
    int PERF_TYPE_TEXT    = 0x00000800;  // a text field
    int PERF_TYPE_ZERO    = 0x00000C00;  // displays a zero
    //
    //  If the PERF_TYPE_NUMBER field was selected, then select one of the
    //  following to describe the Number
    //
    int PERF_NUMBER_HEX      = 0x00000000;  // display as HEX value
    int PERF_NUMBER_DECIMAL  = 0x00010000;  // display as a decimal integer
    int PERF_NUMBER_DEC_1000 = 0x00020000;  // display as a decimal/1000
    //
    //  If the PERF_TYPE_COUNTER value was selected then select one of the
    //  following to indicate the type of counter
    //
    int PERF_COUNTER_VALUE     = 0x00000000;  // display counter value
    int PERF_COUNTER_RATE      = 0x00010000;  // divide ctr / delta time
    int PERF_COUNTER_FRACTION  = 0x00020000;  // divide ctr / base
    int PERF_COUNTER_BASE      = 0x00030000;  // base value used in fractions
    int PERF_COUNTER_ELAPSED   = 0x00040000;  // subtract counter from current time
    int PERF_COUNTER_QUEUELEN  = 0x00050000;  // Use Queuelen processing func.
    int PERF_COUNTER_HISTOGRAM = 0x00060000;  // Counter begins or ends a histogram
    int PERF_COUNTER_PRECISION = 0x00070000;  // divide ctr / private clock
    //
    //  If the PERF_TYPE_TEXT value was selected, then select one of the
    //  following to indicate the type of TEXT data.
    //
    int PERF_TEXT_UNICODE = 0x00000000;  // type of text in text field
    int PERF_TEXT_ASCII   = 0x00010000;  // ASCII using the CodePage field
    //
    //  Timer SubTypes
    //
    int PERF_TIMER_TICK   = 0x00000000;  // use system perf. freq for base
    int PERF_TIMER_100NS  = 0x00100000;  // use 100 NS timer time base units
    int PERF_OBJECT_TIMER = 0x00200000;  // use the object timer freq
    //
    //  Any types that have calculations performed can use one or more of
    //  the following calculation modification flags listed here
    //
    int PERF_DELTA_COUNTER   = 0x00400000;  // compute difference first
    int PERF_DELTA_BASE      = 0x00800000;  // compute base diff as well
    int PERF_INVERSE_COUNTER = 0x01000000;  // show as 1.00-value (assumes:
    int PERF_MULTI_COUNTER   = 0x02000000;  // sum of multiple instances
    //
    //  Select one of the following values to indicate the display suffix (if any)
    //
    int PERF_DISPLAY_NO_SUFFIX = 0x00000000;  // no suffix
    int PERF_DISPLAY_PER_SEC   = 0x10000000;  // "/sec"
    int PERF_DISPLAY_PERCENT   = 0x20000000;  // "%"
    int PERF_DISPLAY_SECONDS   = 0x30000000;  // "secs"
    int PERF_DISPLAY_NOSHOW    = 0x40000000;  // value is not displayed
    //
    //  Predefined counter types
    //

    // 32-bit Counter.  Divide delta by delta time.  Display suffix: "/sec"
    int PERF_COUNTER_COUNTER =
                (PERF_SIZE_DWORD | PERF_TYPE_COUNTER | PERF_COUNTER_RATE |
                PERF_TIMER_TICK | PERF_DELTA_COUNTER | PERF_DISPLAY_PER_SEC);


    // 64-bit Timer.  Divide delta by delta time.  Display suffix: "%"
    int PERF_COUNTER_TIMER =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_RATE |
                PERF_TIMER_TICK | PERF_DELTA_COUNTER | PERF_DISPLAY_PERCENT);

    // Queue Length Space-Time Product. Divide delta by delta time. No Display Suffix.
    int PERF_COUNTER_QUEUELEN_TYPE =
                (PERF_SIZE_DWORD | PERF_TYPE_COUNTER | PERF_COUNTER_QUEUELEN |
                PERF_TIMER_TICK | PERF_DELTA_COUNTER | PERF_DISPLAY_NO_SUFFIX);

    // Queue Length Space-Time Product. Divide delta by delta time. No Display Suffix.
    int PERF_COUNTER_LARGE_QUEUELEN_TYPE =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_QUEUELEN |
                PERF_TIMER_TICK | PERF_DELTA_COUNTER | PERF_DISPLAY_NO_SUFFIX);

    // Queue Length Space-Time Product using 100 Ns timebase.
    // Divide delta by delta time. No Display Suffix.
    int PERF_COUNTER_100NS_QUEUELEN_TYPE =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_QUEUELEN |
                PERF_TIMER_100NS | PERF_DELTA_COUNTER | PERF_DISPLAY_NO_SUFFIX);

    // Queue Length Space-Time Product using Object specific timebase.
    // Divide delta by delta time. No Display Suffix.
    int PERF_COUNTER_OBJ_TIME_QUEUELEN_TYPE =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_QUEUELEN |
                PERF_OBJECT_TIMER | PERF_DELTA_COUNTER | PERF_DISPLAY_NO_SUFFIX);

    // 64-bit Counter.  Divide delta by delta time. Display Suffix: "/sec"
    int PERF_COUNTER_BULK_COUNT =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_RATE |
                PERF_TIMER_TICK | PERF_DELTA_COUNTER | PERF_DISPLAY_PER_SEC);

    // Indicates the counter is not a  counter but rather Unicode text Display as text.
    int PERF_COUNTER_TEXT =
                (PERF_SIZE_VARIABLE_LEN | PERF_TYPE_TEXT | PERF_TEXT_UNICODE |
                PERF_DISPLAY_NO_SUFFIX);

    // Indicates the data is a counter  which should not be
    // time averaged on display (such as an error counter on a serial line)
    // Display as is.  No Display Suffix.
    int PERF_COUNTER_RAWCOUNT =
                (PERF_SIZE_DWORD | PERF_TYPE_NUMBER | PERF_NUMBER_DECIMAL |
                PERF_DISPLAY_NO_SUFFIX);

    // Same as PERF_COUNTER_RAWCOUNT except its size is a large integer
    int PERF_COUNTER_LARGE_RAWCOUNT =
                (PERF_SIZE_LARGE | PERF_TYPE_NUMBER | PERF_NUMBER_DECIMAL |
                PERF_DISPLAY_NO_SUFFIX);

    // Special case for RAWCOUNT that want to be displayed in hex
    // Indicates the data is a counter  which should not be
    // time averaged on display (such as an error counter on a serial line)
    // Display as is.  No Display Suffix.
    int PERF_COUNTER_RAWCOUNT_HEX =
                (PERF_SIZE_DWORD | PERF_TYPE_NUMBER | PERF_NUMBER_HEX |
                PERF_DISPLAY_NO_SUFFIX);

    // Same as PERF_COUNTER_RAWCOUNT_HEX except its size is a large integer
    int PERF_COUNTER_LARGE_RAWCOUNT_HEX =
                (PERF_SIZE_LARGE | PERF_TYPE_NUMBER | PERF_NUMBER_HEX |
                PERF_DISPLAY_NO_SUFFIX);


    // A count which is either 1 or 0 on each sampling interrupt (% busy)
    // Divide delta by delta base. Display Suffix: "%"
    int PERF_SAMPLE_FRACTION =
                (PERF_SIZE_DWORD | PERF_TYPE_COUNTER | PERF_COUNTER_FRACTION |
                PERF_DELTA_COUNTER | PERF_DELTA_BASE | PERF_DISPLAY_PERCENT);

    // A count which is sampled on each sampling interrupt (queue length)
    // Divide delta by delta time. No Display Suffix.
    int PERF_SAMPLE_COUNTER =
                (PERF_SIZE_DWORD | PERF_TYPE_COUNTER | PERF_COUNTER_RATE |
                PERF_TIMER_TICK | PERF_DELTA_COUNTER | PERF_DISPLAY_NO_SUFFIX);

    // A label: no data is associated with this counter (it has 0 length)
    // Do not display.
    int PERF_COUNTER_NODATA =
                (PERF_SIZE_ZERO | PERF_DISPLAY_NOSHOW);

    // 64-bit Timer inverse (e.g., idle is measured, but display busy %)
    // Display 100 - delta divided by delta time.  Display suffix: "%"
    int PERF_COUNTER_TIMER_INV =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_RATE |
                PERF_TIMER_TICK | PERF_DELTA_COUNTER | PERF_INVERSE_COUNTER |
                PERF_DISPLAY_PERCENT);

    // The divisor for a sample, used with the previous counter to form a
    // sampled %.  You must check for >0 before dividing by this!  This
    // counter will directly follow the  numerator counter.  It should not
    // be displayed to the user.
    int PERF_SAMPLE_BASE =
                (PERF_SIZE_DWORD | PERF_TYPE_COUNTER | PERF_COUNTER_BASE |
                PERF_DISPLAY_NOSHOW |
                0x00000001);  // for compatibility with pre-beta versions

    // A timer which, when divided by an average base, produces a time
    // in seconds which is the average time of some operation.  This
    // timer times total operations, and  the base is the number of opera-
    // tions.  Display Suffix: "sec"
    int PERF_AVERAGE_TIMER =
                (PERF_SIZE_DWORD | PERF_TYPE_COUNTER | PERF_COUNTER_FRACTION |
                PERF_DISPLAY_SECONDS);

    // Used as the denominator in the computation of time or count
    // averages.  Must directly follow the numerator counter.  Not dis-
    // played to the user.
    int PERF_AVERAGE_BASE =
                (PERF_SIZE_DWORD | PERF_TYPE_COUNTER | PERF_COUNTER_BASE |
                PERF_DISPLAY_NOSHOW |
                0x00000002);  // for compatibility with pre-beta versions


    // A bulk count which, when divided (typically) by the number of
    // operations, gives (typically) the number of bytes per operation.
    // No Display Suffix.
    int PERF_AVERAGE_BULK =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_FRACTION  |
                PERF_DISPLAY_NOSHOW);

    // 64-bit Timer in object specific units. Display delta divided by
    // delta time as returned in the object type header structure.  Display suffix: "%"
    int PERF_OBJ_TIME_TIMER =
                (PERF_SIZE_LARGE   | PERF_TYPE_COUNTER  | PERF_COUNTER_RATE |
                 PERF_OBJECT_TIMER | PERF_DELTA_COUNTER | PERF_DISPLAY_PERCENT);


    // 64-bit Timer in 100 nsec units. Display delta divided by
    // delta time.  Display suffix: "%"
    int PERF_100NSEC_TIMER =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_RATE |
                PERF_TIMER_100NS | PERF_DELTA_COUNTER | PERF_DISPLAY_PERCENT);

    // 64-bit Timer inverse (e.g., idle is measured, but display busy %)
    // Display 100 - delta divided by delta time.  Display suffix: "%"
    int PERF_100NSEC_TIMER_INV =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_RATE |
                PERF_TIMER_100NS | PERF_DELTA_COUNTER | PERF_INVERSE_COUNTER  |
                PERF_DISPLAY_PERCENT);

    // 64-bit Timer.  Divide delta by delta time.  Display suffix: "%"
    // Timer for multiple instances, so result can exceed 100%.
    int PERF_COUNTER_MULTI_TIMER =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_RATE |
                PERF_DELTA_COUNTER | PERF_TIMER_TICK | PERF_MULTI_COUNTER |
                PERF_DISPLAY_PERCENT);

    // 64-bit Timer inverse (e.g., idle is measured, but display busy %)
    // Display 100 * _MULTI_BASE - delta divided by delta time.
    // Display suffix: "%" Timer for multiple instances, so result
    // can exceed 100%.  Followed by a counter of type _MULTI_BASE.
    int PERF_COUNTER_MULTI_TIMER_INV =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_RATE |
                PERF_DELTA_COUNTER | PERF_MULTI_COUNTER | PERF_TIMER_TICK |
                PERF_INVERSE_COUNTER | PERF_DISPLAY_PERCENT);

    // Number of instances to which the preceding _MULTI_..._INV counter
    // applies.  Used as a factor to get the percentage.
    int PERF_COUNTER_MULTI_BASE =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_BASE |
                PERF_MULTI_COUNTER | PERF_DISPLAY_NOSHOW);

    // 64-bit Timer in 100 nsec units. Display delta divided by delta time.
    // Display suffix: "%" Timer for multiple instances, so result can exceed 100%.
    int PERF_100NSEC_MULTI_TIMER =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_DELTA_COUNTER  |
                PERF_COUNTER_RATE | PERF_TIMER_100NS | PERF_MULTI_COUNTER |
                PERF_DISPLAY_PERCENT);

    // 64-bit Timer inverse (e.g., idle is measured, but display busy %)
    // Display 100 * _MULTI_BASE - delta divided by delta time.
    // Display suffix: "%" Timer for multiple instances, so result
    // can exceed 100%.  Followed by a counter of type _MULTI_BASE.
    int PERF_100NSEC_MULTI_TIMER_INV =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_DELTA_COUNTER  |
                PERF_COUNTER_RATE | PERF_TIMER_100NS | PERF_MULTI_COUNTER |
                PERF_INVERSE_COUNTER | PERF_DISPLAY_PERCENT);

    // Indicates the data is a fraction of the following counter  which
    // should not be time averaged on display (such as free space over
    // total space.) Display as is.  Display the quotient as "%".
    int PERF_RAW_FRACTION =
                (PERF_SIZE_DWORD | PERF_TYPE_COUNTER | PERF_COUNTER_FRACTION |
                PERF_DISPLAY_PERCENT);

    int PERF_LARGE_RAW_FRACTION =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_FRACTION |
                PERF_DISPLAY_PERCENT);

    // Indicates the data is a base for the preceding counter which should
    // not be time averaged on display (such as free space over total space.)
    int PERF_RAW_BASE =
                (PERF_SIZE_DWORD | PERF_TYPE_COUNTER | PERF_COUNTER_BASE |
                PERF_DISPLAY_NOSHOW |
                0x00000003);  // for compatibility with pre-beta versions

    int PERF_LARGE_RAW_BASE =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_BASE |
                PERF_DISPLAY_NOSHOW );

    // The data collected in this counter is actually the start time of the
    // item being measured. For display, this data is subtracted from the
    // sample time to yield the elapsed time as the difference between the two.
    // In the definition below, the PerfTime field of the Object contains
    // the sample time as indicated by the PERF_OBJECT_TIMER bit and the
    // difference is scaled by the PerfFreq of the Object to convert the time
    // units into seconds.
    int PERF_ELAPSED_TIME =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_ELAPSED |
                PERF_OBJECT_TIMER | PERF_DISPLAY_SECONDS);
    //
    //  The following counter type can be used with the preceding types to
    //  define a range of values to be displayed in a histogram.
    //

    int PERF_COUNTER_HISTOGRAM_TYPE = 0x80000000; // Counter begins or ends a histogram
    //
    //  This counter is used to display the difference from one sample
    //  to the next. The counter value is a constantly increasing number
    //  and the value displayed is the difference between the current
    //  value and the previous value. Negative numbers are not allowed
    //  which shouldn't be a problem as long as the counter value is
    //  increasing or unchanged.
    //
    int PERF_COUNTER_DELTA =
                (PERF_SIZE_DWORD | PERF_TYPE_COUNTER | PERF_COUNTER_VALUE |
                PERF_DELTA_COUNTER | PERF_DISPLAY_NO_SUFFIX);

    int PERF_COUNTER_LARGE_DELTA =
                (PERF_SIZE_LARGE | PERF_TYPE_COUNTER | PERF_COUNTER_VALUE |
                PERF_DELTA_COUNTER | PERF_DISPLAY_NO_SUFFIX);
    //
    //  The precision counters are timers that consist of two counter values:
    //          1) the count of elapsed time of the event being monitored
    //          2) the "clock" time in the same units
    //
    //  the precition timers are used where the standard system timers are not
    //  precise enough for accurate readings. It's assumed that the service
    //  providing the data is also providing a timestamp at the same time which
    //  will eliminate any error that may occur since some small and variable
    //  time elapses between the time the system timestamp is captured and when
    //  the data is collected from the performance DLL. Only in extreme cases
    //  has this been observed to be problematic.
    //
    //  when using this type of timer, the definition of the
    //  PERF_PRECISION_TIMESTAMP counter must immediately follow the
    //  definition of the PERF_PRECISION_*_TIMER in the Object header
    //
    // The timer used has the same frequency as the System Performance Timer
    int PERF_PRECISION_SYSTEM_TIMER =
            (PERF_SIZE_LARGE    | PERF_TYPE_COUNTER     | PERF_COUNTER_PRECISION    |
             PERF_TIMER_TICK    | PERF_DELTA_COUNTER    | PERF_DISPLAY_PERCENT   );
    //
    // The timer used has the same frequency as the 100 NanoSecond Timer
    int PERF_PRECISION_100NS_TIMER  =
            (PERF_SIZE_LARGE    | PERF_TYPE_COUNTER     | PERF_COUNTER_PRECISION    |
             PERF_TIMER_100NS   | PERF_DELTA_COUNTER    | PERF_DISPLAY_PERCENT   );
    //
    // The timer used is of the frequency specified in the Object header's
    //  PerfFreq field (PerfTime is ignored)
    int PERF_PRECISION_OBJECT_TIMER =
            (PERF_SIZE_LARGE    | PERF_TYPE_COUNTER     | PERF_COUNTER_PRECISION    |
             PERF_OBJECT_TIMER  | PERF_DELTA_COUNTER    | PERF_DISPLAY_PERCENT   );
    //
    // This is the timestamp to use in the computation of the timer specified
    // in the previous description block
    int PERF_PRECISION_TIMESTAMP = PERF_LARGE_RAW_BASE;
    //
    //  The following are used to determine the level of detail associated
    //  with the counter.  The user will be setting the level of detail
    //  that should be displayed at any given time.
    //
    //
    int PERF_DETAIL_NOVICE   = 100; // The uninformed can understand it
    int PERF_DETAIL_ADVANCED = 200; // For the advanced user
    int PERF_DETAIL_EXPERT   = 300; // For the expert user
    int PERF_DETAIL_WIZARD   = 400; // For the system designer

    int PERF_NO_UNIQUE_ID = -1;

    int PERF_QUERY_OBJECTS = 0x80000000;
    int PERF_QUERY_GLOBAL  = 0x80000001;
    int PERF_QUERY_COSTLY  = 0x80000002;
}
