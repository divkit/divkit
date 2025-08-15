import translations from '../../auto/lang.json';

export const l10nGetter = (lang: string, key: string) => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const translation = (translations as any)[lang]?.[key];

    if (!translation) {
        // todo expose error
        console.error(`Missing translation for key "${key}"`);
    }

    return translation || '';
};
