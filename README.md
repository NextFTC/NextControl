# NextControl

![GitHub Release](https://img.shields.io/github/v/release/NextFTC/NextControl?sort=semver&label=version)
![GitHub Repo stars](https://img.shields.io/github/stars/NextFTC/NextControl?style=flat)
![GitHub last commit](https://img.shields.io/github/last-commit/NextFTC/NextControl)

NextControl is an open-source control library for
the [FIRST Tech Challenge](https://www.firstinspires.org/robotics/ftc). It provides a robust system for creating any
controller imaginable.

The docs are at [nextftc.dev/control](https://nextftc.dev/control).

## Getting Started

### Prerequisites

Ensure you have a copy of [FtcRobotController](https://github.com/FIRST-Tech-Challenge/FtcRobotController).

### Installing

In `build.dependencies.gradle`, add the dependency:

```groovy
implementation 'dev.nextftc:control:VERSION'
```

Replace `VERSION` with the latest version (shown at the top of this README).

## Basic Usage

Create a PID controller:

*Kotlin:*

```kotlin
val controlSystem = controlSystem {
    posPid(0.005, 0.0, 0.0)
}
```

*Java:*

```java
ControlSystem controlSystem = ControlSystem.builder()
        .posPid(0.005, 0, 0)
        .build();
```

Then every loop set your motor power:

*Kotlin:*

```kotlin
motor.power = controlSystem.calculate(
    KineticState(motor.position, motor.velocity)
)
```

*Java:*

```java
motor.setPower(
        controlSystem.calculate(
                KineticState(motor.getPosition(),motor.

getVelocity())
        )
        );
```

For more in-depth usage, read the [docs](https://nextftc.dev/control).

## Built With

- [Kotlin](https://kotlinlang.org/) - A modern, expressive, statically typed, general-purpose programming language for
  the JVM developed by JetBrains and sponsored by Google.
- [Gradle](https://gradle.org/) – Powerful build system for automating compilation, testing, and publishing
- [Kotest](https://kotest.io/) – Flexible and expressive testing framework for Kotlin
- [MockK](https://mockk.io/) – Mocking library for Kotlin

## Contributing

Please read our [contributing page](https://nextftc.dev/contributing) for details on our code
of conduct, and the process for submitting pull requests to us.

## Versioning

We use [Semantic Versioning](http://semver.org/) for versioning. For the versions available, see the [releases on this
repository](https://github.com/NextFTC/NextControl/releases).

## Authors

- [Davis Luxenberg](https://github.com/beepbot99)
- [Zach Harel](https://github.com/zachwaffle4)
- [Rowan McAlpin](https://rowanmcalpin.com)

See also the list of
[contributors](https://github.com/NextFTC/NextControl/contributors)
who participated in this project.

## License

This project is licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.html)

You are free to use, modify, and distribute this software under the terms of the GPLv3. Any derivative work must also be
distributed under the same license.

See the [LICENSE](LICENSE) for more information.
