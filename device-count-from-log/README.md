##

please install maven and java 8+


##
Steps to  logs.

1.place the logs file in folder logfiles in project


2. run the command : mvn clean package

3. run the command : mvn clean compile assembly:single

4. In target folder you will get application-jar-with-dependencies.jar file and use that jar file in any folder and create logfiles folder and the the Jar file

5 To run java -jar application-jar-with-dependencies.jar