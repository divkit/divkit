import { padLeft } from './padLeft';

/**
 * Converts color from app format (ARGB) to css format (RGBA)
 * @param color Color with format #RGB, #ARGB, #RRGGBB, #AARRGGBB
 * @param alpha Color additional opacity
 * @param defaultColor Default color value, used if input color is incorrect
 * @returns Color with opacity if it has correct format, or defaultColor elsewhere
 */
export function correctColor(color: string | undefined, alpha = 1, defaultColor = 'transparent'): string {
    color = (typeof color === 'string' && color || '').toLowerCase();

    if (color.charAt(0) !== '#') {
        return defaultColor;
    }

    const parsedColor = parseColor(color);
    if (parsedColor) {
        parsedColor.a *= alpha;
        return stringifyColorToCss(parsedColor);
    }

    return defaultColor;
}

export function correctColorWithAlpha(color: string | undefined, alpha: number, defaultColor = 'transparent'): string {
    color = (typeof color === 'string' && color || '').toLowerCase();

    if (color.charAt(0) !== '#') {
        return defaultColor;
    }

    const parsedColor = parseColor(color);
    if (parsedColor) {
        parsedColor.a = alpha;
        return stringifyColorToCss(parsedColor);
    }

    return defaultColor;
}

export interface ParsedColor {
    a: number;
    r: number;
    g: number;
    b: number;
}

function stringifyColorToCss(color: ParsedColor): string {
    if (color.a === 255) {
        return `#${[color.r, color.g, color.b].map(it => {
            return padLeft(Math.round(it).toString(16), 2);
        }).join('')}`;
    }

    return `rgba(${color.r},${color.g},${color.b},${(color.a / 255).toFixed(2)})`;
}

export function parseColor(color: string): ParsedColor | null {
    const colorMatch = (
        // #AARRGGBB
        color.match(/^#([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})$/i) ||
        // #ARGB
        color.match(/^#([0-9a-f])([0-9a-f])([0-9a-f])([0-9a-f])$/i) ||
        // #RRGGBB
        color.match(/^#([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})$/i) ||
        // #RGB
        color.match(/^#([0-9a-f])([0-9a-f])([0-9a-f])$/i)
    );

    if (colorMatch) {
        // with alpha part in color
        if (colorMatch.length === 5) {
            const [_, a, r, g, b] = colorMatch;
            const redPart = r.length === 2 ? r : r + r;
            const greenPart = g.length === 2 ? g : g + g;
            const bluePart = b.length === 2 ? b : b + b;
            const alphaPart = a.length === 2 ? a : a + a;

            return {
                a: parseInt(alphaPart, 16),
                r: parseInt(redPart, 16),
                g: parseInt(greenPart, 16),
                b: parseInt(bluePart, 16)
            };
        }

        const [_, r, g, b] = colorMatch;
        const redPart = r.length === 2 ? r : r + r;
        const greenPart = g.length === 2 ? g : g + g;
        const bluePart = b.length === 2 ? b : b + b;

        return {
            a: 255,
            r: parseInt(redPart, 16),
            g: parseInt(greenPart, 16),
            b: parseInt(bluePart, 16)
        };
    }

    return null;
}
