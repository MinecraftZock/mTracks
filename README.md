MX Tracks
=========

Android App for motocross tracks

Testing
-------

only set permissions

	 ./gradlew :MXPro:grantDebugPermission -Ppermissions=android.permission.ACCESS_FINE_LOCATION,android.permission.ACCESS_COARSE_LOCATION

Run tests with all permissions
```
./gradlew clean && 
./gradlew :MXPro:uninstallDebugAndroidTest && 
./gradlew :MXPro:grantDebugPermission -Ppermissions=android.permission.ACCESS_FINE_LOCATION,android.permission.ACCESS_COARSE_LOCATION &&
./gradlew :MXApp:connectedAdminDebugAndroidTest
```
New Google App Store version
-----------------------------

    ./gradlew :MXApp:newVersion
    git push --tags
