

do_configure_prepend () {
    echo "CONFIG_DEBUG=y" >> ${S}/xen/arch/arm/configs/arm64_defconfig
    echo "CONFIG_SCHED_ARINC653=y" >> ${S}/xen/arch/arm/configs/arm64_defconfig
}

EXTRA_OECMAKE += "CONFIG_EARLY_PRINTK=8250,0xfe215040,2 dist-xen"
EXTRA_OECONF += "CONFIG_EARLY_PRINTK=8250,0xfe215040,2 defconfig"
