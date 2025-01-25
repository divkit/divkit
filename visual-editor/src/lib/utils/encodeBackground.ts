export function encodeBackground(background: string): string {
    return background.replace(/'/g, '%27')
        .replace(/\(/g, '%28')
        .replace(/\)/g, '%29');
}
