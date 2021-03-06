package com.whiz.grxp;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.whiz.grxp.R;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DbAdapter extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "grxp.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;

	// the DAO object we use to access the Person table
	private Dao<Person, String> personDao = null;
	private RuntimeExceptionDao<Person, String> personRuntimeDao = null;

	public DbAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DbAdapter.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Person.class);
			//TableUtils.createTable(connectionSource, Trip.class);
			//TableUtils.createTable(connectionSource, Expense.class);
		} catch (SQLException e) {
			Log.e(DbAdapter.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

		// here we try inserting data in the on-create as a test
		RuntimeExceptionDao<Person, String> dao = getPersonDao();
		// create some entries in the onCreate
		dao.create(new Person("Me"));
		Log.i(DbAdapter.class.getName(), "created default entries in onCreate: ");
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DbAdapter.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Person.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DbAdapter.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our Person class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Person, String> getDao() {
		if (personDao == null) {
			try {
				personDao = getDao(Person.class);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return personDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our Person class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Person, String> getPersonDao() {
		if (personRuntimeDao == null) {
			personRuntimeDao = getRuntimeExceptionDao(Person.class);
		}
		return personRuntimeDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		personRuntimeDao = null;
	}
}

