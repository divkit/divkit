import type { LogError, WrappedError } from '../wrapError';
import { BaseInputMask, MaskCharDynamic } from './baseInputMask';
import { textDiff } from '../textDiff';
import data from '../../../../../../shared_data/phone-masks.json';

const UNIVERSAL_MASK = '000000000000000';
const COUNTRY_CODE_END_MARKER = '*';
const EXTRA_NUMBERS = '00';

const DEFAULT_DECODING = [{
    key: '0',
    filter: '\\d',
    placeholder: '_'
}];

export class PhoneInputMask extends BaseInputMask {
    protected decimalSeparator = '.';
    protected localeDigits: Record<string, string> = {};
    protected trimZeroRegExp = new RegExp('');

    constructor(private readonly logError: LogError) {
        super({
            pattern: phoneMaskPattern(''),
            decoding: DEFAULT_DECODING,
            alwaysVisible: false
        });
    }

    override overrideRawValue(newRawValue: string): void {
        this.tryInvalidateMaskDataWith(newRawValue);
        super.overrideRawValue(newRawValue);
    }

    override applyChangeFrom(newValue: string, position?: number): void {
        const diff = textDiff(this.value, newValue);

        if (position !== undefined) {
            diff.start = Math.max(0, position - diff.added);
        }

        const oldRawValue = this.rawValue;

        const tailStart = this.replaceBodyTail(diff, newValue);

        const newRawValue = this.rawValue;
        const newPattern = this.newMaskPatternFor(newRawValue);

        if (newPattern == null) {
            this.calculateCursorPosition(diff, tailStart);
            return;
        }

        this.updateMaskDataWith(newPattern);
        this.replaceChars(newRawValue, 0);

        const rawValueDiff = textDiff(oldRawValue, newRawValue);
        const dynamicDestination = rawValueDiff.start + rawValueDiff.added;
        this.calculateCursorPositionBy(dynamicDestination);
    }

    private calculateCursorPositionBy(dynamicDestination: number): void {
        let index = 0;
        let dynamicCounter = 0;

        while (index < this.destructedValue.length && dynamicCounter < dynamicDestination) {
            if (this.destructedValue[index++] instanceof MaskCharDynamic) {
                dynamicCounter++;
            }
        }

        this.cursorPos = this.firstHolderAfter(index);
    }

    private tryInvalidateMaskDataWith(rawValue: string): void {
        const newPattern = this.newMaskPatternFor(rawValue);
        if (newPattern) {
            this.updateMaskDataWith(newPattern);
        }
    }

    private newMaskPatternFor(rawValue: string): string | null {
        const newPattern = phoneMaskPattern(rawValue);
        const currentPattern = this.maskData.pattern;
        return newPattern !== currentPattern ? newPattern : null;
    }

    private updateMaskDataWith(newPattern: string) {
        return this.updateMaskData({
            pattern: newPattern,
            decoding: DEFAULT_DECODING,
            alwaysVisible: this.maskData.alwaysVisible
        }, false);
    }

    onException(exception: WrappedError) {
        this.logError(exception);
    }
}

function resolveObject(obj: any) {
    if ('$ref' in obj) {
        return data.constants[obj.$ref.split('/').pop() as keyof typeof data.constants];
    }
    return obj;
}

function phoneMaskPattern(val: string): string {
    if (!val) {
        return UNIVERSAL_MASK;
    }
    let current: any = data.properties.value.default_value;
    let countryCodeInd = 0;
    while (!('value' in current)) {
        if (countryCodeInd >= val.length) {
            current = resolveObject(current[COUNTRY_CODE_END_MARKER]);
            break;
        }
        const digit = val[countryCodeInd++];
        current = resolveObject(current[digit in current ? digit : COUNTRY_CODE_END_MARKER]);
    }
    return current.value + EXTRA_NUMBERS;
}
