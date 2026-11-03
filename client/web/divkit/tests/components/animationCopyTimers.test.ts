// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { tick } from 'svelte';
import { render } from '../../src/client';
import type { DivkitInstance, WrappedError } from '../../typings/common';

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

describe('Timers of animation copies', () => {
    test('a visibility transition copy does not make a descendant timer ambiguous', async() => {
        const errors: WrappedError[] = [];
        instance = render({
            target,
            id: 'animation-copy-timers',
            json: {
                card: {
                    log_id: 'test',
                    variables: [
                        { name: 'finished', type: 'integer', value: 0 },
                        { name: 'visible', type: 'boolean', value: false }
                    ],
                    states: [{
                        state_id: 0,
                        div: {
                            type: 'state',
                            id: 'screen',
                            states: [{
                                state_id: 'main',
                                div: {
                                    type: 'container',
                                    id: 'content',
                                    visibility: '@{visible ? \'visible\' : \'gone\'}',
                                    transition_in: { type: 'fade', duration: 100 },
                                    transition_out: { type: 'fade', duration: 100 },
                                    items: [{
                                        type: 'text',
                                        text: 'Finished: @{finished}',
                                        timers: [{
                                            id: 'nested_timer',
                                            duration: 60000,
                                            end_actions: [{
                                                log_id: 'finish',
                                                typed: {
                                                    type: 'set_variable',
                                                    variable_name: 'finished',
                                                    value: { type: 'integer', value: '@{finished + 1}' }
                                                }
                                            }]
                                        }]
                                    }]
                                }
                            }]
                        }
                    }]
                }
            },
            onError({ error }) {
                errors.push(error);
            }
        });
        for (let i = 0; i < 5; ++i) {
            await tick();
        }
        instance.execAction({
            log_id: 'show',
            typed: { type: 'set_variable', variable_name: 'visible', value: { type: 'boolean', value: true } }
        });
        for (let i = 0; i < 5; ++i) {
            await tick();
        }

        instance.execAction({ log_id: 'start', typed: { type: 'timer', id: 'nested_timer', action: 'start' } });
        instance.execAction({ log_id: 'stop', typed: { type: 'timer', id: 'nested_timer', action: 'stop' } });
        await tick();

        expect(errors.map(error => error.message)).toEqual([]);
        expect(target.textContent).toContain('Finished: 1');
    });
});
