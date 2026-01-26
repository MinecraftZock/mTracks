#!/bin/bash
# Helper script to run map location tests
# Usage: ./run-map-tests.sh [simple|all|nyc|tokyo|berlin|munich]

set -e

#cd "$(dirname "$0")/../../.."

case "${1:-simple}" in
  simple)
    echo "Running SimpleMapLocationTest..."
    ./gradlew :app:connectedFreeDebugAndroidTest \
      -Pandroid.testInstrumentationRunnerArguments.class=info.mx.tracks.SimpleMapLocationTest
    ;;

  all)
    echo "Running all map location tests..."
    ./gradlew :app:connectedFreeDebugAndroidTest \
      -Pandroid.testInstrumentationRunnerArguments.class=info.mx.tracks.SimpleMapLocationTest,info.mx.tracks.ActivityMapExtensionLocationTest
    ;;

  nyc)
    echo "Running New York location test..."
    ./gradlew :app:connectedFreeDebugAndroidTest \
      -Pandroid.testInstrumentationRunnerArguments.class=info.mx.tracks.ActivityMapExtensionLocationTest#testMapWithNewYorkLocation
    ;;

  tokyo)
    echo "Running Tokyo location test..."
    ./gradlew :app:connectedFreeDebugAndroidTest \
      -Pandroid.testInstrumentationRunnerArguments.class=info.mx.tracks.ActivityMapExtensionLocationTest#testMapWithTokyoLocation
    ;;

  berlin)
    echo "Running Berlin location test..."
    ./gradlew :app:connectedFreeDebugAndroidTest \
      -Pandroid.testInstrumentationRunnerArguments.class=info.mx.tracks.ActivityMapExtensionLocationTest#testMapWithBerlinLocation
    ;;

  munich)
    echo "Running Munich MX track location test..."
    ./gradlew :app:connectedFreeDebugAndroidTest \
      -Pandroid.testInstrumentationRunnerArguments.class=info.mx.tracks.ActivityMapExtensionLocationTest#testMapWithMXTrackLocation
    ;;

  *)
    echo "Usage: $0 [simple|all|nyc|tokyo|berlin|munich]"
    echo ""
    echo "  simple  - Run SimpleMapLocationTest (default)"
    echo "  all     - Run all map location tests"
    echo "  nyc     - Run New York location test"
    echo "  tokyo   - Run Tokyo location test"
    echo "  berlin  - Run Berlin location test"
    echo "  munich  - Run Munich MX track location test"
    exit 1
    ;;
esac

echo ""
echo "Test completed! Check the output above for results."
echo "Screenshots saved to: /sdcard/Pictures/test_screenshots/"
