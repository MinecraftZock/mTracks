#!/usr/bin/env bash

FILTER=$1

# to previous tag and show commits
function main {
   version=$(echo $1 | tr -d ^)

   if [[ -z "$1" ]]; then
     COUNT_BASE=HEAD # first it's based in HEAD
   else
     COUNT_BASE=$1
   fi
   code=$(git rev-list $COUNT_BASE --count)
   prev=$(git describe --abbrev=0 --tags $1 2>/dev/null)

   if [[ $? == 0 ]]; then
      git log --no-merges --grep=$FILTER --pretty=format:'* %f' $version...$prev | sed 's/-/ /g' > /tmp/releaseNotes
      head -c 497 /tmp/releaseNotes && echo ...
   fi
}

## Execute the script
main ""

