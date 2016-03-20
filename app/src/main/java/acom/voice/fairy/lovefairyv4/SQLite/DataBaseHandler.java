package acom.voice.fairy.lovefairyv4.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nir on 3/18/2016.
 * with help of http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 */


public class DataBaseHandler extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Contacts table name
    private static final String TABLE_CALL_LOG_HISTORY = "callhistorycounter";

    // Constants for Table Columns names
    //Column 0
    public static final String KEY_PHONE_ID = "_id";
    private static final int INDEX_PHONE_ID = 0;
    //Column 1
    public static final String KEY_DURATION = "duration";
    private static final int INDEX_DURATION = 1;
    //Column 2
    public static final String KEY_INCOMING_COUNTER = "incoming_counter";
    private static final int INDEX_INCOMING_COUNTER = 2;
    //Column 3
    public static final String KEY_OUTGOING_COUNTER = "outgoing_counter";
    private static final int INDEX_OUTGOING_COUNTER = 3;
    //Column 4
    public static final String KEY_MISSED_COUNTER = "missed_counter";
    private static final int INDEX_MISSED_COUNTER = 4;

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLE_CALL_LOG_HISTORY + "("
                        + KEY_PHONE_ID + " TEXT PRIMARY KEY,"
                        + KEY_DURATION + " INTEGER,"
                        + KEY_INCOMING_COUNTER + " INTEGER,"
                        + KEY_OUTGOING_COUNTER + " INTEGER,"
                        + KEY_MISSED_COUNTER + " INTEGER"
                        + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_LOG_HISTORY);

        // Create tables again
        onCreate(db);
    }

    public void addMultipleCallLogs(HashMap<String, Integer> durationHM,
                                    HashMap<String, Integer> incomingCounterHM,
                                    HashMap<String, Integer> outgoingCounterHM,
                                    HashMap<String, Integer> missedCounterHM) {

        SQLiteDatabase db = this.getWritableDatabase();

        for (Map.Entry<String, Integer> keyset : durationHM.entrySet()) {

            //Initiate Content Values to Build DB Rows
            //Key value pairs **params:  @Key - column index,@Value - column value
            ContentValues values = new ContentValues();
            values.put(KEY_PHONE_ID, keyset.getKey());
            values.put(KEY_DURATION, keyset.getValue());
            values.put(KEY_INCOMING_COUNTER, incomingCounterHM.get(keyset.getKey()));
            values.put(KEY_OUTGOING_COUNTER, outgoingCounterHM.get(keyset.getKey()));
            values.put(KEY_MISSED_COUNTER, missedCounterHM.get(keyset.getKey()));


            // Inserting Row
            db.insert(TABLE_CALL_LOG_HISTORY, null, values);
        }

        // Closing database connection
        db.close();
    }

    public void addCallLog(String phonenumber, int duration, String callType) {

        SQLiteDatabase db = this.getReadableDatabase();

        Integer mDuration = 0;
        Integer mIncoming = 0;
        Integer mOutgoing = 0;
        Integer mMissed = 0;
        if (isExists(phonenumber)) {
            Cursor cursor = db.query(
                    TABLE_CALL_LOG_HISTORY, //The table name to compile the query against.

                    //A list of which table columns to return. Passing "null" will return all columns.
                    new String[]{KEY_PHONE_ID,
                            KEY_DURATION,
                            KEY_INCOMING_COUNTER,
                            KEY_OUTGOING_COUNTER,
                            KEY_MISSED_COUNTER},

                    // Where-clause, i.e. filter for the selection of data, null will select all data.
                    KEY_PHONE_ID + "=?",
                    //You may include ?s in the "whereClause"".
                    // selectionArgs array
                    new String[]{phonenumber},
                    // These placeholders will get replaced by the values from the selectionArgs array.
                    null,//A filter declaring how to group rows, null will cause the rows to not be grouped.
                    null, //Filter for the groups, null means no filter.
                    null);//Limit
            if (cursor != null) {
                cursor.moveToFirst();
            }

            //Add new phone call stats to te row that been pulled out the table
            mDuration = cursor.getInt(INDEX_DURATION) + duration;
            mIncoming = cursor.getInt(INDEX_INCOMING_COUNTER);
            mOutgoing = cursor.getInt(INDEX_OUTGOING_COUNTER);
            mMissed = cursor.getInt(INDEX_MISSED_COUNTER);

            switch (callType) {
                case KEY_INCOMING_COUNTER:
                    mIncoming = cursor.getInt(INDEX_INCOMING_COUNTER) + 1;
                    break;

                case KEY_OUTGOING_COUNTER:
                    mOutgoing = cursor.getInt(INDEX_OUTGOING_COUNTER) + 1;
                    break;

                case KEY_MISSED_COUNTER:
                    mMissed = cursor.getInt(INDEX_MISSED_COUNTER) + 1;
                    break;
            }
            db.close();


            if (mIncoming == null) {
                mIncoming = 0;
            }
            if (mOutgoing == null) {
                mOutgoing = 0;
            }
            if (mMissed == null) {
                mMissed = 0;
            }

//            Log.e("DataBaseHandler", "Phone: " + phonenumber +
//                    " Duration: " + mDuration +
//                    " IncomingCounter: " + mIncoming +
//                    " Outgoing: " + mOutgoing +
//                    " Missed: " + mMissed);

            db = this.getWritableDatabase();

            //Build Updated Raw format
            ContentValues values = new ContentValues();
            values.put(KEY_PHONE_ID, phonenumber);
            values.put(KEY_DURATION, mDuration);
            values.put(KEY_INCOMING_COUNTER, mIncoming);
            values.put(KEY_OUTGOING_COUNTER, mOutgoing);
            values.put(KEY_MISSED_COUNTER, mMissed);

            // updating row where keyphoneId =? phonenumber
            db.update(TABLE_CALL_LOG_HISTORY, values, KEY_PHONE_ID + " = ?",
                    new String[]{phonenumber});

            db.close();
        }
        //Phone is not Exists
        else {


            switch (callType) {
                case KEY_INCOMING_COUNTER:
                    mIncoming = 1;
                    break;

                case KEY_OUTGOING_COUNTER:
                    mOutgoing = 1;
                    break;

                case KEY_MISSED_COUNTER:
                    mMissed = 1;
                    break;
            }

            //Building the new row
            ContentValues values = new ContentValues();
            values.put(KEY_PHONE_ID, phonenumber);
            values.put(KEY_DURATION, duration);
            values.put(KEY_INCOMING_COUNTER, mIncoming);
            values.put(KEY_OUTGOING_COUNTER, mOutgoing);
            values.put(KEY_MISSED_COUNTER, mMissed);

            // inserting new row
            db.insert(TABLE_CALL_LOG_HISTORY, null, values);
            db.close();
        }
    }

    public boolean isExists(String _id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select 1 from " + TABLE_CALL_LOG_HISTORY + " where " + KEY_PHONE_ID + "= ?",
                new String[]{_id});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean isTopFive (String phoneNumberId, String columnIndex){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_CALL_LOG_HISTORY, //The table name to compile the query against.
                new String[]{KEY_PHONE_ID,columnIndex},//A list of which table columns to return
                null, // filter for the selection of data, null will select all data.
                null, // selectionArgs array
                null, // A filter declaring how to group rows, null will cause the rows to not be grouped.
                null, // Filter for the groups.
                columnIndex + " DESC",//Order By
                "15"); //Limit

        if(cursor.moveToFirst()){
            do {
                if(phoneNumberId.equals(cursor.getString(INDEX_PHONE_ID))){
                    return true;
                }
            }while (cursor.moveToNext());
        }
        return false;
    }





