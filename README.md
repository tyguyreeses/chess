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
[Server Design Image](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5ks9K+KDvvorxLAC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatLlqOJpEuGFocjA3K8gagrCjAQGhqRbqdph5Q0dojrOgSOEsh6uqZLG-rTiGrrSpGHLRjAgnxnKWHJpBJbITy2a5pg-4gtBJRXEBszTrO6DfleGmFNkPYwP2g69DpMB6Y2BlmJwq7eH4gReCg6B7gevjMMe6SZJgpkXkU1DXtIACiu5hfUYXNC0D6qE+3S2XO7a-mcQIlsl6BqQpaWsTA8H2N5AZBvpaAYbJBSiaSMCQsMEA0IJJV1nZ5WMUyzHieUkT1TQUlBrRYRZWgnGJvJGVpsh3kqQgea5Um+Q-lchkhcZXY5GAfYDkOS6OZ4zkbpCtq7tCMAAOKjqyvmngF57MJp15nVFsX2KOSWla1qVsup5TDTlE1svlyCxBdoyqM1M6tRVMEajxNXkmATXDSRHViYUlqUTyMYDdoQpDR9KXVRGmngv1cYcZhsNMbxMAI6DaiwijZoRujFGRBYqB9cAyrnZd7XMyxsnlPTGiUwt6WpuUJ0g5dM1zRNC1LaWr1gytxzi4Fm3mdtvQq2ou0rvt66BNgPhQNg3DwPxhj0ykflnhtgNGeUN4NC9b3BAT6BDnrAByo5qx242SzZXtoLMPR+wH-2pk7MHlJ6er0xDZWzFHozQ06ibcdT8MUkjYdM2RXWY7y0m43Rf1E4L8dk8GFOyVTqM1cANrJ3rIlw6oBQY5J9MyTD4s-VbXqZPTcsx1BD1pj0gdrZrW2WbPDmG2uLkBJYKDKhAyQwAAUhAPK86MgQ6AgoANrdjvTy7VSUneLR6+9LVzkOFvAJvUBwBA8FQGno4AElpBz2+rlX6YcI7v0-t-X+-9RhAIggDEm8oYAACtD5oGTsNd859oE-2gHAlAQDM5cRkF3PCiMgwpyhvzYurNyhUWxuTeQeNQ4v2ytXcW+Vy7yFGlhHOzd3S0wpO3QB0gi6dXoaXW0-cK5hD1kA2hnVkGwWPigAeWc5J-jAfvDB481CqXUjXLSysxEgI1ndRePszErycsbAIXgP5di9LAYA2ALaEHiIkO2N1NZxxMRUcKkVoqxWMF9YOwJyiTyDpeWuIBuB4EZIzPhTdmYJwSVAdi8hkmcKkT1BqhgFA8yycAFJ2Fc5CPiS42R2SJFo3ZN1DYBSpI8xqaUsWAi0kwCqXgHhwAcld3Io03qhSeZ9LKREksiAXGMgnkYrhztSzmNOAvbWS8DaYCAA)
[Server Design txt File](https://drive.google.com/file/d/1NA3oqR_1PY8qOt0kiCo5-pRZeXUMCJJA/view?usp=share_link)
