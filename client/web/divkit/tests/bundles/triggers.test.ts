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

import { render } from '@divkitframework/divkit/client';
import {
    cardWithTrigger,
    catchRejections,
    setStoredValue,
    TRIGGER_CONDITION
} from './variableTriggers';

afterEach(() => {
    document.body.innerHTML = '';
});

describe('test variable triggers of the client bundle', () => {
    test('should report failed actions of variable triggers', async() => {
        const rejections = catchRejections();
        const onError = vi.fn();
        document.body.innerHTML = '<div id="root"></div>';

        const instance = render({
            id: 'test',
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            target: document.querySelector('#root')!,
            json: cardWithTrigger([setStoredValue]),
            store: {
                set() {
                    throw new Error('The store is full');
                }
            },
            onError
        });
        await vi.waitFor(() => {
            expect(onError).toHaveBeenCalled();
        });
        await rejections.settle();
        instance.$destroy();

        expect(rejections.reasons).toEqual([]);
        expect(onError).toHaveBeenCalledWith({
            error: expect.objectContaining({
                message: 'variable_trigger action failed',
                additional: expect.objectContaining({
                    condition: TRIGGER_CONDITION,
                    originalError: new Error('The store is full')
                })
            })
        });
    });
});
