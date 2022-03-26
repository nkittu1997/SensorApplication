package com.example.sensorApplication;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import net.sqlcipher.database.SupportFactory;

@Database(entities = {UserDetails.class}, version = 2)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDetailsImpl userInfoDao();
    private static volatile AppDatabase dbInstance;
//    public static synchronized AppDatabase getInstance(Context context){
//        //Create new database with last name for a name if none exist
//        if(dbInstance == null){
//            dbInstance = Room
//                    .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "gaur")
//                    .allowMainThreadQueries()
//                    .build();
//        }
//        return dbInstance;
//    }
    public static synchronized AppDatabase getInstance(Context context, byte[] passphrase){
        System.out.println("test");
        if(dbInstance== null){
            final SupportFactory factory = new SupportFactory(passphrase);
            System.out.println("test");
            dbInstance = Room
                        .databaseBuilder(context.getApplicationContext(),AppDatabase.class,"gaur")
                        .openHelperFactory(factory)
                        .allowMainThreadQueries()
                        .build();
        }

        return dbInstance;

    }
}
