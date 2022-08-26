# DivKit
<img alt="Playground app" src="client/android/app_screen.png" width="30%" style="margin:20px; float:right;">

<a href="https://github.com/divkit/divkit/stargazers">
<img alt="GitHub stars" src="https://img.shields.io/github/stars/divkit/divkit"></a>

**DivKit** 🐋 is an open source Server-Driven UI (SDUI) framework.
It allows you to roll out server-sourced updates to different app versions. Also, it can be used for fast UI prototyping, allowing you to write a layout once and then ship it to iOS, Android, and Web platforms.
DivKit is an excellent choice to start using server-driven UI in your project because it can be easily integrated as a simple view in any part of your app. At the starting point, you don’t need a server integration. You can include all JSON on the client-side to try it in a real-world application.

Also, we’ve made a [sandbox](https://divkit.tech/playground) for you to experiment with. You can try different samples in the web editor and see the results on the web or in the Android [demo app](https://play.google.com/store/apps/details?id=com.yandex.divkit.demo), both of which are available on Google Play. We’ll publish the iOS demo app shortly. The UI in the demo can be updated live: the sandbox connects to the demo app via web sockets. You can use the [DivKit website](https://divkit.tech/en) to find a lot of handy samples and documentation, but feel free to ask us anything here in the comments or via the [Telegram community chat](https://t.me/divkit_community_en).

[Documentation](https://divkit.tech/doc) | [Medium tutorial](https://medium.com/p/cad519252f0f) | [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

[Twitter](https://twitter.com/DivKitFramework)

## Demo App

<a href='https://play.google.com/store/apps/details?id=com.yandex.divkit.demo&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width="200"/></a>

<img src="client/ios/app_badge_soon.png" width="200">

## Project structure

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
