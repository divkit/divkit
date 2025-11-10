const componentToHex = (num: number) => {
    const hex = num.toString(16);
    return hex.length === 1 ? '0' + hex : hex;
};

export const rgbaComponentsToHex = (color: RGB, opacity?: number) => {
    const alpha = opacity ?? 1;
    return (
        '#' +
        (alpha < 1 ? componentToHex(Math.round(alpha * 255)) : '') +
        componentToHex(Math.round(color.r * 255)) +
        componentToHex(Math.round(color.g * 255)) +
        componentToHex(Math.round(color.b * 255))
    );
};
