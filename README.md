MX Tracks info
==============

Android App für Motocross Strecken

Testen
------

nur die permissions setzen

	./gradlew :MXPro:grantDebugPermission -Ppermissions=android.permission.ACCESS_FINE_LOCATION,android.permission.ACCESS_COARSE_LOCATION

Tests mit den permissions durchführen

./gradlew clean && 
./gradlew :MXPro:uninstallDebugAndroidTest && 
./gradlew :MXPro:grantDebugPermission -Ppermissions=android.permission.ACCESS_FINE_LOCATION,android.permission.ACCESS_COARSE_LOCATION &&
./gradlew :MXApp:connectedAdminDebugAndroidTest

neue Google App Store version
---------

    ./gradlew :MXApp:newVersion
    git push --tags
    
[android]: https://developer.android.com/sdk/
[reversProxy]: https://github.com/ACRA/acralyzer/wiki/Setting-up-a-reverse-proxy/
