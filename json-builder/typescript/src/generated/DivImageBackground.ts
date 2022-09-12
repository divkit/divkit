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
 * Background image.
 */
export class DivImageBackground<T extends DivImageBackgroundProps = DivImageBackgroundProps> {
    readonly _props?: Exact<DivImageBackgroundProps, T>;

    readonly type = 'image';
    /**
     * Image transparency.
     */
    alpha?: Type<number> | DivExpression;
    /**
     * Horizontal image alignment.
     */
    content_alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Vertical image alignment.
     */
    content_alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Image URL.
     */
    image_url: Type<string> | DivExpression;
    /**
     * Background image must be loaded before the display.
     */
    preload_required?: Type<IntBoolean> | DivExpression;
    /**
     * Image scaling.
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

export interface DivImageBackgroundProps {
    /**
     * Image transparency.
     */
    alpha?: Type<number> | DivExpression;
    /**
     * Horizontal image alignment.
     */
    content_alignment_horizontal?: Type<DivAlignmentHorizontal> | DivExpression;
    /**
     * Vertical image alignment.
     */
    content_alignment_vertical?: Type<DivAlignmentVertical> | DivExpression;
    /**
     * Image URL.
     */
    image_url: Type<string> | DivExpression;
    /**
     * Background image must be loaded before the display.
     */
    preload_required?: Type<IntBoolean> | DivExpression;
    /**
     * Image scaling.
     */
    scale?: Type<DivImageScale> | DivExpression;
}
