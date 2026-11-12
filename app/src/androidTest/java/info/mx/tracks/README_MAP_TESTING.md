# Testing ActivityMapExtension with Specific Locations

## Overview

This guide shows you how to test `ActivityMapExtension` with specific geographic locations using Espresso tests.

## How Location is Set

The `ActivityMapExtension` activity accepts location coordinates via Intent extras:
- **Latitude**: `ActivityMapExtension.LAT` (Double)
- **Longitude**: `ActivityMapExtension.LON` (Double)

## Test Structure

### Basic Test Pattern

```kotlin
@Test
fun testMapWithCustomLocation() {
    // 1. Create intent with location coordinates
    val intent = Intent(ApplicationProvider.getApplicationContext(), ActivityMapExtension::class.java).apply {
        putExtra(ActivityMapExtension.LAT, 40.7128)  // Latitude
        putExtra(ActivityMapExtension.LON, -74.0060) // Longitude
    }

    // 2. Launch activity with the intent
    ActivityScenario.launch<ActivityMapExtension>(intent).use { scenario ->
        // 3. Wait for map to load
        Thread.sleep(2000)

        // 4. Perform your assertions and interactions
        onView(isRoot())
            .perform(captureToBitmap { bitmap -> 
                bitmap.writeToTestStorage("map-screenshot") 
            })
    }
}
```

## Example Test Locations

### Popular Cities
```kotlin
// New York City
putExtra(ActivityMapExtension.LAT, 40.7128)
putExtra(ActivityMapExtension.LON, -74.0060)

// Tokyo, Japan
putExtra(ActivityMapExtension.LAT, 35.6762)
putExtra(ActivityMapExtension.LON, 139.6503)

// London, UK
putExtra(ActivityMapExtension.LAT, 51.5074)
putExtra(ActivityMapExtension.LON, -0.1278)

// Munich, Germany (good for MX tracks)
putExtra(ActivityMapExtension.LAT, 48.1351)
putExtra(ActivityMapExtension.LON, 11.5820)
```

## Complete Test Example

See `ActivityMapExtensionLocationTest.kt` for a complete working example with multiple test scenarios:
- New York location test
- Tokyo location test
- Berlin location test
- Custom MX track location test
- Default location (no extras) test

## Running the Tests

### Run all Android tests:
```bash
./gradlew :app:connectedFreeDebugAndroidTest
```

### Run specific test class:
```bash
./gradlew :app:connectedFreeDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=info.mx.tracks.SimpleMapLocationTest
```

### Run specific test method:
```bash
./gradlew :app:connectedFreeDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=info.mx.tracks.SimpleMapLocationTest#testMapWithSpecificLocation
```

### Run multiple test classes (comma-separated):
```bash
./gradlew :app:connectedFreeDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=info.mx.tracks.SimpleMapLocationTest,info.mx.tracks.ActivityMapExtensionLocationTest
```

## Important Notes

1. **IdlingResource**: The tests register `MapIdlingResource` to wait for map readiness and app-owned marker updates.
   Each map fragment owns at most one pending load. Completion or view destruction releases
   that load; repeated resumes do not increment it again, and callbacks from destroyed views
   cannot release another fragment's load.

2. **Deterministic maps**: `BaseSyncTest` enables a debug-only screenshot mode before launch
   and resets it after activity cleanup. Maps use `MAP_TYPE_NONE`, no traffic or device-location
   layers, and a fixed initial zoom (12) and default position (Munich). Intent coordinates still
   select the target, and camera positioning is immediate. App markers, controls, and overlays
   remain visible. Screenshot readiness does not depend on downloading Google tiles.
   Track-edit maps also suppress tiles. Normal app launches do not enable this mode.
   Their marker camera is positioned immediately at zoom 15, and the long-press hint
   is always shown without changing its usage counter. Screenshot capture waits for
   edit-map initialization and the renderer's snapshot callback, so marker position
   and hint do not depend on test order or camera-animation timing.
   Detail screenshots scroll the edit form directly rather than swiping across the
   embedded map, which would pan the camera and move the marker between captures.

