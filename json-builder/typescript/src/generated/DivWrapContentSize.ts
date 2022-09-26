// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * The size of an element adjusts to its contents.
 */
export class DivWrapContentSize<T extends DivWrapContentSizeProps = DivWrapContentSizeProps> {
    readonly _props?: Exact<DivWrapContentSizeProps, T>;

    readonly type = 'wrap_content';
    /**
     * The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On
     * Android always `true`.
     */
    constrained?: Type<IntBoolean | DivExpression>;

    constructor(props?: Exact<DivWrapContentSizeProps, T>) {
        this.constrained = props?.constrained;
    }
}

export interface DivWrapContentSizeProps {
    /**
     * The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On
     * Android always `true`.
     */
    constrained?: Type<IntBoolean | DivExpression>;
}
