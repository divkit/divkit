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
    DivFontFamily,
    DivFontWeight,
    DivSize,
    DivSizeUnit,
    DivTransitionTrigger,
    DivVisibility,
    IDivAccessibility,
    IDivAction,
    IDivBorder,
    IDivEdgeInsets,
    IDivExtension,
    IDivFocus,
    IDivTooltip,
    IDivVisibilityAction,
} from './';

/**
 * Элемент для ввода текста.
 */
export class DivInput<T extends DivInputProps = DivInputProps> {
    readonly _props?: Exact<DivInputProps, T>;

    readonly type = 'input';
    /**
     * Доступность для людей с ограниченными возможностями.
     */
    accessibility?: Type<IDivAccessibility>;
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
     * Расширения для дополнительной обработки элемента. Список расширений см. в разделе
     * [Кастомизация](../../extensions.dita).
     */
    extensions?: Type<NonEmptyArray<IDivExtension>>;
    /**
     * Параметры при фокусировке на элементе или потере фокуса.
     */
    focus?: Type<IDivFocus>;
    /**
     * Семейство шрифта:`text` — стандартный текстовый шрифт;`display` — семейство для шрифтов с
     * крупным кеглем.
     */
    font_family?: Type<DivFontFamily> | DivExpression;
    /**
     * Размер шрифта.
     */
    font_size?: Type<number> | DivExpression;
    /**
     * Единица измерения:`px` — физический пиксель.`dp` — логический пиксель, который не зависит от
     * плотности экрана.`sp` — логический пиксель, который зависит от размера шрифта на устройстве.
     * Указывайте в `sp` высоту. Доступен только на Android.
     */
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
     * Цвет выделения текста. Если значение не задано, будет использован цвет, установленный на
     * клиенте.
     */
    highlight_color?: Type<string> | DivExpression;
    /**
     * Цвет текста.
     */
    hint_color?: Type<string> | DivExpression;
    /**
     * Текст подсказки.
     */
    hint_text?: Type<string> | DivExpression;
    /**
     * Идентификатор элемента. На iOS используется в качестве `accessibilityIdentifier`.
     */
    id?: Type<string>;
    /**
     * Тип клавиатуры.
     */
    keyboard_type?: Type<DivInputKeyboardType> | DivExpression;
    /**
     * Интервал между символами.
     */
    letter_spacing?: Type<number> | DivExpression;
    /**
     * Межстрочный интервал (интерлиньяж) диапазона текста. Отсчет ведется от базовой линии шрифта.
     * Измеряется в единицах, заданных в `font_size_unit`.
     */
    line_height?: Type<number> | DivExpression;
    /**
     * Внешние отступы от рамки элемента.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Максимальное количество строк, которые не будут обрезаны при выходе за ограничения.
     */
    max_lines?: Type<number> | DivExpression;
    /**
     * Нативный внешний вид - линия ввода текста.
     */
    native_interface?: Type<IDivInputNativeInterface>;
    /**
     * Внутренние отступы от рамки элемента.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * Объединяет ячейки в строке элемента [grid](div-grid.md).
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Выделение введенного текста при получении фокуса.
     */
    select_all_on_focus?: Type<IntBoolean> | DivExpression;
    /**
     * Список [действий](div-action.md, которые будут выполнены при выборе элемента в
     * [пейджере](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Цвет текста.
     */
    text_color?: Type<string> | DivExpression;
    /**
     * Название переменной для хранения текста.
     */
    text_variable: Type<string>;
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

