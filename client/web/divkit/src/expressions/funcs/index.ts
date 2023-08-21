import { registerStd } from './std';
import { registerDatetime } from './datetime';
import { registerStrings } from './strings';
import { registerMath } from './math';
import { registerColors } from './colors';
import { registerInterval } from './interval';
import { registerDict } from './dict';

export function register(): void {
    registerStd();
    registerDatetime();
    registerInterval();
    registerStrings();
    registerMath();
    registerColors();
    registerDict();
}
