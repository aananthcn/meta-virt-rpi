# Author: Aananth C N
# Date: 11 May 2021


PROVIDES = "virtual/kernel"

do_configure_prepend() {

}

do_configure() {

}

do_compile() {
    DTBFILE=bcm2711-rpi-4-b.dtb
    DTBXENO=pi4-64-xen

    # utilize kernel/configs/xen.config fragment
    oe_runmake bcm2711_defconfig xen.config

    # make dtb & overlay dtbo
    oe_runmake broadcom/${DTBFILE}
    oe_runmake overlays/${DTBXENO}.dtbo

    # make Image
    oe_runmake
}

do_compile_append() {

}

do_deploy_append() {
    DTBFILE=bcm2711-rpi-4-b.dtb
    DTBXENO=pi4-64-xen
    DESTDIR=${DEPLOYDIR}/bootfiles-kernel

    # Deploy cmdline.txt only for the main kernel package
    if [ ${KERNEL_PACKAGE_NAME} = "kernel" ]; then
        if [ -d ${DESTDIR} ]; then
            rm -rf ${DESTDIR}
        fi
        install -d ${DESTDIR}
        PITFT="${@bb.utils.contains("MACHINE_FEATURES", "pitft", "1", "0", d)}"
        if [ ${PITFT} = "1" ]; then
            PITFT_PARAMS="fbcon=map:10 fbcon=font:VGA8x8"
        fi
        XEN_ARGS="console=hvc0 clk_ignore_unused quiet "
        CMDLINE_XEN="${XEN_ARGS}${CMDLINE}${PITFT_PARAMS}"
        echo "${CMDLINE_XEN}" > ${DESTDIR}/cmdline.txt
    fi

    # Copy dtb & overlay files
    install -m 644 ${B}/arch/arm64/boot/dts/broadcom/${DTBFILE} ${DESTDIR}
    install -m 644 ${B}/arch/arm64/boot/dts/overlays/${DTBXENO}.dtbo ${DESTDIR}
}