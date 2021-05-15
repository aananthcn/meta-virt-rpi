# Author: Aananth C N
# Leveraged from 'xen-image-minimal.bb' from 'meta-virtualization'
# Date: 16 May 2021, 10:35 AM

DESCRIPTION = "A minimal xen image for dom0"
LICENSE = "MIT"

inherit core-image
include xen-image-minimal.bb

# UBOOT_CONFIG = "lc_rpi_4_defconfig"
