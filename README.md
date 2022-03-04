# fipper-kotlin-sdk
A client library for Android (SDK)

Fipper.io - a feature toggle (aka feature flags) software. More info https://fipper.io

## Usage

See the [sample](sample).

More information and more client libraries: https://docs.fipper.io

## How to include
---

With gradle: edit your `build.gradle`:
```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    implementation 'com.github.fipper-io:fipper-kotlin-sdk:0.1.0'
}
```

Or declare it into your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.fipper-io</groupId>
    <artifactId>fipper-kotlin-sdk</artifactId>
    <version>0.1.0</version>
</dependency>
```

## License
-------
    Copyright 2022 Fipper.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
