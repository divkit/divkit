// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean } from '../helper';
import { Type } from '../template';

import {
    Entity,
} from './';

export class EntityWithArrayOfNestedItems<T extends EntityWithArrayOfNestedItemsProps = EntityWithArrayOfNestedItemsProps> {
    readonly _props?: Exact<EntityWithArrayOfNestedItemsProps, T>;

    readonly type = 'entity_with_array_of_nested_items';
    items: Type<(IEntityWithArrayOfNestedItemsItem)[]>;

    constructor(props: Exact<EntityWithArrayOfNestedItemsProps, T>) {
        this.items = props.items;
    }
}

export interface EntityWithArrayOfNestedItemsProps {
    items: Type<(IEntityWithArrayOfNestedItemsItem)[]>;
}

export interface IEntityWithArrayOfNestedItemsItem {
    entity: Type<Entity>;
    property: Type<string | DivExpression>;
}
