type SimpleFunction = (...args: any[]) => any;
type DebouncedFunction<F extends SimpleFunction> = (...args: Parameters<F>) => void;

export function debounce<F extends SimpleFunction>(fn: F, timeout: number): DebouncedFunction<F> {
    let timer: ReturnType<typeof setTimeout> | null;

    return function(this: ThisParameterType<F>, ...args: Parameters<F>) {
        if (timer !== null) {
            clearTimeout(timer);
        }

        timer = setTimeout(() => {
            fn.apply(this, args);
            timer = null;
        }, timeout);
    };
}
