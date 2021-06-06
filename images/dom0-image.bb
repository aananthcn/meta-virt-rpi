# Author: Aananth C N
# Leveraged from https://github.com/dornerworks/xen-rpi4-builder
# Date: 16 May 2021, 10:35 AM

DESCRIPTION = "A minimal xen image for dom0"
LICENSE = "MIT"

inherit core-image
include xen-image-minimal.bb

PROVIDES = "dom0-image"

COMPATIBLE_MACHINE = "^rpi$"

IMAGE_INSTALL_append += " xen tree vim htop dom0-startup init-ifupdown"
IMAGE_INSTALL_append += " xen-tools util-linux e2fsprogs qemu"

DEPENDS += "bootfiles virtual/kernel virtual/bootloader xen xen-tools busybox dom0-startup"
CORE_IMAGE_EXTRA_INSTALL += " u-boot openssh"

DISTRO_FEATURES += " ipv4 ipv6"
# DISTRO_FEATURES += " wifi"

IMAGE_FSTYPES = "ext4"

OMXPLAYER  = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', 'omxplayer', d)}"

RDEPENDS_${PN} = "\
    ${OMXPLAYER} \
    bcm2835-tests \
    rpio \
    rpi-gpio \
    pi-blaster \
    bluez5 \
"

create_config_txt() {
    # MACROS
    XEN_ADDR=0x0020000
    DTBXENO=pi4-64-xen
    BOOTIMG_DIR=boot-img-files
    DEPLOYDIR=$1

    # Create config.txt
    echo "# kernel=kernel8.img" > ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt

    echo "# kernel_address=${XEN_ADDR}" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "kernel=u-boot.bin" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "arm_64bit=1" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "dtoverlay=${DTBXENO}" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "dtoverlay=dwc2" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "# total_mem=1024" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "enable_gic=1" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "#disable_overscan=1" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "# Enable audio (loads snd_bcm2835)" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "dtparam=audio=on" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "[pi4]" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "max_framebuffers=2" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "[all]" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "enable_jtag_gpio=1" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "enable_uart=1" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "uart_2ndstage=1" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
    echo "init_uart_baud=115200" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
}

do_image_complete() {
    # Environment settings
    export PATH=${PATH}:/usr/bin:/sbin/:/usr/sbin

    # Create boot image
    rm -f ${DEPLOY_DIR_IMAGE}/boot.img
    dd if=/dev/zero of=${DEPLOY_DIR_IMAGE}/boot.img bs=1024 count=262144
    mkfs.vfat ${DEPLOY_DIR_IMAGE}/boot.img

    # Create content for boot.img 
    mkdir -p ${DEPLOY_DIR_IMAGE}/boot-img-files/overlays
    cp -r ${DEPLOY_DIR_IMAGE}/bootfiles/* ${DEPLOY_DIR_IMAGE}/boot-img-files/
    cp -r ${DEPLOY_DIR_IMAGE}/bootfiles-kernel/* ${DEPLOY_DIR_IMAGE}/boot-img-files/
    cp -r ${DEPLOY_DIR_IMAGE}/bootfiles-u-boot/* ${DEPLOY_DIR_IMAGE}/boot-img-files/
    create_config_txt ${DEPLOY_DIR_IMAGE}
    cp ${DEPLOY_DIR_IMAGE}/u-boot.bin ${DEPLOY_DIR_IMAGE}/boot-img-files/
    cp ${DEPLOY_DIR_IMAGE}/Image ${DEPLOY_DIR_IMAGE}/boot-img-files/
    cp ${DEPLOY_DIR_IMAGE}/xen-${MACHINE} ${DEPLOY_DIR_IMAGE}/boot-img-files/

    # Copy .dtbo files to overlays directory, in-case direct xen boot is needed.
    if [ -f ${DEPLOY_DIR_IMAGE}/boot-img-files/*.dtbo ]; then
        mv ${DEPLOY_DIR_IMAGE}/boot-img-files/*.dtbo ${DEPLOY_DIR_IMAGE}/boot-img-files/overlays
    fi

    # Copy files to boot.img
    mcopy -vn -i ${DEPLOY_DIR_IMAGE}/boot.img -s ${DEPLOY_DIR_IMAGE}/boot-img-files/* ::

    # Create SD Card image
    IMGFILE=${DEPLOY_DIR_IMAGE}/${PN}-${MACHINE}-sdcard.img
    rm -f ${IMGFILE}
    qemu-img create ${IMGFILE} 3328M
    /sbin/parted ${IMGFILE} --script -- mklabel msdos
    /sbin/parted ${IMGFILE} --script -- mkpart primary fat32 1048576B 268435455B
    /sbin/parted ${IMGFILE} --script -- mkpart primary ext4 268435456B -1s

    # Write boot.img to SD Card image
    dd if=${DEPLOY_DIR_IMAGE}/boot.img of=${IMGFILE} bs=1024 seek=1024 conv=notrunc

    # Write dom0 ext4 image to SD Card image
    ROOTFS=${B}/../deploy-${PN}-image-complete/${PN}-${MACHINE}.ext4
    dd if=${ROOTFS} of=${IMGFILE} bs=1024 seek=262144 conv=notrunc

    # Cleanup
    rm -rf ${DEPLOY_DIR_IMAGE}/boot-img-files
    sync
}