'use strict';

module.exports = async function(step) {
    const directions = {
        up: [{ x: 0.5, y: 0.9 }, { x: 0.5, y: 0.1 }],
        down: [{ x: 0.5, y: 0.1 }, { x: 0.5, y: 0.9 }],
        left: [{ x: 0.9, y: 0.5 }, { x: 0.1, y: 0.5 }],
        right: [{ x: 0.1, y: 0.5 }, { x: 0.9, y: 0.5 }],
    };
    let points;
    if (step.type === 'tap') {
        points = [step.point === undefined ? { x: 0.5, y: 0.5 } : step.point];
    } else if (step.type === 'swipe') {
        if (step.direction !== undefined && step.from === undefined && step.to === undefined) {
            points = directions[step.direction];
        } else if (step.direction === undefined && step.from && step.to) {
            points = [step.from, step.to];
        }
    }
    if (!points || points.some(point => !point ||
        !Number.isFinite(point.x) || !Number.isFinite(point.y) ||
        point.x < 0 || point.x > 1 || point.y < 0 || point.y > 1)) {
        throw new Error(`Invalid ${step.type} coordinates: ${JSON.stringify(step)}`);
    }
    if (points.length === 2 && points[0].x === points[1].x && points[0].y === points[1].y) {
        throw new Error('Swipe points must be different');
    }

    const queryTarget = mode => this.execute((target, mode) => {
        let elements;
        if (target?.type === 'div_id') {
            elements = [...document.querySelectorAll('[data-test-id]')]
                .filter(element => element.getAttribute('data-test-id') === target.id);
        } else if (target?.type === 'text') {
            elements = [...document.querySelectorAll('body *')].filter(element =>
                element.textContent === target.text &&
                ![...element.children].some(child => child.textContent === target.text));
        } else {
            throw new Error(`Unsupported gesture target: ${JSON.stringify(target)}`);
        }
        if (mode !== 'exists') {
            elements = elements.filter(element => {
                const rect = element.getBoundingClientRect();
                return rect.width > 0 && rect.height > 0 && getComputedStyle(element).visibility !== 'hidden' &&
                    !element.closest('[aria-hidden="true"]');
            });
        }
        if (mode !== 'get') {
            return elements.length > 0;
        }
        if (elements.length !== 1) {
            throw new Error(`Expected one gesture target, found ${elements.length}: ${JSON.stringify(target)}`);
        }
        return elements[0];
    }, step.target, mode);

    await this.waitUntil(() => queryTarget('exists'), {
        timeout: 3000,
        timeoutMsg: `Gesture target did not appear within 3s: ${JSON.stringify(step.target)}`,
    });
    await this.waitUntil(() => queryTarget('visible'), {
        timeout: 3000,
        timeoutMsg: `Gesture target did not become visible within 3s: ${JSON.stringify(step.target)}`,
    });
    const target = await queryTarget('get');

    if (step.type === 'tap') {
        const element = await this.$(target);
        if (step.point === undefined) {
            await element.click();
        } else {
            const offsets = await this.execute((element, point) => {
                const rect = element.getBoundingClientRect();
                const origin = element.getClientRects()[0];
                const x = Math.round(rect.left + rect.width * point.x);
                const y = Math.round(rect.top + rect.height * point.y);
                if (!origin || x < 0 || x >= innerWidth || y < 0 || y >= innerHeight) {
                    throw new Error(`Tap point outside viewport: ${x}, ${y}`);
                }
                return {
                    x: x - Math.floor((Math.max(0, origin.left) + Math.min(innerWidth, origin.right)) / 2),
                    y: y - Math.floor((Math.max(0, origin.top) + Math.min(innerHeight, origin.bottom)) / 2),
                };
            }, target, step.point);
            await element.click(offsets);
        }
        return;
    }

    const coordinates = await this.execute((element, points) => {
        const rect = element.getBoundingClientRect();
        return points.map(point => {
            const x = Math.round(rect.left + rect.width * point.x);
            const y = Math.round(rect.top + rect.height * point.y);
            if (x < 0 || x >= innerWidth || y < 0 || y >= innerHeight) {
                throw new Error(`Gesture point outside viewport: ${x}, ${y}`);
            }
            return { x, y };
        });
    }, target, points);

    const actions = [
        { type: 'pointerMove', duration: 0, origin: 'viewport', ...coordinates[0] },
        { type: 'pointerDown', button: 0 },
        { type: 'pause', duration: 200 },
        { type: 'pointerMove', duration: 500, origin: 'viewport', ...coordinates[1] },
        { type: 'pointerUp', button: 0 },
    ];
    try {
        await this.performActions([{ type: 'pointer', id: 'finger', parameters: { pointerType: 'touch' }, actions }]);
    } finally {
        await this.releaseActions();
    }
};
