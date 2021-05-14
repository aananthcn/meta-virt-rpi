# Author: Aananth C N
# Date: 11 May 2021

FILESEXTRAPATHS_prepend := "${THISDIR}/common:"

SRC_URI_append = " \
    file://lc_rpi_4_defconfig \
"

UBOOT_MACHINE = "lc_rpi_4_config"

do_configure_prepend () {
    if [ ! -s ${S}/configs/lc_rpi_4_defconfig ]; then
        install -m 644 ${S}/../lc_rpi_4_defconfig ${S}/configs/
    fi
}
