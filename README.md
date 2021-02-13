ForgeGradle
===========

Minecraft mod development framework used by Forge and FML for the gradle build system

this repository is only for ForgeGradle 2.3.

[Here](https://github.com/anatawa12/ForgeGradle-1.2) is repository for ForgeGradle 1.2. If you're modding for 1.7.x and 1.8, use it.

This project is a fork of [ForgeGradle branch 'FG_2.3'](https://github.com/MinecraftForge/ForgeGradle/tree/FG_2.3).

<!-- [Example project found here](https://github.com/anatawa12/ForgeGradle-example) -->

Example project is now Work In Progress. please wait.

## How to use this ForgeGradle instead of official ForgeGradle

- add jcenter() if not added in repositories in buildscript block.
- replace "net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT" with "com.anatawa12.forge:ForgeGradle:2.3-1.0.1"

if you aren't add any libraries for buildscript, you may able to use buildscript block shown below:

```groovy
buildscript {
    repositories {
        mavenCentral()
        maven {
            name = "forge"
            url = "https://files.minecraftforge.net/maven"
        }
    }
    dependencies {
        classpath("com.anatawa12.forge:ForgeGradle:2.3-1.0.+") {
            changing = true
        }
    }
}
```
