Building JNA for FreeBSD
========================

This recipe was used to build the FreeBSD aarch64 native library on amd64:

```
# Fetch FreeBSD 13.2 image and extract it
wget https://download.freebsd.org/snapshots/VM-IMAGES/13.2-STABLE/aarch64/20231216/FreeBSD-13.2-STABLE-arm64-aarch64-20231216-9986fd59d855-256898.qcow2.xz
xz -d FreeBSD-13.2-STABLE-arm64-aarch64-20231216-9986fd59d855-256898.qcow2.xz

# Ensure there is enough space in the image
qemu-img resize FreeBSD-13.2-STABLE-arm64-aarch64-20231216-9986fd59d855-256898.qcow2 +5G

# Launch aarch64 emulator with downloaded image
qemu-system-aarch64 -m 4096M -cpu cortex-a57 -M virt  \
        -bios /usr/lib/u-boot/qemu_arm64/u-boot.bin \
        -serial telnet::4444,server -nographic \
        -drive if=none,file=PATH_TO_IMAGE/FreeBSD-13.2-STABLE-arm64-aarch64-20231216-9986fd59d855-256898.qcow2,id=hd0 \
        -device virtio-blk-device,drive=hd0 \
        -device virtio-net-device,netdev=net0 \
        -netdev user,id=net0

# Connect to terminal for emulated system and boot into single user mode with default shell
telnet localhost 4444

# Resize partitions
gpart show /dev/vtbd0
gpart recover /dev/vtbd0
gpart show /dev/vtbd0
gpart resize -i 3 /dev/vtbd0
growfs /

# Exit single user mode (BSD boots to multi-user)
exit

# Login as root

# Install prerequisites - part 1 - java, build system, rsync
pkg install openjdk17 wget automake rsync gmake gcc
# Adjust fstab (optional, only needed if reboot is planned)
# fdesc   /dev/fd         fdescfs         rw      0       0
# proc    /proc           procfs          rw      0       0

# Install prerequisites - part 2 - ant
wget https://dlcdn.apache.org//ant/binaries/apache-ant-1.10.14-bin.zip 
unzip apache-ant-1.10.14-bin.zip

# Transfer JNA source code to build environment
rsync -a --exclude=.git USER@BUILD_HOST:src/jnalib/ jnalib/

# Set current date and time (YYYYMMDDHHMM)
date 202312231627

# Build JNA and run unittests
cd jnalib
/root/apache-ant-1.10.14/bin/ant

# Copy jna native library back to host system

```
