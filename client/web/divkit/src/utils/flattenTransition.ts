import type { AnyTransition, AppearanceTransition } from '../types/base';

export function flattenTransition(transition: AppearanceTransition): AnyTransition[] {
    const res: AnyTransition[] = [];

    if (transition.type === 'set') {
        (transition.items || []).forEach(item => {
            res.push(...flattenTransition(item));
        });
    } else {
        res.push(transition);
    }

    return res;
}
