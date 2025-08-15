import {
    describe,
    expect,
    test
} from 'vitest';

import { borderRadius } from '../../src/utils/borderRadius';

describe('borderRadius', () => {
    test('simple', () => {
        expect(borderRadius({
        })).toBe('0 0 0 0');

        expect(borderRadius({
            'top-left': 10
        })).toBe('1em 0 0 0');

        expect(borderRadius({
            'top-left': 10,
            'top-right': 20,
            'bottom-right': 30,
            'bottom-left': 40
        })).toBe('1em 2em 3em 4em');
    });

    test('defaultVal', () => {
        expect(borderRadius({
        }, 10)).toBe('1em 1em 1em 1em');

        expect(borderRadius({
            'top-left': 10
        }, 20)).toBe('1em 2em 2em 2em');

        expect(borderRadius({
            'top-left': 10,
            'top-right': 20,
            'bottom-right': 30,
            'bottom-left': 40
        }, 10)).toBe('1em 2em 3em 4em');
    });

    test('fontSize', () => {
        expect(borderRadius({
        }, 10, 20)).toBe('0.5em 0.5em 0.5em 0.5em');

        expect(borderRadius({
            'top-left': 10
        }, 20, 20)).toBe('0.5em 1em 1em 1em');

        expect(borderRadius({
            'top-left': 10,
            'top-right': 20,
            'bottom-right': 30,
            'bottom-left': 40
        }, 10, 20)).toBe('0.5em 1em 1.5em 2em');
    });
});
