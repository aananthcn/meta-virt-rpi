SRCREV ?= "RELEASE-4.13.0"
XEN_REL ?= "4.13"
XEN_BRANCH ?= "stable-${XEN_REL}"

SRC_URI = "git://xenbits.xen.org/xen.git;branch=${XEN_BRANCH}"

LIC_FILES_CHKSUM ?= "file://COPYING;md5=4295d895d4b5ce9d070263d52f030e49"

PV = "4.13.0"

S = "${WORKDIR}/git"

require xen.inc
require xen-hypervisor.inc
