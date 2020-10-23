# Better Basic Chat Bot

Better Basic Chat Bot is a cool bot that utlises java server input output so you can send and recieve messages back from the server!

## Installation

Download the latest version of java and your favorite compiler. Then download the code files and run them in the compiler

## Functionality and Usage
When first loggin in, a SUBMITNAME request will be sent, so you must enter a username, no whitespaces, with all alphanumeric characters, such as:

```
WonjunLee 
```
After that, the client will display and send NAME [name] to the server for processing.
```
[name] has joined ---> NAME [name]
```
The user will be greeted by a message from a server which displays [name] has joined.
```
WELCOME [name] ---> [name] has joined.
```
It will also display the list of users that are in the server.
```
LIST [list of names] ---> LIST: name1, name2, ... (only displays when you join or when you type /whoishere)
```
There are four other messages that the server will display the following to the client
```
CHAT [name] [msg] ---> [name]: [msg]
PCHAT [name] [msg] ---> [name] (private): [msg]
MUTE [name] ---> You have been muted by [name]
MUTED ---> You are muted. Type /unmute to unmute yourself.
UNMUTE ---> You are unmuted. You can talk now!
EXIT [name] ---> [name] has left.
```
Chat sends a public messages that displays the name and message. A private chat displays only to the mentioned username the message, along with a private tag to denote that it is private. The private message can be sent to multiple people by using multiple "@username" command in one line. The mute will allow the client to mute other users or yourself. After muting someone, it will display to that user that you have muted that user and that user will not be able to talk until he unmutes himself. While the user is muted, no one will be able to see his text, and only he will be able to see that he is muted and has to type /unmute to unmute himself. /whoishere displays all the users currently connected to the server. The exit message exits the server while displaying who has left. 

To make it easier for the client to not have to type out annoying headers, we have shorthands that send the following messages to the server
```
*(during naming) ---> NAME [name]
*(other times) ---> CHAT [msg]
/mute username ---> MUTE [username]
/unmute ---> UNMUTE
@username * ---> PCHAT [username] [msg]
@username1 @username2 @username3... * ---> PCHAT [username1] [msg], PCHAT [username2] [msg], ...
/quit  ---> QUIT
/whoishere ---> LIST
```
These Message headers (e.g. NAME, CHAT, MUTE, etc.) are now their own objects, not String. The communication between the server and client is also serialized, sending object instead of String.

## GUI Implementation

Our program now has a GUI using javafx. After running the program, you will be able to connect to the server and then will be given a text box to enter your name. The rules are same for your name: no whitespaces with all alphanumeric characters. In GUI, the list of the users present in the server will be displayed on the right side. The mute function is now moved to the bottom right with a button system. If you click on the button, you will be provided with a new window, where you will be able to type the username of who you would like to mute. There is also unmute button that will only activate when you are muted. Functions, such as /whoishere and /quit, are removed as they are now implemented in other ways in GUI system, but PCHAT with multiple recipients using @ would still work. Both the GUI and original Chat system shares same Message header, allowing for more flexibility. 

### Process 

We first had to seperate the inner classes into their own classes to prevent merge conflicts. Then we started to see how the code worked, and then implement the requested features, coming up with our own mute feautre.




