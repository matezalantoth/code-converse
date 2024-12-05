FROM node:20

WORKDIR /my-app

COPY package*.json .
RUN npm install

RUN npm install -g @angular/cli

COPY . .

EXPOSE 4200

CMD ["ng","serve", "--host", "0.0.0.0"]
