package com.example.android.lovefairyv4.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nir on 3/18/2016.
 * with help of http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 */


public class DataBaseHandler extends SQLiteOpenHelper {


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Contacts table name
    private static final String TABLE_CONTACTS = "callhistorycounter";

    // Contacts Table Columns names
    private static final String KEY_PHONE_ID = "id";
    private static final String KEY_DURATION = "name";
    private static final String KEY_INCOMING_COUNTER = "incoming_counter";
    private static final String KEY_OUTGOING_COUNTER = "outgoing_counter";
    private static final String KEY_MISSED_COUNTER = "missed_counter";

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_PHONE_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addContact(ContactModel contact) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone Number

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public ContactModel getContact(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_CONTACTS, //The table name to compile the query against.

                new String[] {KEY_PHONE_ID, KEY_NAME, KEY_PH_NO }, //A list of which table columns to
                // return. Passing "null" will return all columns.

                KEY_PHONE_ID + "=?", // Where-clause, i.e. filter for the selection of data, null will select all data.

                new String[] { String.valueOf(id) }, //You may include ?s in the "whereClause"".
                // These placeholders will get replaced by the values from the selectionArgs array.

                null,//A filter declaring how to group rows, null will cause the rows to not be grouped.
                null,//Filter for the groups, null means no filter.
                null);
        if (cursor != null)
            cursor.moveToFirst();

        ContactModel contact = new ContactModel(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return contact;
    }



    // Getting All Contacts
    public List<ContactModel> getAllContacts() {

        List<ContactModel> contactList = new ArrayList<ContactModel>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContactModel contact = new ContactModel();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Getting contacts Count
    public int getContactsCount() {

        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating single contact
    public int updateContact(ContactModel contact) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_PHONE_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }

    // Deleting single contact
    public void deleteContact(ContactModel contact) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_PHONE_ID + " = ?",new String[] { String.valueOf(contact.getID()) });
        db.close();
    }
}
