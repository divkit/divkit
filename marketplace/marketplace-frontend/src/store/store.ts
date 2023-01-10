import { configureStore } from '@reduxjs/toolkit'
import { templatesApi } from '../services/api'
import querySlice from './slices/querySlice'
export const store = configureStore({
  reducer: {
    [templatesApi.reducerPath]: templatesApi.reducer,
    query: querySlice,
  },
  middleware: getDefaultMiddleware =>
    getDefaultMiddleware().concat(templatesApi.middleware),
})

export type AppDispatch = typeof store.dispatch
export type RootState = ReturnType<typeof store.getState>
