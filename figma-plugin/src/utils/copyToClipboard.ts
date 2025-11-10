const copyFallback = (text: string) => {
    const textArea = document.createElement('textarea');
    textArea.value = text;
    textArea.style.opacity = '0';
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
    try {
        document.execCommand('copy');
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (_err) {
        // do nothing
    }
    document.body.removeChild(textArea);
    return Promise.resolve();
};

export const copyToClipboard = (text: string): Promise<void> => {
    if (typeof navigator?.clipboard?.writeText === 'function') {
        return navigator.clipboard.writeText(text).catch(() => copyFallback(text));
    }

    return copyFallback(text);
};
