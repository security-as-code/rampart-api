## Rampart-API

### Building
> Notes:
All commands below should be executed from the root project directory.
A value **must** be provided for properties: jdkHome, buildNumber

On Linux:
```sh
$ ./gradlew clean assemble fatJar javadoc -PjdkHome=/path/to/desired/jdk6 -PbuildNumber=1 [--info | --stacktrace | --debug]
```

### Testing
On Linux:
```sh
$ ./gradlew clean test -PjdkHome=/path/to/desired/jdk6 -PbuildNumber=1 [--info | --stacktrace | --debug]
```
