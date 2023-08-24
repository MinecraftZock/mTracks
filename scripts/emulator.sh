#!/usr/bin/env bash

if [ "$1" == "-h" ] || [ "$1" == "--help" ] ; then
    echo "`basename $0` - run a command spawning an Android Emulator before. It can be used to run Android Tests."
    echo "Usage: `basename $0` COMMAND"
    echo "Example: $ `basename $0` ./gradlew connectedPayGermanyDebugAndroidTest --info"
    exit 0
fi

# grab some variables
ADB=$ANDROID_HOME/platform-tools/adb
AVD_PREFIX="avd_"
SUCCESS=0
ERROR_PORT_LOCKED=43
ERROR_CREATING_AVD=65
ERROR_STARTING_EMULATOR=69
ERROR_WAIT_FOR_BOOT_TIMEOUT=408
LOCK_TIMEOUT_IN_SECONDS=3600
WAIT_TIMEOUT_IN_SECONDS=120


## emulator_count
if [ "x${emulator_count}" != "x" ]; then
    EMULATOR_COUNT="${emulator_count}"
else
    EMULATOR_COUNT="1"
fi

## android_target
if [ "x${android_package}" != "x" ]; then
    ANDROID_PACKAGE="${android_package}"
else
    ANDROID_PACKAGE="system-images;android-28;google_apis;x86"
fi

## android_target
if [ "x${android_abi}" != "x" ]; then
    ANDROID_ABI="${android_abi}"
else
    ANDROID_ABI="google_apis/x86"
fi

## android_skin
if [ "x${android_skin}" != "x" ]; then
    ANDROID_SKIN="${android_skin}"
else
    ANDROID_SKIN="480x800"
fi

## sdcard_size
if [ "x${sdcard_size}" != "x" ]; then
    SDCARD="${sdcard_size}"
else
    SDCARD="1024M"
fi

## port number to start searching for free ports for the console
if [ "x${console_port_start}" != "x" ]; then
    console_port="${console_port_start}"
else
    console_port="5554"
fi

## port number to start searching for free ports for the console
if [ "x${TMPDIR}" == "x" ]; then
    TMPDIR="/tmp"
fi

## http proxy senttings for the emulator
if [[ "x${HTTPPROXY}" == "x" ]]; then
    # HTTPPROXY="-http-proxy abc.de.com:8080"
    HTTPPROXY=""
fi

## set EMU_WINDOW if you want a non headless mode
if [[ "x${EMU_WINDOW}" != "x" ]]; then
    echo "EMU_WINDOW is set (${EMU_WINDOW})! Starting emulator with GUI!"
    EMU_WINDOW=""
elif [[ "$USER" == "hannes" ]]; then
    echo "Running for user $USER ! Starting emulator with GUI!"
    EMU_WINDOW=""
elif [[ "$OSTYPE" == "darwin"* && "$(hostname)" == "Hannes"* ]]; then
    echo "Running on macOS ! Starting emulator with GUI!"
    EMU_WINDOW=""
else
    echo "No EMU_WINDOW is set (${EMU_WINDOW})! Starting emulator without GUI!"
    EMU_WINDOW="-no-window"
fi

# Functions

function checkPortAndLockIt {
    local CONSOLE_PORT=$1
    local LOCK_FILE_PATH="$TMPDIR/${AVD_PREFIX}${CONSOLE_PORT}.lock"

    if lsof -i -P -n | grep ":${CONSOLE_PORT} (LISTEN)\|:$(( ${CONSOLE_PORT} + 1 )) (LISTEN)" --quiet; then
        echo ">>>>>> Port NOT available"
        return $ERROR_PORT_LOCKED
    else
        echo ">>>>>> Port available"
        if [ -f $LOCK_FILE_PATH ]; then
            local LOCK_TIME=`cat ${LOCK_FILE_PATH}`
            local NOW=`date +%s`
            local ELAPSED=`expr $NOW - $LOCK_TIME`
            if [ $ELAPSED -gt $LOCK_TIMEOUT_IN_SECONDS ]; then
                echo ">>>>>> Port locked for more than 1 hour. Overriding..."
                killEmulator $CONSOLE_PORT
                echo `date +%s` > $LOCK_FILE_PATH
                return $SUCCESS
            else
                echo ">>>>>> File locked"
                return $ERROR_PORT_LOCKED
            fi
        else
            echo ">>>>>> File NOT locked. Locking..."
            echo `date +%s` > $LOCK_FILE_PATH
            return $SUCCESS
        fi
    fi
}

