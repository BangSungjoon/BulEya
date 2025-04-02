pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // Hilt 추가
        plugins {
            id("com.google.dagger.hilt.android") version "2.50" // ✅ 여기서도 필요
        }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Mapbox Maven repository
        // ✅ Mapbox Maven 저장소 추가 - local.properties에서 토큰 강제 읽기
        val localProperties = java.util.Properties().apply {
            val file = rootDir.resolve("local.properties")
            if (file.exists()) {
                file.inputStream().use { load(it) }
            }
        }

        val mapboxToken = localProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN")
            ?: error("🚨 MAPBOX_DOWNLOADS_TOKEN is missing in local.properties")

        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox" // 고정값
                password = mapboxToken
            }
        }
    }
}

rootProject.name = "jangan-mobile"
include(":app")
