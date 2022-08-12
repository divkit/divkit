// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAlignmentHorizontal,
    DivAlignmentVertical,
    DivAppearanceTransition,
    DivBackground,
    DivChangeTransition,
    DivFixedSize,
    DivFontFamily,
    DivFontWeight,
    DivGradientBackground,
    DivLineStyle,
    DivSize,
    DivSizeUnit,
    DivTransitionTrigger,
    DivVisibility,
    IDivAccessibility,
    IDivAction,
    IDivAnimation,
    IDivBorder,
    IDivEdgeInsets,
    IDivExtension,
    IDivFocus,
    IDivTooltip,
    IDivVisibilityAction,
} from './';

/**
 * Текст.
 */
export class DivText<T extends DivTextProps = DivTextProps> {
    readonly _props?: Exact<DivTextProps, T>;

    readonly type = 'text';
    /**
     * Доступность для людей с ограниченными возможностями.
     */
    accessibility?: Type<IDivAccessibility>;
    /**
     * Одно действие при нажатии на элемент. Не используется, если задан параметр `actions`.
     */
    action?: Type<IDivAction>;
    /**
     * Анимация действия. Поддерживаются `fade`, `scale` и `set`.
     */
    action_animation?: Type<IDivAnimation>;
    /**
     * Несколько действий при нажатии на элемент.
     */
    actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Горизонтальное выравнивание элемента внутри родительского.
     */
    alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Вертикальное выравнивание элемента внутри родительского.
     */
    alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Устанавливает прозрачность всего элемента: `0` — полностью прозрачный, `1` — непрозрачный.
     */
    alpha?: Type<number> | DivExpression;
    /**
     * Автоматическая обрезка текста под размер контейнера.
     */
    auto_ellipsize?: Type<IntBoolean> | DivExpression;
    /**
     * Фон элемента. Может содержать несколько слоев.
     */
    background?: Type<NonEmptyArray<DivBackground>>;
    /**
     * Рамка элемента.
     */
    border?: Type<IDivBorder>;
    /**
     * Объединяет ячейки в столбце элемента [grid](div-grid.md).
     */
    column_span?: Type<number> | DivExpression;
    /**
     * Действие при двойном нажатии на элемент.
     */
    doubletap_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Маркер обрезки текста. Отображается, когда размер текста превышает ограничение по количеству
     * строк.
     */
    ellipsis?: Type<IDivTextEllipsis>;
    /**
     * Расширения для дополнительной обработки элемента. Список расширений см. в разделе
     * [Кастомизация](../../extensions.dita).
     */
    extensions?: Type<NonEmptyArray<IDivExtension>>;
    /**
     * Параметры при фокусировке на элементе или потере фокуса.
     */
    focus?: Type<IDivFocus>;
    /**
     * Цвет текста при фокусировке на элементе.
     */
    focused_text_color?: Type<string> | DivExpression;
    /**
     * Семейство шрифтов
     */
    font_family?: Type<DivFontFamily> | DivExpression;
    /**
     * Размер шрифта.
     */
    font_size?: Type<number> | DivExpression;
    font_size_unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Начертание.
     */
    font_weight?: Type<DivFontWeight> | DivExpression;
    /**
     * Высота элемента. Для Android: если в этом или в дочернем элементе есть текст, укажите высоту в
     * `sp`, чтобы элемент масштабировался вместе с текстом. Подробнее о единицах измерения размера в
     * разделе [Верстка внутри карточки](../../layout.dita).
     */
    height?: Type<DivSize>;
    /**
     * Идентификатор элемента. На iOS используется в качестве `accessibilityIdentifier`.
     */
    id?: Type<string>;
    /**
     * Изображения, встроенные в текст.
     */
    images?: Type<NonEmptyArray<IDivTextImage>>;
    /**
     * Интервал между символами.
     */
    letter_spacing?: Type<number> | DivExpression;
    /**
     * Межстрочный интервал (интерлиньяж) диапазона текста. Отсчет ведется от базовой линии шрифта.
     */
    line_height?: Type<number> | DivExpression;
    /**
     * Действие при долгом нажатии на элемент.
     */
    longtap_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Внешние отступы от рамки элемента.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Максимальное количество строк, которые не будут обрезаны при выходе за ограничения.
     */
    max_lines?: Type<number> | DivExpression;
    /**
     * Минимальное число обрезанных строк при выходе за ограничения.
     */
    min_hidden_lines?: Type<number> | DivExpression;
    /**
     * Внутренние отступы от рамки элемента.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * Диапазон символов, в котором можно установить дополнительные параметры стиля. Определяется
     * обязательными полями `start` и `end`.
     */
    ranges?: Type<NonEmptyArray<IDivTextRange>>;
    /**
     * Объединяет ячейки в строке элемента [grid](div-grid.md).
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Выделение и копирование текста.
     */
    selectable?: Type<IntBoolean> | DivExpression;
    /**
     * Список [действий](div-action.md, которые будут выполнены при выборе элемента в
     * [пейджере](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Зачеркивание.
     */
    strike?: Type<DivLineStyle> | DivExpression;
    /**
     * Текст.
     */
    text: Type<string> | DivExpression;
    /**
     * Горизонтальное выравнивание текста.
     */
    text_alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Вертикальное выравнивание текста.
     */
    text_alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Цвет текста. Не используется, если задан параметр `text_gradient`.
     */
    text_color?: Type<string> | DivExpression;
    /**
     * Градиентный цвет текста.
     */
    text_gradient?: Type<DivGradientBackground>;
    /**
     * Привязанные к элементу всплывающие подсказки. Подсказка может быть показана по
     * `div-action://show_tooltip?id=`, скрыта по `div-action://hide_tooltip?id=`, где `id` — id
     * подсказки.
     */
    tooltips?: Type<NonEmptyArray<IDivTooltip>>;
    /**
     * Анимация изменения. Воспроизводится при изменении положения или размера элемента в новой
     * верстке.
     */
    transition_change?: Type<DivChangeTransition>;
    /**
     * Анимация появления. Воспроизводится при появлении элемента с новым id. Подробнее о концепции
     * переходов в разделе [Анимация
     * перехода](../../interaction.dita#animation/transition-animation).
     */
    transition_in?: Type<DivAppearanceTransition>;
    /**
     * Анимация исчезания. Воспроизводится при исчезании элемента в новой верстке.
     */
    transition_out?: Type<DivAppearanceTransition>;
    /**
     * Триггеры запуска анимации. Значение по умолчанию: `[state_change, visibility_change]`.
     */
    transition_triggers?: Type<NonEmptyArray<DivTransitionTrigger>>;
    /**
     * Способ обрезания текста. Вместо него используйте `ellipsis`.
     *
     * @deprecated
     */
    truncate?: Type<DivTextTruncate> | DivExpression;
    /**
     * Подчеркивание.
     */
    underline?: Type<DivLineStyle> | DivExpression;
    /**
     * Видимость элемента.
     */
    visibility?: Type<DivVisibility> | DivExpression;
    /**
     * Трекинг видимости одного элемента. Не используется, если задан параметр `visibility_actions`.
     */
    visibility_action?: Type<IDivVisibilityAction>;
    /**
     * Действия при появлении элемента на экране.
     */
    visibility_actions?: Type<NonEmptyArray<IDivVisibilityAction>>;
    /**
     * Ширина элемента.
     */
    width?: Type<DivSize>;

