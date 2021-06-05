# Author: Aananth C N
# Date: 5 Jun 2021, 10:35 AM Saturday
# ---------------------------------------------------------------------------------------------
# This file is created to solve the problem described  below:
#  root@xen-dom0:~# xl create xl_domain_config
#  Parsing config from xl_domain_config
#  libxl: error: libxl_create.c:578:libxl__domain_make: domain creation fail: Permission denied
#  libxl: error: libxl_create.c:975:initiate_domain_create: cannot make domain: -3

SRCREV ?= "RELEASE-4.13.0"
XEN_REL ?= "4.13"
XEN_BRANCH ?= "stable-${XEN_REL}"

SRC_URI = "git://xenbits.xen.org/xen.git;branch=${XEN_BRANCH}"