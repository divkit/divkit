// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { tick } from 'svelte';
import { readable } from 'svelte/store';
import { createGlobalVariablesController, createVariable, render } from '../../src/client';
import type { DivBase, DivkitInstance } from '../../typings/common';

let instance: DivkitInstance | undefined;
let target: HTMLElement;

beforeEach(() => {
    target = document.createElement('div');
    document.body.append(target);
    vi.stubGlobal('ResizeObserver', class {
        observe = vi.fn();
        unobserve = vi.fn();
        disconnect = vi.fn();
    });
});

afterEach(() => {
    instance?.$destroy();
    instance = undefined;
    target.remove();
    vi.unstubAllGlobals();
});

function renderScoped(
    items: DivBase[],
    onAction?: (url: string) => void,
    controller = createGlobalVariablesController()
) {
    instance = render({
        id: 'late-globals',
        target,
        globalVariablesController: controller,
        onCustomAction: action => onAction?.(action.url),
        json: { card: { log_id: 'test', states: [{ state_id: 0, div: {
            type: 'container',
            variables: [{ name: 'outer', type: 'string', value: 'Outer' }],
            items: [{
                type: 'container',
                variables: [{ name: 'inner', type: 'string', value: 'Inner' }],
                items
            }]
        } }] } }
    });
    return controller;
}

function countingTrigger(condition: string): DivBase {
    return {
        type: 'text',
        text: 'Trigger',
        variable_triggers: [{
            mode: 'on_variable',
            condition,
            actions: [{ log_id: 'changed', url: 'test://count/@{js.count}' }]
        }]
    };
}

describe('Late global variables in component scopes', () => {
    test('updates inherited text while a sibling keeps its own variable', async() => {
        const text = "@{outer}/@{inner}/@{js.label !: 'Missing'}";
        const controller = renderScoped([
            { type: 'text', text },
            { type: 'text', text, variables: [{ name: 'js.label', type: 'string', value: 'Local' }] }
        ]);
        await tick();
        expect(target.textContent).toContain('Outer/Inner/Missing');
        expect(target.textContent).toContain('Outer/Inner/Local');

        const label = createVariable('js.label', 'string', 'First');
        controller.setVariable(label);
        await tick();
        expect(target.textContent).toContain('Outer/Inner/First');
        expect(target.textContent).toContain('Outer/Inner/Local');

        label.set('Next');
        await tick();
        expect(target.textContent).toContain('Outer/Inner/Next');
        expect(target.textContent).toContain('Outer/Inner/Local');
    });

    test.each([true, false])('runs one action per update until destroy (destroy before registration: %s)', async earlyDestroy => {
        const urls: string[] = [];
        const controller = renderScoped([countingTrigger('@{(js.count !: 0) > 0}')], url => urls.push(url));
        await tick();
        expect(urls).toEqual([]);

        if (earlyDestroy) {
            instance?.$destroy();
        }
        const count = createVariable('js.count', 'integer', 1);
        controller.setVariable(count);
        await tick();
        count.set('2');
        await tick();
        if (!earlyDestroy) {
            instance?.$destroy();
        }
        instance = undefined;

        count.set('3');
        await tick();
        expect(urls).toEqual(earlyDestroy ? [] : ['test://count/1', 'test://count/2']);
    });

    test('subscribes once to a late variable and follows a lookup that switches names', async() => {
        const urls: string[] = [];
        const controller = createGlobalVariablesController();
        const selector = createVariable('key', 'string', 'js.count');
        const other = createVariable('other', 'integer', 0);
        controller.setVariable(selector);
        controller.setVariable(other);
        renderScoped([countingTrigger('@{(js.count !: 0) > 0 && getIntegerValue(key, 0) > 0}')],
            url => urls.push(url), controller);
        await tick();
        const count = createVariable('js.count', 'integer', 1);
        controller.setVariable(count);
        await tick();
        count.set('2');
        await tick();
        expect(urls).toEqual(['test://count/1', 'test://count/2']);

        selector.set('other');
        await tick();
        expect(urls).toHaveLength(2);
        other.set('1');
        await tick();
        expect(urls).toEqual(['test://count/1', 'test://count/2', 'test://count/2']);
    });

    test('releases the global registration subscription when the card is destroyed', () => {
        const controller = createGlobalVariablesController();
        const registrations = controller.getLastAddedVariableStore();
        const stopped = vi.fn();
        vi.spyOn(controller, 'getLastAddedVariableStore').mockReturnValue(readable('', set => {
            const unsubscribe = registrations.subscribe(set);
            return () => {
                unsubscribe();
                stopped();
            };
        }));
        renderScoped([{ type: 'text', text: "@{js.label !: 'Missing'}" }], undefined, controller);
        expect(stopped).not.toHaveBeenCalled();
        instance?.$destroy();
        instance = undefined;
        expect(stopped).toHaveBeenCalledTimes(1);
    });

    test('keeps observing a card variable declared after a property that reads it by name', async() => {
        instance = render({
            id: 'forward-reference',
            target,
            json: { card: {
                log_id: 'test',
                variables: [
                    { name: 'total', type: 'property', value_type: 'integer',
                        get: "@{(count !: 0) + getIntegerValue('count', 0)}" },
                    { name: 'count', type: 'integer', value: 1 }
                ],
                states: [{ state_id: 0, div: { type: 'text', text: '@{total}' } }]
            } }
        });
        await tick();
        expect(target.textContent?.trim()).toBe('2');
        await instance.execAction({ log_id: 'change', url: 'div-action://set_variable?name=count&value=2' });
        await tick();
        expect(target.textContent?.trim()).toBe('4');
    });
});
