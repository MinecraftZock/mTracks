#!/bin/bash

git checkout MXApp/google-services.json
find . -name '*.p12' |xargs rm
find . -name '*.keystore' |xargs rm