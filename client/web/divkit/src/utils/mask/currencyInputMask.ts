import type { LogError, WrappedError } from '../wrapError';
import { BaseInputMask } from './baseInputMask';
import { TextDiff, textDiff } from '../textDiff';
import { wrapError } from '../wrapError';

export class CurrencyInputMask extends BaseInputMask {
    protected currencyFormatter = new Intl.NumberFormat();
    protected decimalSeparator = '.';
    protected localeDigits: Record<string, string> = {};
    protected trimZeroRegExp = new RegExp('');

    constructor(locale: string | undefined = undefined, private readonly logError: LogError) {
        super({
            pattern: '',
            decoding: [],
            alwaysVisible: false
        });
        this.initFormatter(locale);
    }

    updateCurrencyParams(locale?: string) {
        const currentValue = this.parseFormat(this.rawValue) || 0;
        this.initFormatter(locale);

        const newValue = currentValue.toString().replace('.', this.decimalSeparator);

        this.applyChangeFrom(newValue);
    }

    protected initFormatter(locale?: string): void {
        try {
            this.currencyFormatter = new Intl.NumberFormat(locale, {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            });
            this.decimalSeparator = this.currencyFormatter.format(0)[1];
            this.localeDigits = new Array(10).fill('').reduce((acc, _, i) => {
                acc[i] = this.currencyFormatter.format(i)[0];
                return acc;
            }, {});
            this.trimZeroRegExp = new RegExp(`^${this.localeDigits['0']}+`);
        } catch (err) {
            this.onException(wrapError(err as Error, {
                level: 'error',
                additional: {
                    locale
                }
            }));
        }
    }

    protected invalidateMaskDataForFormatted(forValue: number): void {
        const formatted = this.currencyFormatter.format(forValue);
        const pattern = this.formatPattern(formatted);
        const decoding = [{
            key: '#',
            filter: `[${[...Object.values(this.localeDigits)].join('')}]`,
            placeholder: this.localeDigits[0]
        }, {
            key: this.decimalSeparator,
            filter: `[${this.decimalSeparator}]`,
            placeholder: this.decimalSeparator
        }];

        this.updateMaskData({
            pattern,
            decoding,
            alwaysVisible: this.maskData.alwaysVisible
        }, false);
    }

    overrideRawValue(newRawValue: string): void {
        const parsed = this.parseFormat(newRawValue) || 0;

        this.invalidateMaskDataForFormatted(parsed);

        super.overrideRawValue(newRawValue);
    }

    applyChangeFrom(newValue: string, position?: number): void {
        const diff = textDiff(this.value, newValue);
        const oldSeparatorIndex = this.value.lastIndexOf(this.decimalSeparator);
        const newSeparatorIndex = newValue.lastIndexOf(this.decimalSeparator);
        const needInvalidateMask =
            oldSeparatorIndex !== newSeparatorIndex ||
            (oldSeparatorIndex === -1 && newSeparatorIndex === -1);
        const clearedValue = this.validFormat(newValue, diff);

        this.cleanup(diff);

        const rawValue = this.parseFormat(clearedValue) || 0;

        if (needInvalidateMask) {
            this.invalidateMaskDataForFormatted(rawValue);
        }

        this.replaceChars(clearedValue, 0);

        if (this.value.length > diff.start && !this.isDigit(this.value[diff.start])) {
            this.cursorPos = position ?? this.cursorPosition;
        } else {
            this.cursorPos = Math.abs(this.value.length - (newValue.length - (position ?? this.cursorPosition)));
        }
    }

    protected parseFormat(value: string): number {
        return parseFloat(
            value.replace(/./g, char => {
                const localeDigit = this.localeDigits[char];

                if (localeDigit) {
                    return localeDigit;
                }

                if (char === this.decimalSeparator) {
                    return '.';
                }

                return '';
            })
        );
    }

    protected formatPattern(pattern: string): string {
        let result = '';

        for (const char of pattern) {
            result += this.isDigit(char) ? '#' : char;
        }

        return result;
    }

    protected validFormat(value: string, diff: TextDiff): string {
        if (!value) {
            return '';
        }

        let separatorOutOfDiffIndex = -1;
        let index = 0;

        while (index < value.length) {
            if (value[index] === this.decimalSeparator && !this.inDiff(diff, index)) {
                separatorOutOfDiffIndex = index;
                break;
            }

            index++;
        }

        let replaceCharInDiff = -1;

        if (diff.added === 1 && diff.removed === 0 && [',', '.'].includes(value[diff.start])) {
            replaceCharInDiff = diff.start;
        }

        const maxSeparatorOffset = this.currencyFormatter.resolvedOptions().maximumFractionDigits;
        let leftToInsert = maxSeparatorOffset;

        if (separatorOutOfDiffIndex !== -1) {
            index = separatorOutOfDiffIndex;

            while (index < value.length) {
                if (this.isDigit(value[index]) && !this.inDiff(diff, index)) {
                    leftToInsert--;
                }
                index++;
            }
        } else {
            let oldSeparatorLeft = false;

            for (let i = 0; i < value.length; i++) {
                const char = value[i];
                if (char === this.decimalSeparator) {
                    oldSeparatorLeft = true;
                } else if (!this.inDiff(diff, i) && oldSeparatorLeft && this.isDigit(char)) {
                    leftToInsert--;
                }
            }
        }

        const containsSeparator = value.includes(this.decimalSeparator) || replaceCharInDiff !== -1;
        const result: string[] = [];

        index = value.length - 1;
        let separatorInserted = false;

        while (index >= 0) {
            const char = value[index];
            const canInsertSeparator = result.length <= maxSeparatorOffset;

            if (this.isDigit(char)) {
                if (this.inDiff(diff, index) && !separatorInserted && containsSeparator) {
                    if (leftToInsert > 0) {
                        result.push(char);
                        leftToInsert--;
                    }
                } else {
                    result.push(char);
                }
            } else if (canInsertSeparator && separatorOutOfDiffIndex === -1 && index === replaceCharInDiff) {
                result.push(this.decimalSeparator);
                separatorInserted = true;
            } else if (
                canInsertSeparator &&
                char === this.decimalSeparator &&
                (separatorOutOfDiffIndex === index || separatorOutOfDiffIndex === -1)
            ) {
                result.push(this.decimalSeparator);

                separatorInserted = true;
                separatorOutOfDiffIndex = index;
            }

            index--;
        }

        return result.reverse().join('').replace(this.trimZeroRegExp, '');
    }

    protected inDiff(diff: TextDiff, index: number): boolean {
        return diff.start <= index && index < diff.start + diff.added;
    }

    protected isDigit(char: string): boolean {
        return Boolean(this.localeDigits[char]);
    }

    onException(exception: WrappedError) {
        this.logError(exception);
    }
}
