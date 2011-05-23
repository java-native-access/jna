Dynamically Typed Languages
===========================

Languages such as Jython or JRuby may find it more convenient to access the `NativeLibrary` and Function classes directly rather than establishing a dedicated interface.
Here's a brief example of using JNA from JRuby:
 
    require 'java'

    module Libc
      @@lib = com.sun.jna.NativeLibrary.getInstance("c")
      @@ptr_funcs = [ 'fopen', 'malloc', 'calloc' ]
      def self.method_missing(meth, *args)
        if @@ptr_funcs.include?(meth.to_s)
          @@lib.getFunction(meth.to_s).invokePointer(args.to_java)
        else
          @@lib.getFunction(meth.to_s).invokeInt(args.to_java)
        end
      end
      O_RDONLY = 0
      O_WRONLY = 1
      O_RDWR = 2
    end

    Libc.puts("puts from libc")
    Libc.printf("Hello %s, from printf\n", "World")

    file = Libc.open("/dev/stdout", 1, Libc::O_WRONLY)
    n = Libc.write(file, "Test\n", 5)
    puts "Wrote #{n} bytes via Libc"

    path = "/dev/stdout"
    fp = Libc.fopen(path, "w+")
    Libc.fprintf(fp, "fprintf to %s via stdio\n", path)
    Libc.fflush(fp)
    Libc.fclose(fp)


