#!/bin/sh

### BEGIN INIT INFO
# Provides: dom0-startup.sh
# Required-Start: $network
# Required-Stop: $local_fs
# Default-Start: 2 3 4 5
# Default-Stop: 0 1 6
# Short-Description: Starts the dom0 startup script
# Description: This script will do the things that are to be done
#              at startup.
### END INIT INFO

# Get function from functions library
. /etc/init.d/functions

# Start the service FOO
start() {
        initlog -c "echo -n Starting dom0 startup: "

        ifdown eth0
        sleep 1
        ifup eth0

        success "dom0 startup complete"
        echo
}

# Restart the service FOO
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
        status FOO
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