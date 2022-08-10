import type Shortcut from './shortcut';
import { globalHandler, Handler } from './keybinder';

export type ShortcutList = [Shortcut, Handler, number?][];

export function shortcuts(node: HTMLElement, list: ShortcutList) {
    const unbind = list.map(it => {
        return globalHandler(it[0], it[1], it[2]).unbind;
    });

    return {
        destroy() {
            for (const item of unbind) {
                item();
            }
        }
    };
}
