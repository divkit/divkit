/* eslint-disable @typescript-eslint/no-explicit-any */

import type { JsonVariable } from '../data/customVariables';
import { isPaletteColor, paletteIdToValue, valueToPaletteId } from '../data/palette';

function nameToCode(name: string): string {
    return name.split('').map(char => `u${String(char.charCodeAt(0)).padStart(4, '0')}`).join('');
}

function codeToName(code: string): string {
    return code.split('u').map(part => part ? String.fromCharCode(Number(part)) : '').join('');
}

export function convertDictToPalette(json: any) {
    const palette: {
        light: {
            name: string;
            color: string;
        }[];
        dark: {
            name: string;
            color: string;
        }[];
    } = {
        light: [],
        dark: []
    };
    const now = Date.now();

    const dictPalette = json.card.variables.find((it: JsonVariable) => it.name === 'local_palette')?.value;

    if (dictPalette) {
        json.card.variables = json.card.variables.filter((it: JsonVariable) => it.name !== 'local_palette');
        if (!json.card.variables.length) {
            delete json.card.variables;
        }
    }

    const knownColors = new Set<string>();

    const proc = (json: any): any => {
        if (typeof json === 'number' || typeof json === 'boolean') {
            return json;
        }
        if (typeof json === 'string') {
            if (isPaletteColor(json)) {
                const paletteId = valueToPaletteId(json);
                const paletteItem = dictPalette?.[paletteId];
                if (paletteItem) {
                    const id = 'local_palette.' + paletteId + '.' + now + '.' + nameToCode(paletteItem.name);
                    if (!knownColors.has(id)) {
                        knownColors.add(id);

                        palette.light.push({
                            name: id,
                            color: paletteItem.light
                        });
                        palette.dark.push({
                            name: id,
                            color: paletteItem.dark
                        });
                    }
                    return `@{${id}}`;
                }
                throw new Error(`Palette color "${paletteId}" is not found`);
            } else {
                return json;
            }
        }
        if (!json) {
            return json;
        }
        if (Array.isArray(json)) {
            return json.map(proc);
        }
        const res: Record<string, unknown> = {};
        for (const key in json) {
            res[key] = proc(json[key]);
        }
        return res;
    };

    const copy = proc(json);

    copy.palette = palette;

    return copy;
}

export function convertPaletteToDict(json: any) {
    const palette: Record<string, {
        name: string;
        light: string;
        dark: string;
    }> = {};

    if (json.palette && json.palette.light && json.palette.dark) {
        for (const key in json.palette.light) {
            const parts = json.palette.light[key].name.split('.');
            const name = parts[3] ? codeToName(parts[3]) : parts[1];
            palette[parts[1]] = {
                name,
                light: json.palette.light[key].color,
                dark: json.palette.dark[key].color
            };
        }
    }

    const proc = (json: any): any => {
        if (typeof json === 'number' || typeof json === 'boolean') {
            return json;
        }
        if (typeof json === 'string') {
            if (json.startsWith('@{local_palette.')) {
                const parts = json.split('.');
                const paletteId = parts[1];
                return paletteIdToValue(paletteId);
            }
            return json;
        }
        if (!json) {
            return json;
        }
        if (Array.isArray(json)) {
            return json.map(proc);
        }
        const res: Record<string, unknown> = {};
        for (const key in json) {
            res[key] = proc(json[key]);
        }
        return res;
    };

    const copy = proc(json);

    delete copy.palette;

    if (!json.card.variables) {
        copy.card.variables = [];
    }
    copy.card.variables.push({
        type: 'dict',
        name: 'local_palette',
        value: palette
    });

    return copy;
}
