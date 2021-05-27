# Author: Aananth C N
# Date: 27 May 2021, 9:54 PM
# Reference: https://hub.mender.io/t/how-to-configure-networking-using-systemd-in-yocto-project/1097


PACKAGECONFIG_append = " networkd resolved"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://eth.network \
    file://wlan.network \
"

FILES_${PN} += " \
    ${sysconfdir}/systemd/network/eth.network \
    ${sysconfdir}/systemd/network/wlan.network \
"

RDEPENDS_${PN}_append = " wpa-supplicant "

do_install_append() {
    install -d ${D}${sysconfdir}/systemd/network
    install -m 0644 ${WORKDIR}/eth.network ${D}${sysconfdir}/systemd/network
    install -m 0644 ${WORKDIR}/wlan.network ${D}${sysconfdir}/systemd/network
}