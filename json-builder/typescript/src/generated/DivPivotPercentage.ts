// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

/**
 * Location of the coordinate of the axis of rotation as a percentage relative to the element.
 */
export class DivPivotPercentage<T extends DivPivotPercentageProps = DivPivotPercentageProps> {
    readonly _props?: Exact<DivPivotPercentageProps, T>;

    readonly type = 'pivot-percentage';
    /**
     * Location of the element.
     */
    value: Type<number | DivExpression>;

    constructor(props: Exact<DivPivotPercentageProps, T>) {
        this.value = props.value;
    }
}

export interface DivPivotPercentageProps {
    /**
     * Location of the element.
     */
    value: Type<number | DivExpression>;
}
