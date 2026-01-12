#!/usr/bin/bash

if [ "${DEBUG:-}" = y ]; then
  set -x
fi

set -eu -o pipefail

readonly NATS_IMAGE="docker.io/nats:2.12.2"
readonly CONTAINER_NAME="nats-server"
readonly PID_FILE="/nats.pid"

readonly SCRIPT_FOLDER="$PWD/$(dirname "$0")"

check_command_exists() {
  if ! command -v "$1" > /dev/null 2>&1;  then
    echo "Command $1 does not exist"
    exit 1
  fi
}

start_nats_container() {
  podman run \
    -d \
    --name "$CONTAINER_NAME" \
    --rm \
    -p 127.0.0.1:4222:4222 \
    -v "/$SCRIPT_FOLDER/nats-server.config:/nats-server.config:ro"\
    "$NATS_IMAGE" \
    -pid "$PID_FILE" --config /nats-server.config
}

stop_container() {
  if [ $# -ne 1 ]; then
    echo "missing container id argument"
    exit 1
  fi
  podman stop -t -1 --ignore "$1" >/dev/null || true
}

set_up_signal_handlers() {
  if [ $# -ne 1 ]; then
    echo "missing container id argument"
    exit 1
  fi

  # shellcheck disable=SC2064  # this _should_ expand now
  trap "stop_container '$1'; exit 0" SIGTERM
  # shellcheck disable=SC2064  # this _should_ expand now
  trap "stop_container '$1'; exit 0" SIGINT
}

wait_for_enter() {
  echo "Press ENTER to continue..."
  read -s -r _
}

enter_lame_duck_mode() {
  if [ $# -ne 1 ]; then
    echo "missing container id argument"
    exit 1
  fi
  podman exec "$1" nats-server --sl=ldm="$PID_FILE"
}

main() {
  check_command_exists podman

  if [ $# -ge 1 ]; then
    echo "This script does not take any arguments"
    exit 1
  fi

  if podman container inspect "$CONTAINER_NAME" >/dev/null 2>&1; then
    echo "A container named '$CONTAINER_NAME' already exists. Please remove it first."
    exit 1
  fi

  local container_id

  while true; do
    echo "Next: start NATS server again"
    wait_for_enter
    echo "Starting NATS server"
    container_id=$(start_nats_container)
    set_up_signal_handlers "$container_id"
    echo "Started NATS server (containerID: $container_id)"
    echo

    echo "Next: enter lame duck mode"
    wait_for_enter
    echo "Entering lame duck mode (containerID: $container_id)"
    enter_lame_duck_mode "$container_id"
    echo "Entered lame duck mode (containerID: $container_id)"
    echo

    echo "Next: stop NATS server"
    wait_for_enter
    echo "Stopping NATS server (containerID: $container_id)"
    stop_container "$container_id"
    echo "Stopped NATS server (containerID: $container_id)"
    echo
  done
}

main "$@"
