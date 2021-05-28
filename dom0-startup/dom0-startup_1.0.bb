# Author: Aananth C N
# Leveraged from https://github.com/Xilinx/meta-petalinux/blob/master/recipes-core/sysvinit/sysvinit-inittab_%25.bbappend
# Date: 23 May 2021, 10:23 PM


PROVIDES = "dom0-startup"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6e07b082adc65c2dcf21a5b0794dfb28"

FILESEXTRAPATHS_prepend  := "${THISDIR}/files:"

SRC_URI = " \
    file://COPYING \
    file://dom0-startup.sh \
    file://dom0-startup.service \
"
S = "${WORKDIR}"

inherit systemd

SYSTEMD_SERVICE_${PN} = "dom0-startup.service"
SYSTEMD_AUTO_ENABLE = "enable"

FILES_${PN} += " \
    ${systemd_system_unitdir}/dom0-startup.service \
    ${bindir}/dom0-startup.sh \
"

do_install() {
    install -d ${D}${bindir}
    install -m 755 ${WORKDIR}/dom0-startup.sh ${D}${bindir}

    install -d ${D}${systemd_system_unitdir}
    install -m 644 ${WORKDIR}/dom0-startup.service ${D}${systemd_system_unitdir}
}
