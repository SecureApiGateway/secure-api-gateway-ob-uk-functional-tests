#!/usr/bin/env bash

BASEDIR=$(dirname "$0")
echo "$BASEDIR"

GFOLDER=$(date +"%F_%l_%M_%S")

mkdir "$BASEDIR"/../automating-testing/"$GFOLDER"

openssl req -new -config "$BASEDIR"/../openssl/config/obwac.conf -nodes -out "$BASEDIR"/../automating-testing/$GFOLDER/obwac.csr -keyout "$BASEDIR"/../automating-testing/"$GFOLDER"/OBWac.key

openssl req -new -config "$BASEDIR"/../openssl/config/obseal.conf -nodes -out "$BASEDIR"/../automating-testing/$GFOLDER/obseal.csr -keyout "$BASEDIR"/../automating-testing/"$GFOLDER"/OBSeal.key
