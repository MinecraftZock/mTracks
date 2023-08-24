#!/usr/bin/env bash

SEARCHTOKEN="RELEASE "

mkdir -p MXApp/src/main/admin/release-notes/de-DE
mkdir -p MXApp/src/main/paid/release-notes/en-GB
mkdir -p MXApp/src/main/free/release-notes/en-GB

mkdir -p MXApp/src/paid/res/raw
mkdir -p MXApp/src/free/res/raw

./scripts/generateTagGroupedGitlog.sh > MXApp/src/admin/res/raw/gitlog.json
./scripts/generateTagGroupedGitlog.sh $SEARCHTOKEN | sed "s/$SEARCHTOKEN//g" > MXApp/src/paid/res/raw/gitlog.json
./scripts/generateTagGroupedGitlog.sh $SEARCHTOKEN | sed "s/$SEARCHTOKEN//g" > MXApp/src/free/res/raw/gitlog.json

./scripts/generateReleaseNotes.sh > MXApp/src/main/admin/release-notes/de-DE/default.txt
./scripts/generateReleaseNotes.sh $SEARCHTOKEN | sed "s/$SEARCHTOKEN//g" > MXApp/src/main/paid/release-notes/en-GB/default.txt
./scripts/generateReleaseNotes.sh $SEARCHTOKEN | sed "s/$SEARCHTOKEN//g" > MXApp/src/main/free/release-notes/en-GB/default.txt