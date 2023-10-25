import type { WrappedError } from '../../typings/common';
import type { Variable } from '../../typings/variables';

declare global {
    interface HTMLElement {
        divKitApiCallback?(api: {
            variables: Map<string, Variable>;
            logError(error: WrappedError): void;
        }): void;
    }
}
