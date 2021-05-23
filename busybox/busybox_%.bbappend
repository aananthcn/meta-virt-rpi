# Author: Aananth C N
# Leveraged from http://bec-systems.com/site/1145/modifying-the-busybox-config-in-openembedded
# Date: 23 May 2021, 1:12 PM

SRC_URI += "file://fragment.cfg"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"