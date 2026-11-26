/* eslint-disable import/no-extraneous-dependencies */
/**
 * @vitest-environment jsdom
 */

import {
    afterEach,
    describe,
    expect,
    test,
    vi
} from 'vitest';

import { render as renderOnServer } from '@divkitframework/divkit';
import { render } from '@divkitframework/divkit/client-hydratable';
import { cardWithTrigger, startAnimator } from './variableTriggers';

const json = {
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
};

afterEach(() => {
    vi.unstubAllGlobals();
    document.body.innerHTML = '';
});

describe('test hydration of the server bundle', () => {
    test('text keeps its styles after hydration', () => {
        // The server bundle runs where there is no document
        vi.stubGlobal('document', undefined);
        const html = renderOnServer({ id: 'test', json });
        vi.unstubAllGlobals();
        document.body.innerHTML = `<div id="root">${html}</div>`;

        render({
            id: 'test',
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            target: document.querySelector('#root')!,
            json,
            hydrate: true
        });

        const text = [...document.querySelectorAll('span')].filter(span => span.style.fontSize === '2em');
        expect(text).toHaveLength(1);
        expect(text[0].style.lineHeight).toBe('1.4');
        expect(text[0].style.maxHeight).toBe('2.8em');
    });

    test('the server bundle styles the text where the server has a document too', () => {
        const html = renderOnServer({ id: 'test', json });

        expect(html).toContain('font-size:2em;line-height:1.4;max-height:2.8em;-webkit-line-clamp:2;color:#ff0000');
    });

    test('an animator that a variable trigger starts runs after hydration', async() => {
        const animated = cardWithTrigger([startAnimator]);
        // The server bundle runs where there is no document
        vi.stubGlobal('document', undefined);
        const html = renderOnServer({ id: 'test', json: animated });
        vi.unstubAllGlobals();
        // The server skips the trigger even though jsdom provides frames
        expect(html).toContain('Padding 16');
        document.body.innerHTML = `<div id="root">${html}</div>`;
        // jsdom stamps frames with its own clock, while the animator counts by performance.now()
        vi.stubGlobal('requestAnimationFrame', (callback: FrameRequestCallback) =>
            setTimeout(() => callback(performance.now()), 16));
        vi.stubGlobal('cancelAnimationFrame', (frame: number) => clearTimeout(frame));

        render({
            id: 'test',
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            target: document.querySelector('#root')!,
            json: animated,
            hydrate: true
        });

        await vi.waitFor(() => {
            expect(document.body.textContent).toContain('Padding 8');
        });
    });
});
