// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Процентное значение размера элемента.
 */
export class DivPercentageSize<T extends DivPercentageSizeProps = DivPercentageSizeProps> {
    readonly _props?: Exact<DivPercentageSizeProps, T>;

    readonly type = 'percentage';
    /**
     * Значение размера элемента.
     */
    value: Type<number> | DivExpression;

    constructor(props: Exact<DivPercentageSizeProps, T>) {
        this.value = props.value;
    }
}

interface DivPercentageSizeProps {
    /**
     * Значение размера элемента.
     */
    value: Type<number> | DivExpression;
}
