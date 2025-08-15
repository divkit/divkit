// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithJsonProperty<T extends EntityWithJsonPropertyProps = EntityWithJsonPropertyProps> {
    readonly _props?: Exact<EntityWithJsonPropertyProps, T>;

    readonly type = 'entity_with_json_property';
    json_property?: Type<{}>;

    constructor(props?: Exact<EntityWithJsonPropertyProps, T>) {
        this.json_property = props?.json_property;
    }
}

export interface EntityWithJsonPropertyProps {
    json_property?: Type<{}>;
}
