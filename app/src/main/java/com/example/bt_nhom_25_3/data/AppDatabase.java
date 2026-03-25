package com.example.bt_nhom_25_3.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.bt_nhom_25_3.data.dao.CategoryDao;
import com.example.bt_nhom_25_3.data.dao.OrderDao;
import com.example.bt_nhom_25_3.data.dao.OrderDetailDao;
import com.example.bt_nhom_25_3.data.dao.ProductDao;
import com.example.bt_nhom_25_3.data.dao.UserDao;
import com.example.bt_nhom_25_3.data.entity.Category;
import com.example.bt_nhom_25_3.data.entity.Order;
import com.example.bt_nhom_25_3.data.entity.OrderDetail;
import com.example.bt_nhom_25_3.data.entity.Product;
import com.example.bt_nhom_25_3.data.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailDao orderDetailDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "shopping_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                UserDao userDao = INSTANCE.userDao();
                CategoryDao categoryDao = INSTANCE.categoryDao();
                ProductDao productDao = INSTANCE.productDao();

                // Tạo user mẫu
                userDao.insert(new User("admin", "123", "admin"));
                userDao.insert(new User("user1", "123", "customer"));

                // Tạo categories mẫu
                categoryDao.insert(new Category("Điện thoại", "Các loại smartphone"));
                categoryDao.insert(new Category("Laptop", "Máy tính xách tay"));
                categoryDao.insert(new Category("Phụ kiện", "Tai nghe, cáp, sạc"));

                // Tạo products mẫu
                productDao.insert(new Product("iPhone 14", 20000000, 1, ""));
                productDao.insert(new Product("Samsung S23", 18000000, 1, ""));
                productDao.insert(new Product("Oppo Reno 8", 10000000, 1, ""));
                productDao.insert(new Product("MacBook Air M1", 19000000, 2, ""));
                productDao.insert(new Product("Dell XPS 13", 25000000, 2, ""));
                productDao.insert(new Product("Asus ROG", 30000000, 2, ""));
                productDao.insert(new Product("Tai nghe AirPods", 3000000, 3, ""));
                productDao.insert(new Product("Sạc dự phòng 10000mAh", 500000, 3, ""));
            });
        }
    };
}
