# Author: Aananth C N
# Leveraged from https://github.com/Xilinx/meta-petalinux/blob/master/recipes-core/sysvinit/sysvinit-inittab_%25.bbappend
# Date: 23 May 2021, 10:23 PM


PROVIDES = "dom0-startup"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6e07b082adc65c2dcf21a5b0794dfb28"

FILESEXTRAPATHS_prepend  := "${THISDIR}/files:"

SRC_URI = " \
    file://dom0-startup.sh \
    file://COPYING \
"
S = "${WORKDIR}"
DEPENDS_append = " update-rc.d-native"

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 755 ${WORKDIR}/dom0-startup.sh ${D}${sysconfdir}/init.d/
    update-rc.d -r ${D} dom0-startup.sh start 20 S .
}
