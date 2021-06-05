
include xen-src.inc

PV = "${XEN_REL}"
S = "${WORKDIR}/git"

require xen.inc
require xen-hypervisor.inc


# Following lines were added by Aananth on 30 May 2021 to resolve systemd
# startup issues related to xen daemons
FILESEXTRAPATHS_prepend  := "${THISDIR}/files:"

SRC_URI += "file://xen.conf"

FILES_${PN} += " \
    ${libdir}/modules-load.d/xen.conf \
"

do_install_append() {
    install -d ${D}${libdir}/modules-load.d/
    install -m 644 ${B}/../xen.conf ${D}${libdir}/modules-load.d/xen.conf
}

