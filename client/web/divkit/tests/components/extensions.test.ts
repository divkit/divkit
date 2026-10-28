// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { tick } from 'svelte';
import { render } from '../../src/client';
import type { DivExtensionContext, DivJson, DivkitInstance } from '../../typings/common';
import type { DivExtensionRegistration } from '../../typings/client';

// Vitest resolves Svelte's server entry by default, where afterUpdate is a no-op.
vi.mock('svelte', () => vi.importActual<typeof import('svelte')>('svelte/internal'));

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
    vi.spyOn(HTMLElement.prototype, 'clientWidth', 'get').mockReturnValue(100);
    vi.spyOn(HTMLElement.prototype, 'clientHeight', 'get').mockReturnValue(80);
});

afterEach(() => {
    instance?.$destroy();
    instance = undefined;
    target.remove();
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
});

function json(withExtensions = false): DivJson {
    return { card: {
        log_id: 'extensions',
        variables: [{ name: 'content', type: 'string', value: 'Before' },
            { name: 'alpha', type: 'number', value: 1 }],
        states: [{ state_id: 0, div: {
            type: 'container', id: 'container', items: [
                { type: 'text', id: 'first', text: '@{content}', alpha: '@{alpha}',
                    ...(withExtensions ? { extensions: [{ id: 'observer', params: { label: 'explicit' } }] } : {}) },
                { type: 'text', id: 'second', text: 'Second',
                    ...(withExtensions ? { extensions: [{ id: 'observer', is_enabled: 0 }] } : {}) }
            ]
        } }]
    } };
}

function observer() {
    const created = vi.fn();
    const mounted = vi.fn();
    const updated = vi.fn();
    const unmounted = vi.fn();
    class Observer {
        constructor(private params: object) {
            created(params);
        }
        mountView(node: HTMLElement, context: DivExtensionContext): void {
            mounted(this, node, context.getComponentProperty('id'), this.params);
        }
        updateView(node: HTMLElement): void {
            updated(this, node);
        }
        unmountView(node: HTMLElement): void {
            unmounted(this, node);
        }
    }
    return { Observer, created, mounted, updated, unmounted };
}

