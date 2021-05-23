# Author: Aananth C N
# Date: 23 May 2021, 10:17 AM

# Note: I found this file by the following command
# oe-pkgdata-util find-path /etc/hostname
# oe-pkgdata-util find-path /etc/fstab
# Also refer: https://stackoverflow.com/questions/54605656/how-to-overwrite-linux-system-files-into-the-yocto-filesystem

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

hostname="xen-dom0"