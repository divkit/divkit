// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Размер элемента подстраивается под его содержимое.
 */
export class DivWrapContentSize<T extends DivWrapContentSizeProps = DivWrapContentSizeProps> {
    readonly _props?: Exact<DivWrapContentSizeProps, T>;

    readonly type = 'wrap_content';
    /**
     * Итоговый размер не должен превышать родительский. На iOS и в браузере по умолчанию `false`. На
     * Android всегда `true`.
     */
    constrained?: Type<IntBoolean> | DivExpression;

    constructor(props?: Exact<DivWrapContentSizeProps, T>) {
        this.constrained = props?.constrained;
    }
}

interface DivWrapContentSizeProps {
    /**
     * Итоговый размер не должен превышать родительский. На iOS и в браузере по умолчанию `false`. На
     * Android всегда `true`.
     */
    constrained?: Type<IntBoolean> | DivExpression;
}
