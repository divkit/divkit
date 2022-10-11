// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithArrayOfExpressions<T extends EntityWithArrayOfExpressionsProps = EntityWithArrayOfExpressionsProps> {
    readonly _props?: Exact<EntityWithArrayOfExpressionsProps, T>;

    readonly type = 'entity_with_array_of_expressions';
    items: Type<NonEmptyArray<string | DivExpression>>;

    constructor(props: Exact<EntityWithArrayOfExpressionsProps, T>) {
        this.items = props.items;
    }
}

export interface EntityWithArrayOfExpressionsProps {
    items: Type<NonEmptyArray<string | DivExpression>>;
}
