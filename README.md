# Deepend

## Minecade
This project is run and maintained by [Minecade](http://minecade.com)

![](http://files.enjin.com/265719/images/topMenu/logo_minecade.png)

## Description
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
The protocol is very simple *TODO: Document protocol*

## Building
Build the project using gradle
```gradle build```


## Dependencies
## lombok
We use lombok to simplify our lives, as there is a lot of code
needed to get this whole thing running. 
You can find lombok on their website: [projectlombok.org](https://projectlombok.org/)

## netty
We're using netty to handle the raw networking. We decied to use
netty, as we wouldn't be able to do anything better ourselves

## guava
Guava is very popular, and also very stable. It provides us with
utility classes that would be rather stupid to re-create ourselves.

## Examples
### Client
A client example can be found here: [TestGameClient.java](https://github.com/Minecade/Deepend/blob/master/Client/src/test/java/TestGameClient.java)

### Server
A server example can be found here: [TestGameServer.java](https://github.com/Minecade/Deepend/blob/master/Server/src/test/java/TestGameServer.java)

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
    compile 'com.github.Minecade:Deepend:-SNAPSHOT'
}
```

Maven
```
<dependency>
    <groupId>com.github.Minecade</groupId>
	<artifactId>Deepend</artifactId>
	<version>-SNAPSHOT</version>
</dependency>
```
