import { describe, test, expect } from 'vitest';
import { getTankerKeyRaw, tankerKeyToVariableName } from './tanker';

describe('tanker', () => {
    test('getTankerKeyRaw', () => {
        expect(getTankerKeyRaw('')).toEqual(undefined);
        expect(getTankerKeyRaw('@{}')).toEqual(undefined);
        expect(getTankerKeyRaw('@{tanker/key}')).toEqual('tanker/key');
    });

    test('tankerKeyToVariableName', () => {
        expect(tankerKeyToVariableName('abc')).toEqual('tanker_abc');
        expect(tankerKeyToVariableName('abc.def')).toEqual('tanker_abc_def');
    });
});
