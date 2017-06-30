/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDBHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = CatalogActivity.class.getName();
    private static final int PET_LOADER = 0;
    PetCursorAdapter mPetCursorAdapter;
    private SQLiteDatabase db;
    private PetDBHelper mPetDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        mPetDBHelper = new PetDBHelper(this);


        ListView listView = (ListView) findViewById(R.id.list_view_pet);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        // setUp an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet ( until the loader finishes) so pass in null for the Cursor.
        mPetCursorAdapter = new PetCursorAdapter(this, null);

        listView.setAdapter(mPetCursorAdapter);


        // kick off the loader
        getSupportLoaderManager().initLoader(PET_LOADER, null, this);
        //displayDatabaseInfo();
    }
//
//    // accessing database so that we can see database is working
//    private void displayDatabaseInfo() {
//
//        //db = mPetDBHelper.getReadableDatabase();
//
//        String[] projection = {
//                PetEntry._ID,
//                PetEntry.COLUMN_PET_NAME,
//                PetEntry.COLUMN_PET_BREED,
//                PetEntry.COLUMN_PET_GENDER,
//                PetEntry.COLUMN_PET_WEIGHT
//        };
//        //Cursor cursor = db.query(PetEntry.TABLE_NAME, projection, null, null, null, null, null);
//        Cursor cursor = getContentResolver().query(
//                PetEntry.CONTENT_URI,
//                projection,
//                null,
//                null,
//                null);
//
//        ListView listView = (ListView) findViewById(R.id.list_view_pet);
//        View emptyView = findViewById(R.id.empty_view);
//        listView.setEmptyView(emptyView);
//        PetCursorAdapter petCursorAdapter = new PetCursorAdapter(this, cursor);
//        listView.setAdapter(petCursorAdapter);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPet() {

        // Creating contentValues which will be used for database data insert
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, "7kg");
        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.dummy_data_not_inserted),
                    Toast.LENGTH_SHORT).show();
        } else {
            //displayDatabaseInfo();
            Toast.makeText(getApplicationContext(), getString(R.string.dummy_data_inserted),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //displayDatabaseInfo();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED
        };
        return new CursorLoader(this, // Parent activity context
                PetEntry.CONTENT_URI, // Provider content URI to query
                projection, // Columns to include in the resulting Cursor
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update petCursorAdapter with this new cursor containing updated pet data
        mPetCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback call when the needs to be deleted
        mPetCursorAdapter.swapCursor(null);
    }
}
