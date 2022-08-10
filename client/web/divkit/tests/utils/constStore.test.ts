import { constStore } from '../../src/utils/constStore';

describe('constStore', () => {
    test('simple', () => {
        const store = constStore(1);
        let received;
        store.subscribe(val => {
            received = val;
        });
        expect(received).toBe(1);
    });
});
