export function debounce<F extends (...args: any[]) => any>(func: F, wait: number): F {
    let timeout: ReturnType<typeof setTimeout>;

    return ((...args: any[]) => {
        clearTimeout(timeout);

        timeout = setTimeout(() => {
            func(...args as Parameters<F>);
        }, wait);
    }) as unknown as F;
}