3. **Screenshots**: Tests automatically capture screenshots for visual verification. Find them in:
   ```
   /sdcard/Pictures/test_screenshots/
   ```

4. **Permissions**: Make sure location permissions are granted if your test requires device location.

5. **Test Isolation**: Each test uses `ActivityScenario.launch().use { }` which ensures proper cleanup after each test.

## Real-map integration (no screenshot comparison)

`RealMapIntegrationTest` is skipped unless explicitly enabled. It uses normal map rendering,
waits for `onMapLoaded()` and app marker updates, verifies mock device location, and does not
write screenshots:

```bash
./gradlew :app:connectedPaidDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=info.mx.tracks.RealMapIntegrationTest \
  -Pandroid.testInstrumentationRunnerArguments.realMaps=true
```

Configure `mapKey` in the existing encrypted signing properties. CI now uses this key instead
of overriding it with `"dummy"`; authorize the CI debug certificate SHA-1 and app package in
its Android restrictions. The integration test fails explicitly for a missing/dummy key.
Default CI screenshot runs keep tiles disabled regardless of key availability. To run the
integration check in CI, use a separate job with the command above and no screenshot comparison
or baseline-update steps.

The first screenshot-mode run changes map backgrounds and removes the device-location layer.
Review and update the affected baselines once; subsequent tile/style changes will not affect them.

## Advanced Testing

### Testing with Mock Location Provider

For more controlled testing, you can mock the device's location:

```kotlin
@Before
fun setMockLocation() {
    // Enable mock location for testing
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    locationManager.addTestProvider(
        LocationManager.GPS_PROVIDER,
        false, false, false, false, false, true, true,
        Criteria.POWER_LOW, Criteria.ACCURACY_FINE
    )
    
    val location = Location(LocationManager.GPS_PROVIDER).apply {
        latitude = 40.7128
        longitude = -74.0060
        accuracy = 1.0f
        time = System.currentTimeMillis()
    }
    
    locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
    locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location)
}
```

### Testing Map Interactions

You can also test user interactions with the map:

```kotlin
@Test
fun testMapZoomAndPan() {
    val intent = Intent(ApplicationProvider.getApplicationContext(), ActivityMapExtension::class.java).apply {
        putExtra(ActivityMapExtension.LAT, 48.1351)
        putExtra(ActivityMapExtension.LON, 11.5820)
    }

    ActivityScenario.launch<ActivityMapExtension>(intent).use { scenario ->
        Thread.sleep(2000)
        
        // Test zoom in button
        onView(withContentDescription("Zoom in")).perform(click())
        Thread.sleep(500)
        
        // Test map marker click
        // onView(withId(R.id.map)).perform(clickOnMapAtCoordinates(lat, lon))
        
        // Capture screenshot after interaction
        onView(isRoot()).perform(captureToBitmap { bitmap -> 
            bitmap.writeToTestStorage("map-after-zoom") 
        })
    }
}
```

## Troubleshooting

### Map doesn't load
- Ensure Google Play Services are installed on the test device/emulator
- Check that API key is properly configured in `google-services.json`
- Verify internet connectivity on the test device

### Test times out
- Check if `MapIdlingResource` is properly registered
- For `busy resources=MAP-LOAD`, check Google Maps authentication, connectivity, and
  whether `onMapLoaded()` fires in real-map mode. In screenshot mode check map readiness
  and app marker loading instead. A real map that never finishes loading remains busy;
  extending timeouts or resetting the shared counter does not fix the underlying failure.
- `ActivityMapExtensionLocationTest` also covers view recreation and closing during load
  to detect pending loads leaking between tests.

### Location not applied
- Verify the Intent extras are using the correct keys (`ActivityMapExtension.LAT` and `ActivityMapExtension.LON`)
- Check the activity's `onCreate()` method to see how it processes the Intent
- Look at `FragmentMap` to see how it handles the location extras

## See Also
- `UActivityMapTest.kt` - Basic smoke test for the map activity
- `BaseSyncTest.kt` - Base class with IdlingResource setup
- `ActivityMapExtension.kt` - The activity being tested
