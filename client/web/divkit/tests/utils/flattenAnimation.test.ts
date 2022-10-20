import { flattenAnimation } from '../../src/utils/flattenAnimation';

describe('flattenAnimation', () => {
    test('simple', () => {
        expect(flattenAnimation({
            name: 'fade',
            start_value: 1,
            end_value: 0.4,
            duration: 500,
            interpolator: 'ease_in_out'
        })).toEqual([{
            name: 'fade',
            start_value: 1,
            end_value: 0.4,
            duration: 500,
            interpolator: 'ease_in_out'
        }]);
        expect(flattenAnimation({
            name: 'set',
            items: [{
                name: 'fade',
                start_value: 1,
                end_value: 0.4,
                duration: 500,
                interpolator: 'ease_in_out'
            }]
        })).toEqual([{
            name: 'fade',
            start_value: 1,
            end_value: 0.4,
            duration: 500,
            interpolator: 'ease_in_out'
        }]);
        expect(flattenAnimation({
            name: 'set',
            items: [{
                name: 'fade',
                start_value: 1,
                end_value: 0.4,
                duration: 500,
                interpolator: 'ease_in_out'
            }, {
                name: 'set',
                items: [{
                    name: 'scale',
                    start_value: 1,
                    end_value: 0.4,
                    duration: 500,
                    interpolator: 'ease_in_out'
                }]
            }]
        })).toEqual([{
            name: 'fade',
            start_value: 1,
            end_value: 0.4,
            duration: 500,
            interpolator: 'ease_in_out'
        }, {
            name: 'scale',
            start_value: 1,
            end_value: 0.4,
            duration: 500,
            interpolator: 'ease_in_out'
        }]);
    });
});
