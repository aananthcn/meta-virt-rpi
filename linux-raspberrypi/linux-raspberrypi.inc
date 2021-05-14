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

do_deploy_append() {
    # Deploy cmdline.txt only for the main kernel package
    if [ ${KERNEL_PACKAGE_NAME} = "kernel" ]; then
        install -d ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}
        PITFT="${@bb.utils.contains("MACHINE_FEATURES", "pitft", "1", "0", d)}"
        if [ ${PITFT} = "1" ]; then
            PITFT_PARAMS="fbcon=map:10 fbcon=font:VGA8x8"
        fi
        XEN_ARGS="console=hvc0 clk_ignore_unused "
        CMDLINE_XEN="${XEN_ARGS}${CMDLINE}${PITFT_PARAMS}"
        echo "${CMDLINE_XEN}" > ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/cmdline.txt
    fi
}