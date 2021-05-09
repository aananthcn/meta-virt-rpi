# Linux patches and changes specific to raspberrypi4


FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI_append = " \
        file://0001-Add-Xen-overlay-for-the-Pi-4.patch \
        file://0002-Disable-DMA-in-sdhci-driver.patch \
        file://0003-Fix-PCIe-in-dom0-for-RPi4.patch \
"

do_configure_prepend() {

}

do_configure() {

}

do_compile() {
    DTBFILE=bcm2711-rpi-4-b.dtb
    DTBXENO=pi4-64-xen

    if [ ! -s ${B}/.build-arm64/.config ]; then
        # utilize kernel/configs/xen.config fragment
        oe_runmake O=${B}/.build-arm64 bcm2711_defconfig xen.config
    fi
    oe_runmake O=${B}/.build-arm64 broadcom/${DTBFILE}
    oe_runmake O=${B}/.build-arm64 overlays/${DTBXENO}.dtbo
    if [ ! -s ${B}/.build-arm64/arch/arm64/boot/Image ]; then
        oe_runmake O=.build-arm64
    fi
}

do_compile_append() {

}