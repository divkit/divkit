// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { tick } from 'svelte';
import { render } from '../../src/client';
import type {
    DivBase,
    DivkitInstance,
    DivVariable,
    TypedValue,
    VariableTrigger,
    WrappedError
} from '../../typings/common';

let instance: DivkitInstance | undefined;
let target: HTMLElement;
let errors: WrappedError[];
let triggered: string[];

beforeEach(() => {
    errors = [];
    triggered = [];
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

function renderCard(variables: DivVariable[], div: DivBase): void {
    instance = render({
        target,
        id: 'animation-copy-triggers',
        json: { card: { log_id: 'test', variables, states: [{ state_id: 0, div }] } },
        onError({ error }) {
            errors.push(error);
        },
        onStat({ type, action }) {
            if (type === 'trigger') {
                triggered.push(action.log_id);
            }
        }
    });
}

function setVariable(name: string, value: TypedValue): void {
    instance?.execAction({ log_id: `set_${name}`, typed: { type: 'set_variable', variable_name: name, value } });
}

async function settle(): Promise<void> {
    // A transition waits for a few ticks before it adds the animation copies
    for (let i = 0; i < 5; ++i) {
        await tick();
    }
}

function trigger(logId: string, condition: string): VariableTrigger {
    return {
        condition,
        actions: [{
            log_id: logId,
            typed: { type: 'set_variable', variable_name: 'handled', value: { type: 'boolean', value: true } }
        }]
    };
}

function infinitePager(pages: string[], props: Record<string, unknown> = {}): DivBase {
    return {
        type: 'pager',
        id: 'pager',
        infinite_scroll: true,
        layout_mode: { type: 'percentage', page_width: { value: 100 } },
        items: pages.map(logId => ({
            type: 'text',
            text: logId,
            variable_triggers: [trigger(logId, '@{counter > 0}')]
        })),
        ...props
    } as DivBase;
}

describe('Variable triggers of animation copies', () => {
    test('a visibility transition copy neither reports its evaluated condition nor runs the trigger', async() => {
        // Arrange
        renderCard([
            { name: 'header_visible', type: 'boolean', value: false },
            { name: 'counter', type: 'integer', value: 0 },
            { name: 'handled', type: 'boolean', value: false }
        ], {
            type: 'state',
            id: 'screen',
            states: [{
                state_id: 'main',
                div: {
                    type: 'text',
                    id: 'header',
                    text: 'Header',
                    visibility: '@{header_visible ? \'visible\' : \'gone\'}',
                    transition_in: { type: 'fade', duration: 100 },
                    transition_out: { type: 'fade', duration: 100 },
                    variable_triggers: [trigger('header', '@{counter > 0}')]
                }
            }]
        });
        await settle();

        // Act
        setVariable('header_visible', { type: 'boolean', value: true });
        await settle();
        setVariable('counter', { type: 'integer', value: 1 });
        await settle();
        setVariable('header_visible', { type: 'boolean', value: false });
        await settle();

        // Assert
        expect(errors.map(error => error.message)).toEqual([]);
        expect(triggered).toEqual(['header']);
    });

    test('a change_bounds copy does not repeat the trigger actions', async() => {
        // Arrange
        const moving = (text: string): DivBase => ({
            type: 'text',
            id: 'moving',
            text,
            transition_change: { type: 'change_bounds', duration: 100 },
            variable_triggers: [trigger('moving', '@{counter >= 0}')]
        });
        renderCard([
            { name: 'counter', type: 'integer', value: 0 },
            { name: 'handled', type: 'boolean', value: false }
        ], {
            type: 'state',
            id: 'switcher',
            default_state_id: 'first',
            states: [
                { state_id: 'first', div: moving('First') },
                { state_id: 'second', div: moving('Second') }
            ]
        });
        await settle();
        const elementRuns = triggered.length;

        // Act
        instance?.execAction({ log_id: 'switch', url: 'div-action://set_state?state_id=0/switcher/second' });
        await settle();

        // Assert: the element of the second state runs the trigger as the first one did, the copy does not
        expect(errors.map(error => error.message)).toEqual([]);
        expect(elementRuns).toBeGreaterThan(0);
        expect(triggered).toHaveLength(elementRuns * 2);
    });

    test('the page duplicates of an infinite pager do not repeat the trigger actions', async() => {
        // Arrange
        renderCard([
            { name: 'counter', type: 'integer', value: 0 },
            { name: 'handled', type: 'boolean', value: false }
        ], infinitePager(['first', 'second']));
        await settle();

        // Act
        setVariable('counter', { type: 'integer', value: 1 });
        await settle();

        // Assert
        expect(errors.map(error => error.message)).toEqual([]);
        expect(triggered.sort()).toEqual(['first', 'second']);
    });

    test('the page duplicates in an animation copy of an infinite pager neither report their evaluated condition nor run the trigger', async() => {
        // Arrange
        renderCard([
            { name: 'pager_visible', type: 'boolean', value: false },
            { name: 'counter', type: 'integer', value: 0 },
            { name: 'handled', type: 'boolean', value: false }
        ], {
            type: 'state',
            id: 'screen',
            states: [{
                state_id: 'main',
                div: infinitePager(['first', 'second'], {
                    visibility: '@{pager_visible ? \'visible\' : \'gone\'}',
                    transition_in: { type: 'fade', duration: 100 },
                    transition_out: { type: 'fade', duration: 100 }
                })
            }]
        });
        await settle();

        // Act
        setVariable('pager_visible', { type: 'boolean', value: true });
        await settle();
        setVariable('counter', { type: 'integer', value: 1 });
        await settle();
        setVariable('pager_visible', { type: 'boolean', value: false });
        await settle();

        // Assert
        expect(errors.map(error => error.message)).toEqual([]);
        expect(triggered.sort()).toEqual(['first', 'second']);
    });
});
