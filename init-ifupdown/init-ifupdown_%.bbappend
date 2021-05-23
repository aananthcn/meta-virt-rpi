# Author: Aananth C N
# Date: 23 May 2021, 10:32 AM

# Note: I found this file by the following command
# oe-pkgdata-util find-path /etc/network/interfaces
# Also refer: https://stackoverflow.com/questions/54605656/how-to-overwrite-linux-system-files-into-the-yocto-filesystem


FILESEXTRAPATHS_prepend := "${THISDIR}/files:"