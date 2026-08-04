/* eslint-disable @typescript-eslint/no-explicit-any */
import { Div } from './generated/Div';
import { IDivData } from './generated/DivData';
import { SafeDivExpression } from './safe-expression';
import { escapeExpression } from './expression';

export interface ITemplates {
    [type: string]: Div;
}

export type Type<U> = U | TemplatePropertyReference<any>;

export class TemplatePropertyReference<V extends string = string> {
    public templatePropertyName: V;
    public constructor(name: V) {
        this.templatePropertyName = name;
    }
}

export function reference<V extends string = string>(name: V): TemplatePropertyReference<V> {
    return new TemplatePropertyReference(name);
}

export class TemplateBlock<T extends string = any> {
    public readonly type: T;

    public constructor(type: T, props?: object) {
        this.type = type;

        Object.assign(this, props);
    }

    public getProps(): object {
        return Object.keys(this).reduce<Record<string, unknown>>((acc, k) => {
            if (k !== 'type') {
                acc[k] = true;
            }
            return acc;
        }, {});
    }
}

export function template<T extends string = any>(type: T, props?: object): TemplateBlock<T> {
    return new TemplateBlock(type, props);
}

export function escapeCard(obj: unknown): unknown {
    if (typeof obj === 'string') {
        return escapeExpression(obj);
    } else if (obj === true || obj === false) {
        return obj ? 1 : 0;
    } else if (obj instanceof SafeDivExpression) {
        return obj.toJSON();
    } else if (obj && typeof obj === 'object') {
        if (Array.isArray(obj)) {
            return obj.map(escapeCard);
        } else {
            return Object.keys(obj).reduce<Record<string, unknown>>((acc, item) => {
                acc[item] = escapeCard(obj[item as keyof typeof obj]);
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
