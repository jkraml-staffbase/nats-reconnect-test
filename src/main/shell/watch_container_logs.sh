#!/usr/bin/bash

if [ "${DEBUG:-}" = y ]; then
  set -x
fi

set -eu -o pipefail

readonly CONTAINER_NAME="nats-server"

check_command_exists() {
  if ! command -v "$1" > /dev/null 2>&1;  then
    echo "Command $1 does not exist"
    exit 1
  fi
}


main() {
  check_command_exists podman

  # make sure we can CTRL-C out of this
  trap "exit 0" SIGTERM
  trap "exit 0" SIGINT

  while true; do
    while ! podman container inspect "$CONTAINER_NAME" >/dev/null 2>&1; do
      echo -n .
      sleep 0.5
     done
     echo
    podman logs -f "$CONTAINER_NAME"
  done
}


main