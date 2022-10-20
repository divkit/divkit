import { ActionAnimation, AnyAnimation } from '../types/actionable';

export function flattenAnimation(animation: ActionAnimation): AnyAnimation[] {
    const res: AnyAnimation[] = [];

    if (animation.name === 'set') {
        (animation.items || []).forEach(item => {
            res.push(...flattenAnimation(item));
        });
    } else {
        res.push(animation);
    }

    return res;
}
