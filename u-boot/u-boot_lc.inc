FILESEXTRAPATHS_prepend := "${THISDIR}/common:"

SRC_URI_append = " \
    file://lc_rpi_4_defconfig \
"

UBOOT_MACHINE = "lc_rpi_4_config"

do_configure_prepend () {
    mv ${S}/../lc_rpi_4_defconfig ${S}/configs/
}