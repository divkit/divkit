import type { LogError, WrappedError } from '../wrapError';
import { BaseInputMask, MaskData } from './baseInputMask';

export class FixedLengthInputMask extends BaseInputMask {
    constructor(initialMaskData: MaskData, private readonly logError: LogError) {
        super(initialMaskData);
    }

    onException(exception: WrappedError) {
        this.logError(exception);
    }
}
