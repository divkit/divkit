# DIVKIT

**DivKit** 🐋 is an open source Server-Driven UI (SDUI) framework.
SDUI is a an emerging technique that leverage the server to build the user interfaces of their mobile apps.

The convenience of DivKit as a server-driven UI technology is that it doesn’t require much at the start. You can transfer a view or even a particular element (such as a card or button) to Divs. At the very beginning, even a server isn’t needed: DivKit is easy to use as a framework that facilitates development. You can include all data on the client-side, and when the server response mechanism becomes available, the app can start receiving updates right away.

We’ve made a sandbox for you to experiment with. The DivKit web engine is connected to it, but you can also download iOS/Android app demos from stores and link them to the sandbox. The data will be updated live: the sandbox connects to the playground app via web sockets.

[Documentation](https://divkit.tech/doc). [Medium](https://medium.com/p/cad519252f0f). [Habr](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

[Twitter](https://twitter.com/divkit)

Project structure:

## Api Generator
Used for generating general Api for all platforns from Schema.

## Client
Several libraries for rendering UIs on Android, iOS and Web.

## Json-builder
Libraries that build JSON in DivKit format.

## Schema
JSON schema that describes DivKit data format.

## Test data
Samples and test data.
