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
        return 'transparent'
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
            const [, a, r, g, b] = colorMatch;
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

        const [, r, g, b] = colorMatch;
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
