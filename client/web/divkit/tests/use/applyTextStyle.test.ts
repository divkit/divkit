// @vitest-environment jsdom
import { beforeEach, describe, expect, test } from 'vitest';
import { applyTextStyle } from '../../src/use/applyTextStyle';
import { textGradientProperties } from '../../src/use/animatedTextGradient';

let node: HTMLElement;

beforeEach(() => {
    node = document.createElement('span');
});

describe('text style ownership', () => {
    test('applies ordinary and custom properties including numeric zero', () => {
        applyTextStyle(node, { color: 'red', '--text-color': 'blue', opacity: 0 });
        expect([node.style.color, node.style.getPropertyValue('--text-color'), node.style.opacity])
            .toEqual(['red', 'blue', '0']);
    });

    test('updates existing properties and adds new ones', () => {
        const action = applyTextStyle(node, { color: 'red', '--text-color': 'blue' });
        action.update({ color: 'green', '--text-color': 'black', 'font-size': '20px' });
        expect([node.style.color, node.style.getPropertyValue('--text-color'), node.style.fontSize])
            .toEqual(['green', 'black', '20px']);
    });

    test('removes only its own stale properties', () => {
        // Arrange
        node.style.cssText = 'padding: 5px; --divkit-text-gradient-offset: 25%; --divkit-text-gradient-radius: 100px';
        node.setAttribute('data-gradient-animation', 'js');
        const action = applyTextStyle(node, { color: 'red', '--text-color': 'blue' });
        // Act
        action.update({ color: undefined, padding: undefined });
        // Assert
        expect(node.style.cssText)
            .toBe('padding: 5px; --divkit-text-gradient-offset: 25%; --divkit-text-gradient-radius: 100px;');
        expect(node.getAttribute('data-gradient-animation')).toBe('js');
    });
});

describe('text gradient style conflicts', () => {
    test.each(Object.values(textGradientProperties))('preserves reserved %s on creation and update', property => {
        // Arrange
        node.style.setProperty(property, '25px');
        const action = applyTextStyle(node, { [property]: '100px' });
        // Act
        action.update({ [property]: undefined });
        // Assert
        expect(node.style.getPropertyValue(property)).toBe('25px');
    });
});
