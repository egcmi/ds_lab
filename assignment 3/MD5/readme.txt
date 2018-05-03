
Dear Students,

This folder contains code for the MD5 Tasker that will be used at the
competition. It implements the interface specified in the coursework.

There is also a toy implementation of a client, for a hypothetical
team "TheCoolTeam". The client finds the solution by a brute-force
approach where it tries all numbers from 0 to the upper bound given
by the server.

Please, run it together with the server to see how client and server are 
supposed to interact.

You may also want to  modify the client so that you obtain a second client
that searches with a different strategy, for instance, by trying the possible
numbers downward, from the upper bound down to 0. Then you can connect the 
two clients to the server and let them compete.

Below, there are some hints about a useful adjustment in the server code
and how to compile and run server and client from a command line tool.

Cheers,

Werner Nutt





Adjust Code in server/Server.java
---------------------------------
 - Set  java.rmi.server.hostname  to the IP address of your server host
   If commented out, it defaults to the IP address as retrieved by the Java VM.
   Depending on your computer, the default may result in 127.0.0.1, the localhost 
   address, which is useless for communication among several hosts.
   

Compiling the server from a command line interface
--------------------------------------------------
You can compile the server (and the client) from the command line interface.
That may come in handy when you want to change your code on the RPI without using
a development tool.

Go to the directory MD5/MD5Tasker and launch the command

      javac -d bin -sourcepath src src/server/Server.java 

Here, -d tells the compiler where to store the compilation result, while
-sourcepath tells it where to find the source code.

For the client, use a similar command.


Running the server from a command line
--------------------------------------
You can start the server from the same directory MD5/MD5Tasker by typing

      java -cp bin server/Server

Here, -cp is the classpath where the Java engine can find the binary code
of the classes to execute.

Again, a similar command will work for the client.




 
