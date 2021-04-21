#
# This file was derived from the 'Hello World!' example recipe in the
# Yocto Project Development Manual.
#

DESCRIPTION = "Xen Raspberry Pi4 Builder"
SECTION = "Virtualization"
DEPENDS = ""
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4295d895d4b5ce9d070263d52f030e49"

# FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRCREV = "RELEASE-4.13.0"
SRC_URI = "git://xenbits.xen.org/xen.git;branch=stable-4.13"

# The first patch below is discussed in xen mailing list.
SRC_URI_append = " \
    file://0001-xen_4.13_object_file_redefined_error.patch \
"

# The default LDFLAGS include switches that are recognized by gcc, but ld
# could not understand and threw errors. Let us comment it.
LDFLAGS = ""

PREMIRRORS_prepend = "\
    https://github.com/xen-project/xen.git \
    git://github.com/xen-project/xen.git "

S = "${WORKDIR}/git"

do_deploy() {
    echo "Under construction!"
}

addtask deploy after do_install

do_configure_prepend () {
    echo "CONFIG_DEBUG=y" >> ${S}/xen/arch/arm/configs/arm64_defconfig
    echo "CONFIG_SCHED_ARINC653=y" >> ${S}/xen/arch/arm/configs/arm64_defconfig
}

EXTRA_OECMAKE += "CONFIG_EARLY_PRINTK=8250,0xfe215040,2 dist-xen"
EXTRA_OECONF += "CONFIG_EARLY_PRINTK=8250,0xfe215040,2 defconfig"

do_configure() {
    make -C xen defconfig
}

do_compile() {
    make dist-xen
}