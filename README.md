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
[Server Design Image]([jAfvDB481CqXUjXLSysxEgI1ndRePszErycsbAIXgP5di9LAYA2ALaEHiIkO2N1NZxxMRUcKkVoqxWMF9YOwJyiTyDpeWuIBuB4EZIzPhTdmYJwSVAdi8hkmcKkT1BqhgFA8yycAFJ2Fc5CPiS42R2SJFo3ZN1DYBSpI8xqaUsWAi0kwCqXgHhwAcld3Io03qhSeZ9LKREksiAXGMgnkYrhztSzmNOAvbWS8DaYCAA](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5ks9K+KDvvorxLAC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatLlqOJpEuGFocjA3K8gagrCjAQGhqRbqdph5Q0dojrOpeMHlAAasAyBaJke4IUkACSaAAGYQJxiYEjhLIerqmSxv604hq60qRhy0YwKp8ZylhyaQSWyE8tmuaYP+ILQSUVxAbM06zug35XjZhTZD2MD9oOvQOTATmNi5ZicKu3h+IEXgoOgIn2L4zDHukmSYJ5F5FNQ17SAAoruWX1FlzQtA+qhPt0gVzu2v5nECJblegVkmVVrEwPBcW+gGQbOWgGGGQUmmkjAkLDBANCqR1dZBd1jFMsx2nlJEw00HpQa0WEdVoLJRl-o15TIfFFkIHmjVJvkP5XK5GXuV2ORgH2A5DkuoWeOFG6Qrau7QjAADio6solp4peezC2deX15YV9ijmVnWTZVbLWeU60NTVTWGXB0I-aMqjjTOk09TBGoKQN5JgGN60kTNWmFJalE8jGK3aEKa0wxV-URrZ4LLXGHGYYTTGKTAJOY2osIU2aEbUxRkQWKgS0CQg32-dN4ssWjiujIyFSNJtnYI3uGO-QdR0oydZ2llMkNY+MlT9JbKBidI1sAIy9gAzAALE8J6ZAaFYTF8OgIKADa+6OKxfHbAByo7+3sMCNBdxwnddXk+UOFu-dbFS26ODvO27ntTN7+pEaM4dPIHweh2X-tPFHMd9H0ccJyFK7PeugTYD4UDYNw8DKYYwspElZ43WyZs3g0ENQ8ELPoEO9ejInHbGSjiNz2gsw9IvKAQSj49q56erCzjXWzDv+NOnJMhE+6gsUmTG9i2Rc207y+mM3RSNs6rPFc8GPNDJ80pgNYANoT52w0rfAoNNdLCwdD-ZOzU7YOx1snPWcAB7CyNsjVMv87KlhQdIZeV1Uq3W8vdXoRDHptzXBFAIlgUDKggMkGAAApCAPJ1aGACJXEADZAZjxBmmaolI7wtDttDCac4hy92AIwqAcAIDwSgOfXOxC4ar1TOvaR6At5yIUUolRajRgOz3ngjm8oYAACtOFoBPutd8QdDHKOgCY+20hL5cRvvzYmD8gynzxsrF+ktyhUXptzeQTMAob2CbNSxsF-4GQJthXxd8hajlhEQ5+s1Qlv1tPAz+YQiFxK0gk8ohT5BoPkmk8ofghIoAgaOKBaTyJsWwA07hySr5bWqto9hdjsFqEstZfBAESGdjIXdXyPQaFhQ7gELw8iuxelgMAbAvdCDxESMPAGZCD6XXKBUbKuV8qFWMJo7aa9SzmKgtxHp5QQDcDwIyUW1SfEgLvk81Z7F5BvMQXkhaI1DAKGVHaAUgCUk1M+Y855UBKnAH+bfNp6ZFogrBQi95bNYWrI-n8nJVN2TzQ2MCvSYK8XADQVo4E5RECrMZDg0ZSC3LnU0acKZFCZk0KAA))

[Server Design txt File](https://docs.google.com/document/d/1VxuEIPSLFl423qPo7eujdlGkCSosHBQJhH7fmoy1jOg/edit?usp=sharing)

