# Author: Aananth C N
# Date: 11 May 2021

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
        # oe_runmake O=.build-arm64
        oe_runmake
    fi
}

do_compile_append() {

}