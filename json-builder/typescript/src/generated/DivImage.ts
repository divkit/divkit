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
    DivFadeTransition,
    DivImageScale,
    DivSize,
    DivTransitionTrigger,
    DivVisibility,
    IDivAccessibility,
    IDivAction,
    IDivAnimation,
    IDivAspect,
    IDivBorder,
    IDivEdgeInsets,
    IDivExtension,
    IDivFocus,
    IDivTooltip,
    IDivVisibilityAction,
} from './';

/**
 * Изображение.
 */
export class DivImage<T extends DivImageProps = DivImageProps> {
    readonly _props?: Exact<DivImageProps, T>;

    readonly type = 'image';
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
     * Анимация прозрачности при загрузке изображения.
     */
    appearance_animation?: Type<DivFadeTransition>;
    aspect?: Type<IDivAspect>;
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
     * Горизонтальное выравнивание изображения.
     */
    content_alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Вертикальное выравнивание изображения.
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
     * Устанавливает приоритет показа превью — превью декодируется в главном потоке и показывается
     * первым кадром. Используйте параметр с осторожностью — он ухудшит время показа превью и может
     * ухудшить время запуска приложения.
     */
    high_priority_preview_show?: Type<IntBoolean> | DivExpression;
    /**
     * Идентификатор элемента. На iOS используется в качестве `accessibilityIdentifier`.
     */
    id?: Type<string>;
    /**
     * Прямая ссылка на изображение.
     */
    image_url: Type<string> | DivExpression;
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
     * Фон-заглушка до загрузки изображения.
     */
    placeholder_color?: Type<string> | DivExpression;
    /**
     * До показа необходимо предварительно загрузить фоновое изображение.
     */
    preload_required?: Type<IntBoolean> | DivExpression;
    /**
     * Превью изображения, закодированное в `base64`. Будет показано до загрузки картинки вместо
     * `placeholder_color`. Формат `data url`: `data:[;base64],<data>`
     */
    preview?: Type<string> | DivExpression;
    /**
     * Объединяет ячейки в строке элемента [grid](div-grid.md).
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Масштабирование изображения:`fit` помещает картинку в элемент целиком (свободное место
     * заполняется фоном);`fill` масштабирует картинку по размеру элемента и обрезает лишнее.
     */
    scale?: Type<DivImageScale> | DivExpression;
    /**
     * Список [действий](div-action.md, которые будут выполнены при выборе элемента в
     * [пейджере](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Новый цвет контурного изображения.
     */
    tint_color?: Type<string> | DivExpression;
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

    constructor(props: Exact<DivImageProps, T>) {
        this.accessibility = props.accessibility;
        this.action = props.action;
        this.action_animation = props.action_animation;
        this.actions = props.actions;
        this.alignment_horizontal = props.alignment_horizontal;
        this.alignment_vertical = props.alignment_vertical;
        this.alpha = props.alpha;
        this.appearance_animation = props.appearance_animation;
        this.aspect = props.aspect;
        this.background = props.background;
        this.border = props.border;
        this.column_span = props.column_span;
        this.content_alignment_horizontal = props.content_alignment_horizontal;
        this.content_alignment_vertical = props.content_alignment_vertical;
        this.doubletap_actions = props.doubletap_actions;
        this.extensions = props.extensions;
        this.focus = props.focus;
        this.height = props.height;
        this.high_priority_preview_show = props.high_priority_preview_show;
        this.id = props.id;
        this.image_url = props.image_url;
        this.longtap_actions = props.longtap_actions;
        this.margins = props.margins;
        this.paddings = props.paddings;
        this.placeholder_color = props.placeholder_color;
        this.preload_required = props.preload_required;
        this.preview = props.preview;
        this.row_span = props.row_span;
        this.scale = props.scale;
        this.selected_actions = props.selected_actions;
        this.tint_color = props.tint_color;
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

interface DivImageProps {
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
     * Анимация прозрачности при загрузке изображения.
     */
    appearance_animation?: Type<DivFadeTransition>;
    aspect?: Type<IDivAspect>;
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
     * Горизонтальное выравнивание изображения.
     */
    content_alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Вертикальное выравнивание изображения.
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
     * Устанавливает приоритет показа превью — превью декодируется в главном потоке и показывается
     * первым кадром. Используйте параметр с осторожностью — он ухудшит время показа превью и может
     * ухудшить время запуска приложения.
     */
    high_priority_preview_show?: Type<IntBoolean> | DivExpression;
    /**
     * Идентификатор элемента. На iOS используется в качестве `accessibilityIdentifier`.
     */
    id?: Type<string>;
    /**
     * Прямая ссылка на изображение.
     */
    image_url: Type<string> | DivExpression;
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
     * Фон-заглушка до загрузки изображения.
     */
    placeholder_color?: Type<string> | DivExpression;
    /**
     * До показа необходимо предварительно загрузить фоновое изображение.
     */
    preload_required?: Type<IntBoolean> | DivExpression;
    /**
     * Превью изображения, закодированное в `base64`. Будет показано до загрузки картинки вместо
     * `placeholder_color`. Формат `data url`: `data:[;base64],<data>`
     */
    preview?: Type<string> | DivExpression;
    /**
     * Объединяет ячейки в строке элемента [grid](div-grid.md).
     */
    row_span?: Type<number> | DivExpression;
    /**
     * Масштабирование изображения:`fit` помещает картинку в элемент целиком (свободное место
     * заполняется фоном);`fill` масштабирует картинку по размеру элемента и обрезает лишнее.
     */
    scale?: Type<DivImageScale> | DivExpression;
    /**
     * Список [действий](div-action.md, которые будут выполнены при выборе элемента в
     * [пейджере](div-pager.md).
     */
    selected_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Новый цвет контурного изображения.
     */
    tint_color?: Type<string> | DivExpression;
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
