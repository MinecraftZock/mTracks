# Quick Start: Testing ActivityMapExtension with Location

## TL;DR - Copy & Paste Template

```kotlin
@Test
fun testYourMapScenario() {
    // 1. Create intent with coordinates
    val intent = Intent(ApplicationProvider.getApplicationContext(), ActivityMapExtension::class.java).apply {
        putExtra(ActivityMapExtension.LAT, 48.1351)  // Your latitude
        putExtra(ActivityMapExtension.LON, 11.5820)  // Your longitude
    }

    // 2. Launch and test
    ActivityScenario.launch<ActivityMapExtension>(intent).use {
        Thread.sleep(2000)  // Wait for map
        
        // Your assertions here
        onView(isRoot()).check { _, noViewFoundException ->
            if (noViewFoundException != null) throw noViewFoundException
        }
    }
}
```

## Run Commands

```bash
# Method 1: Using Android Gradle Plugin test task (RECOMMENDED)
./gradlew :app:connectedFreeDebugAndroidTest

# Method 2: Run specific test class with filter
./gradlew :app:connectedFreeDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=info.mx.tracks.SimpleMapLocationTest

# Method 3: Run single test method with filter
./gradlew :app:connectedFreeDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=info.mx.tracks.SimpleMapLocationTest#testMapWithSpecificLocation

# Method 4: Run all tests in androidTest directory
./gradlew :app:connectedAndroidTest
```

## Files Created

1. **`SimpleMapLocationTest.kt`** - Minimal working example (start here!)
2. **`ActivityMapExtensionLocationTest.kt`** - Complete test suite with multiple scenarios
3. **`README_MAP_TESTING.md`** - Comprehensive documentation

## Common Test Locations

```kotlin
// New York
putExtra(ActivityMapExtension.LAT, 40.7128)
putExtra(ActivityMapExtension.LON, -74.0060)

// London  
putExtra(ActivityMapExtension.LAT, 51.5074)
putExtra(ActivityMapExtension.LON, -0.1278)

// Tokyo
putExtra(ActivityMapExtension.LAT, 35.6762)
putExtra(ActivityMapExtension.LON, 139.6503)

// Munich (good for MX tracks)
putExtra(ActivityMapExtension.LAT, 48.1351)
putExtra(ActivityMapExtension.LON, 11.5820)
```

## Important: Setup Requirements

Your test class MUST:
1. Extend `BaseSyncTest`
2. Register `MapIdlingResource` in `@Before`
3. Unregister `MapIdlingResource` in `@After`

```kotlin
@Before
fun setUp() {
    IdlingRegistry.getInstance().register(MapIdlingResource.countingIdlingResource)
}

@After
fun tearDown() {
    IdlingRegistry.getInstance().unregister(MapIdlingResource.countingIdlingResource)
}
```

## Next Steps

1. Start with `SimpleMapLocationTest.kt` to understand the basics
2. Check `ActivityMapExtensionLocationTest.kt` for more examples
3. Read `README_MAP_TESTING.md` for advanced scenarios
4. Run the tests on a connected device/emulator

## Troubleshooting

**Test times out?**
→ Increase `Thread.sleep(2000)` to `Thread.sleep(5000)`

**Map doesn't load?**
→ Check Google Play Services on test device
→ Verify internet connectivity
→ Check API key configuration

**Location not applied?**
→ Verify you're using `ActivityMapExtension.LAT` and `.LON` constants
→ Check if FragmentMap properly reads the extras

## Screenshot Location

Screenshots are saved to:
```
/sdcard/Pictures/test_screenshots/
```

Pull them with:
```bash
adb pull /sdcard/Pictures/test_screenshots/ .
```
