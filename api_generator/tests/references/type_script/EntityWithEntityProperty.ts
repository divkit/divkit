// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

import {
    Entity,
} from './';

export class EntityWithEntityProperty<T extends EntityWithEntityPropertyProps = EntityWithEntityPropertyProps> {
    readonly _props?: Exact<EntityWithEntityPropertyProps, T>;

    readonly type = 'entity_with_entity_property';
    entity?: Type<Entity>;

    constructor(props?: Exact<EntityWithEntityPropertyProps, T>) {
        this.entity = props?.entity;
    }
}

export interface EntityWithEntityPropertyProps {
    entity?: Type<Entity>;
}
