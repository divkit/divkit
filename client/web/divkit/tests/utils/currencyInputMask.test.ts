// Replicate from Android: /client/android/div/src/test/java/com/yandex/div/core/util/mask/CurrencyMaskTest.kt

import { CurrencyInputMask } from '../../src/utils/mask/currencyInputMask';

describe('CurrencyInputMask', () => {
    let logError: jest.Mock;
    let currencyInputMask: CurrencyInputMask;

    function buildCurrencyMask(locale = 'en-EN'): void {
        currencyInputMask = new CurrencyInputMask(locale, logError);
    }

    function assertMask(mask: CurrencyInputMask, expectedValue: string, expectedCursorPosition: number) {
        expect(mask.value).toEqual(expectedValue);
        expect(mask.cursorPosition).toEqual(expectedCursorPosition);
        expect(logError.mock.calls).toEqual([]);
    }

    beforeEach(() => {
        logError = jest.fn();
    });

    test('insert one decimal to empty string', () => {
        buildCurrencyMask();

        currencyInputMask.applyChangeFrom('1', 1);

        assertMask(currencyInputMask, '1', 1);
    });

    test('insert multiple decimal to empty string', () => {
        buildCurrencyMask();

        currencyInputMask.applyChangeFrom('12345', 5);

        assertMask(currencyInputMask, '12,345', 6);
    });

    test('insert one decimal', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1');
        currencyInputMask.applyChangeFrom('12', 2);

        assertMask(currencyInputMask, '12', 2);
    });

    test('insert multiple decimals', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1');
        currencyInputMask.applyChangeFrom('12345', 5);

        assertMask(currencyInputMask, '12,345', 6);
    });

    test('remove one decimal', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('12345');
        currencyInputMask.applyChangeFrom('12,45', 3);

        assertMask(currencyInputMask, '1,245', 3);
    });

    test('remove multiple decimal', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('123456');
        currencyInputMask.applyChangeFrom('12,56', 3);

        assertMask(currencyInputMask, '1,256', 3);
    });

    test('input separator', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1234');
        currencyInputMask.applyChangeFrom('1,234.', 6);

        assertMask(currencyInputMask, '1,234.', 6);
    });

    test('remove separator', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1234.');
        currencyInputMask.applyChangeFrom('1,234', 5);

        assertMask(currencyInputMask, '1,234', 5);
    });

    test('insert decimal after separator', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1234.');
        currencyInputMask.applyChangeFrom('1,234.5', 7);

        assertMask(currencyInputMask, '1,234.5', 7);
    });

    test('remove decimal after separator', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1234.56');
        currencyInputMask.applyChangeFrom('1,234.6', 6);

        assertMask(currencyInputMask, '1,234.6', 6);
    });

    test('insert separator with overflow not allowed', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('12345678');
        currencyInputMask.applyChangeFrom('12,34.5,678', 6);

        assertMask(currencyInputMask, '12,345,678', 5);
    });

    test('insert separator with overflow allowed', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('123456');
        currencyInputMask.applyChangeFrom('123,4.56', 6);

        assertMask(currencyInputMask, '1,234.56', 6);
    });

    test('remove separator from a middle', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1234.56');
        currencyInputMask.applyChangeFrom('1,23456', 5);

        assertMask(currencyInputMask, '123,456', 5);
    });

    test('change locale with value', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1234.56');
        assertMask(currencyInputMask, '1,234.56', 0);

        currencyInputMask.updateCurrencyParams('de-DE');

        assertMask(currencyInputMask, '1.234,56', 0);
    });

    test('input dot as separator for deutsch locale', () => {
        buildCurrencyMask('de-DE');

        currencyInputMask.overrideRawValue('1234567');
        currencyInputMask.applyChangeFrom('1.234.5.67', 8);

        assertMask(currencyInputMask, '12.345,67', 7);
    });

    test('input dot as separator for english locale', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1234567');
        currencyInputMask.applyChangeFrom('1,234,5.67', 8);

        assertMask(currencyInputMask, '12,345.67', 7);
    });

    test('input comma as separator for english locale', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1234567');
        currencyInputMask.applyChangeFrom('1,234,5,67', 8);

        assertMask(currencyInputMask, '12,345.67', 7);
    });

    test('input invalid character', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1234.5');
        currencyInputMask.applyChangeFrom('1,23a4.5', 5);

        assertMask(currencyInputMask, '1,234.5', 4);
    });

    test('input second separator before one', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1234.56');
        currencyInputMask.applyChangeFrom('1,2.34.56', 4);

        assertMask(currencyInputMask, '1,234.56', 3);
    });

    test('input second separator after one', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1234.56');
        currencyInputMask.applyChangeFrom('1,234.5.6', 8);

        assertMask(currencyInputMask, '1,234.56', 7);
    });

    test('fully clear field', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1234.56');
        currencyInputMask.applyChangeFrom('', 0);

        assertMask(currencyInputMask, '', 0);
    });

    test('type zero digits as leading', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('1,234');
        currencyInputMask.applyChangeFrom('0001,234', 3);

        assertMask(currencyInputMask, '1,234', 0);
    });

    test('type char at end with overflow', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('123.45');
        currencyInputMask.applyChangeFrom('123.456', 6);

        assertMask(currencyInputMask, '123.45', 5);
    });

    test('type char and separator at end with overflow', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('12345');
        currencyInputMask.applyChangeFrom('12,3.645', 6);

        assertMask(currencyInputMask, '123.45', 4);
    });

    test('type char and separator at end without overflow', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('123.4');
        currencyInputMask.applyChangeFrom('123.4.5', 7);

        assertMask(currencyInputMask, '123.45', 6);
    });

    test('add character in repeated substring', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('11111');
        currencyInputMask.applyChangeFrom('11,1111', 5);

        assertMask(currencyInputMask, '111,111', 5);
    });

    test('remove character in repeated substring', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('11111');
        currencyInputMask.applyChangeFrom('11,11', 4);

        assertMask(currencyInputMask, '1,111', 4);
    });

    test('remove cluster separator', () => {
        buildCurrencyMask();

        currencyInputMask.overrideRawValue('12345');
        currencyInputMask.applyChangeFrom('12345', 2);

        assertMask(currencyInputMask, '12,345', 2);
    });

    test('use invalid locale', () => {
        buildCurrencyMask('bla-BLA');

        expect(logError.mock.calls.length).toEqual(1);
    });
});
