#!/usr/bin/env sh

mkdir -p ./.git/hooks
cp ./detekt/hook/pre-push ./.git/hooks/
