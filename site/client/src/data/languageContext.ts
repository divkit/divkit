import type lang from '../auto/lang.json';

export const LANGUAGE_CTX = Symbol('language');

export interface LanguageContext {
    getLanguage(): string;
    setLanguage(name: string): void;
    languagesList(): string[];
    l10n(key: keyof typeof lang['en'], overrideLang?: string): string;
}
