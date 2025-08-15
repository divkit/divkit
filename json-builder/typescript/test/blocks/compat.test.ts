import { DivGradientBackground, DivText } from '../../src';

describe('compat tests', (): void => {
    it('block with DivGradientBackground', (): void => {
        const block = new DivText({
            text: 'Text with gradient background',
            background: [
                new DivGradientBackground({
                    angle: 270,
                    colors: ['#112233', '#332211'],
                }),
            ],
        });

        expect(block).toMatchSnapshot();
    });
});
