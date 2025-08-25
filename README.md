# Itemize

A powerful Item and Reward registration API for seamless custom item integration between Minecraft plugins.

> This plugin is currently in an early stage of development and breaking changes **will** occur.

### Features
- Universal item registration system across plugins
- Custom reward management framework
- Cross-plugin compatibility for custom items

### For Server Admins
Any extension plugin built using this plugin will require you to add it as a plugin to your server.

This plugin requires the [AlpineCore](https://github.com/alpine-network/alpine-core) plugin.

The plugin has been explicitly verified to work on releases ranging from `1.8.8` to `1.21.5`.

### For Developers
To use Itemize, you must add it as a dependency to your project:

<details>
<summary>Gradle (Kotlin DSL)</summary>

```kotlin
repositories {
    maven("https://lib.alpn.cloud/releases")
}

dependencies {
    compileOnly("co.crystaldev:itemize-api:0.4.0")
}
```
</details>


<details>
<summary>Gradle (Groovy DSL)</summary>

```groovy
repositories {
    maven {
        url 'https://lib.alpn.cloud/releases'
    }
}

dependencies {
    compileOnly 'co.crystaldev:itemize-api:0.4.0'
}
```
</details>

<details>
<summary>Maven</summary>

```xml
<repositories>
  <repository>
    <name>Alpine Cloud</name>
    <url>https://lib.alpn.cloud/releases</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>co.crystaldev</groupId>
    <artifactId>itemize-api</artifactId>
    <version>0.4.0</version>
  </dependency>
</dependencies>
```
</details>

All classes and methods that are part of the API should have Javadocs. If one does not, open an issue.

Keep in mind that using this library will require it to be added as a plugin on any server using your plugin. **DO NOT** shade it into your own plugin.

### License
This library is licensed under the Mozilla Public License v2.0. For information regarding your requirements in the use of this library, please see [Mozilla's FAQ](https://www.mozilla.org/en-US/MPL/2.0/FAQ/).