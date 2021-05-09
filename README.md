# meta-virt-rpi
To store recipes, scripts and patches for Raspberry Pi

# local.conf contents
MACHINE ?= "raspberrypi4-64" \
PREFERRED_PROVIDER_virtual/bootloader = "u-boot" \
CORE_IMAGE_EXTRA_INSTALL += " u-boot" \
PREFERRED_VERSION_u-boot = "2020.07" \
PREFERRED_VERSION_xen = "4.13.0" \
PREFERRED_VERSION_linux-raspberrypi = "4.19.%" \
DISTRO_FEATURES += " virtualization xen" \
DL_DIR = "/opt/dl-dir"

# bblayer.conf contents
POKY_BBLAYERS_CONF_VERSION = "2"

BBPATH = "${TOPDIR}"
BBFILES ?= ""

BBLAYERS ?= " \
  /home/aananth/projects/linux-club/yocto/poky/meta \
  /home/aananth/projects/linux-club/yocto/poky/meta-poky \
  /home/aananth/projects/linux-club/yocto/poky/meta-yocto-bsp \
  /home/aananth/projects/linux-club/yocto/meta-openembedded/meta-oe \
  /home/aananth/projects/linux-club/yocto/meta-openembedded/meta-filesystems \
  /home/aananth/projects/linux-club/yocto/meta-openembedded/meta-python \
  /home/aananth/projects/linux-club/yocto/meta-openembedded/meta-networking \
  /home/aananth/projects/linux-club/yocto/meta-virtualization \
  /home/aananth/projects/linux-club/yocto/meta-raspberrypi \
  /home/aananth/projects/linux-club/yocto/meta-virt-rpi \
  /home/aananth/projects/linux-club/yocto/meta-lc-rpi \
  "
