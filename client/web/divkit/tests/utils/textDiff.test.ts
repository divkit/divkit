import {
    describe,
    expect,
    test
} from 'vitest';

// Replicate from Android: client/android/div/src/test/java/com/yandex/div/core/util/mask/TextDiffTest.kt

import { textDiff } from '../../src/utils/textDiff';

describe('textDiff', () => {
    test('build diff for empty left', () => {
        expect(textDiff('', 'content')).toStrictEqual({
            start: 0,
            added: 7,
            removed: 0
        });
    });

    test('build diff for empty right', () => {
        expect(textDiff('content', '')).toStrictEqual({
            start: 0,
            added: 0,
            removed: 7
        });
    });

    test('build diff for both empty', () => {
        expect(textDiff('', '')).toStrictEqual({
            start: 0,
            added: 0,
            removed: 0
        });
    });

    test('build diff for equal strings', () => {
        expect(textDiff('abc', 'abc')).toStrictEqual({
            start: 3,
            added: 0,
            removed: 0
        });
    });

    test('build diff for shorter left', () => {
        expect(textDiff('abc', 'abcde')).toStrictEqual({
            start: 3,
            added: 2,
            removed: 0
        });
    });

    test('build diff for shorter right', () => {
        expect(textDiff('abcde', 'abc')).toStrictEqual({
            start: 3,
            added: 0,
            removed: 2
        });
    });

    test('build diff for fully different', () => {
        expect(textDiff('abcde', '12345')).toStrictEqual({
            start: 0,
            added: 5,
            removed: 5
        });
    });

    test('build diff for change at start', () => {
        expect(textDiff('abcde', 'ABcde')).toStrictEqual({
            start: 0,
            added: 2,
            removed: 2
        });
    });

    test('build diff for change at center', () => {
        expect(textDiff('abcde', 'aBCDe')).toStrictEqual({
            start: 1,
            added: 3,
            removed: 3
        });
    });

    test('build diff for change at end', () => {
        expect(textDiff('abcde', 'abcDE')).toStrictEqual({
            start: 3,
            added: 2,
            removed: 2
        });
    });

    test('build diff for shorting replacement', () => {
        expect(textDiff('abcde', 'aFe')).toStrictEqual({
            start: 1,
            added: 1,
            removed: 3
        });
    });

    test('build diff for adding replacement', () => {
        expect(textDiff('aFe', 'abcde')).toStrictEqual({
            start: 1,
            added: 3,
            removed: 1
        });
    });
});
