# output is sometimes
# /Users/hannes/android-sdk/platform-tools/adb devices
#* daemon not running; starting now at tcp:5037
#* daemon started successfully
#List of devices attached
#port=$(adb devices | sed "s/-/ /g" | awk '{print $2}')
port=5554
token=$(cat ~/.emulator_console_auth_token | xargs)
echo "Set mock location $port"
(echo "auth $tokend\r"; echo "geo fix 48.2 10.1\r"; echo "kill\r" ) | telnet localhost $port
sleep 30 && (echo "auth $tokend\r"; echo "geo fix 48.2 10.1\r"; echo "kill\r" ) | telnet localhost $port &
#spawn telnet localhost $port
#expect "OK"
#send "auth $token\r"
#send "geo fix 48.2 10.1"
#send "kill\r"
./gradlew :MXApp:connectedPaidDebugAndroidTest --continue
