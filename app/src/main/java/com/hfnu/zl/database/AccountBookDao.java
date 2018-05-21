package com.hfnu.zl.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ACCOUNT_BOOK".
*/
public class AccountBookDao extends AbstractDao<AccountBook, Long> {

    public static final String TABLENAME = "ACCOUNT_BOOK";

    /**
     * Properties of entity AccountBook.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Type = new Property(1, String.class, "type", false, "TYPE");
        public final static Property Turnover = new Property(2, Boolean.class, "turnover", false, "TURNOVER");
        public final static Property Use = new Property(3, String.class, "use", false, "USE");
        public final static Property Money = new Property(4, Float.class, "money", false, "MONEY");
        public final static Property WriteTime = new Property(5, java.util.Date.class, "writeTime", false, "WRITE_TIME");
        public final static Property Remark = new Property(6, String.class, "remark", false, "REMARK");
    }


    public AccountBookDao(DaoConfig config) {
        super(config);
    }
    
    public AccountBookDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ACCOUNT_BOOK\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TYPE\" TEXT," + // 1: type
                "\"TURNOVER\" INTEGER," + // 2: turnover
                "\"USE\" TEXT," + // 3: use
                "\"MONEY\" REAL," + // 4: money
                "\"WRITE_TIME\" INTEGER," + // 5: writeTime
                "\"REMARK\" TEXT);"); // 6: remark
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACCOUNT_BOOK\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AccountBook entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(2, type);
        }
 
        Boolean turnover = entity.getTurnover();
        if (turnover != null) {
            stmt.bindLong(3, turnover ? 1L: 0L);
        }
 
        String use = entity.getUse();
        if (use != null) {
            stmt.bindString(4, use);
        }
 
        Float money = entity.getMoney();
        if (money != null) {
            stmt.bindDouble(5, money);
        }
 
        java.util.Date writeTime = entity.getWriteTime();
        if (writeTime != null) {
            stmt.bindLong(6, writeTime.getTime());
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(7, remark);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AccountBook entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(2, type);
        }
 
        Boolean turnover = entity.getTurnover();
        if (turnover != null) {
            stmt.bindLong(3, turnover ? 1L: 0L);
        }
 
        String use = entity.getUse();
        if (use != null) {
            stmt.bindString(4, use);
        }
 
        Float money = entity.getMoney();
        if (money != null) {
            stmt.bindDouble(5, money);
        }
 
        java.util.Date writeTime = entity.getWriteTime();
        if (writeTime != null) {
            stmt.bindLong(6, writeTime.getTime());
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(7, remark);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AccountBook readEntity(Cursor cursor, int offset) {
        AccountBook entity = new AccountBook( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // type
            cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0, // turnover
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // use
            cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4), // money
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // writeTime
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // remark
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AccountBook entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setType(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTurnover(cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0);
        entity.setUse(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMoney(cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4));
        entity.setWriteTime(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setRemark(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AccountBook entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AccountBook entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AccountBook entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