describe('Extensions for all components', () => {
    test('creates a separate instance for every component without JSON declarations and cleans up', async() => {
        const { Observer, created, mounted, unmounted } = observer();
        instance = render({ target, id: 'test', json: json(), extensionsForAllComponents: [Observer] });
        await tick();
        expect(created.mock.calls).toEqual([[{}], [{}], [{}], [{}]]);
        expect(mounted.mock.calls.map(call => call[2]).sort()).toEqual(['container', 'first', 'root', 'second']);
        expect(new Set(mounted.mock.calls.map(call => call[0])).size).toBe(4);
        instance.$destroy();
        instance = undefined;
        expect(unmounted.mock.calls).toHaveLength(4);
        expect(new Set(unmounted.mock.calls.map(call => call[0])))
            .toEqual(new Set(mounted.mock.calls.map(call => call[0])));
    });

    test('forwards distinct host parameters to each registration without evaluating expressions', async() => {
        const { Observer, created, mounted } = observer();
        const firstParams = { label: '@{content}', nested: { value: 1 } };
        const secondParams = { label: 'second', enabled: false };
        const registrations: DivExtensionRegistration[] = [
            { Extension: Observer, params: firstParams },
            { Extension: Observer, params: secondParams },
            { Extension: Observer },
            Observer
        ];
        instance = render({ target, id: 'test', json: json(), extensionsForAllComponents: registrations });
        await tick();
        expect(created).toHaveBeenCalledTimes(16);
        for (const id of ['root', 'container', 'first', 'second']) {
            const params = mounted.mock.calls.filter(call => call[2] === id).map(call => call[3]);
            expect(params).toEqual([firstParams, secondParams, {}, {}]);
            expect(params[0]).toBe(firstParams);
            expect(params[1]).toBe(secondParams);
        }
    });

    test('keeps explicit params and is_enabled independent from registrations for all components', async() => {
        const { Observer, created, mounted } = observer();
        instance = render({ target, id: 'test', json: json(true),
            extensions: new Map([['observer', Observer]]), extensionsForAllComponents: [Observer] });
        await tick();
        expect(created.mock.calls.filter(([params]) => 'label' in params)).toEqual([[{ label: 'explicit' }]]);
        expect(mounted.mock.calls.filter(call => call[2] === 'first').map(call => call[3]))
            .toEqual([{ label: 'explicit' }, {}]);
        expect(mounted.mock.calls.filter(call => call[2] === 'second')).toHaveLength(1);
        expect(mounted.mock.calls).toHaveLength(5);
    });

    test('updates instances for all components when component properties change', async() => {
        const { Observer, mounted, updated } = observer();
        instance = render({ target, id: 'test', json: json(), extensionsForAllComponents: [Observer] });
        await tick();
        const first = mounted.mock.calls.find(call => call[2] === 'first');
        updated.mockClear();
        instance.execAction({ log_id: 'change', typed: { type: 'set_variable', variable_name: 'content',
            value: { type: 'string', value: 'After' } } });
        instance.execAction({ log_id: 'change-alpha', typed: { type: 'set_variable', variable_name: 'alpha',
            value: { type: 'number', value: 0.5 } } });
        await tick();
        expect(first?.[1].textContent?.trim()).toBe('After');
        expect(updated.mock.calls).toContainEqual([first?.[0], first?.[1]]);
    });

    test('cleans up the old node and mounts the replacement when actions change the element tag', async() => {
        const { Observer, mounted, unmounted } = observer();
        const params = { label: 'link' };
        instance = render({ target, id: 'test',
            extensionsForAllComponents: [{ Extension: Observer, params }], json: { card: {
                log_id: 'replacement', variables: [{ name: 'link', type: 'string', value: 'https://example.com' }],
                states: [{ state_id: 0, div: { type: 'text', id: 'link', text: 'Open',
                    actions: [{ log_id: 'open', url: '@{link}' }] } }]
            } } });
        await tick();
        const original = mounted.mock.calls.find(call => call[2] === 'link');
        expect(original?.[1].tagName).toBe('A');
        expect(original?.[3]).toBe(params);
        instance.execAction({ log_id: 'change-link', typed: { type: 'set_variable', variable_name: 'link',
            value: { type: 'string', value: 'custom://open' } } });
        await tick();
        await tick();
        expect(original?.[1].isConnected).toBe(false);
        expect(unmounted.mock.calls).toContainEqual([original?.[0], original?.[1]]);
        const replacement = mounted.mock.calls.filter(call => call[2] === 'link')[1];
        expect(replacement?.[1].tagName).toBe('BUTTON');
        expect(replacement?.[3]).toBe(params);
        expect(replacement?.[1].isConnected).toBe(true);
        instance.$destroy();
        instance = undefined;
        expect(unmounted.mock.calls.filter(call => call[1] === original?.[1])).toHaveLength(1);
        expect(unmounted.mock.calls.filter(call => call[1] === replacement?.[1])).toHaveLength(1);
    });

    test('reconciles both categories together on node replacement and destruction', async() => {
        const events: string[] = [];
        class PhaseObserver {
            constructor(private params: { label?: string }) {}
            mountView(node: HTMLElement, context: DivExtensionContext): void {
                if (context.getComponentProperty('id') === 'link') {
                    events.push(`mount:${this.params.label}:${node.tagName}`);
                }
            }
            unmountView(node: HTMLElement, context: DivExtensionContext): void {
                if (context.getComponentProperty('id') === 'link') {
                    events.push(`unmount:${this.params.label}:${node.tagName}`);
                }
            }
        }
        instance = render({ target, id: 'test', extensions: new Map([['observer', PhaseObserver]]),
            extensionsForAllComponents: [{ Extension: PhaseObserver, params: { label: 'all' } }], json: { card: {
                log_id: 'phases', variables: [{ name: 'link', type: 'string', value: 'https://example.com' }],
                states: [{ state_id: 0, div: { type: 'text', id: 'link', text: 'Open',
                    actions: [{ log_id: 'open', url: '@{link}' }],
                    extensions: [{ id: 'observer', params: { label: 'json' } }] } }]
            } } });
        expect(events).toEqual([]);
        await tick();
        expect(events).toEqual(['mount:json:A', 'mount:all:A']);
        events.length = 0;
        instance.execAction({ log_id: 'change-link', typed: { type: 'set_variable', variable_name: 'link',
            value: { type: 'string', value: 'custom://open' } } });
        await tick();
        await tick();
        expect(events).toEqual(['unmount:json:A', 'unmount:all:A', 'mount:json:BUTTON', 'mount:all:BUTTON']);
        events.length = 0;
        instance.$destroy();
        instance = undefined;
        expect(events).toEqual(['unmount:json:BUTTON', 'unmount:all:BUTTON']);
        await tick();
        expect(events).toEqual(['unmount:json:BUTTON', 'unmount:all:BUTTON']);
    });

    test('updates JSON declarations and enabled flags without recreating unaffected registrations', async() => {
        const { Observer, created, mounted, unmounted } = observer();
        instance = render({ target, id: 'test', extensions: new Map([['observer', Observer]]),
            extensionsForAllComponents: [Observer], json: { card: {
                log_id: 'enabled', variables: [{ name: 'enabled', type: 'integer', value: 0 }],
                states: [{ state_id: 0, div: { type: 'container', id: 'container', items: [
                    { type: 'text', id: 'first', text: 'First',
                        extensions: [{ id: 'observer', is_enabled: '@{enabled}', params: { label: 'json' } }] },
                    { type: 'text', id: 'second', text: 'Second' }
                ] } }]
            } } });
        await tick();
        const globalInstance = mounted.mock.calls.find(call => call[2] === 'first')?.[0];
        expect(created.mock.calls.filter(([params]) => 'label' in params)).toEqual([]);
        expect(unmounted).not.toHaveBeenCalled();
        for (const value of [1, 0, 1]) {
            instance.execAction({ log_id: 'toggle', typed: { type: 'set_variable', variable_name: 'enabled',
                value: { type: 'integer', value } } });
            await tick();
            await tick();
        }
        expect(created.mock.calls.filter(([params]) => 'label' in params)).toEqual([
            [{ label: 'json' }], [{ label: 'json' }]
        ]);
        expect(unmounted.mock.calls).toHaveLength(1);
        expect(instance.applyPatch({ patch: { changes: [{ id: 'first', items: [
            { type: 'text', id: 'first', text: 'Patched', extensions: [
                { id: 'observer', params: { label: 'changed' } }, { id: 'observer', params: { label: 'added' } }
            ] }
        ] }] } })).toBe(true);
        await tick();
        await tick();
        expect(created.mock.calls.filter(([params]) => 'label' in params).slice(-2)).toEqual([
            [{ label: 'changed' }], [{ label: 'added' }]
        ]);
        expect(unmounted.mock.calls).toHaveLength(2);
        expect(unmounted.mock.calls.some(call => call[0] === globalInstance)).toBe(false);
        expect(mounted.mock.calls.filter(call => call[0] === globalInstance)).toHaveLength(1);
    });

    test('does not mount after immediate destruction', async() => {
        const { Observer, mounted } = observer();
        instance = render({ target, id: 'test', json: json(true),
            extensions: new Map([['observer', Observer]]), extensionsForAllComponents: [Observer] });
        instance.$destroy();
        instance = undefined;
        await tick();
        expect(mounted).not.toHaveBeenCalled();
    });

    test('keeps JSON extension behavior when extensionsForAllComponents is omitted', async() => {
        const { Observer, created, mounted } = observer();
        instance = render({ target, id: 'test', json: json(true), extensions: new Map([['observer', Observer]]) });
        await tick();
        expect(created.mock.calls).toEqual([[{ label: 'explicit' }]]);
        expect(mounted.mock.calls.map(call => call[2])).toEqual(['first']);
    });
    test('routes actions to the named global extension on the requested component', async() => {
        const handled = vi.fn();
        const onError = vi.fn();
        class Handler {
            handleAction(payload: Record<string, unknown> | undefined, context: DivExtensionContext): boolean {
                handled(payload, context.getComponentProperty('id'));
                return true;
            }
        }
        instance = render({ target, id: 'test', json: json(), onError,
            extensionsForAllComponents: [Handler, { id: 'global', Extension: Handler }] });
        await tick();
        instance.execAction({ log_id: 'handle', payload: { message: 'hello' }, typed: { type: 'extension_action',
            div_id: 'first', extension_id: 'global' } });
        await vi.waitFor(() => expect(handled.mock.calls).toEqual([[{ message: 'hello' }, 'first']]));
        expect(onError).not.toHaveBeenCalled();
        instance.execAction({ log_id: 'missing', typed: { type: 'extension_action',
            div_id: 'first', extension_id: 'missing' } });
        await vi.waitFor(() => expect(onError).toHaveBeenCalled());
        expect(handled).toHaveBeenCalledTimes(1);
    });

    test.each([false, true])('preserves handler order and stops on handled=%s', async handledByJson => {
        const calls: string[] = [];
        class Handler {
            constructor(private params: { label?: string; handled?: boolean }) {}
            async handleAction(): Promise<boolean> {
                calls.push(this.params.label || '');
                return Boolean(this.params.handled);
            }
        }
        const card: DivJson = { card: { log_id: 'order', states: [{ state_id: 0, div: {
            type: 'text', id: 'text', text: 'Text',
            extensions: [{ id: 'handler', params: { label: 'json', handled: handledByJson } }]
        } }] } };
        instance = render({ target, id: 'test', json: card, extensions: new Map([['handler', Handler]]),
            extensionsForAllComponents: [
                { id: 'handler', Extension: Handler, params: { label: 'first' } },
                { id: 'handler', Extension: Handler, params: { label: 'second', handled: true } },
                { id: 'handler', Extension: Handler, params: { label: 'unreachable' } }
            ] });
        await tick();
        instance.execAction({ log_id: 'handle', typed: { type: 'extension_action',
            div_id: 'text', extension_id: 'handler' } });
        await vi.waitFor(() => expect(calls).toEqual(handledByJson ? ['json'] : ['json', 'first', 'second']));
    });

    test('keeps global action handlers through enabled changes and replaces them with their node', async() => {
        const calls: string[] = [];
        const onError = vi.fn();
        class Handler {
            private node: HTMLElement | undefined;
            constructor(private params: { label?: string }) {}
            mountView(node: HTMLElement): void {
                this.node = node;
            }
            unmountView(): void {
                this.node = undefined;
            }
            handleAction(): boolean {
                calls.push(`${this.params.label}:${this.node?.tagName}:${this.node?.isConnected}`);
                return false;
            }
        }
        instance = render({ target, id: 'test', onError, extensions: new Map([['handler', Handler]]),
            extensionsForAllComponents: [{ id: 'handler', Extension: Handler, params: { label: 'global' } }],
            json: { card: { log_id: 'lifecycle', variables: [
                { name: 'enabled', type: 'integer', value: 1 },
                { name: 'url', type: 'string', value: 'https://example.com' }
            ], states: [{ state_id: 0, div: { type: 'text', id: 'text', text: 'Text',
                actions: [{ log_id: 'open', url: '@{url}' }],
                extensions: [{ id: 'handler', is_enabled: '@{enabled}', params: { label: 'json' } }]
            } }] } } });
        await tick();
        const dispatch = () => instance?.execAction({ log_id: 'handle', typed: { type: 'extension_action',
            div_id: 'text', extension_id: 'handler' } });
        dispatch();
        await vi.waitFor(() => expect(calls).toEqual(['json:A:true', 'global:A:true']));
        calls.length = 0;
        instance.execAction({ log_id: 'disable', typed: { type: 'set_variable', variable_name: 'enabled',
            value: { type: 'integer', value: 0 } } });
        await tick();
        await tick();
        dispatch();
        await vi.waitFor(() => expect(calls).toEqual(['global:A:true']));
        calls.length = 0;
        instance.execAction({ log_id: 'replace', typed: { type: 'set_variable', variable_name: 'url',
            value: { type: 'string', value: 'custom://open' } } });
        await tick();
        await tick();
        dispatch();
        await vi.waitFor(() => expect(calls).toEqual(['global:BUTTON:true']));
        expect(onError).not.toHaveBeenCalled();
    });
});
