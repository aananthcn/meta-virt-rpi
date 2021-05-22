# Author: Aananth C N
# Leveraged from https://github.com/dornerworks/xen-rpi4-builder
# Date: 16 May 2021, 10:35 AM

DESCRIPTION = "A minimal xen image for dom0"
LICENSE = "MIT"

inherit core-image
include xen-image-minimal.bb

COMPATIBLE_MACHINE = "^rpi$"
PREFERRED_PROVIDER_virtual/bootloader = "u-boot"

IMAGE_INSTALL_append += " xen packagegroup-rpi-test "
DEPENDS += "bootfiles virtual/kernel virtual/bootloader"
CORE_IMAGE_EXTRA_INSTALL += " u-boot"
PREFERRED_VERSION_u-boot = "2020.07"
PREFERRED_VERSION_xen = "4.13.0"
PREFERRED_VERSION_linux-raspberrypi = "4.19.%"
DISTRO_FEATURES += " virtualization xen" 

IMAGE_FSTYPES = "ext4"


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
    echo "total_mem=1024" >> ${DEPLOYDIR}/${BOOTIMG_DIR}/config.txt
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


create_rootfs_configs() {
    HOSTNAME="xen-dom0"

    # /etc/hostname
    sudo bash -c "echo ${HOSTNAME} > ${IMAGE_ROOTFS}/etc/hostname"

    # /etc/hosts
    sudo bash -c "cat > ${IMAGE_ROOTFS}/etc/hosts" <<EOF
    127.0.0.1	localhost
    127.0.1.1	${HOSTNAME}

    # The following lines are desirable for IPv6 capable hosts
    ::1     ip6-localhost ip6-loopback
    fe00::0 ip6-localnet
    ff00::0 ip6-mcastprefix
    ff02::1 ip6-allnodes
    ff02::2 ip6-allrouters
EOF

    # /etc/fstab
    sudo bash -c "cat > ${IMAGE_ROOTFS}/etc/fstab" <<EOF
    proc            /proc           proc    defaults          0       0
    /dev/mmcblk0p1  /boot           vfat    defaults          0       2
    /dev/mmcblk0p2  /               ext4    defaults,noatime  0       1
EOF

    # /etc/network/interfaces.d/eth0
    sudo bash -c "cat > ${IMAGE_ROOTFS}/etc/network/interfaces.d/eth0" <<EOF
    auto eth0
    iface eth0 inet manual
EOF
    sudo chmod 0644 ${IMAGE_ROOTFS}/etc/network/interfaces.d/eth0

    # /etc/network/interfaces.d/xenbr0
    sudo bash -c "cat > ${IMAGE_ROOTFS}/etc/network/interfaces.d/xenbr0" <<EOF
    auto xenbr0
    iface xenbr0 inet dhcp
    bridge_ports eth0
EOF
    sudo chmod 0644 ${IMAGE_ROOTFS}/etc/network/interfaces.d/xenbr0
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
    qemu-img create ${IMGFILE} 7000M
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