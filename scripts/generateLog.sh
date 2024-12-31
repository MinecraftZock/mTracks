#!/usr/bin/env bash

SEARCHTOKEN="RELEASE "

mkdir -p app/src/main/admin/release-notes/de-DE
mkdir -p app/src/main/paid/release-notes/en-GB
mkdir -p app/src/main/free/release-notes/en-GB

mkdir -p app/src/paid/res/raw
mkdir -p app/src/free/res/raw

./scripts/generateTagGroupedGitlog.sh > app/src/admin/res/raw/gitlog.json
./scripts/generateTagGroupedGitlog.sh $SEARCHTOKEN | sed "s/$SEARCHTOKEN//g" > app/src/paid/res/raw/gitlog.json
./scripts/generateTagGroupedGitlog.sh $SEARCHTOKEN | sed "s/$SEARCHTOKEN//g" > app/src/free/res/raw/gitlog.json

./scripts/generateReleaseNotes.sh > app/src/main/admin/release-notes/de-DE/default.txt
./scripts/generateReleaseNotes.sh $SEARCHTOKEN | sed "s/$SEARCHTOKEN//g" > app/src/main/paid/release-notes/en-GB/default.txt
./scripts/generateReleaseNotes.sh $SEARCHTOKEN | sed "s/$SEARCHTOKEN//g" > app/src/main/free/release-notes/en-GB/default.txt