# Create an array of free port numbers to use for the emulators
# Yes, this looks unnecessarily complex, but CONSOLE_PORT needs to
# be an even integer and can only be within a range of 5554-5584.
function findFreePorts {
    echo "Searching for available ports"
	for (( i=0 ; i < $EMULATOR_COUNT; i++ ))
	{
		while ! checkPortAndLockIt ${console_port}
		do
            echo "Port ${console_port} busy"
			#Console Port should and can only be an even number
			#Android allows only even numbers
			console_port=$(( ${console_port} + 2 ))
			echo PORT1 ${console_port}
		done

		ports[k]=${console_port}

		#Console Port should and can only be an even number
		#Android allows only even numbers
		console_port=$(( ${console_port} + 2 ))
	}

}

# create the AVD
function createAVD {
   local CONSOLE_PORT=$1
   echo "[emulator-$CONSOLE_PORT] Creating emulator $AVD_PREFIX$CONSOLE_PORT"
   eval "cmd='echo no | ${ANDROID_HOME}/tools/bin/avdmanager create avd --force --name $AVD_PREFIX$CONSOLE_PORT --package \"$ANDROID_PACKAGE\" --sdcard $SDCARD --abi $ANDROID_ABI --device \"4in WVGA (Nexus S)\"'"
   echo $cmd

   if ! eval $cmd > /dev/null; then
        exit $ERROR_CREATING_AVD
   fi
   printf 'hw.keyboard=yes\nhw.ramSize=1536\nvm.heapSize=128\ndisk.dataPartition.size=1g\n' >> "$HOME/.android/avd/$AVD_PREFIX$CONSOLE_PORT.avd/config.ini"

}

# start the AVD
# This starts the emulator
function startEmulator {
   local CONSOLE_PORT=$1

   echo ""
   echo "[emulator-$CONSOLE_PORT] Starting emulator with avd $AVD_PREFIX$CONSOLE_PORT and console port $CONSOLE_PORT"
   # not used switches
   # -gpu swiftshader_indirect &  2>/dev/null -no-boot-anim -verbose
   eval "cmd='${ANDROID_HOME}/emulator/emulator -avd $AVD_PREFIX$CONSOLE_PORT -port $CONSOLE_PORT -skin $ANDROID_SKIN $HTTPPROXY -noaudio $EMU_WINDOW -selinux permissive'"

   echo "[emulator-$CONSOLE_PORT] $cmd"
   eval $cmd &

   # Minimum boot up time of the emulator
   sleep 10

   # This waits for emulator to start up
   echo  "[emulator-$CONSOLE_PORT] Waiting for emulator to boot completely"
   wait_for_boot_complete "getprop dev.bootcomplete" 1
   wait_for_boot_complete "getprop sys.boot_completed" 1

}


# unlock Emulator
function unlockEmulator {
   local CONSOLE_PORT=$1

   echo "[emulator-$CONSOLE_PORT] Getting out of home screen"
   $ADB -s emulator-$CONSOLE_PORT shell input keyevent 82
   $ADB -s emulator-$CONSOLE_PORT shell input keyevent 4
}

# disable animations
function disableAnimations {
    local CONSOLE_PORT=$1
    echo "[emulator-$CONSOLE_PORT] Disabling animations for less flaky tests on all devices [start]"
    $ADB -s emulator-$CONSOLE_PORT shell settings put global window_animation_scale 0
    $ADB -s emulator-$CONSOLE_PORT shell settings put global transition_animation_scale 0
    $ADB -s emulator-$CONSOLE_PORT shell settings put global animator_duration_scale 0
    $ADB -s emulator-$CONSOLE_PORT shell settings put system window_animation_scale 0
    $ADB -s emulator-$CONSOLE_PORT shell settings put system transition_animation_scale 0
    $ADB -s emulator-$CONSOLE_PORT shell settings put system animator_duration_scale 0
    echo "[emulator-$CONSOLE_PORT] Disabling animations for less flaky tests on all devices [done]"
#    $ADB -s emulator-$CONSOLE_PORT reboot
#
#    # Minimum boot up time of the emulator
#    sleep 12
#
#    # This waits for emulator to start up
#    echo  "[emulator-$CONSOLE_PORT] Waiting for emulator to boot completely"
#    wait_for_boot_complete "getprop dev.bootcomplete" 1
#    wait_for_boot_complete "getprop sys.boot_completed" 1
}

function cleanLogcat {
    local CONSOLE_PORT=$1
    echo "[emulator-$CONSOLE_PORT] Cleaning logcat [start]"
    $ADB -s emulator-$CONSOLE_PORT logcat -c
    echo "[emulator-$CONSOLE_PORT] Cleaning logcat [done]"
}

# create one emulator instance
function createOneEmulatorInstance {
   local CONSOLE_PORT=$1

   createAVD $CONSOLE_PORT
   startEmulator $CONSOLE_PORT
   unlockEmulator $CONSOLE_PORT
   disableAnimations $CONSOLE_PORT
   cleanLogcat $CONSOLE_PORT
}

