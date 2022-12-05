import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface IInitialState {
  page: number;
  filters: string;
}
const initialState: IInitialState = {
  page: 1,
  filters: '',
};

const querySlice = createSlice({
  initialState,
  name: 'query',
  reducers: {
    setPage: (state, { payload }: PayloadAction<number>) => {
      state.page = payload;
    },
    setFilters: (state, { payload }: PayloadAction<string>) => {
      state.filters = payload;
    },
  },
});

export const { setPage, setFilters } = querySlice.actions;
export default querySlice.reducer;
