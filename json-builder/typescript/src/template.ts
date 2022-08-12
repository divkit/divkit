/* eslint-disable @typescript-eslint/ban-types */

import { Div } from './generated/Div';
import { IDivData } from './generated/DivData';

export interface ITemplates {
    [type: string]: Div;
}

export type Type<U, V extends string = string> = U | TemplatePropertyReference<V, U>;

export class TemplatePropertyReference<V extends string = string, U = {}> {
    public name: V;
    private _value?: U;
    public constructor(name: V) {
        this.name = name;
    }
}

export function reference<V extends string = string, U = {}>(name: V): TemplatePropertyReference<V, U> {
    return new TemplatePropertyReference(name);
}

export class TemplateBlock<T extends string = any, U = object> {
    public readonly type: T;
    public readonly _props?: U;

    public constructor(type: T, props?: U) {
        this.type = type;

        Object.assign(this, props);
    }

    public getProps(): object {
        return Object.keys(this).reduce((acc, k) => {
            if (k !== 'type') {
                acc[k] = true;
            }
            return acc;
        }, {});
    }
}

export function template<T extends string = any, U = object>(type: T, props?: U): TemplateBlock<T, U> {
    return new TemplateBlock(type, props);
}

export function divCard(templates: ITemplates, card: IDivData): { templates: ITemplates; card: IDivData } {
    return {
        templates: templates,
        card: card,
    };
}
