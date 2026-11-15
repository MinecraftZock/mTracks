#!/usr/bin/env bash

files=(
   "release.keystore"
   "debug.keystore"
   "../app/google-services.json"
   "Surveilance-playstore.json"
   "keystore.properties"
)

script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd) || exit 1
cd "$script_dir" || exit 1

has_files=false
end_options=false
for arg in "$@"; do
   if [[ "$end_options" == true ]]; then
      has_files=true
   elif [[ "$arg" == "--" ]]; then
      end_options=true
   elif [[ "$arg" != "--crypt" && "$arg" != -* ]]; then
      has_files=true
   fi
done

if [[ "$has_files" == true ]]; then
   exec bash "$script_dir/../buildSrc/scripts/decrypt.sh" "$@"
else
   exec bash "$script_dir/../buildSrc/scripts/decrypt.sh" "$@" "${files[@]}"
fi
