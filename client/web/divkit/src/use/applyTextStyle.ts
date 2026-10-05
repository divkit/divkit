import type { Style } from '../types/general';
import { textGradientProperties } from './animatedTextGradient';

const reservedProperties = new Set<string>(Object.values(textGradientProperties));

export function applyTextStyle(node: HTMLElement, style: Style) {
    const appliedProperties = new Set<string>();
    const action = {
        update(style: Style): void {
            for (const property of appliedProperties) {
                if (!Object.prototype.hasOwnProperty.call(style, property) || style[property] === undefined) {
                    node.style.removeProperty(property);
                    appliedProperties.delete(property);
                }
            }

            for (const [property, value] of Object.entries(style)) {
                if (reservedProperties.has(property)) {
                    continue;
                }
                if (value !== undefined) {
                    node.style.setProperty(property, String(value));
                    appliedProperties.add(property);
                }
            }
        }
    };
    action.update(style);
    return action;
}