    constructor(props: Exact<DivTextProps, T>) {
        this.accessibility = props.accessibility;
        this.action = props.action;
        this.action_animation = props.action_animation;
        this.actions = props.actions;
        this.alignment_horizontal = props.alignment_horizontal;
        this.alignment_vertical = props.alignment_vertical;
        this.alpha = props.alpha;
        this.auto_ellipsize = props.auto_ellipsize;
        this.background = props.background;
        this.border = props.border;
        this.column_span = props.column_span;
        this.doubletap_actions = props.doubletap_actions;
        this.ellipsis = props.ellipsis;
        this.extensions = props.extensions;
        this.focus = props.focus;
        this.focused_text_color = props.focused_text_color;
        this.font_family = props.font_family;
        this.font_size = props.font_size;
        this.font_size_unit = props.font_size_unit;
        this.font_weight = props.font_weight;
        this.height = props.height;
        this.id = props.id;
        this.images = props.images;
        this.letter_spacing = props.letter_spacing;
        this.line_height = props.line_height;
        this.longtap_actions = props.longtap_actions;
        this.margins = props.margins;
        this.max_lines = props.max_lines;
        this.min_hidden_lines = props.min_hidden_lines;
        this.paddings = props.paddings;
        this.ranges = props.ranges;
        this.row_span = props.row_span;
        this.selectable = props.selectable;
        this.selected_actions = props.selected_actions;
        this.strike = props.strike;
        this.text = props.text;
        this.text_alignment_horizontal = props.text_alignment_horizontal;
        this.text_alignment_vertical = props.text_alignment_vertical;
        this.text_color = props.text_color;
        this.text_gradient = props.text_gradient;
        this.tooltips = props.tooltips;
        this.transition_change = props.transition_change;
        this.transition_in = props.transition_in;
        this.transition_out = props.transition_out;
        this.transition_triggers = props.transition_triggers;
        this.truncate = props.truncate;
        this.underline = props.underline;
        this.visibility = props.visibility;
        this.visibility_action = props.visibility_action;
        this.visibility_actions = props.visibility_actions;
        this.width = props.width;
    }
}

