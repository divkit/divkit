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
    DivSize,
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
 * Сетка с возможностью объединения ячеек по вертикали и горизонтали.
 */
export class DivGrid<T extends DivGridProps = DivGridProps> {
    readonly _props?: Exact<DivGridProps, T>;

    readonly type = 'grid';
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
     * Фон элемента. Может содержать несколько слоев.
     */
    background?: Type<NonEmptyArray<DivBackground>>;
    /**
     * Рамка элемента.
     */
    border?: Type<IDivBorder>;
    /**
     * Количество столбцов.
     */
    column_count: Type<number> | DivExpression;
    /**
     * Объединяет ячейки в столбце элемента [grid](div-grid.md).
     */
    column_span?: Type<number> | DivExpression;
    /**
     * Выравнивание содержимого сетки по горизонтали.
     */
    content_alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Выравнивание содержимого сетки по вертикали.
     */
    content_alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Действие при двойном нажатии на элемент.
     */
    doubletap_actions?: Type<NonEmptyArray<IDivAction>>;
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
     * Содержимое.
     */
    items: Type<NonEmptyArray<Div>>;
    /**
     * Действие при долгом нажатии на элемент.
     */
    longtap_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Внешние отступы от рамки элемента.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Внутренние отступы от рамки элемента.
     */
    paddings?: Type<IDivEdgeInsets>;
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

    constructor(props: Exact<DivGridProps, T>) {
        this.accessibility = props.accessibility;
        this.action = props.action;
        this.action_animation = props.action_animation;
        this.actions = props.actions;
        this.alignment_horizontal = props.alignment_horizontal;
        this.alignment_vertical = props.alignment_vertical;
        this.alpha = props.alpha;
        this.background = props.background;
        this.border = props.border;
        this.column_count = props.column_count;
        this.column_span = props.column_span;
        this.content_alignment_horizontal = props.content_alignment_horizontal;
        this.content_alignment_vertical = props.content_alignment_vertical;
        this.doubletap_actions = props.doubletap_actions;
        this.extensions = props.extensions;
        this.focus = props.focus;
        this.height = props.height;
        this.id = props.id;
        this.items = props.items;
        this.longtap_actions = props.longtap_actions;
        this.margins = props.margins;
        this.paddings = props.paddings;
        this.row_span = props.row_span;
        this.selected_actions = props.selected_actions;
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

interface DivGridProps {
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
     * Фон элемента. Может содержать несколько слоев.
     */
    background?: Type<NonEmptyArray<DivBackground>>;
    /**
     * Рамка элемента.
     */
    border?: Type<IDivBorder>;
    /**
     * Количество столбцов.
     */
    column_count: Type<number> | DivExpression;
    /**
     * Объединяет ячейки в столбце элемента [grid](div-grid.md).
     */
    column_span?: Type<number> | DivExpression;
    /**
     * Выравнивание содержимого сетки по горизонтали.
     */
    content_alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Выравнивание содержимого сетки по вертикали.
     */
    content_alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Действие при двойном нажатии на элемент.
     */
    doubletap_actions?: Type<NonEmptyArray<IDivAction>>;
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
     * Содержимое.
     */
    items: Type<NonEmptyArray<Div>>;
    /**
     * Действие при долгом нажатии на элемент.
     */
    longtap_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Внешние отступы от рамки элемента.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Внутренние отступы от рамки элемента.
     */
    paddings?: Type<IDivEdgeInsets>;
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
