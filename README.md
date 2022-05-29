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

## High-level components: Backend
The centrepieces of the server-side application are the UserService, the Lobby and the Game and Board classes. 
These classes embrace the core functionalities of the application and changes in these classes are what is most easily visible on the client side.

The main function is embedded within the [Application.java](src/main/java/ch/uzh/ifi/hase/soprafs22/Application.java) file.
This method doesn't have notable responsibilities besides starting up the program.

### [The UserService class](src/main/java/ch/uzh/ifi/hase/soprafs22/rest/service/UserService.java)
This class manages everything that interacts with the user repository. 
It is therefore responsible for registering, updating and retrieving users, as well as being responsible for the authentication.
The UserService class is mostly operated by the UserController, which manages all requests from the client concerning the user repository,
but it also communicates with other classes, which might need information about users.

### [The LobbyManager class](src/main/java/ch/uzh/ifi/hase/soprafs22/lobby/LobbyManager.java)
Before a game is created, there needs to be a lobby where 4 users gather. These lobbies exist in the backend as instances of the Lobby class.
The managing of the lobby instances is done by the LobbyManager class, which itself is operated by the LobbyController. 
Similar to the UserController, the LobbyController catches all requests from the client concerning the lobby.
The LobbyManager then processes all requests received by the controller after they have been interpreted and validated by the controller.

### [The Game class](src/main/java/ch/uzh/ifi/hase/soprafs22/game/gameInstance/Game.java)
After a lobby is filled and a game is started, this game exists as an instance of the Game class.
Similar to the LobbyManager class, there also exists a GameManager class which keeps track of the existing game instances, as well as a GameController, which receives all requests concerning the game instance.
However, the controller also performs some actions directly on the game instance, instead of exclusively communicating with the GameManager.
An important component of the Game class is the Board class. Each game instance holds a board instance, which takes over a lot of responsibilities concerning the state of the game.
The Game class itself is supported by a number of other classes in the same package.

## High-level components: Frontend

### [The Menu](https://github.com/sopra-fs22-group-13/braendi-dog-client/blob/main/src/components/views/Menu.js)
On a very basic level, the frontend has two states: Outside a game and inside a game. 
When outside a game, the user will usually find themselves in the main menu. From the main menu they can directly perform most other functionalities.
These include, but are not restricted to, visiting a profile, opening a game lobby and joining an existing lobby.
The menu page is constructed from the Menu.js component.

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



## Roadmap
There are many things that could be implemented to develop the project further. But we decided on these three

In the front-end, the marble class would have to be reformatted as it has too many responsibilities and is too big. 
The two other features that would change both the front-end and  back-end are the possibility of team play and an AI . 
For the former it would mean that there would be the possibility to swap cards with your teammate at the beginning of each round and the possibility to move the other player's marble. These rules already exist in the original board game.
The second feature would allow the game to be played in the absence of other players. This design would be larger than the other two features, but would greatly help the playability of the game as it would allow even smaller groups of 4 people to play.
