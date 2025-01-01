#!/usr/bin/env bash

FILTER=$1

# recursive function iterate from tag to previous tag and show commits
function main {
   version=$(echo $1 | tr -d ^)
   versionPrint=$version
   REVERT_OLD="Revert \""
   REVERT_NEW="Revert '"
   DOPPELHOCHKOMMA="\"\""
   DOPPELHOCHKOMMAFIX="'\""

   if [[ -z "$1" ]]; then
     COUNT_BASE=HEAD # first it's based in HEAD
     versionPrint="no version"
   else
     COUNT_BASE=$1
   fi
   code=$(git rev-list $COUNT_BASE --count)
   prev=$(git describe --abbrev=0 --tags $1 2>/dev/null)

   if [[ $? == 0 ]]; then
      git log --no-merges --grep=$FILTER --pretty=format:'{%n "version": "'$versionPrint'",%n "code": "'$code'",%n "date": "%ad",%n "message": "%s"%n},' $version...$prev | sed "s/$REVERT_OLD/$REVERT_NEW/g" | sed "s/$DOPPELHOCHKOMMA/$DOPPELHOCHKOMMAFIX/g"
      main $prev^
   else # show all to first commit
      entries=$(git log --grep=$FILTER --pretty=format:'{%n "version": "'$versionPrint'",%n "code": "'$code'",%n "date": "%ad",%n "message": "%s"%n},' $version...$(git rev-list --max-parents=0 HEAD)) | sed "s/$REVERT_OLD/$REVERT_NEW/g" | sed "s/$DOPPELHOCHKOMMA/$DOPPELHOCHKOMMAFIX/g"
      # https://unix.stackexchange.com/questions/144298/delete-the-last-character-of-a-string-using-string-manipulation-in-shell-script
      if [[ -z "$1" ]]; then
         echo "${entries::-1}"
      fi
   fi
}

## Execute the script
echo "["
main ""
echo "]"

