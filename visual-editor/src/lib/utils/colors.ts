export interface ParsedColor {
    a: number;
    r: number;
    g: number;
    b: number;
}

export interface ParsedHSVColor {
    a: number;
    h: number;
    s: number;
    v: number;
}

export function stringifyColorToCss(color?: ParsedColor): string {
    if (!color) {
        return 'transparent';
    }

    if (color.a === 255) {
        return `#${[color.r, color.g, color.b].map(it => {
            return Math.round(it).toString(16).padStart(2, '0');
        }).join('')}`;
    }

    return `rgba(${color.r},${color.g},${color.b},${(color.a / 255).toFixed(2)})`;
}

export function stringifyColorToDivKit(color: ParsedColor): string {
    if (color.a === 255) {
        return `#${[color.r, color.g, color.b].map(it => {
            return Math.round(it).toString(16).padStart(2, '0');
        }).join('')}`;
    }

    return `#${[color.a, color.r, color.g, color.b].map(it => {
        return Math.round(it).toString(16).padStart(2, '0');
    }).join('')}`;
}

export function divkitColorToCss(color: string): string {
    const parsed = parseColor(color);

    if (!parsed) {
        return '';
    }

    return stringifyColorToCss(parsed);
}

export function divkitColorToCssWithoutAlpha(color: string): string {
    const parsed = parseColor(color);
    if (!parsed) {
        return 'transparent';
    }
    parsed.a = 255;
    return stringifyColorToCss(parsed);
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

export function hsvToRgb(color: ParsedHSVColor): ParsedColor {
    const { h, s, v, a } = color;

    let r;
    let g;
    let b;

    const i = Math.floor(h * 6);
    const f = h * 6 - i;
    const p = v * (1 - s);
    const q = v * (1 - f * s);
    const t = v * (1 - (1 - f) * s);

    switch (i % 6) {
        case 0: {
            r = v;
            g = t;
            b = p;
            break;
        }
        case 1: {
            r = q;
            g = v;
            b = p;
            break;
        }
        case 2: {
            r = p;
            g = v;
            b = t;
            break;
        }
        case 3: {
            r = p;
            g = q;
            b = v;
            break;
        }
        case 4: {
            r = t;
            g = p;
            b = v;
            break;
        }
        case 5: {
            r = v;
            g = p;
            b = q;
            break;
        }
        default: {
            throw new Error('Default fallback');
        }
    }

    return {
        r: Math.round(r * 255),
        g: Math.round(g * 255),
        b: Math.round(b * 255),
        a: Math.round(a * 255)
    };
}

export function rgbToHsv(color: ParsedColor): ParsedHSVColor {
    let { r, g, b, a } = color;

    r /= 255;
    g /= 255;
    b /= 255;
    a /= 255;

    const max = Math.max(r, g, b);
    const min = Math.min(r, g, b);
    let h: number;
    const v = max;

    const d = max - min;
    const s = max === 0 ? 0 : d / max;

    if (max === min) {
        h = 0;
    } else {
        switch (max) {
            case r: {
                h = (g - b) / d + (g < b ? 6 : 0);
                break;
            }
            case g: {
                h = (b - r) / d + 2;
                break;
            }
            case b: {
                h = (r - g) / d + 4;
                break;
            }
            default: {
                throw new Error('Default fallback');
            }
        }

        h /= 6;
    }

    return {
        h,
        s,
        v,
        a
    };
}
