# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Chess Server Design
[Server Design Image](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5ks9K+KDvvorxLAC5wFrKaooOUCAHjysLwYeACSaAAGYQNiCaGC6YZumSFIGrS5ajiaRLhhaHLcryBqCsKMBAY6zoEvhLIerqmSxv604hq60qRhy0YwNx8ZyrhyaQSW+6HtmuaYP+ILQSUVxAbM06zug35XkphTZD2MD9oOvRqTAGmNlpZicKu3h+IEXgoOge4Hr4zDHukmSYPpF5FNQ17SAAoruAX1AFzQtA+qhPt05lzu2v5nECJaxegClSQlOFwS5voBkGmloNh4kamxpIwJCwwQDQ3G5XG2jkUybpUeUkQVTQIlBmJMGdop5Qya5ckIHm6Wdj+VzaX5uldjkYB9gOQ5LtZni2RukK2ru0IwAA4qOrLuaeXnnswynXptIXhfYo4xXlFloPFbI9WZ11xWlSUZeJcHQttoyqDVM43YVXV4RRBEwOSVI0rxdWhsDAmFJaMA0badHaEKYSidozGJqxMOlWDX0oLCyPyPVZoRnDHIIzytoBaAqRbaOqN2gKGOmDh3Xpb1n07QNQ2vSNOlXFMF3feMlQ9MLKAodIswnpkBp3LMOgIKADby6OswSwAcqOewwI043HEmemHbNxk9ELO2ixU4ujlLMsefqpFvkKysgKrTvAaDo7a6Muv61ZK5LeugTYD4UDYNw8CcYY+MpA7B3TWyo2VLUDTnZdwRPegQ5azrd2Sa95QpWgsw26MPsoBBfPKeCMCenq+O-flGve2RmO4djDXsaDREQx1UP8eagnlIjTNQPRaP9-I7fFTj7o92Aje56MJOUcPlO8vjE9e+Xo7Q13Q81-KMCKsqM9Awf8-ADaS+jnxJVk+y5TCVvGNs0biWpuUcDR-jPMvamfmE0rjL0rvnU43kZqGTmr0UBC1A5rjsgESwKBlQQGSDAAAUhAHk9NRiBCVirBO5gk4CxTpSO8LQJZXTrDdIcEdgAoKgHACA8EoAt1GFLA2HYC5f0erQucpYGFMJYWwjhktpBV0AUfWCMAABWOC0CN2LqXYRlBRHQHEVwmAAMnRYxkA-Qi4NKC-XvnPJqG8kb2hRgxdG0936d1JqUPGo5YQSzMZfCxo9X7yEZu4t+RUL5OJgBVDgSB0KWFvqMRkswibAFXo1deABZCAYSIk7xQJ1PREk-wcywYov+ah5KKSASpUs3DJqQNNvNAONlg4BC8IwrsXpYDAGwBHQg8REhx32pA0hwDKiBWCqFcKxh865MLjAABUFLwwXKCAbgeAFDtNhLoliBi57zMWePbQqz96kwsYgFpIllQbOAOfXhwJv7bP-iUj+ycegVM7FU6BZt4GYCAA)
