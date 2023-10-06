import type { Readable } from 'svelte/store';
import type lang from '../auto/lang.json';

export const LANGUAGE_CTX = Symbol('language');

export interface LanguageContext {
    lang: Readable<string>;
    getLanguage(): string;
    setLanguage(name: string): void;
    languagesList(): string[];
    l10n: Readable<(key: keyof typeof lang['en'], overrideLang?: string) => string>;
}
