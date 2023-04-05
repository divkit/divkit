describe('regression', () => {
    describe('Variables in DivImage', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('variables/div_image');
        });
       
        it('Click on blue label tint color', async function() {
            await this.browser.$('span=set blue tint color').then(elem => elem.click());

            await this.browser.assertView('blue_parallelogram', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
        })

        it('Click on red label tint color', async function() {
            await this.browser.$('span=set red tint color').then(elem => elem.click());

            await this.browser.assertView('red_parallelogram', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
        })

        it('Click on red label img1', async function() {
            await this.browser.$('span=img#1').then(elem => elem.click());

            await this.browser.assertView('red_camera_icon', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
        })

        it('Click on blue label img1', async function() {
            await this.browser.$('span=img#1').then(elem => elem.click());
            await this.browser.$('span=set blue tint color').then(elem => elem.click());

            await this.browser.assertView('blue_camera_icon', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
        })

        it('Click on red label img2', async function() {
            await this.browser.$('span=img#2').then(elem => elem.click());

            await this.browser.assertView('red_stripe_pattern', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
        })

        it('Click on blue label img2', async function() {
            await this.browser.$('span=img#2').then(elem => elem.click());
            await this.browser.$('span=set blue tint color').then(elem => elem.click());

            await this.browser.assertView('blue_stripe_pattern', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
        })
   
    });
});