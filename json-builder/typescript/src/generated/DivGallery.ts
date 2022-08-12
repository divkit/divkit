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
    IDivBorder,
    IDivEdgeInsets,
    IDivExtension,
    IDivFocus,
    IDivTooltip,
    IDivVisibilityAction,
} from './';

/**
 * Галерея. Содержит горизонтальный или вертикальный набор карточек, которые можно скроллить.
 */
export class DivGallery<T extends DivGalleryProps = DivGalleryProps> {
    readonly _props?: Exact<DivGalleryProps, T>;

    readonly type = 'gallery';
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
     * Количество колонок для плиточной верстки.
     */
    column_count?: Type<number> | DivExpression;
    /**
     * Объединяет ячейки в столбце элемента [grid](div-grid.md).
     */
    column_span?: Type<number> | DivExpression;
    /**
     * Выравнивание элементов в направлении, перпендикулярном направлению скролла. В горизонтальных
     * галереях:`start` — выравнивание по верхнему краю карточки;`center` — по центру;`end` — по
     * нижнему краю.</p><p>В вертикальных галереях:`start` — выравнивание по левому краю
     * карточки;`center` — по центру;`end` — по правому краю.
     */
    cross_content_alignment?: Type<DivGalleryCrossContentAlignment> | DivExpression;
    /**
     * Порядковый номер элемента галереи, к которому будет выполнен скролл по умолчанию. Для
     * `scroll_mode`:`default` — положение скролла устанавливается в начало элемента, без учета
     * `item_spacing`;`paging` — положение скролла устанавливается в центр элемента.
     */
    default_item?: Type<number> | DivExpression;
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
     * Расстояние между элементами.
     */
    item_spacing?: Type<number> | DivExpression;
    /**
     * Элементы галереи. Скролл к элементам можно осуществить с
     * помощью:`div-action://set_current_item?id=&item=` — скролл к элементу с порядковым номером
     * `item` внутри элемента, с заданным
     * `id`;`div-action://set_next_item?id=[&overflow={clamp|ring}]` — скролл к следующему элементу
     * внутри элемента, с заданным `id`;`div-action://set_previous_item?id=[&overflow={clamp|ring}]`
     * — скролл к предыдущему элементу внутри элемента, с заданным `id`.</p><p>Опциональный параметр
     * `overflow` позволяет задать навигацию при достижении первого или последнего элемента:`clamp` —
     * переход остановится на пограничном элементе;`ring` — переход в начало или конец, в зависимости
     * от текущего элемента.</p><p>По умолчанию, `clamp`.
     */
    items: Type<NonEmptyArray<Div>>;
    /**
     * Внешние отступы от рамки элемента.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Ориентация галереи.
     */
    orientation?: Type<DivGalleryOrientation> | DivExpression;
    /**
     * Внутренние отступы от рамки элемента.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * При включенном параметре галерея не будет передавать жест скролла родительскому элементу.
     */
    restrict_parent_scroll?: Type<IntBoolean> | DivExpression;
    /**
     * Объединяет ячейки в строке элемента [grid](div-grid.md).
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Тип скролла: `default` — непрерывный, `paging` — постраничный.
     */
    scroll_mode?: Type<DivGalleryScrollMode> | DivExpression;
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

    constructor(props: Exact<DivGalleryProps, T>) {
        this.accessibility = props.accessibility;
        this.alignment_horizontal = props.alignment_horizontal;
        this.alignment_vertical = props.alignment_vertical;
        this.alpha = props.alpha;
        this.background = props.background;
        this.border = props.border;
        this.column_count = props.column_count;
        this.column_span = props.column_span;
        this.cross_content_alignment = props.cross_content_alignment;
        this.default_item = props.default_item;
        this.extensions = props.extensions;
        this.focus = props.focus;
        this.height = props.height;
        this.id = props.id;
        this.item_spacing = props.item_spacing;
        this.items = props.items;
        this.margins = props.margins;
        this.orientation = props.orientation;
        this.paddings = props.paddings;
        this.restrict_parent_scroll = props.restrict_parent_scroll;
        this.row_span = props.row_span;
        this.scroll_mode = props.scroll_mode;
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

interface DivGalleryProps {
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
     * Количество колонок для плиточной верстки.
     */
    column_count?: Type<number> | DivExpression;
    /**
     * Объединяет ячейки в столбце элемента [grid](div-grid.md).
     */
    column_span?: Type<number> | DivExpression;
    /**
     * Выравнивание элементов в направлении, перпендикулярном направлению скролла. В горизонтальных
     * галереях:`start` — выравнивание по верхнему краю карточки;`center` — по центру;`end` — по
     * нижнему краю.</p><p>В вертикальных галереях:`start` — выравнивание по левому краю
     * карточки;`center` — по центру;`end` — по правому краю.
     */
    cross_content_alignment?: Type<DivGalleryCrossContentAlignment> | DivExpression;
    /**
     * Порядковый номер элемента галереи, к которому будет выполнен скролл по умолчанию. Для
     * `scroll_mode`:`default` — положение скролла устанавливается в начало элемента, без учета
     * `item_spacing`;`paging` — положение скролла устанавливается в центр элемента.
     */
    default_item?: Type<number> | DivExpression;
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
     * Расстояние между элементами.
     */
    item_spacing?: Type<number> | DivExpression;
    /**
     * Элементы галереи. Скролл к элементам можно осуществить с
     * помощью:`div-action://set_current_item?id=&item=` — скролл к элементу с порядковым номером
     * `item` внутри элемента, с заданным
     * `id`;`div-action://set_next_item?id=[&overflow={clamp|ring}]` — скролл к следующему элементу
     * внутри элемента, с заданным `id`;`div-action://set_previous_item?id=[&overflow={clamp|ring}]`
     * — скролл к предыдущему элементу внутри элемента, с заданным `id`.</p><p>Опциональный параметр
     * `overflow` позволяет задать навигацию при достижении первого или последнего элемента:`clamp` —
     * переход остановится на пограничном элементе;`ring` — переход в начало или конец, в зависимости
     * от текущего элемента.</p><p>По умолчанию, `clamp`.
     */
    items: Type<NonEmptyArray<Div>>;
    /**
     * Внешние отступы от рамки элемента.
     */
    margins?: Type<IDivEdgeInsets>;
    /**
     * Ориентация галереи.
     */
    orientation?: Type<DivGalleryOrientation> | DivExpression;
    /**
     * Внутренние отступы от рамки элемента.
     */
    paddings?: Type<IDivEdgeInsets>;
    /**
     * При включенном параметре галерея не будет передавать жест скролла родительскому элементу.
     */
    restrict_parent_scroll?: Type<IntBoolean> | DivExpression;
    /**
     * Объединяет ячейки в строке элемента [grid](div-grid.md).
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Тип скролла: `default` — непрерывный, `paging` — постраничный.
     */
    scroll_mode?: Type<DivGalleryScrollMode> | DivExpression;
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

export type DivGalleryCrossContentAlignment =
    | 'start'
    | 'center'
    | 'end';

export type DivGalleryOrientation =
    | 'horizontal'
    | 'vertical';

export type DivGalleryScrollMode =
    | 'paging'
    | 'default';

