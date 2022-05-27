# SoPra RESTful Service Template FS22


# Launch & Deployment
##IntelliJ
To build right click the `build.gradle` file and choose `Run Build`
<br>The server will now listen on [http://localhost:8080](http://localhost:8080)
## VS Code
The following extensions will help you to run it more easily:
-   `pivotal.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`
-   `richardwillis.vscode-gradle`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs22` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

## Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

Build:
`
./gradlew build
`

Run:
`
./gradlew bootRun
`
<br>The server will now listen on [http://localhost:8080](http://localhost:8080)

Test:
`
./gradlew test
`

### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## Debugging in IntelliJ
To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command) in IntelliJ,
do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug"Name of your task"

## Environment Variables
If you want to run with voice chat enabled you must have the api and app id set as environment variables
```bash
api.key=[secret]
```
```bash
api.url=[secret]
```
Finally, to enable the voice chat set
```bash
api.enabled=true
```
### Environment Variables in IntelliJ
To set these variables in IntelliJ
1. Open Tab: **Run**/Edit Configurations
2. Add a new Gradle Configuration
3. Specify your run configuration (just running is `./gradlew bootRun`)
4. Set the Environment Variables `api.key=[secret];api.url=[secret]api.enabled=true`
5. Apply the changes



### Roadmap
There are many things that could be implemented to develop the project further. But we decided on these three

In the front-end, the marble class would have to be reformatted as it has too many responsibilities and is too big. 
The two other features that would change both the front-end and  back-end are the possibility of team play and an AI . 
For the former it would mean that there would be the possibility to swap cards with your teammate at the beginning of each round and the possibility to move the other player's marble. These rules already exist in the original board game.
The second feature would allow the game to be played in the absence of other players. This design would be larger than the other two features, but would greatly help the playability of the game as it would allow even smaller groups of 4 people to play.
