// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Сплошной цвет фона.
 */
export class DivSolidBackground<T extends DivSolidBackgroundProps = DivSolidBackgroundProps> {
    readonly _props?: Exact<DivSolidBackgroundProps, T>;

    readonly type = 'solid';
    /**
     * Цвет.
     */
    color: Type<string> | DivExpression;

    constructor(props: Exact<DivSolidBackgroundProps, T>) {
        this.color = props.color;
    }
}

interface DivSolidBackgroundProps {
    /**
     * Цвет.
     */
    color: Type<string> | DivExpression;
}
