# Author: Aananth C N
# Date: 11 May 2021

# U-Boot verssion and machine specific changes:

require u-boot_lc.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PV}/${MACHINE}:"

SRC_URI_append_${MACHINE} = " \
        file://1-2-bcmgenet-fix-DMA-buffer-management.patch \
        file://2-2-bcmgenet-Add-support-for-rgmii-rxid.patch \
"
