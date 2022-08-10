// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Element size adjusts to a parent element.
 */
export class DivMatchParentSize<T extends DivMatchParentSizeProps = DivMatchParentSizeProps> {
    readonly _props?: Exact<DivMatchParentSizeProps, T>;

    readonly type = 'match_parent';
    /**
     * Weight when distributing free space between elements with the size type `match_parent` inside
     * an element. If the weight isn't specified, the elements will divide the place equally.
     */
    weight?: Type<number> | DivExpression;

    constructor(props?: Exact<DivMatchParentSizeProps, T>) {
        this.weight = props?.weight;
    }
}

interface DivMatchParentSizeProps {
    /**
     * Weight when distributing free space between elements with the size type `match_parent` inside
     * an element. If the weight isn't specified, the elements will divide the place equally.
     */
    weight?: Type<number> | DivExpression;
}
