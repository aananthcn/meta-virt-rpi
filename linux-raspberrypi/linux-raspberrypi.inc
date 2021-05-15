# Author: Aananth C N
# Date: 11 May 2021

do_configure_prepend() {

}

do_configure() {

}

do_compile() {
    DTBFILE=bcm2711-rpi-4-b.dtb
    DTBXENO=pi4-64-xen

    if [ ! -s ${B}/.config ]; then
        # utilize kernel/configs/xen.config fragment
        oe_runmake bcm2711_defconfig xen.config
    fi

    # make dtb & overlay dtbo
    oe_runmake broadcom/${DTBFILE}
    oe_runmake overlays/${DTBXENO}.dtbo

    # make Image
    if [ ! -s ${B}/arch/arm64/boot/Image ]; then
        oe_runmake
    fi
}

do_compile_append() {

}

do_deploy_append() {
    DTBFILE=bcm2711-rpi-4-b.dtb
    DTBXENO=pi4-64-xen

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

        # Copy dtb & overlay files
        install -m 644 ${B}/arch/arm64/boot/dts/broadcom/${DTBFILE} ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}
        install -m 644 ${B}/arch/arm64/boot/dts/overlays/${DTBXENO}.dtbo ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}
    fi
}