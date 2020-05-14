#!/bin/bash

APP_PATH=dbexample-talkative-lynx.apps.pcfone.io

curl -X POST -H "Content-type: application/json" https://${APP_PATH}/bookmarks  -d '{ "name": "Blick", "url": "http://blick.ch" }'
