# Deepend

## What is this?
This project is meant to offer a fresh take on data backend management, and cross-instance sychronization. It does this in a neat and mobile manner. 

Deepend is a project consisting of a custom protocol, a core API, a server module and a client implementation. All of this is written to allow for extreme customization, and is meant to provide the ability to implement Deepend into any pre-existing applications. Deepend aims to have a really easy to follow protocol, and is using netty for the networking backend. It's currently far from finished, but it already includes working modules for everything. The document is very vague, but is coming soon.

Deepend includes a lot of cool stuff. For example: You're able to map objects to the protocol (by wrapping them) and the core will automatically convert the objects for you. The protocol includes a CHECK_DATA method which allows you to only poll information that has been updated. The system has native callback support and much, much, more. 

## Technical Description
A custom Server/Client protcol, lightly based off of Netty. Deepend aims to 
create a very lightweight, and straight forward system for data synchronisation.
The server holds data. The server will send out, update or add data, based on
requests sent by the client.

## Data... storage?
That data is instantaneous and will not be saved when the server shuts down. This
is not meant to be a replacement for data storage, such as MySQL, but rather
a way to have data shared across multiple instances of a program.

## Modules
The core project consists of three parts:

* Core - This is the core of Deepend, and contains most of the logic
* Client - Client specific files, alongside with the client itself & the client API
* Server - This is the server

## Protocol
The protocol is very simple: [Read more here...](https://github.com/DeependProject/Deepend/wiki/Protocol)

| Channel | Wiki | Description |
| --- | --- | --- |
| ADD_DATA | [link](https://github.com/DeependProject/Deepend/wiki/ADD_DATA) | Add/Update data to the server |
| GET_DATA | [link](https://github.com/DeependProject/Deepend/wiki/GET_DATA) | Retrieve data from the server |
| REMOVE_DATA | [link](https://github.com/DeependProject/Deepend/wiki/REMOVE_DATA) | Delete data from the server |
| CHECK_DATA | [link](https://github.com/DeependProject/Deepend/wiki/CHECK_DATA) | Used to check if data has been updated |
| AUTHENTICATE | [link](https://github.com/DeependProject/Deepend/wiki/AUTHENTICATE) | Used to authenticate the client and generate a UUID |

## Building
Build the project using gradle: ```gradlew build```

## Dependencies
| Dependency | Link | Why? |
| --- | --- | --- |
| lombok | [projectlombok.org](https://projectlombok.org/) | Makes code prettier |
| snakeyaml | [snakeyaml.org](http://www.snakeyaml.org/) | YAML Processing |
| log4j 2| [logging.apache.org](http://logging.apache.org/log4j/2.x/) | Proper logging |
| jewelcli | [jewelcli.lexicalscope.com](http://jewelcli.lexicalscope.com) | Command line arguments |

## Examples
### Client
A client example can be found here: [TestGameClient.java](https://github.com/DeependProject/Deepend/blob/master/Client/src/main/java/com/minecade/deepend/client/test/TestGameClient.java)

### Server
A server example can be found here: [TestGameServer.java](https://github.com/DeependProject/Deepend/blob/master/Server/src/main/java/com/minecade/deepend/server/test/TestGameServer.java)

## CI
We have a CI build for this: <http://ci.intellectualsites.com/job/Deepend/>

## Use this as a dependency
### Repo
Gradle
```	
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
```
Maven
```
<repositories>
	<repository>
        <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```
### Dependency
Gradle
```
dependencies {
    compile 'com.github.Minecade:Deepend:v0.2'
}
```

Maven
```
<dependency>
    <groupId>com.github.Minecade</groupId>
	<artifactId>Deepend</artifactId>
	<version>v0.2</version>
</dependency>
```

## Minecade
This project is run and maintained by [Minecade](http://minecade.com)

![](http://files.enjin.com/265719/images/topMenu/logo_minecade.png)
