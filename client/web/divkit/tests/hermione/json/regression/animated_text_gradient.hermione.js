async function openAnimatedTextGradientFixture() {
    await this.browser.yaOpenRegressionJson('animated_text_gradient');
}

async function setReducedMotion(browser) {
    await browser.sendCommand('Emulation.setEmulatedMedia', {
        features: [
            {
                name: 'prefers-reduced-motion',
                value: 'reduce'
            }
        ]
    });
}

async function resetEmulatedMedia(browser) {
    await browser.sendCommand('Emulation.setEmulatedMedia', {
        media: '',
        features: []
    });
}

async function getGradientLayoutStyle(browser, testId) {
    return browser.execute(id => {
        const node = document.querySelector(`[data-test-id="${id}"] > span`);
        const computed = getComputedStyle(node);

        return {
            animationName: computed.animationName,
            backgroundPosition: computed.backgroundPosition,
            backgroundRepeat: computed.backgroundRepeat,
            backgroundSize: computed.backgroundSize
        };
    }, testId);
}

async function getGradientFrame(browser, testId, phase) {
    return browser.execute((id, progress) => {
        const node = document.querySelector(`[data-test-id="${id}"] > span`);
        const animation = node.getAnimations()[0];
        animation.pause();
        animation.currentTime = animation.effect.getTiming().duration * progress;
        const computed = getComputedStyle(node);
        return {
            image: computed.backgroundImage,
            offset: computed.getPropertyValue('--divkit-text-gradient-offset').trim(),
            radius: computed.getPropertyValue('--divkit-text-gradient-radius').trim(),
            size: computed.backgroundSize
        };
    }, testId, phase);
}

describe('Animated text gradient timing', () => {
    hermione.only.in('chromeMobile', 'Animation requires CSS.registerProperty');
    beforeEach(openAnimatedTextGradientFixture);

    it('Uses the default animation parameters', async function() {
        const style = await this.browser.execute(() => {
            const node = document.querySelector('[data-test-id="animated-default"] > span');
            const computed = getComputedStyle(node);

            return {
                duration: computed.animationDuration,
                iterationCount: computed.animationIterationCount,
                timingFunction: computed.animationTimingFunction
            };
        });

        style.should.deep.equal({
            duration: '1.6s',
            iterationCount: 'infinite',
            timingFunction: 'linear'
        });
    });

    hermione.only.in('chromeMobile', 'Animation requires CSS.registerProperty');
    it('Uses a custom animation duration', async function() {
        const duration = await this.browser.execute(() => {
            const node = document.querySelector('[data-test-id="animated-custom"] > span');

            return getComputedStyle(node).animationDuration;
        });

        duration.should.equal('2.4s');
    });

});

describe('Animated text gradient direction', () => {
    hermione.only.in('chromeMobile', 'Animation requires CSS.registerProperty');
    beforeEach(openAnimatedTextGradientFixture);

    it('Restores the full unscaled linear gradient in the middle of the cycle', async function() {
        const frame = await getGradientFrame(this.browser, 'animated-default', 0.5);
        frame.image.should.contain('linear-gradient(90deg');
        frame.image.should.contain('rgb(255, 59, 48)');
        frame.image.should.contain('rgb(255, 255, 255)');
        frame.image.should.contain('rgb(0, 122, 255)');
        frame.offset.should.equal('0%');
        frame.size.should.equal('auto');
    });

    hermione.only.in('chromeMobile', 'Animation requires CSS.registerProperty');
    it('Moves the vertical gradient forward in the second half of the cycle', async function() {
        const frame = await getGradientFrame(this.browser, 'animated-range', 0.75);
        frame.image.should.contain('linear-gradient(0deg');
        frame.offset.should.equal('50%');
        frame.size.should.equal('auto');
    });
});

describe('Animated radial text gradient geometry', () => {
    hermione.only.in('chromeMobile', 'Animation requires CSS.registerProperty');
    beforeEach(openAnimatedTextGradientFixture);

    it('Keeps the radial radius fixed while moving its center', async function() {
        const start = await getGradientFrame(this.browser, 'animated-custom', 0);
        const end = await getGradientFrame(this.browser, 'animated-custom', 0.75);
        start.image.should.contain('radial-gradient(');
        end.image.should.not.equal(start.image);
        start.offset.should.equal('-100%');
        end.offset.should.equal('50%');
        end.radius.should.equal(start.radius);
        end.radius.should.not.equal('');
        end.size.should.equal('auto');
    });
});

describe('Animated text gradient expressions', () => {
    hermione.only.in('chromeMobile', 'Animation requires CSS.registerProperty');
    beforeEach(openAnimatedTextGradientFixture);

    it('Updates an expression-based animation duration', async function() {
        await this.browser.$('[data-test-id="animated-custom"]').then(elem => elem.click());

        await this.browser.waitUntil(() => {
            return this.browser.execute(() => {
                const node = document.querySelector('[data-test-id="animated-custom"] > span');

                return getComputedStyle(node).animationDuration === '0.8s';
            });
        });

        const duration = await this.browser.execute(() => {
            const node = document.querySelector('[data-test-id="animated-custom"] > span');

            return getComputedStyle(node).animationDuration;
        });

        duration.should.equal('0.8s');
    });
});

