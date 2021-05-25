#!/bin/sh
# Author: Aananth C N
# Date: 23 May 2021, 10:23 PM

### BEGIN INIT INFO
# Provides: dom0-startup.sh
# Required-Start: $network
# Required-Stop: $local_fs
# Default-Start: 2 3 4 5
# Default-Stop: 0 1 6
# Short-Description: Starts the dom0 startup script
# Description: This is a startup script for dom0. User can add what they want here!
### END INIT INFO

# Get function from functions library
. /etc/init.d/functions

# Start the service dom0-startup.sh
start() {
        initlog -c "echo -n Starting dom0 startup: "

        ifdown eth0
        sleep 1
        ifup eth0

        success "dom0 startup complete"
        echo
}

# Restart the service dom0-startup.sh
stop() {
        initlog -c "echo -n Stopping dom0 startup: "
        echo
}

### main logic ###
case "$1" in
  start)
        start
        ;;
  stop)
        stop
        ;;
  status)
        status dom0-startup.sh
        ;;
  restart|reload|condrestart)
        stop
        start
        ;;
  *)
        echo $"Usage: $0 {start|stop|restart|reload|status}"
        exit 1
esac

exit 0