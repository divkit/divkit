# DivKit TypeScript JSON Buidler

[Документация DivKit](https://divkit.tech/doc/)

## Что это и зачем
Библиотека `@divkit/jsonbuilder` предоставляет типобезопасные инструменты для генерации JSON-описаний высокоуровневой верстки для клиентов DivKit.

## Пример

```typescript
import { divCard, DivContainer, DivText, reference, rewritRefs, template, templateHelper } from '@divkit/jsonbuilder';

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

В результате `JSON.stringify(divCard(...))` вернет следующий JSON:
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

## Типизированные шаблоны (compile-time валидация)
Для проверки компилятором набора шаблонных свойств можно использовать вспомогательную функцию `templateHelper`. Типизация работает корректно только при включенном флаге [strictNullChecks](https://www.typescriptlang.org/docs/handbook/compiler-options.html) в tsconfig.json!

```typescript
const block = template('header', {
    title: 'Some Title'
});

// вариант с templateHelper проверяет список параметров и их типы во время компиляции
const safeBlock = tHelper.header({
    title: 'Some Title'
});
```

## Гарантии валидности

При разработке карточки нужно самостоятельно следить за тем, чтобы

- Текстовые строки не были пустыми;
- Урлы картинок, действий и др. были валидными;
- Массивы не был пустыми.

В сомнительных случаях сверяйтесь с [документацией DivKit](https://divkit.tech/doc/)


[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

[Twitter](https://twitter.com/DivKitFramework)