describe('Static animated text gradient', () => {
    beforeEach(openAnimatedTextGradientFixture);

    it('Keeps a zero-duration gradient visible and static', async function() {
        const style = await this.browser.execute(() => {
            const node = document.querySelector('[data-test-id="animated-zero"] > span');
            const computed = getComputedStyle(node);

            return {
                animationName: computed.animationName,
                backgroundImage: computed.backgroundImage
            };
        });

        style.animationName.should.equal('none');
        style.backgroundImage.should.not.equal('none');
    });

    it('Keeps the gradient when the text is focused', async function() {
        await this.browser.keys('Tab');
        await this.browser.keys('Tab');

        const style = await this.browser.execute(() => {
            const node = document.querySelector('[data-test-id="animated-focus"]');
            const computed = getComputedStyle(node.querySelector('span'));

            return {
                isFocused: document.activeElement === node,
                color: computed.color,
                backgroundImage: computed.backgroundImage
            };
        });

        style.isFocused.should.equal(true);
        style.color.should.equal('rgba(0, 0, 0, 0)');
        style.backgroundImage.should.not.equal('none');
    });
});

describe('Animated text gradient child content', () => {
    beforeEach(openAnimatedTextGradientFixture);

    it('Keeps the multiline range color outside the gradient', async function() {
        const segmentColors = await this.browser.execute(() => {
            const node = document.querySelector('[data-test-id="animated-range"] > span');

            return Array.from(node.children, child => getComputedStyle(child).color);
        });

        segmentColors.should.include('rgb(255, 214, 10)');
    });

    it('Keeps range color and inline image outside a static gradient', async function() {
        const content = await this.browser.execute(() => {
            const node = document.querySelector('[data-test-id="animated-image"] > span');

            return {
                rangeColor: getComputedStyle(node.querySelector('span')).color,
                hasInlineImage: Boolean(node.querySelector('img'))
            };
        });

        content.should.deep.equal({
            rangeColor: 'rgb(0, 255, 0)',
            hasInlineImage: true
        });
    });
});

describe('Animated text gradient reduced motion', () => {
    hermione.only.in('chromeMobile', 'Media emulation requires the Chrome DevTools Protocol');

    beforeEach(openAnimatedTextGradientFixture);

    afterEach(async function() {
        await resetEmulatedMedia(this.browser);
    });

    it('Uses the same layout as a static gradient', async function() {
        await setReducedMotion(this.browser);

        const reducedMotionStyle = await getGradientLayoutStyle(this.browser, 'animated-default');
        const staticGradientStyle = await getGradientLayoutStyle(this.browser, 'animated-zero');

        reducedMotionStyle.should.deep.equal(staticGradientStyle);
    });
});

describe('Animated text gradient compatibility', () => {
    hermione.only.in('firefoxMobile', 'Firefox 69 does not support CSS.registerProperty');
    beforeEach(openAnimatedTextGradientFixture);

    it('Animates the linear gradient when property interpolation is unavailable', async function() {
        // Arrange
        const style = await this.browser.execute(() => {
            const node = document.querySelector('[data-test-id="animated-default"] > span');
            const computed = getComputedStyle(node);
            return { animation: computed.animationName, image: computed.backgroundImage, size: computed.backgroundSize };
        });
        // Act
        await this.browser.waitUntil(() => this.browser.execute(initialImage => {
            const node = document.querySelector('[data-test-id="animated-default"] > span');
            const image = getComputedStyle(node).backgroundImage;
            return image.startsWith('linear-gradient(') && image !== initialImage;
        }, style.image));
        // Assert
        style.animation.should.equal('none');
        style.image.should.contain('linear-gradient(90deg');
        style.size.should.equal('auto');
    });

    it('Moves the radial center without changing its radius in the fallback', async function() {
        // Arrange
        const start = await this.browser.execute(() => {
            const node = document.querySelector('[data-test-id="animated-custom"] > span');
            const computed = getComputedStyle(node);
            return {
                image: computed.backgroundImage,
                radius: computed.getPropertyValue('--divkit-text-gradient-radius')
            };
        });
        // Act
        await this.browser.waitUntil(() => this.browser.execute(initialImage => {
            const node = document.querySelector('[data-test-id="animated-custom"] > span');
            const image = getComputedStyle(node).backgroundImage;
            return image.startsWith('radial-gradient(') && image !== initialImage;
        }, start.image));
        // Assert
        const radius = await this.browser.execute(() => {
            const node = document.querySelector('[data-test-id="animated-custom"] > span');
            return getComputedStyle(node).getPropertyValue('--divkit-text-gradient-radius');
        });
        start.image.should.contain('radial-gradient(');
        start.radius.should.not.equal('');
        radius.should.equal(start.radius);
    });
});
