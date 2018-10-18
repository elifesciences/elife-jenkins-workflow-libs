#!/bin/bash
set -e

if [ "$#" -ne 3 ]; then
    echo "Usage: $0 USER HOST PORT"
    echo "Example: $0 elife-alfred-user localhost 16022"
    exit 1
fi

username="$1"
host="$2"
port="$3"

# TODO: only add remote if not existing
if git remote | grep '^jenkins$'; then
    git remote rm jenkins
fi
git remote add jenkins "ssh://${username}@${host}:${port}/workflowLibs.git"
commit=$(git rev-parse HEAD)
git checkout master
git reset --hard "${commit}"
git push jenkins master
