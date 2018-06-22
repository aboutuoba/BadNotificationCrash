#!/bin/bash

if [ -z $1 -o -z $2 ];
then
	echo "help:"
	echo -e "\t$0 apk_path pacakge_name"
	exit -1
fi
COUNT=1
while true
do
	echo "Current count:${COUNT}"
	echo "Install update..."
	adb install -t -r $1
	echo "Sleep 2 seconds..."
	sleep 2
	echo "Launch up the app.."
	adb shell am start $2 -a android.intent.action.MAIN
	echo "Sleep 10 seconds for waitting your crash!"
	sleep 10
	((COUNT++))
done
