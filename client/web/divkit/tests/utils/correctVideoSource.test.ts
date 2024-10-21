import { correctVideoSource } from '../../src/utils/correctVideoSource';

describe('correctVideoSource', () => {
    test('simple', () => {
        expect(correctVideoSource(undefined, [])).toEqual([]);
        expect(correctVideoSource([], [])).toEqual([]);

        expect(correctVideoSource([{
            type: 'video_source',
            url: 'abcde',
            mime_type: 'def'
        }], [])).toEqual([{
            src: 'abcde',
            type: 'def'
        }]);
    });

    test('incorrect', () => {
        expect(correctVideoSource({
            //@ts-expect-error incorrect type
            type: 'video2',
            video_sources: [{
                type: 'video_source',
                url: 'abcde'
            }]
        }, [])).toEqual([]);

        expect(correctVideoSource([{
            url: 'abcde',
            mime_type: 'video/abc'
        }], [])).toEqual([]);

        expect(correctVideoSource([{
            type: 'video_source'
        }], [])).toEqual([]);
    });
});
