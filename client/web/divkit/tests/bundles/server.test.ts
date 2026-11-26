/* eslint-disable import/no-extraneous-dependencies */
import {
    describe,
    expect,
    test,
    vi
} from 'vitest';

import { render } from '@divkitframework/divkit';
import {
    cardWithTrigger,
    catchRejections,
    setStoredValue,
    startAnimator
} from './variableTriggers';

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

describe('test server bundle', () => {
    test('should run without errors', () => {
        expect(() => {
            render({
                id: 'test',
                json
            });
        }).not.toThrow();
    });

    test('should return valid html', () => {
        const html = render({
            id: 'test',
            json
        });
        expect(typeof html).toBe('string');
        // Try to find valid html tags
        expect(Boolean(html.match(/<(“[^”]*”|'[^’]*’|[^'”>])*>/g))).toBe(true);
    });

    test('should render text styles', () => {
        const html = render({
            id: 'test',
            json: {
                card: {
                    log_id: 'test',
                    states: [{
                        state_id: 0,
                        div: {
                            type: 'text',
                            text: 'Test',
                            font_size: 20,
                            line_height: 28,
                            max_lines: 2,
                            text_color: '#ff0000'
                        }
                    }]
                },
                templates: {}
            }
        });
        // Svelte actions do not run on the server, the text needs its style before hydration
        expect(html).toContain('font-size:2em;line-height:1.4;max-height:2.8em;-webkit-line-clamp:2;color:#ff0000');
    });

    test('should not run variable triggers', async() => {
        const rejections = catchRejections();
        const onError = vi.fn();
        const set = vi.fn();

        const html = render({
            id: 'test',
            json: cardWithTrigger([startAnimator, setStoredValue]),
            store: {
                set
            },
            onError
        });
        await rejections.settle();

        expect(rejections.reasons).toEqual([]);
        expect(onError).not.toHaveBeenCalled();
        expect(set).not.toHaveBeenCalled();
        // The server draws the first frame, the trigger runs its actions after hydration
        expect(html).toContain('Padding 16');
    });

    test('should log error on empty id', () => {
        const spy = vi.spyOn(console, 'error').mockImplementation(noop);

        // @ts-expect-error "id" is required
        render({
            json
        });

        expect(spy).toHaveBeenCalled();
    });

    test('should log error on empty json', () => {
        const spy = vi.spyOn(console, 'error').mockImplementation(noop);

        // @ts-expect-error "json" is required
        render({
            id: 'test'
        });

        expect(spy).toHaveBeenCalled();
    });
});
