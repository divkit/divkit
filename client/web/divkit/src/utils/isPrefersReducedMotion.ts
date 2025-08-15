let media: MediaQueryList | undefined;

export function isPrefersReducedMotion(): boolean {
    if (typeof matchMedia === 'undefined') {
        return false;
    }

    if (!media) {
        media = window.matchMedia('(prefers-reduced-motion)');
    }

    return media.matches;
}