interface DivTextProps {
    /**
     * Доступность для людей с ограниченными возможностями.
     */
    accessibility?: Type<IDivAccessibility>;
    /**
     * Одно действие при нажатии на элемент. Не используется, если задан параметр `actions`.
     */
    action?: Type<IDivAction>;
    /**
     * Анимация действия. Поддерживаются `fade`, `scale` и `set`.
     */
    action_animation?: Type<IDivAnimation>;
    /**
     * Несколько действий при нажатии на элемент.
     */
    actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Горизонтальное выравнивание элемента внутри родительского.
     */
    alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Вертикальное выравнивание элемента внутри родительского.
     */
    alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Устанавливает прозрачность всего элемента: `0` — полностью прозрачный, `1` — непрозрачный.
     */
    alpha?: Type<number> | DivExpression;
    /**
     * Автоматическая обрезка текста под размер контейнера.
     */
    auto_ellipsize?: Type<IntBoolean> | DivExpression;
    /**
     * Фон элемента. Может содержать несколько слоев.
     */
    background?: Type<NonEmptyArray<DivBackground>>;
    /**
     * Рамка элемента.
     */
    border?: Type<IDivBorder>;
    /**
     * Объединяет ячейки в столбце элемента [grid](div-grid.md).
     */
    column_span?: Type<number> | DivExpression;
    /**
     * Действие при двойном нажатии на элемент.
     */
    doubletap_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Маркер обрезки текста. Отображается, когда размер текста превышает ограничение по количеству
     * строк.
     */
    ellipsis?: Type<IDivTextEllipsis>;
    /**
     * Расширения для дополнительной обработки элемента. Список расширений см. в разделе
     * [Кастомизация](../../extensions.dita).
     */
    extensions?: Type<NonEmptyArray<IDivExtension>>;
    /**
     * Параметры при фокусировке на элементе или потере фокуса.
     */
    focus?: Type<IDivFocus>;
    /**
     * Цвет текста при фокусировке на элементе.
     */
    focused_text_color?: Type<string> | DivExpression;
    /**
     * Семейство шрифтов
     */
    font_family?: Type<DivFontFamily> | DivExpression;
    /**
     * Размер шрифта.
     */
    font_size?: Type<number> | DivExpression;
    font_size_unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Начертание.
     */
    font_weight?: Type<DivFontWeight> | DivExpression;
    /**
     * Высота элемента. Для Android: если в этом или в дочернем элементе есть текст, укажите высоту в
     * `sp`, чтобы элемент масштабировался вместе с текстом. Подробнее о единицах измерения размера в
     * разделе [Верстка внутри карточки](../../layout.dita).
     */
    height?: Type<DivSize>;
    /**
     * Идентификатор элемента. На iOS используется в качестве `accessibilityIdentifier`.
     */
    id?: Type<string>;
    /**
     * Изображения, встроенные в текст.
     */
    images?: Type<NonEmptyArray<IDivTextImage>>;
    /**
     * Интервал между символами.
     */
    letter_spacing?: Type<number> | DivExpression;
    /**
     * Межстрочный интервал (интерлиньяж) диапазона текста. Отсчет ведется от базовой линии шрифта.
     */
    line_height?: Type<number> | DivExpression;
    /**
     * Действие при долгом нажатии на элемент.
     */
    longtap_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Внешние отступы от рамки элемента.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Максимальное количество строк, которые не будут обрезаны при выходе за ограничения.
     */
    max_lines?: Type<number> | DivExpression;
    /**
     * Минимальное число обрезанных строк при выходе за ограничения.
     */
    min_hidden_lines?: Type<number> | DivExpression;
    /**
     * Внутренние отступы от рамки элемента.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * Диапазон символов, в котором можно установить дополнительные параметры стиля. Определяется
     * обязательными полями `start` и `end`.
     */
    ranges?: Type<NonEmptyArray<IDivTextRange>>;
    /**
     * Объединяет ячейки в строке элемента [grid](div-grid.md).
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Выделение и копирование текста.
     */
    selectable?: Type<IntBoolean> | DivExpression;
    /**
     * Список [действий](div-action.md, которые будут выполнены при выборе элемента в
     * [пейджере](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Зачеркивание.
     */
    strike?: Type<DivLineStyle> | DivExpression;
    /**
     * Текст.
     */
    text: Type<string> | DivExpression;
    /**
     * Горизонтальное выравнивание текста.
     */
    text_alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Вертикальное выравнивание текста.
     */
    text_alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Цвет текста. Не используется, если задан параметр `text_gradient`.
     */
    text_color?: Type<string> | DivExpression;
    /**
     * Градиентный цвет текста.
     */
    text_gradient?: Type<DivGradientBackground>;
    /**
     * Привязанные к элементу всплывающие подсказки. Подсказка может быть показана по
     * `div-action://show_tooltip?id=`, скрыта по `div-action://hide_tooltip?id=`, где `id` — id
     * подсказки.
     */
    tooltips?: Type<NonEmptyArray<IDivTooltip>>;
    /**
     * Анимация изменения. Воспроизводится при изменении положения или размера элемента в новой
     * верстке.
     */
    transition_change?: Type<DivChangeTransition>;
    /**
     * Анимация появления. Воспроизводится при появлении элемента с новым id. Подробнее о концепции
     * переходов в разделе [Анимация
     * перехода](../../interaction.dita#animation/transition-animation).
     */
    transition_in?: Type<DivAppearanceTransition>;
    /**
     * Анимация исчезания. Воспроизводится при исчезании элемента в новой верстке.
     */
    transition_out?: Type<DivAppearanceTransition>;
    /**
     * Триггеры запуска анимации. Значение по умолчанию: `[state_change, visibility_change]`.
     */
    transition_triggers?: Type<NonEmptyArray<DivTransitionTrigger>>;
    /**
     * Способ обрезания текста. Вместо него используйте `ellipsis`.
     *
     * @deprecated
     */
    truncate?: Type<DivTextTruncate> | DivExpression;
    /**
     * Подчеркивание.
     */
    underline?: Type<DivLineStyle> | DivExpression;
    /**
     * Видимость элемента.
     */
    visibility?: Type<DivVisibility> | DivExpression;
    /**
     * Трекинг видимости одного элемента. Не используется, если задан параметр `visibility_actions`.
     */
    visibility_action?: Type<IDivVisibilityAction>;
    /**
     * Действия при появлении элемента на экране.
     */
    visibility_actions?: Type<NonEmptyArray<IDivVisibilityAction>>;
    /**
     * Ширина элемента.
     */
    width?: Type<DivSize>;
}

