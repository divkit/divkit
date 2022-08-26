# DivKit TypeScript JSON Buidler

[DivKit Documentation](https://divkit.tech/doc/)

## What is this and what for
`@divkitframework/jsonbuilder` library provides type safe tools to generate DivKit JSON

## Example

```typescript
import { divCard, DivContainer, DivText, reference, rewritRefs, template, templateHelper } from '@divkitframework/jsonbuilder';

const templates = {
  sampleBlock: new DivContainer({
    items: [
      template('header', {
        text: reference('title')
      }),
      template('header', {
        text: reference('subtitle')
      }),
    ],
  }),
  header: new DivText({
    font_size: 24,
  }),
};

const tHelper = templateHelper(templates);

console.log(JSON.stringify(
  divCard(rewriteRefs(templates), {
    log_id: 'sample_card',
    states: [
      {
        state_id: 0,
        div: tHelper.sampleBlock({
          title: "Some Title",
          subtitle: "Some Subtitle",
        }),
      },
    ],
  }
)));
```

In the result `JSON.stringify(divCard(...))` will return JSON below:
```json
{
  "templates": {
    "sampleBlock": {
      "type": "container",
      "items": [
        {
          "type": "header",
          "$text": "title"
        },
        {
          "type": "header",
          "$text": "subtitle"
        },
      ]
    },
    "header": {
      "type": "text",
      "font_size": 24
    }
  },
  "card": {
    "log_id": "sample_card",
    "states": [
      {
        "state_id": 0,
        "div": {
          "type": "sampleBlock",
          "title": "Some Title",
          "subtitle": "Some Subtitle",
        }
      }
    ]
  }
}
```

## Typesafe templates with compile-time validation

You can use `templateHelper` helper function to achieve compile time template parameters validation. Type safety works only when you enable [strictNullChecks](https://www.typescriptlang.org/docs/handbook/compiler-options.html) in tsconfig.json

```typescript
const block = template('header', {
    title: 'Some Title'
});

// using templateHelper for checking template parameters at compile-time
const safeBlock = tHelper.header({
    title: 'Some Title'
});
```

## Validity guarantees

While developing cards you need to make sure that:

- Textual string are not empty;
- Urls of images, actions, etc are valid;
- Arrays are non-empty.

[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

[Twitter](https://twitter.com/DivKitFramework)
