#!/bin/sh

set -e

script=debian.sh
path=$(dirname $0)/$script
cp $path /media/sdcard/

echo "Script $path copied to sdcard."
echo
echo 'Now on the android shell, do the following:'
echo '  su -c "mount -o remount,rw $(mount | grep system | cut -d '\'' '\'' -f 1,2); cp /storage/sdcard0/'$script' /system/bin/'$script'; mount -o remount,ro $(mount | grep system | cut -d '\'' '\'' -f 1,2)" -'
