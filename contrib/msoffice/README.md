This project contains wrappers for invoking Microsoft Word and Microsoft Excel using the JNA library.

There are lines of development here: 

* Pure OLE2 Automation, using the objects com.sun.jna.platform.win32.COM.office.MSWord and com.sun.jna.platform.win32.COM.MSExcel

* Java Representation of COM Classes and Interfaces, using the objects under com.sun.jna.platform.win32.util.office

The first approach is simpler to use than the second one; if there is no need for complex interactions with the API, or a simple Office automation is desired, then use the first approach.

The second approach is more complex to use, and requires detailed knowledge of the COM interactions between the different objects. It is the best approach to implement complex interactions with the Microsoft Office applications from a Java application.

There is a lot of work to be done for both approaches:

* Transform JUnit 3 testcases into JUnit 4 testcases.
* Convert "Demos" into JUnit 4 annotated testcases.
* Write additional JUnit 4 annotated testcases.
* Add all interfaces.
* Add all operations of each interface.