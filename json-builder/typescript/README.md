# DivKit TypeScript JSON Buidler

[Документация DivKit](https://doc.yandex-team.ru/divkit/overview/)

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

В сомнительных случаях сверяйтесь с [документацией DivKit](https://doc.yandex-team.ru/divkit/overview/)

## Обновление библиотеки по JSON-схеме

Основные типы данных автоматически генерируются по [схеме](https://a.yandex-team.ru/arcadia/divkit/public/schema). Для этого используется генератор, бинарники которого лежат в [отдельном репозитории](https://bitbucket.browser.yandex-team.ru/projects/stardust/repos/mobile-homeapi-binaries).

Обновление библиотеки в соответствии со свежей схемой выглядит следующим образом:

```bash
# клонирование репозиториев с генератором
git clone https://bitbucket.browser.yandex-team.ru/scm/stardust/mobile-homeapi-binaries.git

# генерация кода
./codegen.sh
```

Далее создаём пулл-реквест как обычно.

## Ссылки
[Документация divkit](https://doc.yandex-team.ru/divkit/overview/)

[Чат поддержки в Telegram](https://t.me/joinchat/FtO3zxdxMWOsQzndJmlC0Q)
