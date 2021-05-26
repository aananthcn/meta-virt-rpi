<span style="color:#0060C0">Welcome to the yocto layer that stores the recipes that are customised to make Xen to work on Raspberry Pi 4.</span>

# Getting Started
## Setup yocto 
 * git clone https://github.com/aananthcn/meta-virt-rpi.git
 * ./meta-virt-rpi/setup-yocto.sh
After the above 2 steps, if you do ls from your current directory, you should see the following:
  `meta-openembedded  meta-raspberrypi  meta-virt-rpi  meta-virtualization  poky`

## Build the target image
 * source poky/oe-init-build-env
 * bitbake dom0-image
   * This will take some time (~8 hrs for first time on 10th Gen Intel i5 machine with 16GB RAM)

## Flash the image
 * From build folder navigate to `tmp-glibc/deploy/images/raspberrypi4-64`
 * Check for image with name `dom0-image-raspberrypi4-64-sdcard.img`
 * Follow either of these steps to flash the above image into the sd card
   * Linux: https://www.raspberrypi.org/documentation/installation/installing-images/linux.md
   * Windows: https://www.balena.io/etcher/



# Design Notes
## meta-virt-rpi
To store recipes, scripts and patches for Raspberry Pi

## local.conf contents
MACHINE ?= "raspberrypi4-64" \
PREFERRED_PROVIDER_virtual/bootloader = "u-boot" \
PREFERRED_PROVIDER_virtual/kernel = "linux-xen" \
CORE_IMAGE_EXTRA_INSTALL += " u-boot" \
PREFERRED_VERSION_u-boot = "2020.07" \
PREFERRED_VERSION_xen = "4.13.0" \
DISTRO_FEATURES += " virtualization xen" \
DL_DIR = "/opt/dl-dir"

## bblayer.conf contents
POKY_BBLAYERS_CONF_VERSION = "2"

BBPATH = "${TOPDIR}"
BBFILES ?= ""

BBLAYERS ?= " \
  /home/aananth/projects/xen-rpi4yocto/poky/meta \
  /home/aananth/projects/xen-rpi4yocto/poky/meta-poky \
  /home/aananth/projects/xen-rpi4yocto/poky/meta-yocto-bsp \
  /home/aananth/projects/xen-rpi4yocto/meta-openembedded/meta-oe \
  /home/aananth/projects/xen-rpi4yocto/meta-openembedded/meta-filesystems \
  /home/aananth/projects/xen-rpi4yocto/meta-openembedded/meta-python \
  /home/aananth/projects/xen-rpi4yocto/meta-openembedded/meta-networking \
  /home/aananth/projects/xen-rpi4yocto/meta-virtualization \
  /home/aananth/projects/xen-rpi4yocto/meta-raspberrypi \
  /home/aananth/projects/xen-rpi4yocto/meta-virt-rpi \
  "

# u-boot Environment to boot Xen
setenv bootargs 'console=hvc0 clk_ignore_unused root=/dev/mmcblk0p2 rootwait' \
setenv do_dtbsetup 'fatload mmc 0:1 ${fdt_addr} bcm2711-rpi-4-b.dtb && fdt addr ${fdt_addr} && fdt resize && fatload mmc 0:1 0x18000 overlays/pi4-64-xen.dtbo && fdt apply 0x18000' \
setenv load_xen 'fatload mmc 0:1 0x200000 xen-raspberrypi4-64' \
setenv load_dom0 'fatload mmc 0:1 0x480000 Image' \
setenv xen_bootcmd 'booti 0x200000 - ${fdt_addr}' \
setenv boot_xen 'run do_dtbsetup; run load_xen; run load_dom0; run xen_bootcmd'
