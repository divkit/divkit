// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * При наведении меняет фон элемента.
 */
export class DivHoverBackgroundColor<T extends DivHoverBackgroundColorProps = DivHoverBackgroundColorProps> {
    readonly _props?: Exact<DivHoverBackgroundColorProps, T>;

    readonly type = 'background-color';
    /**
     * Цвет фона.
     */
    color: Type<string> | DivExpression;

    constructor(props: Exact<DivHoverBackgroundColorProps, T>) {
        this.color = props.color;
    }
}

interface DivHoverBackgroundColorProps {
    /**
     * Цвет фона.
     */
    color: Type<string> | DivExpression;
}