# kill emulator
function killEmulator {
   local CONSOLE_PORT=$1
   local LOCK_FILE_PATH="$TMPDIR/${AVD_PREFIX}${CONSOLE_PORT}.lock"

   echo "[emulator-$CONSOLE_PORT] Killing emulator"
   $ADB -s emulator-$CONSOLE_PORT emu kill || echo "Unable to kill emulator..."
   rm $LOCK_FILE_PATH || echo "Lock file not found..."
}

# create the AVD
function deleteAVD {
   local CONSOLE_PORT=$1
   echo "[emulator-$CONSOLE_PORT] Deleting emulator $AVD_PREFIX$CONSOLE_PORT"
   echo no | ${ANDROID_HOME}/tools/bin/avdmanager delete avd --name $AVD_PREFIX$CONSOLE_PORT > /dev/null
}

# kill and delete one emulator instance, for running monkey test we have to postpone the delete of emulators
function killAndDeleteOneEmulatorInstance {
   local CONSOLE_PORT=$1

   sleep 5s && killEmulator $CONSOLE_PORT &
   sleep 5s && deleteAVD $CONSOLE_PORT &
}

# function to really, really check things are booted up
function wait_for_boot_complete {
    local boot_property=$1
    local boot_property_test=$2
    echo "[emulator-$CONSOLE_PORT] Checking $boot_property..."
    local ELAPSED=0
    until $ADB -s emulator-$CONSOLE_PORT shell $boot_property | grep "$boot_property_test"; do
        sleep 1
        let "ELAPSED++"
        if [ $ELAPSED -gt $WAIT_TIMEOUT_IN_SECONDS ] ; then
            echo "[emulator-$CONSOLE_PORT] ERROR: \"$1\" timeout!"
            killAndDeleteOneEmulatorInstance $CONSOLE_PORT
            exit $ERROR_WAIT_FOR_BOOT_TIMEOUT
        fi
    done
    echo "[emulator-$CONSOLE_PORT] \"$1\" successful"
}

## The main function :)
function main {
   # This is only needed if we use a debug emulator and we want to collect core dumps.
   ulimit -S -c unlimited
   local CONSOLE_PORT=0

   # Get an array of free port numbers to use
   findFreePorts

   # Based on the port number found, create an instance of an emulator up to the EMULATOR_COUNT specified
   for (( l=0, i=0 ; l < ${#ports[@]}, i < $EMULATOR_COUNT; i++, l=l+3 ))
   {
      echo "ports for emulator $((i+1)): console=${ports[$l]}"
      createOneEmulatorInstance ${ports[$l]}
      CONSOLE_PORT=${ports[$l]}
   }
   #do not show virtual keyboard
   echo "[emulator-$CONSOLE_PORT] do not show virtual keyboard"
   $ADB -s emulator-$CONSOLE_PORT shell settings put secure show_ime_with_hard_keyboard 0

   echo "[emulator-$CONSOLE_PORT] Emulator startup complete!"

   export ANDROID_SERIAL="emulator-${ports[0]}"
   CMD_TO_RUN=$@

   echo "[emulator-$CONSOLE_PORT] RunningTest \"$CMD_TO_RUN\""
   echo StartTest on $ANDROID_SERIAL at $(uname -n) $(pwd)

   adblogcatfile=adblogcat.$ANDROID_SERIAL.$(date +%Y-%m-%d_%H-%M).log
   echo "[emulator-$CONSOLE_PORT] adb logcat file $(pwd)/$adblogcatfile"
   echo "$ADB -s emulator-$CONSOLE_PORT logcat" > $adblogcatfile
   $ADB -s emulator-$CONSOLE_PORT logcat >> $adblogcatfile &

   eval $CMD_TO_RUN
   RET_CODE=$?

   if [ ! $? -eq 0 ]; then
     echo "!! When the Emulator was crashing, with this you get the right info for Google devs : (you can maybe remove >full< )"
     echo "  gdb -c /opt/jenkins/coredump.$BASHPID ~/android-sdk/emulator/qemu/linux-x86_64/qemu-system-i386 -ex 'set logging on' -ex 'set pagination off' -ex 'thread apply all bt full' -ex quit"
     echo " "
   fi

   echo DoneTest on $ANDROID_SERIAL with returnCode $RET_CODE at $(uname -n) in $(pwd)
   # Based on the port number found, create an instance of an emulator up to the EMULATOR_COUNT specified
   for (( l=0, i=0 ; l < ${#ports[@]}, i < $EMULATOR_COUNT; i++, l=l+3 ))
   {
      echo "[emulator-$CONSOLE_PORT] killing and deleting emulator $((i+1)): console=${ports[$l]}"
      killAndDeleteOneEmulatorInstance ${ports[$l]}
   }
}

## Execute the script
main $@

exit $RET_CODE
