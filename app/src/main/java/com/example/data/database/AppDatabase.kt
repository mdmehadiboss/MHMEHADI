package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ProductDao
import com.example.data.dao.SaleDao
import com.example.data.entity.ProductEntity
import com.example.data.entity.SaleEntity
import com.example.data.entity.SaleItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ProductEntity::class, SaleEntity::class, SaleItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "glass_pos_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialProducts(database.productDao())
                    }
                }
            }
        }

        suspend fun populateInitialProducts(dao: ProductDao) {
            val initialProducts = listOf(
                ProductEntity(name = "Basmati Rice (1kg)", price = 120.0, stock = 50, category = "Grocery", sku = "RIC-01"),
                ProductEntity(name = "Soybean Oil (1L)", price = 185.0, stock = 30, category = "Grocery", sku = "OIL-01"),
                ProductEntity(name = "Milk Tea Pack", price = 45.0, stock = 100, category = "Beverage", sku = "TEA-01"),
                ProductEntity(name = "Mineral Water (500ml)", price = 20.0, stock = 80, category = "Beverage", sku = "WAT-01"),
                ProductEntity(name = "Crispy Potato Chips", price = 30.0, stock = 60, category = "Snacks", sku = "CHP-01"),
                ProductEntity(name = "Chocolate Biscuit", price = 50.0, stock = 40, category = "Snacks", sku = "BIS-01"),
                ProductEntity(name = "Organic Honey (250g)", price = 350.0, stock = 15, category = "Grocery", sku = "HNY-01"),
                ProductEntity(name = "USB-C Fast Cable", price = 220.0, stock = 25, category = "Electronics", sku = "CAB-01"),
                ProductEntity(name = "Wireless Optical Mouse", price = 450.0, stock = 12, category = "Electronics", sku = "MOU-01"),
                ProductEntity(name = "Paracetamol 500mg (Strip)", price = 25.0, stock = 90, category = "Pharmacy", sku = "MED-01")
            )
            dao.insertProducts(initialProducts)
        }
    }
}
