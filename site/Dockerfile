FROM node:16
MAINTAINER Aleksandr Nefedov <4eb0da@yandex-team.ru>

ENV NODE_ENV=production

COPY client/dist /application/dist
COPY server/package.json server/package-lock.json server/app.js /application/
COPY server/src/* /application/src/

RUN cd /application && npm ci

CMD node /application/app.js
# CMD node -v
EXPOSE 80
