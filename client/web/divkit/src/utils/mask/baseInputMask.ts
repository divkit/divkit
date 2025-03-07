// Replicate from Android: client/android/div/src/main/java/com/yandex/div/core/util/mask/BaseInputMask.kt

import type { WrappedError } from '../wrapError';
import { wrapError } from '../wrapError';
import { type TextDiff, textDiff } from '../textDiff';

export interface MaskKey {
    key: string;
    filter?: string | undefined;
    placeholder: string;
}

export interface MaskData {
    pattern: string;
    decoding: MaskKey[];
    alwaysVisible: boolean;
}

interface MaskChar {
    char: string | null;
}

export class MaskCharStatic implements MaskChar {
    constructor(public char: string) {}
}

export class MaskCharDynamic implements MaskChar {
    constructor(public char: string | null, public filter: RegExp, public placeholder: string) {}
}

export abstract class BaseInputMask {
    protected maskData: MaskData;
    protected filters: Map<string, RegExp> = new Map();
    protected destructedValue: MaskChar[] = [];
    protected cursorPos = 0;

    constructor(initialMaskData: MaskData) {
        this.maskData = initialMaskData;
        this.updateMaskData(initialMaskData);
    }

    get cursorPosition(): number {
        return this.cursorPos;
    }

    get rawValue(): string {
        return this.collectValueRange(0, this.destructedValue.length - 1);
    }

    get value(): string {
        let str = '';

        for (let i = 0; i < this.destructedValue.length; ++i) {
            const char = this.destructedValue[i];

            if (char instanceof MaskCharStatic) {
                str += char.char;
            } else if (char instanceof MaskCharDynamic) {
                if (char.char) {
                    str += char.char;
                } else if (this.maskData.alwaysVisible) {
                    str += char.placeholder;
                } else {
                    break;
                }
            }
        }

        return str;
    }

    protected firstEmptyHolderIndex(): number {
        const index = this.destructedValue.findIndex(maskChar => {
            return maskChar instanceof MaskCharDynamic && !maskChar.char;
        });

        if (index !== -1) {
            return index;
        }

        return this.destructedValue.length;
    }

    abstract onException(exception: WrappedError): void;

    updateMaskData(newMaskData: MaskData, restoreValue = true): void {
        const previousRawValue = this.maskData !== newMaskData && restoreValue ? this.rawValue : null;

        this.filters = new Map();
        this.maskData = newMaskData;

        this.maskData.decoding.forEach(maskKey => {
            if (maskKey.filter) {
                try {
                    const regexp = new RegExp(maskKey.filter);
                    this.filters.set(maskKey.key, regexp);
                } catch (err) {
                    this.onException(wrapError(err as Error, {
                        level: 'error',
                        additional: {
                            key: maskKey.key
                        }
                    }));
                }
            }
        });

        this.destructedValue = this.maskData.pattern.split('').map(maskChar => {
            const mappingItem = this.maskData.decoding.find(it => it.key === maskChar);

            if (mappingItem) {
                return new MaskCharDynamic(
                    null,
                    this.filters.get(mappingItem.key) as RegExp,
                    mappingItem.placeholder
                );
            }

            return new MaskCharStatic(maskChar);
        });

        if (previousRawValue !== null) {
            this.overrideRawValue(previousRawValue);
        }
    }

    overrideRawValue(newRawValue: string): void {
        this.clearRange(0, this.destructedValue.length);

        this.replaceChars(newRawValue, 0);

        this.cursorPos = Math.min(this.cursorPos, this.value.length);
    }

    applyChangeFrom(newValue: string, position?: number): void {
        const diff = textDiff(this.value, newValue);

        if (position !== undefined) {
            diff.start = Math.max(0, position - diff.added);
        }

        const tailStart = this.replaceBodyTail(diff, newValue);

        this.calculateCursorPosition(diff, tailStart);
    }

    protected replaceBodyTail(diff: TextDiff, newValue: string): number {
        const body = this.buildBodySubstring(diff, newValue);
        const tail = this.buildTailSubstring(diff);

        this.cleanup(diff);

        const fehi = this.firstEmptyHolderIndex();

        const maxShift = tail ? this.calculateMaxShift(tail, fehi) : undefined;

        this.replaceChars(body, fehi, maxShift);

        const tailStart = this.firstEmptyHolderIndex();

        this.replaceChars(tail, tailStart);

        return tailStart;
    }

