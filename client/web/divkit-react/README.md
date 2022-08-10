## DivKit for Web (react way)

Компонент для реакта для отрисовки divjson, использующий `@yandex-int/divkit`.

[Смотреть основную документацию по дивкиту](https://a.yandex-team.ru/arc_vcs/portal/frontend/divkit/README.md).

### Использование

```tsx
import { Divkit } from '@yandex-int/divkit-react';

return <Divkit id="smth" json={json} />;
```

Все остальные пропсы аналогичны параметрам функции `render` из пакета `@yandex-int/divkit`.

### Обязательно стоит настроить сборку

Для сборки только нужного кода, стоит обязательно настроить `DefinePlugin` с флагом `process.env.IS_SERVER`:

```js
new webpack.DefinePlugin({
    'process.env.IS_SERVER': JSON.stringify(isServer)
})
```

Где `isServer` - используемое вами окружение, `true` или `false`.

### SSR

Есть поддержка SSR. На клиенте компонент гидрируется по тем же пропсам.
