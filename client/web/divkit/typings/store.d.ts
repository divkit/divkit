export type StoreTypes = 'string' | 'number' | 'boolean';

export interface Store {
    /**
     * Fetches primitive from the store
     * @param name
     * @param type Expected value tpye
     */
    getValue(
        name: string,
        type: StoreTypes
    ): string | number | boolean | undefined;

    /**
     * Save primitive into the store
     * @param name
     * @param type Value type (for example, can be url)
     * @param value
     * @param lifetime Value lifetime in seconds
     */
    setValue(
        name: string,
        type: StoreTypes,
        value: string | number | boolean,
        lifetime: number
    ): void;
}
