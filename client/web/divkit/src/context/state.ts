import type { AppearanceTransition, DivBaseData } from '../types/base';
import type { TemplateContext } from '../../typings/common';
import type { TransitionChange } from '../types/base';

export const STATE_CTX = Symbol('state');

export interface StateInterface {
    getChild(id: string): StateInterface | undefined;
    setState(stateId: string): void;
}

export interface StateCtxValue {
    registerInstance(id: string, block: StateInterface): void;

    unregisterInstance(id: string): void;

    registerChildWithTransitionIn(
        json: DivBaseData,
        templateContext: TemplateContext,
        transitions: AppearanceTransition,
        node: HTMLElement
    ): Promise<void>;

    registerChildWithTransitionOut(
        json: DivBaseData,
        templateContext: TemplateContext,
        transitions: AppearanceTransition,
        node: HTMLElement
    ): Promise<void>;

    registerChildWithTransitionChange(
        json: DivBaseData,
        templateContext: TemplateContext,
        transitions: TransitionChange,
        node: HTMLElement
    ): Promise<void>;

    hasTransitionChange(id?: string): boolean;

    runVisibilityTransition(
        json: DivBaseData,
        templateContext: TemplateContext,
        transitions: AppearanceTransition,
        node: HTMLElement,
        direction: 'in' | 'out'
    ): Promise<void>;

    registerChild(id: string): void;
    unregisterChild(id: string): void;
}
