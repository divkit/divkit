import type { MaybeMissing } from '../expressions/json';
import type { Accessibility } from '../types/base';

export function composeAccessibilityDescription(accessibility: MaybeMissing<Accessibility>): string {
    return [
        accessibility.state_description,
        accessibility.description,
        accessibility.hint
    ].filter(Boolean).join(', ');
}
