import { getBackground } from '../../src/utils/background';

describe('background', () => {
    test('getBackground', () => {
        expect(getBackground([{
            type: 'solid',
            color: '#fc0'
        }])).toMatchSnapshot();

        expect(getBackground([{
            type: 'gradient',
            colors: ['#fc0', '#f00']
        }])).toMatchSnapshot();

        expect(getBackground([{
            type: 'gradient',
            colors: ['#fc0', '#f00'],
            angle: 123
        }])).toMatchSnapshot();

        expect(getBackground([{
            type: 'image',
            image_url: 'https://ya.ru'
        }])).toMatchSnapshot();

        expect(getBackground([{
            type: 'image',
            image_url: 'https://ya.ru',
            content_alignment_horizontal: 'right',
            content_alignment_vertical: 'bottom',
            scale: 'fit'
        }])).toMatchSnapshot();
    });

    test('escape', () => {
        expect(getBackground([{
            type: 'image',
            image_url: '"<script&'
        }])).toMatchSnapshot();
    });
});
