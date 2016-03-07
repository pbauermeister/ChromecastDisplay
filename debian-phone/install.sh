#!/bin/sh

set -e
cp debian.sh /media/sdcard/

echo 'Script debian.sh copied to sdcard.'
echo
echo 'Now on the android shell, do the following:'
echo '  su -c "mount -o remount,rw $(mount | grep system | cut -d '\'' '\'' -f 1,2); cp /storage/sdcard0/debian.sh /system/bin/debian.sh; mount -o remount,ro $(mount | grep system | cut -d '\'' '\'' -f 1,2)" -'
