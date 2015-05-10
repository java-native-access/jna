RaspberryPi Development Environment
===================================

After installing the [Raspian](http://downloads.raspberrypi.org/raspbian_latest) OS onto your SD card, a few more tools must be installed before you can build JNA on a
RaspberryPi. The additional tools below are needed as of Raspian, Release date:2015-05-05.

* Various build tools and X11 libs:

        $ sudo apt-get install autoconf automake libtool libx11-dev libxt-dev

    If you are missing these tools, when you build you are likely to see errors like those below:
  
        ./autogen.sh: 2: exec: autoreconf: not found
     
    or
        
        fatal error: X11/Xlib.h: No such file or directory

        
