package com.example;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;

public class MyClass {
    public static void main(String[] arg) {
        //使用greendao创建数据库 大大提高编码效率 降低错误率
      /*  Schema schema = new Schema(1, "com.wynkl.mn.database");
        //用户表
        Entity diary = schema.addEntity("Diary");
        diary.implementsSerializable();

        diary.addIdProperty().primaryKey().autoincrement();
        diary.addStringProperty("passWord");
        diary.addStringProperty("diaryType");
        diary.addStringProperty("picture");
        diary.addStringProperty("content");
        diary.addStringProperty("record");
        diary.addStringProperty("mood");
        diary.addDateProperty("writeTime");

        Entity accountBook = schema.addEntity("AccountBook");
        accountBook.implementsSerializable();
        accountBook.addIdProperty().primaryKey().autoincrement();;
        accountBook.addStringProperty("type");//类型
        accountBook.addBooleanProperty("turnover");//收入或支出
        accountBook.addStringProperty("use");//用途
        accountBook.addFloatProperty("money");//金额
        accountBook.addDateProperty("writeTime");//时间
        accountBook.addStringProperty("remark");//备注

        try {
            new DaoGenerator().generateAll(schema, "app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
