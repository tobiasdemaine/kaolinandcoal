FROM debian
WORKDIR /app
COPY package.json ./
COPY package-lock.json ./
ENV PATH /app/node_modules/.bin:$PATH
RUN apt-get update
RUN apt-get -y install curl gnupg
RUN curl -sL https://deb.nodesource.com/setup_17.x  | bash -
RUN apt-get -y install nodejs
RUN npm install
COPY . ./
CMD ["npm", "run", "start"]