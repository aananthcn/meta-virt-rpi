# Author: Aananth C N
# Date: 22 May 2021, 11:37 AM

inherit kernel
PROVIDES = "virtual/kernel"

FILESEXTRAPATHS_prepend := "${THISDIR}/files/4.19/:"

# Linux patches and changes specific to raspberrypi4
SRC_URI = " \
    git://github.com/raspberrypi/linux.git;protocol=http;branch=rpi-4.19.y \
    file://0001-Add-Xen-overlay-for-the-Pi-4.patch \
    file://0002-Disable-DMA-in-sdhci-driver.patch \
    file://0003-Fix-PCIe-in-dom0-for-RPi4.patch \
"
SRCREV = "cc39f1c9f82f6fe5a437836811d906c709e0661c"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

do_configure() {
    # utilize kernel/configs/xen.config fragment
    oe_runmake -C ${S} O=${B} bcm2711_defconfig xen.config
}

do_compile() {
    DTBFILE=bcm2711-rpi-4-b.dtb
    DTBXENO=pi4-64-xen

    # make dtb & overlay dtbo
    oe_runmake broadcom/${DTBFILE}
    oe_runmake overlays/${DTBXENO}.dtbo

    # make Image
    oe_runmake
}

do_deploy_append() {
    DTBFILE=bcm2711-rpi-4-b.dtb
    DTBXENO=pi4-64-xen
    DESTDIR=${DEPLOYDIR}/bootfiles-kernel

    # Deploy cmdline.txt only for the main kernel package
    if [ -d ${DESTDIR} ]; then
        rm -rf ${DESTDIR}
    fi
    install -d ${DESTDIR}
    
    # Add cmdline.txt file with good know working value
    CMDLINE_XEN="console=hvc0 clk_ignore_unused root=/dev/mmcblk0p2 rootwait"
    echo "${CMDLINE_XEN}" > ${DESTDIR}/cmdline.txt

    # Copy dtb & overlay files
    install -m 644 ${B}/arch/arm64/boot/dts/broadcom/${DTBFILE} ${DESTDIR}
    install -m 644 ${B}/arch/arm64/boot/dts/overlays/${DTBXENO}.dtbo ${DESTDIR}
}
