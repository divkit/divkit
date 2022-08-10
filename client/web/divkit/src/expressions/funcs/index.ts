import { registerStd } from './std';
import { registerDatetime } from './datetime';
import { registerStrings } from './strings';
import { registerMath } from './math';
import { registerColors } from './colors';

export function register(): void {
    registerStd();
    registerDatetime();
    registerStrings();
    registerMath();
    registerColors();
}