    protected buildBodySubstring(diff: TextDiff, newValue: string): string {
        return newValue.substring(diff.start, diff.start + diff.added);
    }

    protected buildTailSubstring(diff: TextDiff): string {
        return this.collectValueRange(
            diff.start + diff.removed,
            this.destructedValue.length - 1
        );
    }

    protected calculateMaxShift(str: string, start: number): number {
        if (this.filters.size <= 1) {
            let dynamicLeft = 0;
            let index = start;

            while (index < this.destructedValue.length) {
                if (this.destructedValue[index] instanceof MaskCharDynamic) {
                    ++dynamicLeft;
                }
                ++index;
            }

            return Math.max(0, dynamicLeft - str.length);
        }

        const initialInsertableSubstring = this.calculateInsertableSubstring(str, start);

        let index = 0;

        while (
            index < this.destructedValue.length &&
                initialInsertableSubstring === this.calculateInsertableSubstring(str, start + index)
        ) {
            ++index;
        }

        return Math.max(0, index - 1);
    }

    protected cleanup(diff: TextDiff): void {
        if (diff.added === 0 && diff.removed === 1) {
            let index = diff.start;

            while (index >= 0) {
                const maskChar = this.destructedValue[index];

                if (maskChar instanceof MaskCharDynamic && maskChar.char !== null) {
                    maskChar.char = null;

                    break;
                } else {
                    --index;
                }
            }
        }

        this.clearRange(diff.start, this.destructedValue.length);
    }

    protected clearRange(start: number, end: number): void {
        let index = start;

        while (index < end && index < this.destructedValue.length) {
            const holder = this.destructedValue[index];

            if (holder instanceof MaskCharDynamic) {
                holder.char = null;
            }

            ++index;
        }
    }

    protected calculateCursorPosition(diff: TextDiff, tailStart: number) {
        const fehi = this.firstEmptyHolderIndex();

        let positionByDiff: number;
        if (diff.start < fehi) {
            positionByDiff = Math.min(this.firstHolderAfter(tailStart), this.value.length);
        } else {
            positionByDiff = fehi;
        }

        this.cursorPos = positionByDiff;
    }

    protected calculateInsertableSubstring(substring: string, start: number): string {
        let str = '';

        let index = start;

        const moveToAndGetNextHolderFilter: () => RegExp | undefined = () => {
            while (index < this.destructedValue.length && !(this.destructedValue[index] instanceof MaskCharDynamic)) {
                ++index;
            }

            return (this.destructedValue[index] as MaskCharDynamic)?.filter;
        };

        substring.split('').forEach(char => {
            const maskCharFilter = moveToAndGetNextHolderFilter();

            if (maskCharFilter?.test(char)) {
                str += char;
                ++index;
            }
        });

        return str;
    }

    protected collectValueRange(start: number, end: number): string {
        let str = '';

        let index = start;

        while (index <= end) {
            const maskChar = this.destructedValue[index];

            if (maskChar instanceof MaskCharDynamic && maskChar.char !== null) {
                str += maskChar.char;
            }

            ++index;
        }

        return str;
    }

    protected replaceChars(substring: string, start: number, count?: number): void {
        let trimmedSubstring = this.calculateInsertableSubstring(substring, start);
        if (count !== undefined) {
            trimmedSubstring = trimmedSubstring.substring(0, count);
        }

        let index = start;

        let insertableCharIndex = 0;

        while (index < this.destructedValue.length && insertableCharIndex < trimmedSubstring.length) {
            const maskChar = this.destructedValue[index];
            const char = trimmedSubstring[insertableCharIndex];

            if (maskChar instanceof MaskCharDynamic) {
                maskChar.char = char;

                ++insertableCharIndex;
            }

            ++index;
        }
    }

    protected firstHolderAfter(start: number): number {
        let index = start;

        while (index < this.destructedValue.length) {
            const holder = this.destructedValue[index];

            if (holder instanceof MaskCharDynamic) {
                break;
            } else {
                ++index;
            }
        }

        return index;
    }
}
