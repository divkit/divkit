import { render } from '@divkitframework/divkit';

const json = {
    card: {
        log_id: 'test',
        states: [
            {
                state_id: 0,
                div: {
                    type: 'container',
                    items: [{
                        type: 'text',
                        text: 'Test'
                    }]
                }
            }
        ]
    },
    templates: {}
};

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

describe('test server bundle', () => {
    test('should run without errors', () => {
        expect(() => {
            render({
                id: 'test',
                json
            });
        }).not.toThrow();
    });

    test('should return valid html', () => {
        const html = render({
            id: 'test',
            json
        });
        expect(typeof html).toBe('string');
        // Try to find valid html tags
        expect(Boolean(html.match(/<(“[^”]*”|'[^’]*’|[^'”>])*>/g))).toBe(true);
    });

    test('should log error on empty id', () => {
        const spy = jest.spyOn(console, 'error').mockImplementation(noop);

        // @ts-expect-error "id" is required
        render({
            json
        });

        expect(spy).toHaveBeenCalled();
    });

    test('should log error on empty json', () => {
        const spy = jest.spyOn(console, 'error').mockImplementation(noop);

        // @ts-expect-error "json" is required
        render({
            id: 'test'
        });

        expect(spy).toHaveBeenCalled();
    });
});
