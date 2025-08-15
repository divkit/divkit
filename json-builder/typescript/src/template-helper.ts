/* eslint-disable @typescript-eslint/ban-types */

import { Div } from './generated/Div';
import { ITemplates, TemplateBlock, TemplatePropertyReference } from './template';

// https://github.com/Microsoft/TypeScript/issues/29594#issuecomment-507701193
type UnionToIntersection<U> = (U extends any ? (k: U) => void : never) extends (k: infer I) => void ? I : never;
type IsNeverType<T> = [T] extends [never] ? true : false;
type NU<T> = unknown extends T ? {} : IsNeverType<T> extends true ? {} : T;
type EU<T> = Exclude<T, undefined>;

type Var<U extends string, T> = TemplatePropertyReference<U, T>;

type FilterVars<T extends {}, X> = {
    [K in keyof T]: T[K] extends Var<infer V, infer T>
        ? { [I in V]: T }
        : T[K] extends (infer S)[]
        ? NU<DistributeTemplateVars<S, X>>
        : T[K] extends number
        ? {}
        : T[K] extends infer Y
        ? NU<Vars<Y, X>>
        : {};
};

type TemplateVars<T, X> = T extends TemplateBlock<infer K2, any>
    ? K2 extends keyof X
        ? Vars<EU<T['_props']>, X> & Omit<Vars<X[K2], X>, keyof EU<T['_props']>>
        : {}
    : Vars<T, X>;

// Transforms "A | B | C" into the "Vars<A> | Vars<B> | Vars<C>"
type DistributeTemplateVars<T, X> = UnionToIntersection<T extends any ? TemplateVars<T, X> : never>;

type Vars<T, X> = '_props' extends keyof T
    ? UnionToIntersection<FilterVars<EU<T['_props']>, X>[keyof EU<T['_props']>]>
    : UnionToIntersection<FilterVars<T, X>[keyof T]>;

type VarMap<T, X> = {
    [K in keyof T]: T[K] extends Var<string, unknown> ? K : undefined extends Vars<{ K: T[K] }, X> ? never : K;
};

type VarList<T, X> = VarMap<T, X>[keyof T];

type OptionalVars<T extends Div, X> = {
    [K in Exclude<keyof T, 'type' | '_props' | VarList<T['_props'], X>>]?: Exclude<T[K], TemplatePropertyReference>;
};

export type TemplateHelper<U extends ITemplates> = {
    [K in keyof U & string]: U[K] extends TemplateBlock<infer K2, infer P>
        ? K2 extends keyof U
            ? (props: Omit<Vars<U[K2], U> & OptionalVars<U[K2], U>, keyof P> & Vars<P, U>) => TemplateBlock<K>
            : unknown
        : (props: Vars<U[K], U> & OptionalVars<U[K], U>) => TemplateBlock<K>;
};

/**
 * Creates typed functions for templates instance construction
 * @param templates templates map of the form { template_name: template }
 * @example
 * const templates = {
 *   template: new DivContainer({
 *     paddings: { left: 3 },
 *     items: [ new DivText({ text: reference('var') }) ]
 *   })
 * };
 * const helpers = templateHelper(templates);
 * helpers.template({var: '123'});
 */
export function templateHelper<T extends ITemplates>(templates: T): TemplateHelper<T> {
    const helpers = {};

    for (const key of Object.keys(templates)) {
        helpers[key] = (props: object): TemplateBlock<string> => new TemplateBlock(key, props);
    }

    return helpers as TemplateHelper<T>;
}
