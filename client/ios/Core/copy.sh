#!/bin/bash

if [[ $# -eq 0 ]] ; then
    echo "Supply path to browser repository"
    exit 1
fi

rm BasePublic/*
find $1/src/base/ios/yandex_public -name *.swift -exec cp {} BasePublic \;
rm BasePublic/OSX.swift

rm BaseTinyPublic/*
find $1/src/base/ios/yandex/tiny_public -name *.swift -exec cp {} BaseTinyPublic \;
rm BaseTinyPublic/macOS.swift

rm BaseUIPublic/*
find $1/src/base/ios/yandex/ui_public -name *.swift -exec cp {} BaseUIPublic \;

rm CommonCorePublic/*
find $1/src/yandex/ios/search_app/CommonCore/CommonCorePublic -name *.swift -exec cp {} CommonCorePublic \;

rm NetworkingPublic/*
find $1/src/yandex/ios/search_app/CommonCore/NetworkingPublic -name *.swift -exec cp {} NetworkingPublic \;
rm NetworkingPublic/OSX.swift
