// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAlignmentHorizontal,
    DivAlignmentVertical,
    DivImageScale,
} from './';

/**
 * Фоновое изображение.
 */
export class DivImageBackground<T extends DivImageBackgroundProps = DivImageBackgroundProps> {
    readonly _props?: Exact<DivImageBackgroundProps, T>;

    readonly type = 'image';
    /**
     * Прозрачность изображения.
     */
    alpha?: Type<number> | DivExpression;
    /**
     * Горизонтальное выравнивание изображения.
     */
    content_alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Вертикальное выравнивание изображения.
     */
    content_alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Ссылка на изображение.
     */
    image_url: Type<string> | DivExpression;
    /**
     * До показа необходимо предварительно загрузить фоновое изображение.
     */
    preload_required?: Type<IntBoolean> | DivExpression;
    /**
     * Масштабирование изображения.
     */
    scale?: Type<DivImageScale> | DivExpression;

    constructor(props: Exact<DivImageBackgroundProps, T>) {
        this.alpha = props.alpha;
        this.content_alignment_horizontal = props.content_alignment_horizontal;
        this.content_alignment_vertical = props.content_alignment_vertical;
        this.image_url = props.image_url;
        this.preload_required = props.preload_required;
        this.scale = props.scale;
    }
}

interface DivImageBackgroundProps {
    /**
     * Прозрачность изображения.
     */
    alpha?: Type<number> | DivExpression;
    /**
     * Горизонтальное выравнивание изображения.
     */
    content_alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Вертикальное выравнивание изображения.
     */
    content_alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Ссылка на изображение.
     */
    image_url: Type<string> | DivExpression;
    /**
     * До показа необходимо предварительно загрузить фоновое изображение.
     */
    preload_required?: Type<IntBoolean> | DivExpression;
    /**
     * Масштабирование изображения.
     */
    scale?: Type<DivImageScale> | DivExpression;
}
