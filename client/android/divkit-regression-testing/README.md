# DivKit Regression Testing

Модуль ручного регрессионого тестирования DivKit. Содержит сценарии и сопутсвующие файлы версток для ручного прогона сценариев.

## Прогон сценариев

Осуществляется в `DivKit Playground`:

- Запустить `DivKit Playground`
- Выбрать раздел Testing. Загрузится список сценариев
- При выборе каждого из сценариев загрузиться Div2View с заданными для сценария параметрами и описание сценария (шаги и ожидаемый результат). Перейти на следующий сценарий можно прямо с этого экрана по кнопкам в тулбаре.

Начиная с Android 5.0+ доступна возможность записи экрана во время работы над сценарием. Её можно отключить на главном экрана Testing, в случае возникновения проблем.

## Добавление нового сценария

Для добавления нового сценария требуется:

1. Добавить файл json-файл с версткой в [`../../../test_data/regression_test_data`](divkit/public/test_data/regression_test_data).
1. Добавить сценарий в файл [`../../../test_data/regression_test_data/index.json`](divkit/public/test_data/regression_test_data/index.json):
    - Задать заголовок в поле `title`
    - Перечислить шаги сценария в поле `steps`
    - Перечислить ожидаемые результаты в поле `expected_results`
    - Опционально указать `tags` для группировки и фильтрации сценариев
    - Опционально указать `priority`, один из `blocker`, `critical`, `normal` и `minor`. По-умолчанию `normal`.
    - Задать `file` - путь к ранее добавленному файлу верстки
1. Проверить работу сценария. См. **Прогон сценария**

## Development

Входной точкой в модуль является [`RegressionActivity`](src/main/java/com/yandex/divkit/regression/RegressionActivity.kt).
Состояние ui `RegressionActivity` обновляются из [`RegressionViewModel#uiState`](src/main/java/com/yandex/divkit/regression/RegressionViewModel.kt).
`RegressionViewModel#uiState` представляет собой [Kotlin Flow](https://developer.android.com/kotlin/flow).

[Сценарии](src/main/java/com/yandex/divkit/regression/data/Scenario.kt) загружаются из [`ScenariosRepository`](src/main/java/com/yandex/divkit/regression/data/ScenariosRepository.kt),
который создается на уровне [`DivKitApplication`](../divkit-demo-app/src/main/java/com/yandex/divkit/demo/DivkitApplication.kt).

Данные сценариев отображаются в списке [`ScenarioListAdapter`](src/main/java/com/yandex/divkit/regression/ScenarioListAdapter.kt).

Данные каждого сценария отображаются в [`ScenarioActivity`](src/main/java/com/yandex/divkit/regression/ScenarioActivity.kt). За создание `Div2View` отвечает реализация в `divkit-demo-app` [`Div2ViewCreator`](src/main/java/com/yandex/divkit/regression/Div2ViewCreator.kt)
