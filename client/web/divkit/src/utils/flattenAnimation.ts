import type { MaybeMissing } from '../expressions/json';
import type { Animation, AnyAnimation } from '../types/animation';

export function flattenAnimation(animation: MaybeMissing<Animation>): MaybeMissing<AnyAnimation>[] {
    const res: MaybeMissing<AnyAnimation>[] = [];

    if (animation.name === 'set') {
        (animation.items || []).forEach(item => {
            res.push(...flattenAnimation(item));
        });
    } else {
        res.push(animation as MaybeMissing<AnyAnimation>);
    }

    return res;
}
