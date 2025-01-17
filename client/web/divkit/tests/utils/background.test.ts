import {
    describe,
    expect,
    test
} from 'vitest';

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

    test('radial', () => {
        expect(getBackground([{
            type: 'radial_gradient',
            colors: ['#fc0', '#f00']
        }])).toMatchSnapshot();

        expect(getBackground([{
            type: 'radial_gradient',
            colors: ['#fc0', '#f00'],
            radius: {
                type: 'fixed',
                value: 100
            }
        }])).toMatchSnapshot();

        expect(getBackground([{
            type: 'radial_gradient',
            colors: ['#fc0', '#f00'],
            radius: {
                type: 'relative',
                value: 'nearest_corner'
            }
        }])).toMatchSnapshot();

        expect(getBackground([{
            type: 'radial_gradient',
            colors: ['#fc0', '#f00'],
            radius: {
                type: 'relative',
                value: 'nearest_side'
            }
        }])).toMatchSnapshot();

        expect(getBackground([{
            type: 'radial_gradient',
            colors: ['#fc0', '#f00'],
            radius: {
                type: 'relative',
                value: 'farthest_side'
            }
        }])).toMatchSnapshot();

        expect(getBackground([{
            type: 'radial_gradient',
            colors: ['#fc0', '#f00'],
            radius: {
                type: 'relative',
                value: 'farthest_corner'
            }
        }])).toMatchSnapshot();

        expect(getBackground([{
            type: 'radial_gradient',
            colors: ['#fc0', '#f00'],
            center_x: {
                type: 'fixed',
                value: 100
            }
        }])).toMatchSnapshot();

        expect(getBackground([{
            type: 'radial_gradient',
            colors: ['#fc0', '#f00'],
            center_x: {
                type: 'relative',
                value: .7
            },
            center_y: {
                type: 'fixed',
                value: 150
            }
        }])).toMatchSnapshot();

        expect(getBackground([{
            type: 'radial_gradient',
            colors: ['#fc0', '#f00'],
            center_x: {
                type: 'relative',
                value: 1
            },
            center_y: {
                type: 'relative',
                value: 1
            },
            radius: {
                type: 'relative',
                value: 'nearest_side'
            }
        }])).toMatchSnapshot();
    });
});
