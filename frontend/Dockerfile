FROM ubuntu:24.04

# install nodejs

RUN apt-get update && apt-get install -y nodejs npm

WORKDIR /gradmap/frontend

COPY package*.json ./

RUN npm install

COPY . .

ENV PORT=5173

EXPOSE 5173


# run the npm server
CMD ["npm", "run", "dev"]
