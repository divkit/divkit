# DIVKIT

**DivKit** 🐋 is an open source Server-Driven UI (SDUI) framework.
SDUI is a an emerging technique that leverage the server to build the user interfaces of their mobile apps.

DivKit is a good choice to start using the Server-Driven UI in your project because it can be easily integrated as a simple view in any place of your app. At the start point, you do not need  a server integration. You can include all JSON on the client-side to try it in a real world application.

We’ve made a sandbox for you to experiment with. The DivKit Web engine is connected to it, but you can also download iOS/Android app demos from stores and link them to the sandbox. The data will be updated live: the sandbox connects to the playground app via web sockets.

[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

[Twitter](https://twitter.com/DivKitFramework)

Project structure:

## Client
Client side libraries for rendering UI on Android, iOS and Web.

[Android](client/android/), [iOS](client/ios/), [Web](client/web/divkit)

## Json-builder
Server side libraries for building JSON in DivKit format.

[TypeScript](json-builder/typescript/), [Kotlin](json-builder/kotlin/)

## Schema
JSON schema that describes DivKit data format.

## Api Generator
Used for generating general Api for all platforms from Schema.

## Test data
Samples and test data.
