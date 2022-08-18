import { simpleCheckInput } from '../../src/utils/simpleCheckInput';

describe('simpleCheckInput', () => {
    test('simple', () => {
        expect(simpleCheckInput({
        })).toMatchSnapshot();

        expect(simpleCheckInput({
            // @ts-expect-error Not enough required fields
            card: {},
            templates: {}
        })).toMatchSnapshot();

        expect(simpleCheckInput({
            card: {
                log_id: 'abc',
                states: []
            },
            templates: {}
        })).toMatchSnapshot();

        expect(simpleCheckInput({
            card: {
                log_id: 'abc',
                states: [{
                    state_id: 0,
                    div: {
                        type: 'text'
                    }
                }]
            },
            templates: {
                text: {}
            }
        })).toMatchSnapshot();

        expect(simpleCheckInput({
            card: {
                log_id: 'abc',
                // @ts-expect-error Explicit incorrect json
                states: [{
                    state_id: 0
                }]
            },
            templates: {}
        })).toMatchSnapshot();

        expect(simpleCheckInput({
            card: {
                log_id: 'abc',
                states: [{
                    state_id: 0,
                    div: {
                        type: 'text'
                    }
                }]
            },
            templates: {}
        })).toMatchSnapshot();
    });
});
