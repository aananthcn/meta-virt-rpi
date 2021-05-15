# Author: Aananth C N
# Date: 11 May 2021

FILESEXTRAPATHS_prepend := "${THISDIR}/files/5.4/:"

# Linux patches and changes specific to raspberrypi4
SRC_URI_append = " \
        file://0001-Add-Xen-overlay-for-the-Pi-4.patch \
        file://0002-Disable-DMA-in-sdhci-driver.patch \
"

require linux-raspberrypi.inc