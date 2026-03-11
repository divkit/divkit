package com.yandex.div.gradle.aar

import org.gradle.api.tasks.bundling.Zip
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault
abstract class ZipStubAarTask : Zip()
