# SoPra FS22 Brändi Dog (Server)

# Introduction
The aim of our project was to develop an online version of the popular swiss board game "Brändi Dog". We wanted to make a more accessible version of the game that you can play from anywhere. Our version lets you play from anywhere if you have an internet connection and a modern browser (no guarantees for Internet Explorer). The game interface should be familiar to people who have already played the game and if needed be there is a small section with the rules. Our version provides some features that the physical version of the game doesn't have, most notably that it shows you where your marble can move based on the chosen card. Our external API enables the use of voice communication, so the players can talk to each other while playing (not moderated).

# Technologies
Frontend
- **React / JavaScript / Node.js / Mui Framework** - Frontend Development
- **SockJS / STOMPJS** - Websocket (Frontend)

Backend
- **Java** - Backend Development Language
- **Gradle** - Build-Automation
- **Spring Boot Web** - RESTful service
- **Spring Boot Websocket** - Websocket (Backend)
- **JPA** - Database interface

Various
- **PostgreSQL** - Production Database
- **GitHub** - Version control, Deployment
- **Heroku** - Deployment

# High-level components
The centrepieces of the server-side application are the UserService, the Lobby and the Game and Board classes. 
These classes embrace the core functionalities of the application and changes in these classes are what is most easily visible on the client side.

### The UserService class
This class manages everything that interacts with the user repository. 
It is therefore responsible for registering, updating and retrieving users, as well as being responsible for the authentication.
The UserService class is mostly operated by the UserController, which manages all requests from the client concerning the user repository,
but it also communicates with other classes, which might need information about users.

### The Lobby class
Before a game is created, there needs to be a lobby where 4 users gather. These lobby exist in the backend as instances of the Lobby class.
This class stores all information about the lobby instance and performs actions on it that do not depend on any other sources.
The managing of the lobby instances is done by the LobbyManager class, which itself is operated by the LobbyController. 
Similar to the UserController, the LobbyController handles all requests from the client concerning the lobby.

### The Game class
After a lobby is filled and a game is started, this game exists as an instance of the Game class. Again, this class performs all actions that barely require external information.
As with the Lobby class, the GameManager class keeps track of the game instances and communicates with the GameController, to then operate on the game instances.

### The Board class
A component of each game instance is an instance of the Board class. Since Braendy Dog is a classical board game, the board is where most of the actions happen.
The Board class is therefore responsible to keep track of the state of the game, provide information tho outside classes or helper classes and finally operate on the board state.
Given how many responsibilities the class has, there exist a multitude of other classes supporting it in its actions, which can be found in the same package.
This package is pretty much dominated by the algorithm behind moves and move validation. <br>

# Launch & Deployment
## IntelliJ
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

# Roadmap
With a fully functioning base version of the game, the next steps to develop the application in the future would be:
- **Game mode 'Team play'** <br> In this game mode 2 players go up against another team. This enhances strategic gameplay and would include the swap of a card with your teammate at the beginning of each round and the possibility to move each others marbles under certain conditions (see original [instructions of Brändi Dog](https://www.braendi-dog.de/braendi-dog-spielregeln-anleitungen.html)).
- **Implementation of an AI opponent** <br> In this game mode the AI compensates for any missing players, allowing for a game of dog with less than 4 users. This increase the user experience and advance the playability of the application.
- **Refactoring the Marbles class** <br> This step only affects the Frontend. Currently the Marbles class has too many responsibilities and collected quite some technical debt. Refactoring it would increase the evolvability and maintainability of the application.

# Authors
@LucaZwahlen
@BlackStar5044
@SandroVonlanthen
@shzeng
@Xyarlo

# License
This Project is licensed under GNU General Public License v3.0. See [COPYING](COPYING) for more.

# Acknowledgments
Thank you @joelruettimann for tutoring and overlooking this project.
Thank you @royru , @realChesta , @alexscheitlin , @jan-kreischer , @alainkueng , and @solodezaldivar for providing the template for this project.
