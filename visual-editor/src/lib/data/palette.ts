import { divkitColorToCss, divkitColorToCssWithoutAlpha } from '../utils/colors';

export interface JsonPalette {
    name: string;
    light: string;
    dark: string;
}

export interface PaletteItem {
    id: string;
    name: string;
    light: string;
    dark: string;
}

export function isPaletteColor(value: string): boolean {
    return Boolean(value?.startsWith('@{getDictOptColor(\'#00ffffff\', local_palette, \''));
}

export function valueToPaletteId(value: string): string {
    const match = /\@\{getDictOptColor\('#00ffffff', local_palette, '([^']+)', theme\)}/.exec(value);
    if (match) {
        return match[1];
    }
    return '';
}

export function paletteIdToValue(paletteId: string): string {
    return `@{getDictOptColor('#00ffffff', local_palette, '${paletteId}', theme)}`;
}

export function palettePreview(list: PaletteItem[], value: string): string {
    if (!isPaletteColor(value)) {
        return '';
    }

    const paletteId = valueToPaletteId(value);
    const paletteItem = list.find(it => it.id === paletteId);

    return `Palette: ${paletteItem?.name || paletteId}`;
}

export function colorToCss(color: string, withAlpha: boolean, palette: PaletteItem[], previewTheme: 'light' | 'dark'): string {
    const func = withAlpha ? divkitColorToCss : divkitColorToCssWithoutAlpha;

    if (isPaletteColor(color)) {
        const paletteId = valueToPaletteId(color);
        const paletteItem = palette.find(it => it.id === paletteId);
        if (paletteItem) {
            return func(paletteItem[previewTheme]);
        }
        return '';
    }

    return func(color);
}
