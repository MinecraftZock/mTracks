dev:
	./gradlew :MXPro:assembleDebug

build:
	./gradlew :MXPro:assembleRelease

release:
	@./gradlew \
		assemblePraxisRelease \
		-Pandroid.injected.signing.store.file=$(ANDROID_KEYFILE) \
		-Pandroid.injected.signing.store.password=$(ANDROID_STORE_PASSWORD) \
		-Pandroid.injected.signing.key.alias=$(ANDROID_KEY_ALIAS) \
		-Pandroid.injected.signing.key.password=$(ANDROID_KEY_PASSWORD)
	rm -f \
		app/build/outputs/apk/*unaligned.apk \
		app/build/outputs/apk/*.txt

clean:
	./gradlew clean
