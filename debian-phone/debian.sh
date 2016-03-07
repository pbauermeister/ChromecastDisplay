#!/system/bin/sh

# Script to be run on Android (rooted, with busybox) to have Debian.

arg=$1

loopno=254
loopdev=/dev/block/loop$loopno
mntpt=/data/local/linux-debian
sdcard=$(readlink -f /sdcard)
lockfile=/data/local/tmp/debian.started

if [ "$arg" == "-h" ] ; then
    echo "Usage: $0 [OPTION]"
    echo "With no argument, enter a shell under Debian."
    echo
    echo "Options:"
    echo "  -h          show this help"
    echo "  mount       init Debian"
    echo "  unmount     uninit Debian"
    echo "  -c COMMAND  execute COMMAND"
    exit 0
fi

if [ "$arg" == "unmount" ] ; then
    echo Unmounting $mntpt/media/sdcard ...
    umount $mntpt/media/sdcard
    for f in dev/pts dev proc sys ; do
        echo Unmounting $mntpt/$f ...
        umount $mntpt/$f
    done
    echo Unmounting $mntpt ...
    umount $mntpt
    echo Removing loop dev $loopdev ...
    losetup -d $loopdev
    rm $loopdev
    rm -f $lockfile
    echo Done.
    exit 0;
fi

if [ "$arg" == "mount" ] ; then
    if [ -f $lockfile ] ; then
        echo "Debian seems already initialized."
        exit 0;
    fi

    echo "Initializing Debian ..."
    echo mknod $loopdev b 7 $loopno ...
    mknod $loopdev b 7 $loopno
    echo $loopdev $loopno created, init needed.
    echo losetup $loopdev /sdcard/linux-debian.img ...
    losetup $loopdev /sdcard/linux-debian.img

    echo busybox mount -t ext2 -o relatime $loopdev $mntpt ...
    busybox mount -t ext2 -o relatime $loopdev $mntpt

    for f in dev dev/pts proc sys ; do
        echo mount -o bind /$f $mntpt/$f ...
        mount -o bind /$f $mntpt/$f
    done

    echo mount -o bind $sdcard $mntpt/media/sdcard ...
    mount -o bind $sdcard $mntpt/media/sdcard

    touch $lockfile
    echo "Debian is ready. Call $0 without parameters to enter shell."
else
    export HOME=/root
    export USER=root
    export TERM=screen
    export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$PATH

    if [ "$arg" == "-c" ] ; then
        shift
        chroot $mntpt /bin/bash -c "$*"
    else
        echo Welcome to Debian!
        chroot $mntpt /bin/bash -l || (
            if [ $? = 127 ] ; then
                echo "Run '$0 mount' to initialize Debian."
            fi  
        )
    fi
fi

