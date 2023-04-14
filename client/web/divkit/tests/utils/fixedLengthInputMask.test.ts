// Replicate from Android: client/android/div/src/test/java/com/yandex/div/core/util/mask/MaskHelperTest.kt

import type { MaskData } from '../../src/utils/mask/baseInputMask';
import { FixedLengthInputMask } from '../../src/utils/mask/fixedLengthInputMask';

describe('FixedLengthInputMask', () => {
    const noisyString = '!a@9#b$8%c^7&d*';
    let logError: jest.Mock;
    let fixedInputMask: FixedLengthInputMask;

    function buildPhoneMask(alwaysVisible: boolean): void {
        const maskData: MaskData = {
            pattern: '+7 (###) ###-##-##',
            decoding: [{
                key: '#',
                filter: '\\d',
                placeholder: '_'
            }],
            alwaysVisible
        };

        fixedInputMask = new FixedLengthInputMask(maskData, logError);
    }

    function buildMultikeyMask(alwaysVisible: boolean): void {
        const maskData: MaskData = {
            pattern: '$ ### $$',
            decoding: [{
                key: '#',
                filter: '\\d',
                placeholder: '_'
            }, {
                key: '$',
                filter: '[a-zA-Z]',
                placeholder: '-'
            }],
            alwaysVisible
        };

        fixedInputMask = new FixedLengthInputMask(maskData, logError);
    }

    function buildCodeMask(alwaysVisible: boolean): void {
        const maskData: MaskData = {
            pattern: '####-####-####-####',
            decoding: [{
                key: '#',
                filter: '.',
                placeholder: '_'
            }],
            alwaysVisible
        };

        fixedInputMask = new FixedLengthInputMask(maskData, logError);
    }

    beforeEach(() => {
        logError = jest.fn();
    });

    test('calculate multikey insertable string from center', () => {
        buildMultikeyMask(true);

        // eslint-disable-next-line dot-notation
        expect(fixedInputMask['calculateInsertableSubstring'](noisyString, 3)).toEqual('98cd');
        expect(logError.mock.calls).toEqual([]);
    });

    test('collect value range from start to end', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12345');

        // eslint-disable-next-line dot-notation
        expect(fixedInputMask['collectValueRange'](0, fixedInputMask.value.length - 1)).toEqual('12345');
        expect(logError.mock.calls).toEqual([]);
    });

    test('collect value range from center', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12345');

        // eslint-disable-next-line dot-notation
        expect(fixedInputMask['collectValueRange'](6, 9)).toEqual('34');
        expect(logError.mock.calls).toEqual([]);
    });

    test('characters replacement with clear substring', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12345');

        // eslint-disable-next-line dot-notation
        fixedInputMask['replaceChars']('987', 5);

        expect(fixedInputMask.value).toEqual('+7 (198) 75_-__-__');
        expect(logError.mock.calls).toEqual([]);
    });

    test('characters replacement with noisy substring', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12345');

        // eslint-disable-next-line dot-notation
        fixedInputMask['replaceChars'](noisyString, 5);

        expect(fixedInputMask.value).toEqual('+7 (198) 75_-__-__');
        expect(logError.mock.calls).toEqual([]);
    });

    test('characters replacement with substring at the end', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('1234567890');

        // eslint-disable-next-line dot-notation
        fixedInputMask['replaceChars']('555', 16);

        expect(fixedInputMask.value).toEqual('+7 (123) 456-78-55');
        expect(logError.mock.calls).toEqual([]);
    });

    test('insert valid character to valid position', () => {
        buildPhoneMask(true);

        fixedInputMask.applyChangeFrom('+7 (1___) ___-__-__', 5);

        expect(fixedInputMask.value).toEqual('+7 (1__) ___-__-__');
        expect(logError.mock.calls).toEqual([]);
    });

    test('insert invalid character to valid position', () => {
        buildPhoneMask(true);

        fixedInputMask.applyChangeFrom('+7 (a___) ___-__-__', 5);

        expect(fixedInputMask.value).toEqual('+7 (___) ___-__-__');
        expect(logError.mock.calls).toEqual([]);
    });

    test('insert valid character before valid position', () => {
        buildPhoneMask(true);

        fixedInputMask.applyChangeFrom('+17 (___) ___-__-__', 2);

        expect(fixedInputMask.value).toEqual('+7 (1__) ___-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(5);
        expect(logError.mock.calls).toEqual([]);
    });

    test('insert valid character after valid position', () => {
        buildPhoneMask(true);

        fixedInputMask.applyChangeFrom('+7 (___) ___-_1-__', 15);

        expect(fixedInputMask.value).toEqual('+7 (1__) ___-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(5);
        expect(logError.mock.calls).toEqual([]);
    });

    test('insert between filled dynamic', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12345');
        fixedInputMask.applyChangeFrom('+7 (1203) 45_-__-__', 7);

        expect(fixedInputMask.value).toEqual('+7 (120) 345-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(9);
        expect(logError.mock.calls).toEqual([]);
    });

    test('replace valid character before valid position', () => {
        buildPhoneMask(true);

        fixedInputMask.applyChangeFrom('+1(___) ___-__-__', 2);

        expect(fixedInputMask.value).toEqual('+7 (1__) ___-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(5);
        expect(logError.mock.calls).toEqual([]);
    });

    test('replace valid character after valid position', () => {
        buildPhoneMask(true);

        fixedInputMask.applyChangeFrom('+7 (___) ___-__-__1', 19);

        expect(fixedInputMask.value).toEqual('+7 (1__) ___-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(5);
        expect(logError.mock.calls).toEqual([]);
    });

    test('remove static char before first dynamic', () => {
        buildPhoneMask(false);

        fixedInputMask.applyChangeFrom('+7 ', 3);

        expect(fixedInputMask.value).toEqual('+7 (');
        expect(fixedInputMask.cursorPosition).toEqual(4);
        expect(logError.mock.calls).toEqual([]);
    });

    test('remove static char after dynamic', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12345');
        fixedInputMask.applyChangeFrom('+7 (123)45_-__-__', 8);

        expect(fixedInputMask.value).toEqual('+7 (124) 5__-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(6);
        expect(logError.mock.calls).toEqual([]);
    });

    test('remove character before first slot', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12345');
        fixedInputMask.applyChangeFrom('+ (123) 45_-__-__', 1);

        expect(fixedInputMask.value).toEqual('+7 (123) 45_-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(4);
        expect(logError.mock.calls).toEqual([]);
    });

    test('remove first dynamic character', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12345');
        fixedInputMask.applyChangeFrom('+7 (23) 45_-__-__', 4);

        expect(fixedInputMask.value).toEqual('+7 (234) 5__-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(4);
        expect(logError.mock.calls).toEqual([]);
    });

    test('remove multiple dynamic character at center', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12345');
        fixedInputMask.applyChangeFrom('+7 (1) 45_-__-__', 5);

        expect(fixedInputMask.value).toEqual('+7 (145) ___-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(5);
        expect(logError.mock.calls).toEqual([]);
    });

    test('remove end dynamic character', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12345');
        fixedInputMask.applyChangeFrom('+7 (123) 4_-__-__', 10);

        expect(fixedInputMask.value).toEqual('+7 (123) 4__-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(10);
        expect(logError.mock.calls).toEqual([]);
    });

    test('remove placeholder after last dynamic character', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12345');
        fixedInputMask.applyChangeFrom('+7 (123) 45_-_-__', 14);

        expect(fixedInputMask.value).toEqual('+7 (123) 4__-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(10);
        expect(logError.mock.calls).toEqual([]);
    });

    test('remove static character after last dynamic character', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12345');
        fixedInputMask.applyChangeFrom('+7 (123) 45_-____', 15);

        expect(fixedInputMask.value).toEqual('+7 (123) 4__-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(10);
        expect(logError.mock.calls).toEqual([]);
    });

    test('remove character from center multikey mask', () => {
        buildMultikeyMask(true);

        fixedInputMask.overrideRawValue('A123BC');
        fixedInputMask.applyChangeFrom('A 13 BC', 3);

        expect(fixedInputMask.value).toEqual('A 13_ --');
        expect(fixedInputMask.cursorPosition).toEqual(3);
        expect(logError.mock.calls).toEqual([]);
    });

    test('remove char from code mask', () => {
        buildCodeMask(true);

        fixedInputMask.overrideRawValue('abcde012345');
        fixedInputMask.applyChangeFrom('abcd-e12-345_-____', 6);

        expect(fixedInputMask.value).toEqual('abcd-e123-45__-____');
        expect(fixedInputMask.cursorPosition).toEqual(6);
        expect(logError.mock.calls).toEqual([]);
    });

    test('remove char from repeated part', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('11111');
        fixedInputMask.applyChangeFrom('+7 (11) 11_-__-__', 5);

        expect(fixedInputMask.value).toEqual('+7 (111) 1__-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(5);
        expect(logError.mock.calls).toEqual([]);
    });

    test('insert char to repeated part', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('1111');
        fixedInputMask.applyChangeFrom('+7 (1111) 1__-__-__', 6);

        expect(fixedInputMask.value).toEqual('+7 (111) 11_-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(6);
        expect(logError.mock.calls).toEqual([]);
    });

    test('cursor jumps over static char', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('12');
        fixedInputMask.applyChangeFrom('+7 (123_) ___-__-__', 7);

        expect(fixedInputMask.value).toEqual('+7 (123) ___-__-__');
        expect(fixedInputMask.cursorPosition).toEqual(9);
        expect(logError.mock.calls).toEqual([]);
    });

    test('char insertion will not cause character lost', () => {
        buildPhoneMask(true);

        fixedInputMask.overrideRawValue('1234567890');
        fixedInputMask.applyChangeFrom('+7 (123) 4056-78-90', 11);

        expect(fixedInputMask.value).toEqual('+7 (123) 456-78-90');
        expect(fixedInputMask.cursorPosition).toEqual(10);
        expect(logError.mock.calls).toEqual([]);
    });

    test('char insertion will not cause character lost with multikey', () => {
        buildMultikeyMask(true);

        fixedInputMask.overrideRawValue('A124');
        fixedInputMask.applyChangeFrom('A 1234 --', 5);

        expect(fixedInputMask.value).toEqual('A 124 --');
        expect(fixedInputMask.cursorPosition).toEqual(4);
        expect(logError.mock.calls).toEqual([]);
    });

    test('multiple char insertion with multikey', () => {
        const maskData: MaskData = {
            pattern: '###$$$',
            decoding: [{
                key: '#',
                filter: '[1-9]',
                placeholder: '_'
            }, {
                key: '$',
                filter: '[5-9]',
                placeholder: '_'
            }],
            alwaysVisible: false
        };

        fixedInputMask = new FixedLengthInputMask(maskData, logError);

        fixedInputMask.overrideRawValue('1256');
        fixedInputMask.applyChangeFrom('123456', 4);

        expect(fixedInputMask.value).toEqual('12356');
        expect(fixedInputMask.cursorPosition).toEqual(3);
        expect(logError.mock.calls).toEqual([]);
    });
});
