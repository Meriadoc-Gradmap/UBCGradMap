FROM ubuntu:24.04

# install nodejs

# RUN apt-get update && apt-get install -y nodejs npm
RUN apt-get update
RUN apt-get install -y openjdk-17-jdk-headless
RUN apt-get install -y wget unzip
# RUN wget https://services.gradle.org/distributions/gradle-8.11.1-bin.zip -P /tmp
# RUN unzip -d /opt/gradle /tmp/gradle-*.zip

# ENV GRADLE_HOME=/opt/gradle/gradle-8.11.1
# ENV PATH=$PATH:$GRADLE_HOME/bin 

RUN apt-get install -y nodejs npm

WORKDIR /gradmap/frontend

COPY frontend/package*.json ./

RUN npm install

# Install nginx
RUN apt-get install -y nginx


ENV PORT=5173
EXPOSE 5173 8080 80

WORKDIR /gradmap

COPY . .

WORKDIR /gradmap/app
# RUN gradle clean build

WORKDIR /gradmap/frontend 

# Quickly change the code to make it point to the proper endpoint
RUN sed -i "s/dev_yes/dev_no/g" src/Course.tsx

# Build the project
RUN npm run build

# Move it to the right place
RUN rm -rf /var/www/html
RUN mkdir /var/www/html
RUN mv dist/* /var/www/html

# Configure nginx
RUN cp nginx.conf /etc/nginx/sites-available/server.conf
RUN ln -s /etc/nginx/sites-available/server.conf /etc/nginx/sites-enabled/
RUN unlink /etc/nginx/sites-enabled/default

RUN alias scrape="./gradlew runScraperNoCache"

WORKDIR /gradmap 
RUN ./gradlew clean


RUN chmod +x ./runner.sh
RUN sed -i "s/^M$//" ./runner.sh
CMD ["./runner.sh"]
# CMD nginx
