# Author: Aananth C N
# Date: 11 May 2021

FILESEXTRAPATHS_prepend := "${THISDIR}/common:"

SRC_URI_append = " \
    file://acn_rpi_4_defconfig \
    file://uboot.env \
"

UBOOT_MACHINE = "acn_rpi_4_config"

do_configure_prepend () {
    install -m 644 ${S}/../acn_rpi_4_defconfig ${S}/configs/
}

do_deploy_append() {
    DESTDIR=${DEPLOYDIR}/bootfiles-u-boot
    rm -rf ${DESTDIR}
    mkdir -p ${DESTDIR}
    cp ${S}/../uboot.env ${DESTDIR}
}
