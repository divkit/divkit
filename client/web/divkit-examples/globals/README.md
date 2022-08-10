## Рисуем divjson на клиенте без сборки

Для работы нужно подключить 2 файла:

* `@yandex-int/divkit/dist/client.css`
* `@yandex-int/divkit/dist/browser/client.js`

После этого можно будет рисовать с помощью функции `render`:

```js
Ya.Divkit.render({
    id: 'test',
    target: element,
    json: {}
});
```
