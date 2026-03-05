export function convertedFileName(fileName: string, ext: string): string {
    if (fileName.includes('base64,')) {
        return `image.${ext}`;
    }

    const cutted = fileName.replace(/\.[^.]+$/, '').split('/').pop() as string;
    if (cutted.length > 60 || cutted.length < 4) {
        return `image.${ext}`;
    }

    return `${cutted}.${ext}`;
}
