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

# WORKDIR /gradmap/frontend

# COPY frontend/package*.json ./

# RUN npm install

# COPY frontend/ .

# ENV PORT=5173

# EXPOSE 5173

# run the npm server
# WORKDIR /gradmap/frontend
# CMD ["npm", "run", "dev"]



WORKDIR /gradmap

COPY . . 

WORKDIR /gradmap/app
RUN gradle clean build
# ENTRYPOINT [ "java", "-jar", "./app/build/libs/app.jar" ]
ENTRYPOINT ["gradle", "run"]


EXPOSE 8080

