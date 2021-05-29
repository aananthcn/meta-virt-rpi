#! /bin/bash
# Author: Aananth C N
# Date: 25 May 2021, 10:25 AM
# This script will setup yocto (i.e., setup layers and conf files) for Xen
# Virtualization development on Raspberry Pi 4.

# Macros
SCRIPT=$(readlink -f "$0")
SCRIPTDIR=$(dirname "$SCRIPT")
WORKDIR=$(dirname "$SCRIPTDIR")
TOPDIRPRINT='BBPATH = "${TOPDIR}"'
echo "Current directory: $SCRIPTDIR"

# Functions
copy_bblayer_conf() {
    cat > conf/bblayers.conf <<EOF
# POKY_BBLAYERS_CONF_VERSION is increased each time build/conf/bblayers.conf
# changes incompatibly
POKY_BBLAYERS_CONF_VERSION = "2"

${TOPDIRPRINT}
BBFILES ?= ""

BBLAYERS ?= " \
  ${WORKDIR}/poky/meta \
  ${WORKDIR}/poky/meta-poky \
  ${WORKDIR}/poky/meta-yocto-bsp \
  ${WORKDIR}/meta-openembedded/meta-oe \
  ${WORKDIR}/meta-openembedded/meta-filesystems \
  ${WORKDIR}/meta-openembedded/meta-python \
  ${WORKDIR}/meta-openembedded/meta-networking \
  ${WORKDIR}/meta-virtualization \
  ${WORKDIR}/meta-raspberrypi \
  ${WORKDIR}/meta-virt-rpi \
  "
EOF
}

create_local_conf() {
    cat > conf/local.conf <<EOF
MACHINE ?= "raspberrypi4-64"

PREFERRED_PROVIDER_virtual/bootloader = "u-boot"
PREFERRED_PROVIDER_virtual/kernel = "linux-xen" 
PREFERRED_VERSION_u-boot = "2020.07"
PREFERRED_VERSION_xen = "4.13.0"

DISTRO_FEATURES += " virtualization xen" 
EXTRA_IMAGE_FEATURES += "allow-empty-password"
EXTRA_IMAGE_FEATURES += "empty-root-password"
DL_DIR = "/opt/dl-dir"

DISTRO_FEATURES_append = " systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED += "sysvinit"
VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_initscripts = "systemd-compat-units"

IMAGE_INSTALL_append = " linux-firmware-rpidistro-bcm43430 "

DISTRO ?= "xen-rpi"
EOF
}

# Instructions - Clone Layers
echo "Changing directory to $WORKDIR"
cd ${WORKDIR}
if [ ! -d poky ]; then
    git clone --depth 1 -b dunfell http://git.yoctoproject.org/git/poky
fi

if [ ! -d meta-raspberrypi ]; then
    git clone --depth 1 -b dunfell git://git.yoctoproject.org/meta-raspberrypi
fi

if [ ! -d meta-openembedded ]; then
    git clone --depth 1 -b dunfell git://git.openembedded.org/meta-openembedded
fi

if [ ! -d meta-virtualization ]; then
    git clone --depth 1 -b dunfell https://git.yoctoproject.org/git/meta-virtualization
fi

# Setup build
if [ ! -d build ]; then
    source poky/oe-init-build-env
    copy_bblayer_conf
    create_local_conf
fi

# Final message to developers
echo ""
echo "Now run the following command:"
echo "------------------------------"
echo "$ cd ${WORKDIR}"
echo "$ source poky/oe-init-build-env"
echo "$ bitbake dom0-image"
echo ""