Testing
-------

only set permissions

	 ./gradlew :MXPro:grantDebugPermission -Ppermissions=android.permission.ACCESS_FINE_LOCATION,android.permission.ACCESS_COARSE_LOCATION

Run tests with all permissions
```
./gradlew clean && 
./gradlew :MXPro:uninstallDebugAndroidTest && 
./gradlew :MXPro:grantDebugPermission -Ppermissions=android.permission.ACCESS_FINE_LOCATION,android.permission.ACCESS_COARSE_LOCATION &&
./gradlew :app:connectedAdminDebugAndroidTest
```
New Google App Store version
-----------------------------

    ./gradlew :app:newVersion
    git push --tags
