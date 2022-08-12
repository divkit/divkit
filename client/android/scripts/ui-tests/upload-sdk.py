#!/usr/local/bin/python
# -*- coding: utf-8 -*-
from os import system
import sys

version = sys.argv[1]
system('ls sdk-' + version + '/system-images/android-' + version + '/default/')
system('cd sdk-' + version + ' && shopt -s dotglob && tar -zcvf ../emulator-android-' + version + '.tar.gz * ')
system('aws --endpoint-url=http://s3.mds.yandex.net s3 cp emulator-android-' + version +
       '.tar.gz   s3://alicekit/emulator-android-cmdline-tools' + version + '.tar.gz')
system('sha1sum emulator-android-' + version + '.tar.gz')
