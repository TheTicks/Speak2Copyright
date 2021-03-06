package theticks.s2t;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to avoid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        try {
            this.database = openHelper.getReadableDatabase();
        } catch(Exception e) {
            // TODO Log exceptions and handle it in a recoverable way
        }
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of studies
     */
    public List<String> getStudies() {
        if (database == null) {
            return new ArrayList<String>();
        }

        List<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT name FROM studies", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public Map<String, List<String>> executeSQL(String sql){
        Map<String, List<String>> data = new HashMap<String, List<String>>();
        Cursor cursor = database.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        for(String columnName: columnNames) {
            data.put(columnName, new ArrayList<String>());
        }
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            for(int i=0; i<cursor.getColumnCount();i++) {
                String columnName = cursor.getColumnName(i);
                List<String> values = data.get(columnName);
                values.add(cursor.getString(i));
            }
            cursor.moveToNext();
        }
        cursor.close();
        return data;
    }
}
