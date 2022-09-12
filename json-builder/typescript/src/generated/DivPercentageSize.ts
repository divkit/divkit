// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Percentage value of the element size.
 */
export class DivPercentageSize<T extends DivPercentageSizeProps = DivPercentageSizeProps> {
    readonly _props?: Exact<DivPercentageSizeProps, T>;

    readonly type = 'percentage';
    /**
     * Element size value.
     */
    value: Type<number> | DivExpression;

    constructor(props: Exact<DivPercentageSizeProps, T>) {
        this.value = props.value;
    }
}

export interface DivPercentageSizeProps {
    /**
     * Element size value.
     */
    value: Type<number> | DivExpression;
}
