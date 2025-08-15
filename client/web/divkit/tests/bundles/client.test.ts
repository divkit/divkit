/* eslint-disable import/no-extraneous-dependencies */
/**
 * @vitest-environment jsdom
 */

import {
    describe,
    expect,
    test,
    beforeAll,
    vi
} from 'vitest';

import { render } from '@divkitframework/divkit/client';

const json = {
    card: {
        log_id: 'test',
        states: [
            {
                state_id: 0,
                div: {
                    type: 'container',
                    items: [{
                        type: 'text',
                        text: 'Test'
                    }]
                }
            }
        ]
    },
    templates: {}
};

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

describe('test client bundle', () => {
    beforeAll(() => {
        document.body.innerHTML = '<div id="root"></div>';
    });

    test('should run without errors', () => {
        expect(() => {
            render({
                id: 'test',
                // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                target: document.querySelector('#root')!,
                json
            });
        }).not.toThrow();
    });

    test('should log error on empty id', () => {
        const spy = vi.spyOn(console, 'error').mockImplementation(noop);

        // @ts-expect-error "id" is required
        render({
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            target: document.querySelector('#root')!,
            json
        });

        expect(spy).toHaveBeenCalled();
    });

    test('should log error on empty json', () => {
        const spy = vi.spyOn(console, 'error').mockImplementation(noop);

        // @ts-expect-error "json" is required
        render({
            id: 'test',
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            target: document.querySelector('#root')!
        });

        expect(spy).toHaveBeenCalled();
    });

    test('should throw error on empty target', () => {
        expect(() => {
            // @ts-expect-error "target" is required
            render({
                id: 'test',
                json
            });
        }).toThrow();
    });
});
