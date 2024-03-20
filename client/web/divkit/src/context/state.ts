import type { MaybeMissing } from '../expressions/json';
import type { AppearanceTransition, DivBaseData } from '../types/base';
import type { TransitionChange } from '../types/base';
import type { ComponentContext } from '../types/componentContext';

export const STATE_CTX = Symbol('state');

export interface StateInterface {
    getChild(id: string): StateInterface | undefined;
    setState(stateId: string): Promise<void>;
}

export interface StateCtxValue {
    registerInstance(id: string, block: StateInterface): void;

    unregisterInstance(id: string): void;

    registerChildWithTransitionIn(
        json: DivBaseData,
        componentContext: ComponentContext,
        transitions: MaybeMissing<AppearanceTransition>,
        node: HTMLElement
    ): Promise<void>;

    registerChildWithTransitionOut(
        json: DivBaseData,
        componentContext: ComponentContext,
        transitions: MaybeMissing<AppearanceTransition>,
        node: HTMLElement
    ): Promise<void>;

    registerChildWithTransitionChange(
        json: DivBaseData,
        componentContext: ComponentContext,
        transitions: MaybeMissing<TransitionChange>,
        node: HTMLElement
    ): Promise<void>;

    hasTransitionChange(id?: string): boolean;

    runVisibilityTransition(
        json: DivBaseData,
        componentContext: ComponentContext,
        transitions: MaybeMissing<AppearanceTransition>,
        node: HTMLElement,
        direction: 'in' | 'out'
    ): Promise<void>;

    registerChild(id: string): void;
    unregisterChild(id: string): void;
}
