import type { Readable } from 'svelte/store';
import type lang from '../../auto/lang.json';
import type { Locale } from '../../lib';

export const LANGUAGE_CTX = Symbol('language');

export interface LanguageContext {
    lang: Readable<Locale>;
    getLanguage(): string;
    l10n: Readable<<T extends string | string[] = string>(key: (keyof typeof lang['en']) | string, overrideLang?: string) => T>;
    l10nString: Readable<(key: (keyof typeof lang['en']) | string, overrideLang?: string) => string>;
}
