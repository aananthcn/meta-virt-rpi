include xen-src.inc

PV = "${XEN_REL}+git${SRCPV}"
S = "${WORKDIR}/git"

require xen.inc
require xen-tools.inc
