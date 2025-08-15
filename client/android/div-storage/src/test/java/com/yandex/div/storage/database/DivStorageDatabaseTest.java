package com.yandex.div.storage.database;

import static com.yandex.div.storage.database.StorageSchema.DB_VERSION;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.database.CursorWindow;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;

import com.yandex.div.storage.DivStorageImpl;
import com.yandex.div.storage.templates.RawTemplateData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.ExceptionsKt;
import kotlin.collections.CollectionsKt;

@SuppressWarnings("KotlinInternalInJava")
@RunWith(RobolectricTestRunner.class)
public class DivStorageDatabaseTest {
    private static final long TEST_TIMEOUT = 5000L;
    @SuppressWarnings("unused") // used to preload cursor window size which could be slow in robolectric outside of tests
    private static final CursorWindow sPreloadedCursorWindow = new CursorWindow("test");

    private DivStorageImpl mStorage;
    private AndroidDatabaseOpenHelper mHelper;
    private DatabaseOpenHelperProvider mProvider = spy(new DatabaseOpenHelperProvider() {
        @Override
        @NonNull
        public DatabaseOpenHelper provide(@NonNull Context context,
                                          @NonNull String name, int version,
                                          @NonNull DatabaseOpenHelper.CreateCallback ccb,
                                          @NonNull DatabaseOpenHelper.UpgradeCallback ucb) {
            if (mHelper == null) {
                mHelper = spy(new AndroidDatabaseOpenHelper(context, name, version, ccb, ucb));
                SQLiteDatabase spyDb = spy(SQLiteDatabase.create(null));
                spyDb.setForeignKeyConstraintsEnabled(true);
                doNothing().when(spyDb).close();
                DatabaseOpenHelper.Database db = mHelper.wrapDataBase(spyDb);
                doReturn(db).when(mHelper).getWritableDatabase();
                doReturn(db).when(mHelper).getReadableDatabase();
            }
            return mHelper;
        }
    });

    @Before
    public void setUp() {
        mStorage = spy(new DivStorageImpl(
                ApplicationProvider.getApplicationContext(),
                mProvider, "test-db"
        ));
        mHelper.getWritableDatabase().execSQL("PRAGMA foreign_keys = ON");
    }

    @Test
    public void onCreateMustCallCreateDatabase() {
        // Set
        DatabaseOpenHelper.Database database = mock(DatabaseOpenHelper.Database.class);
        DivStorageImpl spy = mStorage;
        doNothing().when(spy).createTables(any(DatabaseOpenHelper.Database.class));
        doNothing().when(spy).dropTables(any(DatabaseOpenHelper.Database.class));

        // Do
        spy.onCreate(database);

        // Verify
        verify(spy, times(0)).dropTables(database);
        verify(spy, times(1)).createTables(database);
    }

    @Test
    public void onUpdateMustNotRecreateDatabaseIfActualVersion() {
        // Set
        DatabaseOpenHelper.Database database = mock(DatabaseOpenHelper.Database.class);

        // Do
        mStorage.onUpgrade(database, DB_VERSION, DB_VERSION);

        // Verify
        verify(mStorage, times(0)).dropTables(database);
        verify(mStorage, times(0)).createTables(database);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void storageStatementExecutionNotConcealingExceptions () {
        List<? extends StorageException> exceptions =
                mStorage.getStatementExecutor()
                        .execute(StorageStatements.INSTANCE.deleteTemplatesWithoutLinksToCards())
                        .getErrors();
        Assert.assertEquals(1, exceptions.size());
        StorageException exception = exceptions.get(0);
        Assert.assertEquals(SQLException.class, exception.getCause().getClass());
    }

    @Test
    public void storageCreateDatabaseWithGivenPrefix() {
        verify(mProvider, times(1)).provide(
          any(), eq("test-db-div-storage.db"), eq(DB_VERSION), any(), any()
        );
    }


    @NonNull
    private Map<String, byte[]> toMap(@NonNull List<RawTemplateData> templates) {
        Map<String, byte[]> result = new HashMap<>();
        for (RawTemplateData template : templates) {
            result.put(template.getHash(), template.getData());
        }
        return result;
    }

    private void assertErrorsIsEmpty(List<? extends Exception> errors) {
        String msg = CollectionsKt.joinToString(
                errors, ", ", "", "", -1, "...", ExceptionsKt::stackTraceToString
        );
        Assert.assertTrue(msg, errors.isEmpty());
    }
}
