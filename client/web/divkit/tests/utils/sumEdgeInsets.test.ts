import { sumEdgeInsets } from '../../src/utils/sumEdgeInsets';

describe('sumEdgeInsets', () => {
    it('simple', () => {
        expect(sumEdgeInsets(null, null)).toEqual({});

        expect(sumEdgeInsets({
            top: 10
        }, null)).toEqual({
            top: 10
        });

        expect(sumEdgeInsets(null, {
            top: 10
        })).toEqual({
            top: 10
        });

        expect(sumEdgeInsets({
            top: 10
        }, {
            top: 10
        })).toEqual({
            top: 20
        });

        expect(sumEdgeInsets({
            top: 10,
            left: 10
        }, {
            top: 10,
            right: 10
        })).toEqual({
            top: 20,
            right: 10,
            left: 10
        });
    });
});
