export function loadFileAsBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();

        reader.onerror = () => {
            reject(new Error('Failed to load the file'));
        };

        reader.onload = () => {
            resolve(reader.result as string);
        };

        reader.readAsDataURL(file);
    });
}
