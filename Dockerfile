FROM ubuntu:24.04

# install nodejs

# RUN apt-get update && apt-get install -y nodejs npm
RUN apt-get update
RUN apt-get install -y openjdk-17-jdk
RUN apt-get install -y wget unzip
RUN wget https://services.gradle.org/distributions/gradle-8.11.1-bin.zip -P /tmp
RUN unzip -d /opt/gradle /tmp/gradle-*.zip

ENV GRADLE_HOME=/opt/gradle/gradle-8.11.1
ENV PATH=$PATH:$GRADLE_HOME/bin 


RUN apt-get install -y nodejs npm

WORKDIR /gradmap/frontend

COPY frontend/package*.json ./

RUN npm install

ENV PORT=5173
EXPOSE 5173 8080


WORKDIR /gradmap

COPY . . 

WORKDIR /gradmap/app
RUN gradle clean build

WORKDIR /gradmap 
CMD ["./runner.sh"]