export type DivTextTruncate =
    | 'none'
    | 'start'
    | 'end'
    | 'middle';

/**
 * Маркер обрезки текста. Отображается, когда размер текста превышает ограничение по количеству
 * строк.
 */
export interface IDivTextEllipsis {
    /**
     * Действия при нажатии на маркер обрезки.
     */
    actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Изображения, встроенные в маркер обрезки.
     */
    images?: Type<NonEmptyArray<IDivTextImage>>;
    /**
     * Диапазоны символов внутри маркера обрезки с различными стилями текста.
     */
    ranges?: Type<NonEmptyArray<IDivTextRange>>;
    /**
     * Текст маркера.
     */
    text: Type<string> | DivExpression;
}

/**
 * Изображение.
 */
export interface IDivTextImage {
    /**
     * Высота изображения.
     */
    height?: Type<DivFixedSize>;
    /**
     * Символ, перед которым нужно вставить изображение. Чтобы вставить изображение в конец текста,
     * укажите номер последнего символа плюс один.
     */
    start: Type<number> | DivExpression;
    /**
     * Новый цвет контурного изображения.
     */
    tint_color?: Type<string> | DivExpression;
    /**
     * Ссылка на изображение.
     */
    url: Type<string> | DivExpression;
    /**
     * Ширина изображения.
     */
    width?: Type<DivFixedSize>;
}

