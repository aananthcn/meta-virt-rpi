# Author: Aananth C N
# Date: 16 May 2021, 12.02 AM

do_deploy() {
    install -d ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}
    # cp ${S}/config.txt ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/

    XEN_ADDR=0x0020000
    DTBXENO=pi4-64-xen

    echo "# kernel=kernel8.img" > ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt

    echo "# kernel_address=${XEN_ADDR}" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "kernel=u-boot.bin" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "arm_64bit=1" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "dtoverlay=${DTBXENO}" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "dtoverlay=dwc2" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "total_mem=1024" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "enable_gic=1" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "#disable_overscan=1" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "# Enable audio (loads snd_bcm2835)" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "dtparam=audio=on" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "[pi4]" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "max_framebuffers=2" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "[all]" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "enable_jtag_gpio=1" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "enable_uart=1" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "uart_2ndstage=1" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
    echo "init_uart_baud=11520" >> ${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt
}
