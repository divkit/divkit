'use strict';

/**
 * Simulate long tap on element
 *
 * @alias browser.yaLongTap
 *
 * @param {string} selector
 * @param {number} duration
 * @returns {browser}
 */
module.exports = async function (selector, duration) {
    const elem = await this.$(selector);
    const [
        {x, y},
        {width, height},
        viewport
    ] = await Promise.all([
        elem.getLocation(),
        elem.getSize(),
        this.execute(() => {
            return {
                x: window.scrollX,
                y: window.scrollY,
                width: window.innerWidth,
                height: window.innerHeight
            };
        })
    ]);

    const xCoord = Math.round(x - viewport.x + Math.min(width, viewport.width) * 0.5);
    const yCoord = Math.round(y - viewport.y + Math.min(height, viewport.width) * 0.5);

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
                    x: xCoord,
                    y: yCoord
                },
                {
                    type: 'pointerDown',
                    button: 0
                },
                {
                    type: 'pause',
                    duration
                },
                {
                    type: 'pointerUp',
                    button: 0
                },
            ]
        }
    ]);
};
