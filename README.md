# HTTP-Notification-Plugin
This project is a custom plugin for Rundeck that sends HTTP notifications. It allows Rundeck to send HTTP requests to specified endpoints.

## Building

To build the project, run:

mvn clean package

## Testing

To run all tests:

mvn test

This will run all tests and produce:

target/rundeck-http-notification-1.0-SNAPSHOT.jar

#Quick Install

curl https://raw.githubusercontent.com/rundeck/packaging/main/scripts/rpm-setup.sh 2> /dev/null | sudo bash -s rundeckpro
sudo yum install java rundeckpro-enterprise
