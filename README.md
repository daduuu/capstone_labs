# Better Basic Chat Bot

Better Basic Chat Bot is a cool bot that utlises java server input output so you can send and recieve messages back from the server!

## Installation

Download the latest version of java and your favorite compiler. Then download the code files and run them in the compiler

## Functionality and Usage
When first loggin in, a SUBMITNAME request will be sent, so you must enter a username, no whitespaces, with all alphanumeric characters, beginning with a \*, such as

```
*WonjunLee 
```
After that, the client will display and send NAME [name] to the server for processing.
```
[name] has joined ---> NAME [name]
```
The user will be greeted by a message from a server which displays [name] has joined.
```
WELCOME [name] ---> [name] has joined.
```
There are four other messages that the server will display the following to the client
```
CHAT [name] [msg] ---> [name]: [msg]
PCHAT [name] [msg] ---> [name] (private): [msg]
MUTE [name] (WRITE DOCUMENTATION ON MUTE)
EXIT [name] ---> [name] has left.
```
Chat sends a public messages that displays the name and message. A private chat displays only to the mentioned username the message, along with a private tag to denote that it is private. The mute (WRITE DOCUMENTATION ON MUTE). The exit message exits the server while displaying who has left. 

To make it easier for the client to not have to type out annoying headers, we have shorthands that send the following messages to the server
```
*(during naming) ---> NAME [name]
*(other times) ---> CHAT [msg]
SYMBOL ---> MUTE [name]
@username * ---> PCHAT [username] [msg]
/quit  ---> QUIT
```

### Process 

We first had to seperate the inner classes into their own classes to prevent merge conflicts. Then we started to see how the code worked, and then implement the requested features, coming up with our own mute feautre.




