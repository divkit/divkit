// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    Div,
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
    IDivCornersRadius,
    IDivEdgeInsets,
    IDivExtension,
    IDivFocus,
    IDivTooltip,
    IDivVisibilityAction,
} from './';

/**
 * Табы. Высота первого таба определяется его содержимым, а высота остальных [зависит от
 * платформы](../../location.dita#tabs).
 */
export class DivTabs<T extends DivTabsProps = DivTabsProps> {
    readonly _props?: Exact<DivTabsProps, T>;

    readonly type = 'tabs';
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
     * Обновление высоты при смене активного элемента. В браузере значение всегда `true`.
     */
    dynamic_height?: Type<IntBoolean> | DivExpression;
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
     * Разделительная черта между табами и содержимым.
     */
    has_separator?: Type<IntBoolean> | DivExpression;
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
     * Табы. Переход между табами можно осуществить с
     * помощью:`div-action://set_current_item?id=&item=` — задать текущий таб с порядковым номером
     * `item` внутри элемента, с заданным
     * `id`;`div-action://set_next_item?id=[&overflow={clamp|ring}]` — перейти на следующий таб
     * внутри элемента, с заданным `id`;`div-action://set_previous_item?id=[&overflow={clamp|ring}]`
     * — перейти на предыдущий таб внутри элемента, с заданным `id`.</p><p>Опциональный параметр
     * `overflow` позволяет задать навигацию при достижении первого или последнего элемента:`clamp` —
     * переход остановится на пограничном элементе;`ring` — переход в начало или конец, в зависимости
     * от текущего элемента.</p><p>По умолчанию, `clamp`.
     */
    items: Type<NonEmptyArray<IDivTabsItem>>;
    /**
     * Внешние отступы от рамки элемента.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Внутренние отступы от рамки элемента.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * При включенном параметре табы не будут передавать жест скролла родительскому элементу.
     */
    restrict_parent_scroll?: Type<IntBoolean> | DivExpression;
    /**
     * Объединяет ячейки в строке элемента [grid](div-grid.md).
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Список [действий](div-action.md, которые будут выполнены при выборе элемента в
     * [пейджере](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Порядковый номер таба, который будет открываться по умолчанию.
     */
    selected_tab?: Type<number> | DivExpression;
    /**
     * Цвет разделителя.
     */
    separator_color?: Type<string> | DivExpression;
    /**
     * Отступы от разделительной черты. Не используется, если `has_separator = false`.
     */
    separator_paddings?: Type<IDivEdgeInsets>;
    /**
     * Переключение табов скроллом по содержимому.
     */
    switch_tabs_by_content_swipe_enabled?: Type<IntBoolean> | DivExpression;
    /**
     * Стиль оформления заголовков табов.
     */
    tab_title_style?: Type<IDivTabsTabTitleStyle>;
    /**
     * Отступы в названии таба.
     */
    title_paddings?: Type<IDivEdgeInsets>;
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

