/* eslint-disable @typescript-eslint/ban-types */

import { Div } from './generated/Div';
import { IDivData } from './generated/DivData';
import { UnsafeDivExpression } from './unsafe-expression';
import { escapeExpression } from './expression';

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

export function escapeCard(obj: unknown): unknown {
    if (typeof obj === 'string') {
        return escapeExpression(obj);
    } else if (obj instanceof UnsafeDivExpression) {
        return obj.toJSON();
    } else if (obj && typeof obj === 'object') {
        if (Array.isArray(obj)) {
            return obj.map(escapeCard);
        } else {
            return Object.keys(obj).reduce((acc, item) => {
                acc[item] = escapeCard(obj[item]);
                return acc;
            }, {});
        }
    }
    return obj;
}

class Card {
    public templates: ITemplates;
    public card: IDivData;

    public constructor(templates: ITemplates, card: IDivData) {
        this.templates = templates;
        this.card = card;
    }

    public toJSON(): unknown {
        return escapeCard(this);
    }
}

export function divCard(templates: ITemplates, card: IDivData): { templates: ITemplates; card: IDivData } {
    return new Card(templates, card);
}