//    // Adding new contact
//    public void addContact(ContactModel contact) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contact.getName()); // Contact Name
//        values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone Number
//
//        // Inserting Row
//        db.insert(TABLE_CALL_LOG_HISTORY, null, values);
//        db.close(); // Closing database connection
//    }
//
//    // Getting single contact
//    public ContactModel getContact(int id) {
//
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(
//                TABLE_CALL_LOG_HISTORY, //The table name to compile the query against.
//
//                new String[] {KEY_PHONE_ID, KEY_NAME, KEY_PH_NO }, //A list of which table columns to
//                // return. Passing "null" will return all columns.
//
//                KEY_PHONE_ID + "=?", // Where-clause, i.e. filter for the selection of data, null will select all data.
//
//                new String[] { String.valueOf(id) }, //You may include ?s in the "whereClause"".
//                // These placeholders will get replaced by the values from the selectionArgs array.
//
//                null,//A filter declaring how to group rows, null will cause the rows to not be grouped.
//                null,//Filter for the groups, null means no filter.
//                null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        ContactModel contact = new ContactModel(Integer.parseInt(cursor.getString(0)),
//                cursor.getString(1), cursor.getString(2));
//        // return contact
//        return contact;
//    }
//
//
//
//    // Getting All Contacts
//    public List<ContactModel> getAllContacts() {
//
//        List<ContactModel> contactList = new ArrayList<ContactModel>();
//
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + TABLE_CALL_LOG_HISTORY;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery,null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                ContactModel contact = new ContactModel();
//                contact.setID(Integer.parseInt(cursor.getString(0)));
//                contact.setName(cursor.getString(1));
//                contact.setPhoneNumber(cursor.getString(2));
//                // Adding contact to list
//                contactList.add(contact);
//            } while (cursor.moveToNext());
//        }
//
//        // return contact list
//        return contactList;
//    }
//
//    // Getting contacts Count
//    public int getContactsCount() {
//
//        String countQuery = "SELECT  * FROM " + TABLE_CALL_LOG_HISTORY;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();
//
//        // return count
//        return cursor.getCount();
//    }
//
//    // Updating single contact
//    public int updateContact(ContactModel contact) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contact.getName());
//        values.put(KEY_PH_NO, contact.getPhoneNumber());
//
//        // updating row
//        return db.update(TABLE_CALL_LOG_HISTORY, values, KEY_PHONE_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//    }
//
//    // Deleting single contact
//    public void deleteContact(ContactModel contact) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_CALL_LOG_HISTORY, KEY_PHONE_ID + " = ?",new String[] { String.valueOf(contact.getID()) });
//        db.close();
//    }
}
