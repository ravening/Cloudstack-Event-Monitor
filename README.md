# CloudStack Events and Alerts monitoring

A Spring Boot application to monitor events and alerts in the CloudStack platform

## Getting Started

Steps mentioned below will tell you how to get started withe the project.

### Prerequisites

What things you need to install the software and how to install them

```
Java 8+
Apache Maven
Any IDE to import the project


Optional

ELK stack to export the logs to elasticsearch
```

### Installing

A step by step series of instructions that tell you how to start the project

Download the project from github

```
git clone <repo url>
```

Import the project into any IDE as a Maven project.

To talk to CloudStack, you need the URL endpoint, ApiKey and SecretKey.
You need to enter these values in "application.properties" file.

It can connect to multiple CLoudStack endpoints simultaneously.
If you have 5 data centers with CloudStack installed
in different regions then you need to enter the above
mentioned three parameters corresponding to the location
name.

For ex: If you have CloudStack instance running in USA,
EUROPE and ASIA location, below is the configuration
you need to enter


```
cloudstack.platforms=usa,europe,asia

usa.url=<https://...>
usa.apiKey=
usa.secretKey=

europe.url=
europe.apiKey=
europe.secretKey=

asia.url=
asia.apiKey=
asia.secretKey=
```


Once these changes are done, you are all good to go.
Only steps left is to build the packages and start the project

Build the project from the top directory using the command

```bash
mvn package -DskipTests
```

This will generate the jar file in the target directory
Now run the project using

```bash
java -jar target/event-monitor-0.0.1.jar
```

Navigate to the following link in your browser.
The application runs the api's every 1 minute and if there are any events or\
alerts generated in the cloudstack platform then they will be automatically displayed\
in the UI.

```
http://localhost:88888/index.html
```

This app stores all the error logs in /root/application.log which can be configured

You can export the log file to an elastic search cluster using

```bash
/usr/share/logstash/bin/logstash -f logstash.conf
```


## Authors

* **Rakesh Venkatesh** - *Initial work* - [ravening](https://github.com/ravening)