    constructor(props: Exact<DivInputProps, T>) {
        this.accessibility = props.accessibility;
        this.alignment_horizontal = props.alignment_horizontal;
        this.alignment_vertical = props.alignment_vertical;
        this.alpha = props.alpha;
        this.background = props.background;
        this.border = props.border;
        this.column_span = props.column_span;
        this.extensions = props.extensions;
        this.focus = props.focus;
        this.font_family = props.font_family;
        this.font_size = props.font_size;
        this.font_size_unit = props.font_size_unit;
        this.font_weight = props.font_weight;
        this.height = props.height;
        this.highlight_color = props.highlight_color;
        this.hint_color = props.hint_color;
        this.hint_text = props.hint_text;
        this.id = props.id;
        this.keyboard_type = props.keyboard_type;
        this.letter_spacing = props.letter_spacing;
        this.line_height = props.line_height;
        this.margins = props.margins;
        this.max_lines = props.max_lines;
        this.native_interface = props.native_interface;
        this.paddings = props.paddings;
        this.row_span = props.row_span;
        this.select_all_on_focus = props.select_all_on_focus;
        this.selected_actions = props.selected_actions;
        this.text_color = props.text_color;
        this.text_variable = props.text_variable;
        this.tooltips = props.tooltips;
        this.transition_change = props.transition_change;
        this.transition_in = props.transition_in;
        this.transition_out = props.transition_out;
        this.transition_triggers = props.transition_triggers;
        this.visibility = props.visibility;
        this.visibility_action = props.visibility_action;
        this.visibility_actions = props.visibility_actions;
        this.width = props.width;
    }
}

interface DivInputProps {
    /**
     * Доступность для людей с ограниченными возможностями.
     */
    accessibility?: Type<IDivAccessibility>;
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
     * Расширения для дополнительной обработки элемента. Список расширений см. в разделе
     * [Кастомизация](../../extensions.dita).
     */
    extensions?: Type<NonEmptyArray<IDivExtension>>;
    /**
     * Параметры при фокусировке на элементе или потере фокуса.
     */
    focus?: Type<IDivFocus>;
    /**
     * Семейство шрифта:`text` — стандартный текстовый шрифт;`display` — семейство для шрифтов с
     * крупным кеглем.
     */
    font_family?: Type<DivFontFamily> | DivExpression;
    /**
     * Размер шрифта.
     */
    font_size?: Type<number> | DivExpression;
    /**
     * Единица измерения:`px` — физический пиксель.`dp` — логический пиксель, который не зависит от
     * плотности экрана.`sp` — логический пиксель, который зависит от размера шрифта на устройстве.
     * Указывайте в `sp` высоту. Доступен только на Android.
     */
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
     * Цвет выделения текста. Если значение не задано, будет использован цвет, установленный на
     * клиенте.
     */
    highlight_color?: Type<string> | DivExpression;
    /**
     * Цвет текста.
     */
    hint_color?: Type<string> | DivExpression;
    /**
     * Текст подсказки.
     */
    hint_text?: Type<string> | DivExpression;
    /**
     * Идентификатор элемента. На iOS используется в качестве `accessibilityIdentifier`.
     */
    id?: Type<string>;
    /**
     * Тип клавиатуры.
     */
    keyboard_type?: Type<DivInputKeyboardType> | DivExpression;
    /**
     * Интервал между символами.
     */
    letter_spacing?: Type<number> | DivExpression;
    /**
     * Межстрочный интервал (интерлиньяж) диапазона текста. Отсчет ведется от базовой линии шрифта.
     * Измеряется в единицах, заданных в `font_size_unit`.
     */
    line_height?: Type<number> | DivExpression;
    /**
     * Внешние отступы от рамки элемента.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Максимальное количество строк, которые не будут обрезаны при выходе за ограничения.
     */
    max_lines?: Type<number> | DivExpression;
    /**
     * Нативный внешний вид - линия ввода текста.
     */
    native_interface?: Type<IDivInputNativeInterface>;
    /**
     * Внутренние отступы от рамки элемента.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * Объединяет ячейки в строке элемента [grid](div-grid.md).
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Выделение введенного текста при получении фокуса.
     */
    select_all_on_focus?: Type<IntBoolean> | DivExpression;
    /**
     * Список [действий](div-action.md, которые будут выполнены при выборе элемента в
     * [пейджере](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Цвет текста.
     */
    text_color?: Type<string> | DivExpression;
    /**
     * Название переменной для хранения текста.
     */
    text_variable: Type<string>;
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

export type DivInputKeyboardType =
    | 'text'
    | 'phone'
    | 'number'
    | 'email'
    | 'uri'
    | 'date';

/**
 * Нативный внешний вид - линия ввода текста.
 */
export interface IDivInputNativeInterface {
    /**
     * Цвет линии ввода текста.
     */
    color: Type<string> | DivExpression;
}