/**
 * Дополнительные параметры диапазона символов.
 */
export interface IDivTextRange {
    /**
     * Действие при нажатии на текст.
     */
    actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Порядковый номер последнего символа, который будет включен в диапазон.
     */
    end: Type<number> | DivExpression;
    /**
     * Семейство шрифтов
     */
    font_family?: Type<DivFontFamily> | DivExpression;
    /**
     * Размер шрифта.
     */
    font_size?: Type<number> | DivExpression;
    /**
     * Единица измерения:`px` — физический пиксель.`dp` — логический пиксель, который не зависит от
     * плотности экрана.`sp` — логический пиксель, который зависит от размера шрифта на устройстве.
     * Указывайте в `sp` высоту.
     */
    font_size_unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Начертание.
     */
    font_weight?: Type<DivFontWeight> | DivExpression;
    /**
     * Расстояние между символами.
     */
    letter_spacing?: Type<number> | DivExpression;
    /**
     * Межстрочный интервал (интерлиньяж) диапазона текста. Отсчет ведется от базовой линии шрифта.
     * Измеряется в единицах, заданных в `font_size_unit`.
     */
    line_height?: Type<number> | DivExpression;
    /**
     * Порядковый номер символа, с которого начинается диапазон. Первый символ имеет номер `0`.
     */
    start: Type<number> | DivExpression;
    /**
     * Зачеркивание.
     */
    strike?: Type<DivLineStyle> | DivExpression;
    /**
     * Цвет текста.
     */
    text_color?: Type<string> | DivExpression;
    /**
     * Верхний отступ диапазона текста. Измеряется в единицах, заданных в `font_size_unit`.
     */
    top_offset?: Type<number> | DivExpression;
    /**
     * Подчеркивание.
     */
    underline?: Type<DivLineStyle> | DivExpression;
}

