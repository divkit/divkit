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
    DivDrawable,
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
    IDivPoint,
    IDivTooltip,
    IDivVisibilityAction,
} from './';

/**
 * Слайдер для выбора значения в диапазоне.
 */
export class DivSlider<T extends DivSliderProps = DivSliderProps> {
    readonly _props?: Exact<DivSliderProps, T>;

    readonly type = 'slider';
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
     * Внешние отступы от рамки элемента.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Максимальное значение. Должно быть больше минимального.
     */
    max_value?: Type<number> | DivExpression;
    /**
     * Минимальное значение.
     */
    min_value?: Type<number> | DivExpression;
    /**
     * Внутренние отступы от рамки элемента.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * Объединяет ячейки в строке элемента [grid](div-grid.md).
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Доступность для второго указателя.
     */
    secondary_value_accessibility?: Type<IDivAccessibility>;
    /**
     * Список [действий](div-action.md, которые будут выполнены при выборе элемента в
     * [пейджере](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Стиль второго указателя.
     */
    thumb_secondary_style?: Type<DivDrawable>;
    /**
     * Стиль текста во втором указателе.
     */
    thumb_secondary_text_style?: Type<IDivSliderTextStyle>;
    /**
     * Название переменной для хранения текущего значения второго указателя.
     */
    thumb_secondary_value_variable?: Type<string>;
    /**
     * Стиль первого указателя.
     */
    thumb_style: Type<DivDrawable>;
    /**
     * Стиль текста в первом указателе.
     */
    thumb_text_style?: Type<IDivSliderTextStyle>;
    /**
     * Название переменной для хранения текущего значения указателя.
     */
    thumb_value_variable?: Type<string>;
    /**
     * Стиль активных засечек.
     */
    tick_mark_active_style?: Type<DivDrawable>;
    /**
     * Стиль неактивных засечек.
     */
    tick_mark_inactive_style?: Type<DivDrawable>;
    /**
     * Привязанные к элементу всплывающие подсказки. Подсказка может быть показана по
     * `div-action://show_tooltip?id=`, скрыта по `div-action://hide_tooltip?id=`, где `id` — id
     * подсказки.
     */
    tooltips?: Type<NonEmptyArray<IDivTooltip>>;
    /**
     * Стиль активной части шкалы.
     */
    track_active_style: Type<DivDrawable>;
    /**
     * Стиль неактивной части шкалы.
     */
    track_inactive_style: Type<DivDrawable>;
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

    constructor(props: Exact<DivSliderProps, T>) {
        this.accessibility = props.accessibility;
        this.alignment_horizontal = props.alignment_horizontal;
        this.alignment_vertical = props.alignment_vertical;
        this.alpha = props.alpha;
        this.background = props.background;
        this.border = props.border;
        this.column_span = props.column_span;
        this.extensions = props.extensions;
        this.focus = props.focus;
        this.height = props.height;
        this.id = props.id;
        this.margins = props.margins;
        this.max_value = props.max_value;
        this.min_value = props.min_value;
        this.paddings = props.paddings;
        this.row_span = props.row_span;
        this.secondary_value_accessibility = props.secondary_value_accessibility;
        this.selected_actions = props.selected_actions;
        this.thumb_secondary_style = props.thumb_secondary_style;
        this.thumb_secondary_text_style = props.thumb_secondary_text_style;
        this.thumb_secondary_value_variable = props.thumb_secondary_value_variable;
        this.thumb_style = props.thumb_style;
        this.thumb_text_style = props.thumb_text_style;
        this.thumb_value_variable = props.thumb_value_variable;
        this.tick_mark_active_style = props.tick_mark_active_style;
        this.tick_mark_inactive_style = props.tick_mark_inactive_style;
        this.tooltips = props.tooltips;
        this.track_active_style = props.track_active_style;
        this.track_inactive_style = props.track_inactive_style;
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

interface DivSliderProps {
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
     * Внешние отступы от рамки элемента.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Максимальное значение. Должно быть больше минимального.
     */
    max_value?: Type<number> | DivExpression;
    /**
     * Минимальное значение.
     */
    min_value?: Type<number> | DivExpression;
    /**
     * Внутренние отступы от рамки элемента.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * Объединяет ячейки в строке элемента [grid](div-grid.md).
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Доступность для второго указателя.
     */
    secondary_value_accessibility?: Type<IDivAccessibility>;
    /**
     * Список [действий](div-action.md, которые будут выполнены при выборе элемента в
     * [пейджере](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Стиль второго указателя.
     */
    thumb_secondary_style?: Type<DivDrawable>;
    /**
     * Стиль текста во втором указателе.
     */
    thumb_secondary_text_style?: Type<IDivSliderTextStyle>;
    /**
     * Название переменной для хранения текущего значения второго указателя.
     */
    thumb_secondary_value_variable?: Type<string>;
    /**
     * Стиль первого указателя.
     */
    thumb_style: Type<DivDrawable>;
    /**
     * Стиль текста в первом указателе.
     */
    thumb_text_style?: Type<IDivSliderTextStyle>;
    /**
     * Название переменной для хранения текущего значения указателя.
     */
    thumb_value_variable?: Type<string>;
    /**
     * Стиль активных засечек.
     */
    tick_mark_active_style?: Type<DivDrawable>;
    /**
     * Стиль неактивных засечек.
     */
    tick_mark_inactive_style?: Type<DivDrawable>;
    /**
     * Привязанные к элементу всплывающие подсказки. Подсказка может быть показана по
     * `div-action://show_tooltip?id=`, скрыта по `div-action://hide_tooltip?id=`, где `id` — id
     * подсказки.
     */
    tooltips?: Type<NonEmptyArray<IDivTooltip>>;
    /**
     * Стиль активной части шкалы.
     */
    track_active_style: Type<DivDrawable>;
    /**
     * Стиль неактивной части шкалы.
     */
    track_inactive_style: Type<DivDrawable>;
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

/**
 * Описания пока нет
 */
export interface IDivSliderTextStyle {
    /**
     * Размер шрифта.
     */
    font_size: Type<number> | DivExpression;
    font_size_unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Начертание.
     */
    font_weight?: Type<DivFontWeight> | DivExpression;
    /**
     * Сдвиг относительно центра.
     */
    offset?: Type<IDivPoint>;
    /**
     * Цвет текста.
     */
    text_color?: Type<string> | DivExpression;
}

