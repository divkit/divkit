import type { MaybeMissing } from '../expressions/json';

export function variationSettingsToString(variationSettings: MaybeMissing<Record<string, string>> | undefined): string {
    if (variationSettings && typeof variationSettings === 'object') {
        const vals: string[] = [];
        for (const key in variationSettings) {
            const val = variationSettings[key];
            vals.push(`"${key}" ${val}`);
        }
        return vals.join(', ');
    }

    return '';
}