    constructor(props: Exact<DivTabsProps, T>) {
        this.accessibility = props.accessibility;
        this.alignment_horizontal = props.alignment_horizontal;
        this.alignment_vertical = props.alignment_vertical;
        this.alpha = props.alpha;
        this.background = props.background;
        this.border = props.border;
        this.column_span = props.column_span;
        this.dynamic_height = props.dynamic_height;
        this.extensions = props.extensions;
        this.focus = props.focus;
        this.has_separator = props.has_separator;
        this.height = props.height;
        this.id = props.id;
        this.items = props.items;
        this.margins = props.margins;
        this.paddings = props.paddings;
        this.restrict_parent_scroll = props.restrict_parent_scroll;
        this.row_span = props.row_span;
        this.selected_actions = props.selected_actions;
        this.selected_tab = props.selected_tab;
        this.separator_color = props.separator_color;
        this.separator_paddings = props.separator_paddings;
        this.switch_tabs_by_content_swipe_enabled = props.switch_tabs_by_content_swipe_enabled;
        this.tab_title_style = props.tab_title_style;
        this.title_paddings = props.title_paddings;
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

interface DivTabsProps {
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
     * Обновление высоты при смене активного элемента. В браузере значение всегда `true`.
     */
    dynamic_height?: Type<IntBoolean> | DivExpression;
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
     * Разделительная черта между табами и содержимым.
     */
    has_separator?: Type<IntBoolean> | DivExpression;
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
     * Табы. Переход между табами можно осуществить с
     * помощью:`div-action://set_current_item?id=&item=` — задать текущий таб с порядковым номером
     * `item` внутри элемента, с заданным
     * `id`;`div-action://set_next_item?id=[&overflow={clamp|ring}]` — перейти на следующий таб
     * внутри элемента, с заданным `id`;`div-action://set_previous_item?id=[&overflow={clamp|ring}]`
     * — перейти на предыдущий таб внутри элемента, с заданным `id`.</p><p>Опциональный параметр
     * `overflow` позволяет задать навигацию при достижении первого или последнего элемента:`clamp` —
     * переход остановится на пограничном элементе;`ring` — переход в начало или конец, в зависимости
     * от текущего элемента.</p><p>По умолчанию, `clamp`.
     */
    items: Type<NonEmptyArray<IDivTabsItem>>;
    /**
     * Внешние отступы от рамки элемента.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Внутренние отступы от рамки элемента.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * При включенном параметре табы не будут передавать жест скролла родительскому элементу.
     */
    restrict_parent_scroll?: Type<IntBoolean> | DivExpression;
    /**
     * Объединяет ячейки в строке элемента [grid](div-grid.md).
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Список [действий](div-action.md, которые будут выполнены при выборе элемента в
     * [пейджере](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Порядковый номер таба, который будет открываться по умолчанию.
     */
    selected_tab?: Type<number> | DivExpression;
    /**
     * Цвет разделителя.
     */
    separator_color?: Type<string> | DivExpression;
    /**
     * Отступы от разделительной черты. Не используется, если `has_separator = false`.
     */
    separator_paddings?: Type<IDivEdgeInsets>;
    /**
     * Переключение табов скроллом по содержимому.
     */
    switch_tabs_by_content_swipe_enabled?: Type<IntBoolean> | DivExpression;
    /**
     * Стиль оформления заголовков табов.
     */
    tab_title_style?: Type<IDivTabsTabTitleStyle>;
    /**
     * Отступы в названии таба.
     */
    title_paddings?: Type<IDivEdgeInsets>;
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

/**
 * Таб.
 */
export interface IDivTabsItem {
    /**
     * Содержимое таба.
     */
    div: Type<Div>;
    /**
     * Заголовок таба.
     */
    title: Type<string> | DivExpression;
    /**
     * Действие при нажатии на заголовок активного таба.
     */
    title_click_action?: Type<IDivAction>;
}

/**
 * Стиль оформления заголовков табов.
 */
export interface IDivTabsTabTitleStyle {
    /**
     * Цвет фона заголовка активного таба.
     */
    active_background_color?: Type<string> | DivExpression;
    /**
     * Начертание заголовка активного таба.
     */
    active_font_weight?: Type<DivFontWeight> | DivExpression;
    /**
     * Цвет текста заголовка активного таба.
     */
    active_text_color?: Type<string> | DivExpression;
    /**
     * Длительность анимации смены активного заголовка.
     */
    animation_duration?: Type<number> | DivExpression;
    /**
     * Анимация смены активного заголовка.
     */
    animation_type?: Type<TabTitleStyleAnimationType> | DivExpression;
    /**
     * Радиус скругления углов заголовка. Если параметр не задан, то максимальное скругление
     * (половина от наименьшего размера). Не используется, если задан параметр `corners_radius`.
     */
    corner_radius?: Type<number> | DivExpression;
    /**
     * Радиусы скругления углов нескольких заголовков. Пустые значения заменяются на `corner_radius`.
     */
    corners_radius?: Type<IDivCornersRadius>;
    /**
     * Семейство шрифтов
     */
    font_family?: Type<DivFontFamily> | DivExpression;
    /**
     * Размер шрифта заголовка.
     */
    font_size?: Type<number> | DivExpression;
    /**
     * Единицы измерения размера шрифта заголовка.
     */
    font_size_unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Начертание. Вместо него используйте `active_font_weight` и `inactive_font_weight`.
     *
     * @deprecated
     */
    font_weight?: Type<DivFontWeight> | DivExpression;
    /**
     * Цвет фона заголовка неактивного таба.
     */
    inactive_background_color?: Type<string> | DivExpression;
    /**
     * Начертание заголовка неактивного таба.
     */
    inactive_font_weight?: Type<DivFontWeight> | DivExpression;
    /**
     * Цвет текста заголовка неактивного таба.
     */
    inactive_text_color?: Type<string> | DivExpression;
    /**
     * Интервал между соседними заголовками табов.
     */
    item_spacing?: Type<number> | DivExpression;
    /**
     * Интервал между символами заголовка.
     */
    letter_spacing?: Type<number> | DivExpression;
    /**
     * Межстрочный интервал (интерлиньяж) диапазона текста. Отсчет ведется от базовой линии шрифта.
     */
    line_height?: Type<number> | DivExpression;
    /**
     * Отступы вокруг заголовка таба.
     */
    paddings?: Type<IDivEdgeInsets>;
}

export type TabTitleStyleAnimationType =
    | 'slide'
    | 'fade'
    | 'none';


