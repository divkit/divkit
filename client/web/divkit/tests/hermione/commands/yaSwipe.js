'use strict';

/**
 * Simulate swipe
 *
 * @alias browser.yaSwipe
 *
 * @param {YaSwipeParams} params
 * @returns {browser}
 */
module.exports = async function({ selector, duration, direction, offset }) {
    const elem = await this.$(selector);
    let [
        {x, y},
        {width, height},
        viewport
    ] = await Promise.all([
        elem.getLocation(),
        elem.getSize(),
        this.execute(() => {
            return {
                scrollX: window.scrollX,
                scrollY: window.scrollY,
                width: window.innerWidth,
                height: window.innerHeight
            };
        })
    ]);

    const xK = direction === 'left' ? -1 : direction === 'right' ? +1 : 0;
    const yK = direction === 'up' ? -1 : direction === 'down' ? +1 : 0;
    x = Math.round(x - viewport.scrollX + Math.min(width, viewport.width - x) * (0.5 - 0.3 * xK));
    y = Math.round(y - viewport.scrollY + Math.min(height, viewport.height - y) * (0.5 - 0.3 * yK));

    const moves = {
        'up': {x, y: Math.max(0, y - offset)},
        'down': {x, y: Math.min(viewport.height, y + offset)},
        'left': {x: Math.max(0, x - offset), y},
        'right': {x: Math.min(viewport.width, x + offset), y}
    };
    const move = moves[direction];

    if (!move) {
        throw new TypeError(`direction must be one of ${Object.keys(moves)}, got ${direction}`);
    }

    await this.performActions([
        {
            type: 'pointer',
            id: 'finger',
            parameters: {
                pointerType: 'touch'
            },
            actions: [
                {
                    type: 'pointerMove',
                    duration: 0,
                    origin: 'viewport',
                    x,
                    y
                },
                {
                    type: 'pointerDown',
                    button: 0
                },
                {
                    type: 'pointerMove',
                    duration: duration,
                    origin: 'viewport',
                    x: move.x,
                    y: move.y
                },
                {
                    type: 'pointerUp',
                    button: 0
                }
            ]
        }
    ]);
};

/**
 * @typedef YaSwipeParams
 * @property {string} selector
 * @property {string} direction
 * @property {number} duration
 * @property {number} offset
 */
