import { type JsonPalette, type PaletteItem } from './palette';

export function paletteToDivjson(palette: PaletteItem[]): object {
    return palette.reduce((acc, item) => {
        acc[item.id] = {
            name: item.name,
            light: item.light,
            dark: item.dark
        };

        return acc;
    }, {} as Record<string, JsonPalette>);
}

export function divjsonToString(json: Record<string, unknown>) {
    return JSON.stringify(json, (key, val) => {
        if (key === '__leafId' || key === '__key') {
            return undefined;
        }
        return val;
    }, 2);
}
